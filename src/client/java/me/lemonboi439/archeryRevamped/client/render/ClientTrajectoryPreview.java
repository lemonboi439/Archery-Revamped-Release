package me.lemonboi439.archeryRevamped.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.enchantment.RicochetEnchantment;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import me.lemonboi439.archeryRevamped.physics.ArrowPhysicsEngine;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
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
 * Client-only trajectory renderer for the 1.20.1 port.
 *
 * <p>It follows the 1.21.11 implementation's simulation order exactly: move,
 * raycast, reflect when Ricochet can bounce, then apply the shared physics
 * preview. The old Fabric renderer has no render-pipeline API, so this writes
 * a depth-tested line buffer directly during {@link WorldRenderEvents#LAST}.</p>
 */
public final class ClientTrajectoryPreview {
    private static final int TRAIL_LIFETIME_MILLIS = 10_000;
    private static final int MAX_PREDICTION_TICKS = 1200;
    private static final int CURVE_SUBDIVISIONS = 3;
    private static final int OLDEST_FADE_POINT_COUNT = 24;
    private static final int LINE_ALPHA = 165;
    private static final double SLOWEST_COLOUR_SPEED = 0.05D;
    private static final double FASTEST_COLOUR_SPEED = 3.15D;
    private static final int[][] SPEED_COLOURS = {
            {45, 90, 255}, {40, 210, 230}, {60, 210, 100},
            {245, 220, 55}, {255, 140, 35}, {235, 45, 45}
    };

    private static final Map<UUID, PathHistory> PATHS = new HashMap<>();
    private static World trackedWorld;
    private static boolean trajectoryEnabled;
    private static boolean colourVisualisationEnabled;

    private ClientTrajectoryPreview() {
    }

    public static void register() {
        WorldRenderEvents.LAST.register(ClientTrajectoryPreview::render);
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

    public static boolean isTrajectoryEnabled() {
        return trajectoryEnabled;
    }

    public static boolean isColourVisualisationEnabled() {
        return colourVisualisationEnabled;
    }

    private static void render(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || context.matrixStack() == null) {
            return;
        }
        if (trackedWorld != client.world) {
            PATHS.clear();
            trackedWorld = client.world;
        }

        long now = System.currentTimeMillis();
        Map<UUID, ArcheryArrowEntity> visibleArrows = new HashMap<>();
        for (ArcheryArrowEntity arrow : client.world.getEntitiesByClass(
                ArcheryArrowEntity.class, client.player.getBoundingBox().expand(512.0D), Entity::isAlive)) {
            if (!arrow.isTrajectoryPreviewEnabled()) {
                continue;
            }
            UUID id = arrow.getUuid();
            visibleArrows.put(id, arrow);
            PathHistory history = PATHS.get(id);
            if (history == null) {
                if (arrow.isTrajectoryFinished() || arrow.isArrowInGround()) {
                    continue;
                }
                history = new PathHistory();
                PATHS.put(id, history);
            }
            history.seedInitialPrediction(client, arrow);
            history.recordActual(arrow.getTrajectorySpawnPosition(),
                    arrow.getLerpedPos(context.tickDelta()), arrow.age);
            if (arrow.isTrajectoryFinished() || arrow.isArrowInGround()) {
                history.finish(now);
            }
        }

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

        if (PATHS.isEmpty() && !trajectoryEnabled) {
            return;
        }

        Camera camera = context.camera();
        Vec3d cameraPosition = camera.getPos();
        MatrixStack matrices = context.matrixStack();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        for (Map.Entry<UUID, PathHistory> entry : PATHS.entrySet()) {
            PathHistory history = entry.getValue();
            if (history.actualPoints.size() < 2) {
                continue;
            }
            List<Vec3d> points = new ArrayList<>(history.actualPoints);
            ArcheryArrowEntity arrow = visibleArrows.get(entry.getKey());
            if (arrow != null && !history.finished && arrow.isAlive()) {
                List<Vec3d> prediction = predict(client, arrow, arrow.getLerpedPos(context.tickDelta()),
                        arrow.getVelocity(), arrow.getBounceCount(), arrow.getRicochetLevel());
                if (prediction.size() > 1) {
                    points.addAll(prediction.subList(1, prediction.size()));
                }
            }
            drawSmoothPath(buffer, matrices, points, cameraPosition, history.finished, now, history.finishedAtMillis);
        }
        if (trajectoryEnabled) {
            drawAimingPreview(buffer, matrices, client, cameraPosition);
        }
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.disableBlend();
    }

    private static void drawAimingPreview(BufferBuilder buffer, MatrixStack matrices,
                                          MinecraftClient client, Vec3d cameraPosition) {
        PlayerEntity player = client.player;
        ItemStack weapon = player.getActiveItem();
        if (!player.isUsingItem()
                || (!(weapon.getItem() instanceof BowItem) && !(weapon.getItem() instanceof CrossbowItem))) {
            return;
        }
        Vec3d direction = player.getRotationVector().normalize();
        Vec3d start = cameraPosition.add(direction.multiply(0.16D));
        List<Vec3d> points = predict(client, player, start, direction.multiply(getAimingSpeed(player, weapon)),
                0, getRicochetLevel(client, weapon));
        drawSmoothPath(buffer, matrices, points, cameraPosition, false, System.currentTimeMillis(), 0L);
    }

    private static List<Vec3d> predict(MinecraftClient client, Entity source, Vec3d start,
                                        Vec3d initialVelocity, int initialBounces, int ricochetLevel) {
        List<Vec3d> points = new ArrayList<>();
        points.add(start);
        Vec3d position = start;
        Vec3d velocity = initialVelocity;
        int bounces = initialBounces;
        int maxTicks = Math.max(1, Math.min(MAX_PREDICTION_TICKS, ConfigManager.getMaxLifetimeTicks()));
        for (int tick = 0; tick < maxTicks && velocity.lengthSquared() >= 1.0E-8D; tick++) {
            Vec3d next = position.add(velocity);
            BlockHitResult blockHit = client.world.raycast(new RaycastContext(position, next,
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, source));
            Vec3d entityHit = findEntityImpact(client, source, position, next);
            if (entityHit != null && (blockHit.getType() == HitResult.Type.MISS
                    || entityHit.squaredDistanceTo(position) < blockHit.getPos().squaredDistanceTo(position))) {
                points.add(entityHit);
                break;
            }
            if (blockHit.getType() != HitResult.Type.MISS) {
                Vec3d impact = blockHit.getPos();
                points.add(impact);
                if (bounces >= ricochetLevel) {
                    break;
                }
                Direction side = blockHit.getSide();
                Vec3d normal = new Vec3d(side.getOffsetX(), side.getOffsetY(), side.getOffsetZ());
                double dot = velocity.dotProduct(normal);
                velocity = velocity.subtract(normal.multiply(2.0D * dot))
                        .multiply(1.0D - ConfigManager.getRicochetVelocityLossPercent() / 100.0D);
                bounces++;
                position = impact.add(normal.multiply(0.01D));
                velocity = ArrowPhysicsEngine.applyPreviewPhysics(client.world, position, velocity);
                points.add(position);
                continue;
            }
            points.add(next);
            velocity = ArrowPhysicsEngine.applyPreviewPhysics(client.world, position, velocity);
            position = next;
        }
        return points;
    }

    private static Vec3d findEntityImpact(MinecraftClient client, Entity source, Vec3d start, Vec3d end) {
        Box search = new Box(start, end).expand(1.0D);
        Entity owner = source instanceof ArcheryArrowEntity arrow ? arrow.getOwner() : source;
        Vec3d closest = null;
        double closestDistance = Double.POSITIVE_INFINITY;
        for (Entity candidate : client.world.getOtherEntities(source, search,
                entity -> entity.isAlive() && !entity.isSpectator() && entity != owner)) {
            var hit = candidate.getBoundingBox().expand(candidate.getTargetingMargin()).raycast(start, end);
            if (hit.isPresent() && hit.get().squaredDistanceTo(start) < closestDistance) {
                closest = hit.get();
                closestDistance = closest.squaredDistanceTo(start);
            }
        }
        return closest;
    }

    private static double getAimingSpeed(PlayerEntity player, ItemStack weapon) {
        if (weapon.getItem() instanceof BowItem) {
            double charge = Math.min(1.0D, Math.max(0.0D, player.getItemUseTime() / 20.0D));
            charge = (charge * charge + charge * 2.0D) / 3.0D;
            return Math.max(0.12D, charge * 3.0D);
        }
        if (CrossbowItem.isCharged(weapon)) {
            return 3.15D;
        }
        return 3.15D * Math.min(1.0D, Math.max(0.05D, player.getItemUseTime() / 25.0D));
    }

    private static int getRicochetLevel(MinecraftClient client, ItemStack weapon) {
        return ConfigManager.limitEnchantmentLevel(
                EnchantmentHelper.getLevel(RicochetEnchantment.ENCHANTMENT, weapon),
                RicochetEnchantment.MAX_LEVEL);
    }

    private static void drawSmoothPath(BufferBuilder buffer, MatrixStack matrices, List<Vec3d> points,
                                       Vec3d cameraPosition, boolean finished, long now, long finishedAt) {
        if (points.size() < 2) {
            return;
        }
        int renderedSegments = (points.size() - 1) * CURVE_SUBDIVISIONS;
        for (int segment = 0; segment < points.size() - 1; segment++) {
            for (int part = 0; part < CURVE_SUBDIVISIONS; part++) {
                Vec3d start = smoothPoint(points, segment, part / (double) CURVE_SUBDIVISIONS).subtract(cameraPosition);
                Vec3d end = smoothPoint(points, segment, (part + 1.0D) / CURVE_SUBDIVISIONS).subtract(cameraPosition);
                int index = segment * CURVE_SUBDIVISIONS + part;
                int[] colour = colourForSpeed(points.get(segment + 1).subtract(points.get(segment)).length());
                putVertex(buffer, matrices, start, colour, alphaFor(index, finished, now, finishedAt));
                putVertex(buffer, matrices, end, colour, alphaFor(index + 1, finished, now, finishedAt));
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
        if (incoming.lengthSquared() < 1.0E-8D || outgoing.lengthSquared() < 1.0E-8D
                || incoming.normalize().dotProduct(outgoing.normalize()) < 0.35D) {
            return p1.lerp(p2, t);
        }
        double t2 = t * t;
        double t3 = t2 * t;
        return p1.multiply(2.0D).add(p2.subtract(p0).multiply(t))
                .add(p0.multiply(2.0D).subtract(p1.multiply(5.0D)).add(p2.multiply(4.0D)).subtract(p3).multiply(t2))
                .add(p3.subtract(p0).add(p1.multiply(3.0D)).subtract(p2.multiply(3.0D)).multiply(t3)).multiply(0.5D);
    }

    private static void putVertex(BufferBuilder buffer, MatrixStack matrices, Vec3d point, int[] colour, int alpha) {
        buffer.vertex(matrices.peek().getPositionMatrix(), (float) point.x, (float) point.y, (float) point.z)
                .color(colour[0], colour[1], colour[2], alpha).next();
    }

    private static int alphaFor(int index, boolean finished, long now, long finishedAt) {
        double progress = Math.min(1.0D, index / (double) OLDEST_FADE_POINT_COUNT);
        int alpha = (int) Math.round(LINE_ALPHA * Math.pow(progress, 1.75D));
        if (finished) {
            alpha = (int) Math.round(alpha * Math.max(0.0D, 1.0D - (now - finishedAt) / (double) TRAIL_LIFETIME_MILLIS));
        }
        return alpha;
    }

    private static int[] colourForSpeed(double speed) {
        if (!colourVisualisationEnabled) {
            return new int[]{70, 210, 255};
        }
        double progress = Math.max(0.0D, Math.min(1.0D, (speed - SLOWEST_COLOUR_SPEED)
                / (FASTEST_COLOUR_SPEED - SLOWEST_COLOUR_SPEED)));
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
        private int lastAge = Integer.MIN_VALUE;
        private boolean spawnSeeded;
        private boolean finished;
        private long finishedAtMillis;

        void seedInitialPrediction(MinecraftClient client, ArcheryArrowEntity arrow) {
            Vec3d spawn = arrow.getTrajectorySpawnPosition();
            Vec3d velocity = arrow.getTrajectoryInitialVelocity();
            if (spawn == null || velocity == null || arrow.age <= 0 || spawnSeeded) {
                return;
            }
            List<Vec3d> simulated = predict(client, arrow, spawn, velocity, 0, arrow.getRicochetLevel());
            int endpoint = Math.min(simulated.size() - 1, arrow.age);
            actualPoints.clear();
            actualPoints.addAll(simulated.subList(0, endpoint + 1));
            spawnSeeded = true;
            lastAge = endpoint;
        }

        void recordActual(Vec3d spawn, Vec3d current, int age) {
            if (!spawnSeeded && spawn != null) {
                actualPoints.add(spawn);
                spawnSeeded = true;
            }
            if (actualPoints.isEmpty()) {
                actualPoints.add(current);
            } else if (lastAge != age) {
                actualPoints.add(current);
                lastAge = age;
            } else {
                actualPoints.set(actualPoints.size() - 1, current);
            }
        }

        void finish(long now) {
            if (!finished) {
                finished = true;
                finishedAtMillis = now;
            }
        }

        boolean expired(long now) {
            return finished && now - finishedAtMillis >= TRAIL_LIFETIME_MILLIS;
        }
    }
}
