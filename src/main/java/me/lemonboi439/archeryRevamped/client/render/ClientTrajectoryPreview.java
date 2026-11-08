package me.lemonboi439.archeryRevamped.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.enchantment.RicochetEnchantment;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import me.lemonboi439.archeryRevamped.physics.ArrowPhysicsEngine;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
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
 * a depth-tested line buffer at the end of the level render pass.</p>
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
    private static Level trackedWorld;
    private static boolean trajectoryEnabled;
    private static boolean colourVisualisationEnabled;

    private ClientTrajectoryPreview() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ClientTrajectoryPreview::onRenderLevelStage);
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

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            render(event);
        }
    }

    private static void render(RenderLevelStageEvent event) {
        Minecraft client = Minecraft.getInstance();
        float tickDelta = event.getPartialTick();
        if (client.player == null || client.level == null) {
            return;
        }
        if (trackedWorld != client.level) {
            PATHS.clear();
            trackedWorld = client.level;
        }

        long now = System.currentTimeMillis();
        Map<UUID, ArcheryArrowEntity> visibleArrows = new HashMap<>();
        for (ArcheryArrowEntity arrow : client.level.getEntitiesOfClass(
                ArcheryArrowEntity.class, client.player.getBoundingBox().inflate(512.0D), Entity::isAlive)) {
            if (!arrow.isTrajectoryPreviewEnabled()) {
                continue;
            }
            UUID id = arrow.getUUID();
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
                    arrow.getPosition(tickDelta), arrow.tickCount);
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

        Camera camera = event.getCamera();
        Vec3 cameraPosition = camera.getPosition();
        PoseStack matrices = event.getPoseStack();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        for (Map.Entry<UUID, PathHistory> entry : PATHS.entrySet()) {
            PathHistory history = entry.getValue();
            if (history.actualPoints.size() < 2) {
                continue;
            }
            List<Vec3> points = new ArrayList<>(history.actualPoints);
            ArcheryArrowEntity arrow = visibleArrows.get(entry.getKey());
            if (arrow != null && !history.finished && arrow.isAlive()) {
                List<Vec3> prediction = predict(client, arrow, arrow.getPosition(tickDelta),
                        arrow.getDeltaMovement(), arrow.getBounceCount(), arrow.getRicochetLevel());
                if (prediction.size() > 1) {
                    points.addAll(prediction.subList(1, prediction.size()));
                }
            }
            drawSmoothPath(buffer, matrices, points, cameraPosition, history.finished, now, history.finishedAtMillis);
        }
        if (trajectoryEnabled) {
            drawAimingPreview(buffer, matrices, client, cameraPosition);
        }
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }

    private static void drawAimingPreview(BufferBuilder buffer, PoseStack matrices,
                                          Minecraft client, Vec3 cameraPosition) {
        Player player = client.player;
        ItemStack weapon = player.getUseItem();
        if (!player.isUsingItem()
                || (!(weapon.getItem() instanceof BowItem) && !(weapon.getItem() instanceof CrossbowItem))) {
            return;
        }
        Vec3 direction = player.getLookAngle().normalize();
        Vec3 start = cameraPosition.add(direction.scale(0.16D));
        List<Vec3> points = predict(client, player, start, direction.scale(getAimingSpeed(player, weapon)),
                0, getRicochetLevel(client, weapon));
        drawSmoothPath(buffer, matrices, points, cameraPosition, false, System.currentTimeMillis(), 0L);
    }

    private static List<Vec3> predict(Minecraft client, Entity source, Vec3 start,
                                        Vec3 initialVelocity, int initialBounces, int ricochetLevel) {
        List<Vec3> points = new ArrayList<>();
        points.add(start);
        Vec3 position = start;
        Vec3 velocity = initialVelocity;
        int bounces = initialBounces;
        int maxTicks = Math.max(1, Math.min(MAX_PREDICTION_TICKS, ConfigManager.getMaxLifetimeTicks()));
        for (int tick = 0; tick < maxTicks && velocity.lengthSqr() >= 1.0E-8D; tick++) {
            Vec3 next = position.add(velocity);
            BlockHitResult blockHit = client.level.clip(new ClipContext(position, next,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, source));
            Vec3 entityHit = findEntityImpact(client, source, position, next);
            if (entityHit != null && (blockHit.getType() == HitResult.Type.MISS
                    || entityHit.distanceToSqr(position) < blockHit.getLocation().distanceToSqr(position))) {
                points.add(entityHit);
                break;
            }
            if (blockHit.getType() != HitResult.Type.MISS) {
                Vec3 impact = blockHit.getLocation();
                points.add(impact);
                if (bounces >= ricochetLevel) {
                    break;
                }
                Direction side = blockHit.getDirection();
                Vec3 normal = new Vec3(side.getStepX(), side.getStepY(), side.getStepZ());
                double dot = velocity.dot(normal);
                velocity = velocity.subtract(normal.scale(2.0D * dot))
                        .scale(1.0D - ConfigManager.getRicochetVelocityLossPercent() / 100.0D);
                bounces++;
                position = impact.add(normal.scale(0.01D));
                velocity = ArrowPhysicsEngine.applyPreviewPhysics(client.level, position, velocity);
                points.add(position);
                continue;
            }
            points.add(next);
            velocity = ArrowPhysicsEngine.applyPreviewPhysics(client.level, position, velocity);
            position = next;
        }
        return points;
    }

    private static Vec3 findEntityImpact(Minecraft client, Entity source, Vec3 start, Vec3 end) {
        AABB search = new AABB(start, end).inflate(1.0D);
        Entity owner = source instanceof ArcheryArrowEntity arrow ? arrow.getOwner() : source;
        Vec3 closest = null;
        double closestDistance = Double.POSITIVE_INFINITY;
        for (Entity candidate : client.level.getEntities(source, search,
                entity -> entity.isAlive() && !entity.isSpectator() && entity != owner)) {
            var hit = candidate.getBoundingBox().inflate(candidate.getPickRadius()).clip(start, end);
            if (hit.isPresent() && hit.get().distanceToSqr(start) < closestDistance) {
                closest = hit.get();
                closestDistance = closest.distanceToSqr(start);
            }
        }
        return closest;
    }

    private static double getAimingSpeed(Player player, ItemStack weapon) {
        if (weapon.getItem() instanceof BowItem) {
            double charge = Math.min(1.0D, Math.max(0.0D, player.getTicksUsingItem() / 20.0D));
            charge = (charge * charge + charge * 2.0D) / 3.0D;
            return Math.max(0.12D, charge * 3.0D);
        }
        if (CrossbowItem.isCharged(weapon)) {
            return 3.15D;
        }
        return 3.15D * Math.min(1.0D, Math.max(0.05D, player.getTicksUsingItem() / 25.0D));
    }

    private static int getRicochetLevel(Minecraft client, ItemStack weapon) {
        return ConfigManager.limitEnchantmentLevel(
                EnchantmentHelper.getItemEnchantmentLevel(RicochetEnchantment.ENCHANTMENT, weapon),
                RicochetEnchantment.MAX_LEVEL);
    }

    private static void drawSmoothPath(BufferBuilder buffer, PoseStack matrices, List<Vec3> points,
                                       Vec3 cameraPosition, boolean finished, long now, long finishedAt) {
        if (points.size() < 2) {
            return;
        }
        int renderedSegments = (points.size() - 1) * CURVE_SUBDIVISIONS;
        for (int segment = 0; segment < points.size() - 1; segment++) {
            for (int part = 0; part < CURVE_SUBDIVISIONS; part++) {
                Vec3 start = smoothPoint(points, segment, part / (double) CURVE_SUBDIVISIONS).subtract(cameraPosition);
                Vec3 end = smoothPoint(points, segment, (part + 1.0D) / CURVE_SUBDIVISIONS).subtract(cameraPosition);
                int index = segment * CURVE_SUBDIVISIONS + part;
                int[] colour = colourForSpeed(points.get(segment + 1).subtract(points.get(segment)).length());
                putVertex(buffer, matrices, start, colour, alphaFor(index, finished, now, finishedAt));
                putVertex(buffer, matrices, end, colour, alphaFor(index + 1, finished, now, finishedAt));
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
        if (incoming.lengthSqr() < 1.0E-8D || outgoing.lengthSqr() < 1.0E-8D
                || incoming.normalize().dot(outgoing.normalize()) < 0.35D) {
            return p1.lerp(p2, t);
        }
        double t2 = t * t;
        double t3 = t2 * t;
        return p1.scale(2.0D).add(p2.subtract(p0).scale(t))
                .add(p0.scale(2.0D).subtract(p1.scale(5.0D)).add(p2.scale(4.0D)).subtract(p3).scale(t2))
                .add(p3.subtract(p0).add(p1.scale(3.0D)).subtract(p2.scale(3.0D)).scale(t3)).scale(0.5D);
    }

    private static void putVertex(BufferBuilder buffer, PoseStack matrices, Vec3 point, int[] colour, int alpha) {
        buffer.vertex(matrices.last().pose(), (float) point.x, (float) point.y, (float) point.z)
                .color(colour[0], colour[1], colour[2], alpha).endVertex();
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
        private final List<Vec3> actualPoints = new ArrayList<>();
        private int lastAge = Integer.MIN_VALUE;
        private boolean spawnSeeded;
        private boolean finished;
        private long finishedAtMillis;

        void seedInitialPrediction(Minecraft client, ArcheryArrowEntity arrow) {
            Vec3 spawn = arrow.getTrajectorySpawnPosition();
            Vec3 velocity = arrow.getTrajectoryInitialVelocity();
            if (spawn == null || velocity == null || arrow.tickCount <= 0 || spawnSeeded) {
                return;
            }
            List<Vec3> simulated = predict(client, arrow, spawn, velocity, 0, arrow.getRicochetLevel());
            int endpoint = Math.min(simulated.size() - 1, arrow.tickCount);
            actualPoints.clear();
            actualPoints.addAll(simulated.subList(0, endpoint + 1));
            spawnSeeded = true;
            lastAge = endpoint;
        }

        void recordActual(Vec3 spawn, Vec3 current, int age) {
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
