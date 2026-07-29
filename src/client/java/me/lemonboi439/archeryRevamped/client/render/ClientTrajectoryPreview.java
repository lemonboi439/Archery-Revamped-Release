package me.lemonboi439.archeryRevamped.client.render;

import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.physics.ArrowPhysicsEngine;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.registry.RegistryKeys;
import me.lemonboi439.archeryRevamped.enchantment.RicochetEnchantment;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Renders the trajectory command as one client-side trail and prediction.
 *
 * <p>The points behind the arrow are checkpoints sampled from the actual
 * custom arrow. The points ahead are simulated with the same movement order
 * used by the server: position, vanilla/configured physics, then collision.
 * No particles or entities are created by this renderer.</p>
 */
public final class ClientTrajectoryPreview {
    private static final int TRAIL_LIFETIME_MILLIS = 10_000;
    private static final int MAX_PREDICTION_TICKS = 1200;
    private static final int CURVE_SUBDIVISIONS = 3;
    private static final int OLDEST_FADE_POINT_COUNT = 24;
    private static final int LINE_ALPHA = 150;
    private static final int TUBE_SIDES = 6;
    private static final double TUBE_RADIUS = 0.025D;
    private static final double SLOWEST_COLOUR_SPEED = 0.05D;
    private static final double FASTEST_COLOUR_SPEED = 3.15D;
    // Ordered slowest -> fastest: blue, cyan, green, yellow, orange, red.
    private static final int[][] SPEED_COLOURS = {
            {45, 90, 255},
            {40, 210, 230},
            {60, 210, 100},
            {245, 220, 55},
            {255, 140, 35},
            {235, 45, 45}
    };

    private static final RenderLayer TRAJECTORY_LAYER = RenderLayer.of(
            "archery_revamped_trajectory",
            net.minecraft.client.render.RenderSetup.builder(RenderPipelines.DEBUG_QUADS)
                    .translucent()
                    .expectedBufferSize(32768)
                    .build()
    );

    // Entity ids are reused by Minecraft. UUIDs keep a new arrow from
    // inheriting the trail history of an older arrow with the same id.
    private static final Map<UUID, PathHistory> PATHS = new HashMap<>();
    private static World trackedWorld;
    private static boolean trajectoryEnabled;
    private static boolean colourVisualisationEnabled;

    private ClientTrajectoryPreview() {
    }

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(ClientTrajectoryPreview::render);
    }

    public static void setTrajectoryEnabled(boolean enabled) {
        trajectoryEnabled = enabled;
        if (!enabled) {
            PATHS.clear();
        }
    }

    public static void setTrajectoryState(boolean enabled, boolean colourVisualisation) {
        colourVisualisationEnabled = colourVisualisation;
        setTrajectoryEnabled(enabled);
    }

    private static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null
                || context.matrices() == null || context.consumers() == null) {
            return;
        }

        if (trackedWorld != client.world) {
            PATHS.clear();
            trackedWorld = client.world;
        }

        long now = System.currentTimeMillis();
        Map<UUID, ArcheryArrowEntity> visibleArrows = new HashMap<>();

        // The server's entity tracking range is still the hard limit, but a
        // large query keeps long flights from being clipped by this renderer.
        for (ArcheryArrowEntity arrow : client.world.getEntitiesByClass(
                ArcheryArrowEntity.class,
                client.player.getBoundingBox().expand(512.0D),
                ArcheryArrowEntity::isAlive)) {
            if (!arrow.isTrajectoryPreviewEnabled()) {
                continue;
            }

            UUID arrowId = arrow.getUuid();
            visibleArrows.put(arrowId, arrow);

            PathHistory history = PATHS.get(arrowId);
            if (history == null) {
                // A stuck/finished arrow remains alive and pickupable. Its
                // expired trail must not be created again on the next frame.
                if (arrow.isTrajectoryFinished() || arrow.isArrowInGround()) {
                    continue;
                }
                history = new PathHistory();
                PATHS.put(arrowId, history);
            }
            history.seedInitialPrediction(client, arrow);
            history.recordActual(
                    arrow.getTrajectorySpawnPosition(),
                    arrow.getLerpedPos(client.getRenderTickCounter().getTickProgress(false)),
                    arrow.age
            );

            if (arrow.isTrajectoryFinished() || arrow.isArrowInGround()) {
                history.finish(now);
            }
        }

        // A discarded impact arrow can disappear in the same update that its
        // tracked finished flag arrives. Keep its authoritative checkpoints
        // for the requested ten seconds instead of losing the trail.
        Iterator<Map.Entry<UUID, PathHistory>> iterator = PATHS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, PathHistory> entry = iterator.next();
            PathHistory history = entry.getValue();
            if (!visibleArrows.containsKey(entry.getKey()) && !history.finished) {
                history.finish(now);
            }
            if (history.expired(now)) {
                iterator.remove();
            }
        }

        if (!PATHS.isEmpty()) {
            renderPaths(context, client, visibleArrows, now);
        }

        if (trajectoryEnabled) {
            renderAimingPreview(context, client);
        }

    }

    private static void renderPaths(WorldRenderContext context, MinecraftClient client,
                                    Map<UUID, ArcheryArrowEntity> visibleArrows, long now) {
        Camera camera = client.gameRenderer.getCamera();
        Vec3d cameraPosition = camera.getCameraPos();
        MatrixStack matrices = context.matrices();
        VertexConsumerProvider consumers = context.consumers();
        VertexConsumer vertices = consumers.getBuffer(TRAJECTORY_LAYER);

        matrices.push();
        MatrixStack.Entry matrix = matrices.peek();
        for (Map.Entry<UUID, PathHistory> entry : PATHS.entrySet()) {
            PathHistory history = entry.getValue();
            if (history.actualPoints.size() < 2) {
                continue;
            }

            List<Vec3d> points = new ArrayList<>(history.actualPoints);
            ArcheryArrowEntity arrow = visibleArrows.get(entry.getKey());
            if (arrow != null && !history.finished && arrow.isAlive()) {
                Vec3d currentPosition = arrow.getLerpedPos(client.getRenderTickCounter().getTickProgress(false));
                List<Vec3d> prediction = predict(client, arrow, currentPosition, arrow.getVelocity(),
                        arrow.getBounceCount(), arrow.getRicochetLevel());
                if (!prediction.isEmpty()) {
                    // The first prediction point is the current arrow tip,
                    // which is already the last actual checkpoint.
                    points.addAll(prediction.subList(1, prediction.size()));
                }
            }

            renderSmoothPath(vertices, matrix, points, cameraPosition, history.finished, now,
                    history.finishedAtMillis);
        }
        matrices.pop();
    }

    /**
     * Predicts until the first entity/block impact or the configured arrow
     * lifetime. Each block segment is raycast before it is accepted. A
     * ricochet reflects the simulated velocity and carries on, so the
     * predicted path follows the same bounce budget as the arrow.
     */
    private static List<Vec3d> predict(MinecraftClient client, Entity source,
                                       Vec3d startPosition, Vec3d startVelocity,
                                       int initialBounceCount, int ricochetLevel) {
        List<Vec3d> points = new ArrayList<>();
        points.add(startPosition);

        Vec3d position = startPosition;
        Vec3d velocity = startVelocity;
        int bounceCount = initialBounceCount;

        int predictionTicks = Math.max(1, Math.min(
                MAX_PREDICTION_TICKS, ConfigManager.getMaxLifetimeTicks()));
        for (int tick = 0; tick < predictionTicks; tick++) {
            if (velocity.lengthSquared() < 1.0E-8D) {
                break;
            }

            Vec3d nextPosition = position.add(velocity);
            BlockHitResult blockHit = raycastBlock(client, source, position, nextPosition);
            Vec3d entityHit = findEntityImpact(client, source, position, nextPosition);
            if (entityHit != null && (blockHit.getType() == HitResult.Type.MISS
                    || entityHit.subtract(position).lengthSquared()
                    < blockHit.getPos().subtract(position).lengthSquared())) {
                points.add(entityHit);
                break;
            }

            if (blockHit.getType() != HitResult.Type.MISS) {
                Vec3d impactPosition = blockHit.getPos();
                points.add(impactPosition);

                if (bounceCount < ricochetLevel) {
                    Direction side = blockHit.getSide();
                    Vec3d normal = side.getDoubleVector();
                    double dot = velocity.dotProduct(normal);
                    Vec3d reflected = velocity
                            .subtract(normal.multiply(2.0D * dot))
                            .multiply(1.0D - ConfigManager.getRicochetVelocityLossPercent() / 100.0D);

                    bounceCount++;
                    position = impactPosition.add(normal.multiply(0.01D));
                    velocity = ArrowPhysicsEngine.applyPreviewPhysics(client.world, position, reflected);
                    points.add(position);
                    continue;
                }
                break;
            }

            points.add(nextPosition);
            velocity = ArrowPhysicsEngine.applyPreviewPhysics(client.world, position, velocity);
            position = nextPosition;
        }
        return points;
    }

    private static Vec3d findEntityImpact(MinecraftClient client, Entity source,
                                          Vec3d start, Vec3d end) {
        Box searchBox = new Box(start, end).expand(1.0D);
        Entity owner = source instanceof ArcheryArrowEntity arrow ? arrow.getOwner() : source;
        Vec3d closest = null;
        double closestDistance = Double.POSITIVE_INFINITY;

        for (Entity candidate : client.world.getOtherEntities(source, searchBox,
                entity -> entity.isAlive() && !entity.isSpectator() && entity != owner)) {
            var hit = candidate.getBoundingBox()
                    .expand(candidate.getTargetingMargin())
                    .raycast(start, end);
            if (hit.isEmpty()) {
                continue;
            }

            Vec3d hitPosition = hit.get();
            double distance = hitPosition.subtract(start).lengthSquared();
            if (distance < closestDistance) {
                closest = hitPosition;
                closestDistance = distance;
            }
        }
        return closest;
    }

    private static BlockHitResult raycastBlock(MinecraftClient client, Entity arrow,
                                               Vec3d start, Vec3d end) {
        return client.world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                arrow
        ));
    }

    private static void renderAimingPreview(WorldRenderContext context, MinecraftClient client) {
        PlayerEntity player = client.player;
        ItemStack weapon = player.getActiveItem();
        if (!player.isUsingItem()
                || (!(weapon.getItem() instanceof BowItem) && !(weapon.getItem() instanceof CrossbowItem))) {
            return;
        }

        float tickProgress = client.getRenderTickCounter().getTickProgress(false);
        Vec3d direction = player.getRotationVector().normalize();
        // Start at the rendered camera/aim origin. Using the player's feet
        // plus standingEyeHeight puts the preview below the crosshair when
        // camera interpolation or first-person bobbing is active.
        Camera camera = client.gameRenderer.getCamera();
        Vec3d start = camera.getCameraPos().add(direction.multiply(0.16D));
        double speed = getAimingSpeed(player, weapon);
        int ricochetLevel = getRicochetLevel(client, weapon);
        List<Vec3d> points = predict(client, player, start, direction.multiply(speed), 0, ricochetLevel);
        if (points.size() < 2) {
            return;
        }

        MatrixStack matrices = context.matrices();
        VertexConsumer vertices = context.consumers().getBuffer(TRAJECTORY_LAYER);
        matrices.push();
        renderSmoothPath(vertices, matrices.peek(), points, camera.getCameraPos(), false,
                System.currentTimeMillis(), 0L);
        matrices.pop();
    }

    private static double getAimingSpeed(PlayerEntity player, ItemStack weapon) {
        if (weapon.getItem() instanceof BowItem) {
            double useProgress = Math.min(1.0D, Math.max(0.0D, player.getItemUseTime() / 20.0D));
            useProgress = (useProgress * useProgress + useProgress * 2.0D) / 3.0D;
            // Keep the preview visible during the first frames of a draw.
            return Math.max(0.12D, useProgress * 3.0D);
        }

        if (CrossbowItem.isCharged(weapon)) {
            return 3.15D;
        }

        double charge = Math.min(1.0D, Math.max(0.05D, player.getItemUseTime() / 25.0D));
        return 3.15D * charge;
    }

    private static int getRicochetLevel(MinecraftClient client, ItemStack weapon) {
        return client.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT)
                .getOptional(RicochetEnchantment.KEY)
                .map(entry -> EnchantmentHelper.getLevel(entry, weapon))
                .map(level -> ConfigManager.limitEnchantmentLevel(level, RicochetEnchantment.MAX_LEVEL))
                .orElse(0);
    }

    private static void renderSmoothPath(VertexConsumer vertices, MatrixStack.Entry matrix,
                                         List<Vec3d> points, Vec3d cameraPosition,
                                         boolean finished, long now, long finishedAtMillis) {
        int rawSegmentCount = points.size() - 1;
        int renderedSegmentCount = rawSegmentCount * CURVE_SUBDIVISIONS;
        if (renderedSegmentCount <= 0) {
            return;
        }

        for (int segment = 0; segment < rawSegmentCount; segment++) {
            for (int subdivision = 0; subdivision < CURVE_SUBDIVISIONS; subdivision++) {
                double t0 = subdivision / (double) CURVE_SUBDIVISIONS;
                double t1 = (subdivision + 1) / (double) CURVE_SUBDIVISIONS;
                Vec3d start = smoothPoint(points, segment, t0).subtract(cameraPosition);
                Vec3d end = smoothPoint(points, segment, t1).subtract(cameraPosition);

                int startIndex = segment * CURVE_SUBDIVISIONS + subdivision;
                int endIndex = startIndex + 1;
                int startAlpha = alphaFor(startIndex, renderedSegmentCount, finished, now, finishedAtMillis);
                int endAlpha = alphaFor(endIndex, renderedSegmentCount, finished, now, finishedAtMillis);
                int[] colour = colourForSpeed(points.get(segment + 1).subtract(points.get(segment)).length());
                putTubeSegment(vertices, matrix, start, end, startAlpha, endAlpha, colour);
            }
        }
    }

    private static Vec3d smoothPoint(List<Vec3d> points, int segment, double t) {
        Vec3d p0 = points.get(Math.max(0, segment - 1));
        Vec3d p1 = points.get(segment);
        Vec3d p2 = points.get(segment + 1);
        Vec3d p3 = points.get(Math.min(points.size() - 1, segment + 2));
        Vec3d incoming = p1.subtract(p0);
        Vec3d outgoing = p2.subtract(p1);

        // Never round a sharp collision corner. A Catmull-Rom curve can
        // overshoot through the wall at a ricochet and create the old spikes.
        if (incoming.lengthSquared() < 1.0E-8D || outgoing.lengthSquared() < 1.0E-8D
                || incoming.normalize().dotProduct(outgoing.normalize()) < 0.35D) {
            return p1.lerp(p2, t);
        }
        return catmullRom(points, segment, t);
    }

    private static Vec3d catmullRom(List<Vec3d> points, int segment, double t) {
        Vec3d p0 = points.get(Math.max(0, segment - 1));
        Vec3d p1 = points.get(segment);
        Vec3d p2 = points.get(segment + 1);
        Vec3d p3 = points.get(Math.min(points.size() - 1, segment + 2));

        double t2 = t * t;
        double t3 = t2 * t;
        return p1.multiply(2.0D)
                .add(p2.subtract(p0).multiply(t))
                .add(p0.multiply(2.0D).subtract(p1.multiply(5.0D))
                        .add(p2.multiply(4.0D)).subtract(p3).multiply(t2))
                .add(p3.subtract(p0).add(p1.multiply(3.0D)).subtract(p2.multiply(3.0D))
                        .multiply(t3))
                .multiply(0.5D);
    }

    /**
     * Emits a small six-sided tube for one path segment. Minecraft's line
     * primitive is a screen-facing flat strip, which becomes visibly chunky
     * and broken at distance. Quads give the trail a stable, thin volume.
     */
    private static void putTubeSegment(VertexConsumer vertices, MatrixStack.Entry matrix,
                                       Vec3d start, Vec3d end,
                                       int startAlpha, int endAlpha, int[] colour) {
        Vec3d axis = end.subtract(start);
        double lengthSquared = axis.lengthSquared();
        if (lengthSquared < 1.0E-10D) {
            return;
        }
        axis = axis.normalize();

        Vec3d midpoint = start.add(end).multiply(0.5D);
        Vec3d toCamera = midpoint.multiply(-1.0D);
        if (toCamera.lengthSquared() < 1.0E-10D) {
            toCamera = new Vec3d(0.0D, 0.0D, 1.0D);
        } else {
            toCamera = toCamera.normalize();
        }

        Vec3d side = axis.crossProduct(toCamera);
        if (side.lengthSquared() < 1.0E-10D) {
            side = axis.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
            if (side.lengthSquared() < 1.0E-10D) {
                side = axis.crossProduct(new Vec3d(1.0D, 0.0D, 0.0D));
            }
        }
        side = side.normalize();
        Vec3d up = axis.crossProduct(side).normalize();

        for (int sideIndex = 0; sideIndex < TUBE_SIDES; sideIndex++) {
            double angle0 = sideIndex * Math.PI * 2.0D / TUBE_SIDES;
            double angle1 = (sideIndex + 1) * Math.PI * 2.0D / TUBE_SIDES;
            Vec3d offset0 = side.multiply(Math.cos(angle0) * TUBE_RADIUS)
                    .add(up.multiply(Math.sin(angle0) * TUBE_RADIUS));
            Vec3d offset1 = side.multiply(Math.cos(angle1) * TUBE_RADIUS)
                    .add(up.multiply(Math.sin(angle1) * TUBE_RADIUS));

            putTubeVertex(vertices, matrix, start.add(offset0), startAlpha, colour);
            putTubeVertex(vertices, matrix, end.add(offset0), endAlpha, colour);
            putTubeVertex(vertices, matrix, end.add(offset1), endAlpha, colour);
            putTubeVertex(vertices, matrix, start.add(offset1), startAlpha, colour);
        }
    }

    private static void putTubeVertex(VertexConsumer vertices, MatrixStack.Entry matrix,
                                      Vec3d position, int alpha, int[] colour) {
        vertices.vertex(matrix, (float) position.x, (float) position.y, (float) position.z)
                .color(colour[0], colour[1], colour[2], alpha);
    }

    private static int alphaFor(int index, int segmentCount, boolean finished,
                                long now, long finishedAtMillis) {
        double oldestProgress = Math.min(1.0D,
                index / (double) Math.max(1, OLDEST_FADE_POINT_COUNT));
        int alpha = (int) Math.round(LINE_ALPHA * Math.pow(oldestProgress, 1.75D));

        if (finished) {
            double lifetime = Math.max(0.0D, now - finishedAtMillis)
                    / (double) TRAIL_LIFETIME_MILLIS;
            alpha = (int) Math.round(alpha * Math.max(0.0D, 1.0D - lifetime));
        }
        return alpha;
    }

    private static int[] colourForSpeed(double speed) {
        if (!colourVisualisationEnabled) {
            return new int[]{70, 210, 255};
        }

        double progress = (speed - SLOWEST_COLOUR_SPEED)
                / (FASTEST_COLOUR_SPEED - SLOWEST_COLOUR_SPEED);
        progress = Math.max(0.0D, Math.min(1.0D, progress));
        double scaled = progress * (SPEED_COLOURS.length - 1);
        int lower = Math.min(SPEED_COLOURS.length - 2, (int) Math.floor(scaled));
        double blend = scaled - lower;
        int[] first = SPEED_COLOURS[lower];
        int[] second = SPEED_COLOURS[lower + 1];
        return new int[]{
                (int) Math.round(first[0] + (second[0] - first[0]) * blend),
                (int) Math.round(first[1] + (second[1] - first[1]) * blend),
                (int) Math.round(first[2] + (second[2] - first[2]) * blend)
        };
    }

    private static final class PathHistory {
        private final List<Vec3d> actualPoints = new ArrayList<>();
        private int lastRecordedAge = Integer.MIN_VALUE;
        private boolean spawnPositionSeeded;
        private boolean provisionalFirstPoint;
        private boolean predictedEndpointSeeded;
        private boolean finished;
        private long finishedAtMillis;

        private Vec3d firstSpawnPosition;

        private void seedInitialPrediction(MinecraftClient client, ArcheryArrowEntity arrow) {
            Vec3d spawnPosition = arrow.getTrajectorySpawnPosition();
            Vec3d initialVelocity = arrow.getTrajectoryInitialVelocity();
            if (spawnPosition == null || initialVelocity == null || arrow.age <= 0
                    || spawnPositionSeeded) {
                return;
            }

            List<Vec3d> simulated = predict(client, arrow, spawnPosition, initialVelocity,
                    0, arrow.getRicochetLevel());
            if (simulated.isEmpty()) {
                return;
            }

            int endpointIndex = Math.min(simulated.size() - 1, arrow.age);
            actualPoints.clear();
            actualPoints.addAll(simulated.subList(0, endpointIndex + 1));
            firstSpawnPosition = spawnPosition;
            spawnPositionSeeded = true;
            provisionalFirstPoint = false;
            lastRecordedAge = endpointIndex;
            predictedEndpointSeeded = true;
        }

        private void recordActual(Vec3d spawnPosition, Vec3d currentPosition, int age) {
            if (lastRecordedAge != Integer.MIN_VALUE
                    && (age < lastRecordedAge
                    || (spawnPosition != null && firstSpawnPosition != null
                    && spawnPosition.squaredDistanceTo(firstSpawnPosition) > 4.0D))) {
                actualPoints.clear();
                spawnPositionSeeded = false;
                finished = false;
                finishedAtMillis = 0L;
                lastRecordedAge = Integer.MIN_VALUE;
                firstSpawnPosition = null;
                provisionalFirstPoint = false;
                predictedEndpointSeeded = false;
            }

            if (!spawnPositionSeeded && spawnPosition != null) {
                firstSpawnPosition = spawnPosition;
                if (provisionalFirstPoint && !actualPoints.isEmpty()) {
                    actualPoints.add(0, spawnPosition);
                } else {
                    actualPoints.add(spawnPosition);
                }
                spawnPositionSeeded = true;
                provisionalFirstPoint = false;
            }

            if (actualPoints.isEmpty()) {
                actualPoints.add(currentPosition);
                provisionalFirstPoint = true;
            }

            if (predictedEndpointSeeded && lastRecordedAge == age) {
                actualPoints.set(actualPoints.size() - 1, currentPosition);
                predictedEndpointSeeded = false;
            } else if (lastRecordedAge != age) {
                predictedEndpointSeeded = false;
                actualPoints.add(currentPosition);
                lastRecordedAge = age;
            } else {
                // Replace the live endpoint between client ticks without
                // changing the complete authoritative checkpoint history.
                actualPoints.set(actualPoints.size() - 1, currentPosition);
            }
        }

        private void finish(long now) {
            if (!finished) {
                finished = true;
                finishedAtMillis = now;
            }
        }

        private boolean expired(long now) {
            return finished && now - finishedAtMillis >= TRAIL_LIFETIME_MILLIS;
        }
    }
}
