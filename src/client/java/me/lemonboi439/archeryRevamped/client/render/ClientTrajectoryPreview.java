package me.lemonboi439.archeryRevamped.client.render;

import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.physics.ArrowPhysicsEngine;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import me.lemonboi439.archeryRevamped.enchantment.RicochetEnchantment;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

    private static final RenderType TRAJECTORY_LAYER = RenderType.create(
            "archery_revamped_trajectory",
            net.minecraft.client.renderer.rendertype.RenderSetup.builder(RenderPipelines.DEBUG_QUADS)
                    .sortOnUpload()
                    .bufferSize(32768)
                    .createRenderSetup()
    );

    // Entity ids are reused by Minecraft. UUIDs keep a new arrow from
    // inheriting the trail history of an older arrow with the same id.
    private static final Map<UUID, PathHistory> PATHS = new HashMap<>();
    private static Level trackedWorld;
    private static boolean trajectoryEnabled;
    private static boolean colourVisualisationEnabled;

    private ClientTrajectoryPreview() {
    }

    public static void register() {
        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register(ClientTrajectoryPreview::render);
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

    private static void render(LevelRenderContext context) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null
                || context.poseStack() == null || context.bufferSource() == null) {
            return;
        }

        if (trackedWorld != client.level) {
            PATHS.clear();
            trackedWorld = client.level;
        }

        long now = System.currentTimeMillis();
        Map<UUID, ArcheryArrowEntity> visibleArrows = new HashMap<>();

        // The server's entity tracking range is still the hard limit, but a
        // large query keeps long flights from being clipped by this renderer.
        for (ArcheryArrowEntity arrow : client.level.getEntitiesOfClass(
                ArcheryArrowEntity.class,
                client.player.getBoundingBox().inflate(512.0D),
                ArcheryArrowEntity::isAlive)) {
            if (!arrow.isTrajectoryPreviewEnabled()) {
                continue;
            }

            UUID arrowId = arrow.getUUID();
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
                    arrow.getPosition(client.getDeltaTracker().getGameTimeDeltaPartialTick(false)),
                    arrow.tickCount
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

    private static void renderPaths(LevelRenderContext context, Minecraft client,
                                    Map<UUID, ArcheryArrowEntity> visibleArrows, long now) {
        Camera camera = client.gameRenderer.getMainCamera();
        Vec3 cameraPosition = camera.position();
        PoseStack matrices = context.poseStack();
        MultiBufferSource consumers = context.bufferSource();
        VertexConsumer vertices = consumers.getBuffer(TRAJECTORY_LAYER);

        matrices.pushPose();
        PoseStack.Pose matrix = matrices.last();
        for (Map.Entry<UUID, PathHistory> entry : PATHS.entrySet()) {
            PathHistory history = entry.getValue();
            if (history.actualPoints.size() < 2) {
                continue;
            }

            List<Vec3> points = new ArrayList<>(history.actualPoints);
            ArcheryArrowEntity arrow = visibleArrows.get(entry.getKey());
            if (arrow != null && !history.finished && arrow.isAlive()) {
                Vec3 currentPosition = arrow.getPosition(client.getDeltaTracker().getGameTimeDeltaPartialTick(false));
                List<Vec3> prediction = predict(client, arrow, currentPosition, arrow.getDeltaMovement(),
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
        matrices.popPose();
    }

    /**
     * Predicts until the first entity/block impact or the configured arrow
     * lifetime. Each block segment is raycast before it is accepted. A
     * ricochet reflects the simulated velocity and carries on, so the
     * predicted path follows the same bounce budget as the arrow.
     */
    private static List<Vec3> predict(Minecraft client, Entity source,
                                       Vec3 startPosition, Vec3 startVelocity,
                                       int initialBounceCount, int ricochetLevel) {
        List<Vec3> points = new ArrayList<>();
        points.add(startPosition);

        Vec3 position = startPosition;
        Vec3 velocity = startVelocity;
        int bounceCount = initialBounceCount;

        int predictionTicks = Math.max(1, Math.min(
                MAX_PREDICTION_TICKS, ConfigManager.getMaxLifetimeTicks()));
        for (int tick = 0; tick < predictionTicks; tick++) {
            if (velocity.lengthSqr() < 1.0E-8D) {
                break;
            }

            Vec3 nextPosition = position.add(velocity);
            BlockHitResult blockHit = raycastBlock(client, source, position, nextPosition);
            Vec3 entityHit = findEntityImpact(client, source, position, nextPosition);
            if (entityHit != null && (blockHit.getType() == HitResult.Type.MISS
                    || entityHit.subtract(position).lengthSqr()
                    < blockHit.getLocation().subtract(position).lengthSqr())) {
                points.add(entityHit);
                break;
            }

            if (blockHit.getType() != HitResult.Type.MISS) {
                Vec3 impactPosition = blockHit.getLocation();
                points.add(impactPosition);

                if (bounceCount < ricochetLevel) {
                    Direction side = blockHit.getDirection();
                    Vec3 normal = side.getUnitVec3();
                    double dot = velocity.dot(normal);
                    Vec3 reflected = velocity
                            .subtract(normal.scale(2.0D * dot))
                            .scale(1.0D - ConfigManager.getRicochetVelocityLossPercent() / 100.0D);

                    bounceCount++;
                    position = impactPosition.add(normal.scale(0.01D));
                    velocity = ArrowPhysicsEngine.applyPreviewPhysics(client.level, position, reflected);
                    points.add(position);
                    continue;
                }
                break;
            }

            points.add(nextPosition);
            velocity = ArrowPhysicsEngine.applyPreviewPhysics(client.level, position, velocity);
            position = nextPosition;
        }
        return points;
    }

    private static Vec3 findEntityImpact(Minecraft client, Entity source,
                                          Vec3 start, Vec3 end) {
        AABB searchBox = new AABB(start, end).inflate(1.0D);
        Entity owner = source instanceof ArcheryArrowEntity arrow ? arrow.getOwner() : source;
        Vec3 closest = null;
        double closestDistance = Double.POSITIVE_INFINITY;

        for (Entity candidate : client.level.getEntities(source, searchBox,
                entity -> entity.isAlive() && !entity.isSpectator() && entity != owner)) {
            var hit = candidate.getBoundingBox()
                    .inflate(candidate.getPickRadius())
                    .clip(start, end);
            if (hit.isEmpty()) {
                continue;
            }

            Vec3 hitPosition = hit.get();
            double distance = hitPosition.subtract(start).lengthSqr();
            if (distance < closestDistance) {
                closest = hitPosition;
                closestDistance = distance;
            }
        }
        return closest;
    }

    private static BlockHitResult raycastBlock(Minecraft client, Entity arrow,
                                               Vec3 start, Vec3 end) {
        return client.level.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                arrow
        ));
    }

    private static void renderAimingPreview(LevelRenderContext context, Minecraft client) {
        Player player = client.player;
        ItemStack weapon = player.getUseItem();
        if (!player.isUsingItem()
                || (!(weapon.getItem() instanceof BowItem) && !(weapon.getItem() instanceof CrossbowItem))) {
            return;
        }

        float tickProgress = client.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Vec3 direction = player.getLookAngle().normalize();
        // Start at the rendered camera/aim origin. Using the player's feet
        // plus standingEyeHeight puts the preview below the crosshair when
        // camera interpolation or first-person bobbing is active.
        Camera camera = client.gameRenderer.getMainCamera();
        Vec3 start = camera.position().add(direction.scale(0.16D));
        double speed = getAimingSpeed(player, weapon);
        int ricochetLevel = getRicochetLevel(client, weapon);
        List<Vec3> points = predict(client, player, start, direction.scale(speed), 0, ricochetLevel);
        if (points.size() < 2) {
            return;
        }

        PoseStack matrices = context.poseStack();
        VertexConsumer vertices = context.bufferSource().getBuffer(TRAJECTORY_LAYER);
        matrices.pushPose();
        renderSmoothPath(vertices, matrices.last(), points, camera.position(), false,
                System.currentTimeMillis(), 0L);
        matrices.popPose();
    }

    private static double getAimingSpeed(Player player, ItemStack weapon) {
        if (weapon.getItem() instanceof BowItem) {
            double useProgress = Math.min(1.0D, Math.max(0.0D, player.getTicksUsingItem() / 20.0D));
            useProgress = (useProgress * useProgress + useProgress * 2.0D) / 3.0D;
            // Keep the preview visible during the first frames of a draw.
            return Math.max(0.12D, useProgress * 3.0D);
        }

        if (CrossbowItem.isCharged(weapon)) {
            return 3.15D;
        }

        double charge = Math.min(1.0D, Math.max(0.05D, player.getTicksUsingItem() / 25.0D));
        return 3.15D * charge;
    }

    private static int getRicochetLevel(Minecraft client, ItemStack weapon) {
        return client.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(RicochetEnchantment.KEY)
                .map(entry -> EnchantmentHelper.getItemEnchantmentLevel(entry, weapon))
                .map(level -> ConfigManager.limitEnchantmentLevel(level, RicochetEnchantment.MAX_LEVEL))
                .orElse(0);
    }

    private static void renderSmoothPath(VertexConsumer vertices, PoseStack.Pose matrix,
                                         List<Vec3> points, Vec3 cameraPosition,
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
                Vec3 start = smoothPoint(points, segment, t0).subtract(cameraPosition);
                Vec3 end = smoothPoint(points, segment, t1).subtract(cameraPosition);

                int startIndex = segment * CURVE_SUBDIVISIONS + subdivision;
                int endIndex = startIndex + 1;
                int startAlpha = alphaFor(startIndex, renderedSegmentCount, finished, now, finishedAtMillis);
                int endAlpha = alphaFor(endIndex, renderedSegmentCount, finished, now, finishedAtMillis);
                // Colour the two ends independently. The previous renderer
                // selected one colour per physics segment, making the speed
                // visualisation visibly step between checkpoints.
                int[] startColour = colourForSpeed(interpolatedSegmentSpeed(points, segment, t0));
                int[] endColour = colourForSpeed(interpolatedSegmentSpeed(points, segment, t1));
                putTubeSegment(vertices, matrix, start, end, startAlpha, endAlpha, startColour, endColour);
            }
        }
    }

    private static Vec3 smoothPoint(List<Vec3> points, int segment, double t) {
        Vec3 p0 = points.get(Math.max(0, segment - 1));
        Vec3 p1 = points.get(segment);
        Vec3 p2 = points.get(segment + 1);
        Vec3 p3 = points.get(Math.min(points.size() - 1, segment + 2));
        Vec3 incoming = p1.subtract(p0);
        Vec3 outgoing = p2.subtract(p1);

        // Never round a sharp collision corner. A Catmull-Rom curve can
        // overshoot through the wall at a ricochet and create the old spikes.
        if (incoming.lengthSqr() < 1.0E-8D || outgoing.lengthSqr() < 1.0E-8D
                || incoming.normalize().dot(outgoing.normalize()) < 0.35D) {
            return p1.lerp(p2, t);
        }
        return catmullRom(points, segment, t);
    }

    private static Vec3 catmullRom(List<Vec3> points, int segment, double t) {
        Vec3 p0 = points.get(Math.max(0, segment - 1));
        Vec3 p1 = points.get(segment);
        Vec3 p2 = points.get(segment + 1);
        Vec3 p3 = points.get(Math.min(points.size() - 1, segment + 2));

        double t2 = t * t;
        double t3 = t2 * t;
        return p1.scale(2.0D)
                .add(p2.subtract(p0).scale(t))
                .add(p0.scale(2.0D).subtract(p1.scale(5.0D))
                        .add(p2.scale(4.0D)).subtract(p3).scale(t2))
                .add(p3.subtract(p0).add(p1.scale(3.0D)).subtract(p2.scale(3.0D))
                        .scale(t3))
                .scale(0.5D);
    }

    /**
     * Interpolates the speed at each end of the curve segment. This keeps the
     * red-to-blue visualisation continuous even where a path is subdivided.
     */
    private static double interpolatedSegmentSpeed(List<Vec3> points, int segment, double t) {
        double current = points.get(segment + 1).subtract(points.get(segment)).length();
        double previous = segment > 0
                ? points.get(segment).subtract(points.get(segment - 1)).length()
                : current;
        double next = segment + 2 < points.size()
                ? points.get(segment + 2).subtract(points.get(segment + 1)).length()
                : current;
        double startSpeed = (previous + current) * 0.5D;
        double endSpeed = (current + next) * 0.5D;
        return startSpeed + (endSpeed - startSpeed) * t;
    }

    /**
     * Emits a small six-sided tube for one path segment. Minecraft's line
     * primitive is a screen-facing flat strip, which becomes visibly chunky
     * and broken at distance. Quads give the trail a stable, thin volume.
     */
    private static void putTubeSegment(VertexConsumer vertices, PoseStack.Pose matrix,
                                       Vec3 start, Vec3 end,
                                       int startAlpha, int endAlpha,
                                       int[] startColour, int[] endColour) {
        Vec3 axis = end.subtract(start);
        double lengthSquared = axis.lengthSqr();
        if (lengthSquared < 1.0E-10D) {
            return;
        }
        axis = axis.normalize();

        Vec3 midpoint = start.add(end).scale(0.5D);
        Vec3 toCamera = midpoint.scale(-1.0D);
        if (toCamera.lengthSqr() < 1.0E-10D) {
            toCamera = new Vec3(0.0D, 0.0D, 1.0D);
        } else {
            toCamera = toCamera.normalize();
        }

        Vec3 side = axis.cross(toCamera);
        if (side.lengthSqr() < 1.0E-10D) {
            side = axis.cross(new Vec3(0.0D, 1.0D, 0.0D));
            if (side.lengthSqr() < 1.0E-10D) {
                side = axis.cross(new Vec3(1.0D, 0.0D, 0.0D));
            }
        }
        side = side.normalize();
        Vec3 up = axis.cross(side).normalize();

        for (int sideIndex = 0; sideIndex < TUBE_SIDES; sideIndex++) {
            double angle0 = sideIndex * Math.PI * 2.0D / TUBE_SIDES;
            double angle1 = (sideIndex + 1) * Math.PI * 2.0D / TUBE_SIDES;
            Vec3 offset0 = side.scale(Math.cos(angle0) * TUBE_RADIUS)
                    .add(up.scale(Math.sin(angle0) * TUBE_RADIUS));
            Vec3 offset1 = side.scale(Math.cos(angle1) * TUBE_RADIUS)
                    .add(up.scale(Math.sin(angle1) * TUBE_RADIUS));

            putTubeVertex(vertices, matrix, start.add(offset0), startAlpha, startColour);
            putTubeVertex(vertices, matrix, end.add(offset0), endAlpha, endColour);
            putTubeVertex(vertices, matrix, end.add(offset1), endAlpha, endColour);
            putTubeVertex(vertices, matrix, start.add(offset1), startAlpha, startColour);
        }
    }

    private static void putTubeVertex(VertexConsumer vertices, PoseStack.Pose matrix,
                                      Vec3 position, int alpha, int[] colour) {
        vertices.addVertex(matrix, (float) position.x, (float) position.y, (float) position.z)
                .setColor(colour[0], colour[1], colour[2], alpha);
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
        private final List<Vec3> actualPoints = new ArrayList<>();
        private int lastRecordedAge = Integer.MIN_VALUE;
        private boolean spawnPositionSeeded;
        private boolean provisionalFirstPoint;
        private boolean predictedEndpointSeeded;
        private boolean finished;
        private long finishedAtMillis;

        private Vec3 firstSpawnPosition;

        private void seedInitialPrediction(Minecraft client, ArcheryArrowEntity arrow) {
            Vec3 spawnPosition = arrow.getTrajectorySpawnPosition();
            Vec3 initialVelocity = arrow.getTrajectoryInitialVelocity();
            if (spawnPosition == null || initialVelocity == null || arrow.tickCount <= 0
                    || spawnPositionSeeded) {
                return;
            }

            List<Vec3> simulated = predict(client, arrow, spawnPosition, initialVelocity,
                    0, arrow.getRicochetLevel());
            if (simulated.isEmpty()) {
                return;
            }

            int endpointIndex = Math.min(simulated.size() - 1, arrow.tickCount);
            actualPoints.clear();
            actualPoints.addAll(simulated.subList(0, endpointIndex + 1));
            firstSpawnPosition = spawnPosition;
            spawnPositionSeeded = true;
            provisionalFirstPoint = false;
            lastRecordedAge = endpointIndex;
            predictedEndpointSeeded = true;
        }

        private void recordActual(Vec3 spawnPosition, Vec3 currentPosition, int age) {
            if (lastRecordedAge != Integer.MIN_VALUE
                    && (age < lastRecordedAge
                    || (spawnPosition != null && firstSpawnPosition != null
                    && spawnPosition.distanceToSqr(firstSpawnPosition) > 4.0D))) {
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
