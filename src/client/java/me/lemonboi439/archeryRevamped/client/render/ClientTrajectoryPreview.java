package me.lemonboi439.archeryRevamped.client.render;

import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import me.lemonboi439.archeryRevamped.physics.ArrowPhysicsEngine;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Lightweight 1.21.1 world-render implementation of the trajectory lines. */
public final class ClientTrajectoryPreview {
    private static final int MAX_TICKS = 160;
    private static final Map<UUID, List<Vec3d>> TRAILS = new HashMap<>();
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
            TRAILS.clear();
        }
    }

    public static void setTrajectoryState(boolean enabled, boolean colourVisualisation) {
        colourVisualisationEnabled = colourVisualisation;
        setTrajectoryEnabled(enabled);
    }

    private static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!trajectoryEnabled || client.player == null || client.world == null || context.consumers() == null) {
            return;
        }

        MatrixStack matrices = context.matrixStack();
        Vec3d camera = context.camera().getPos();
        VertexConsumer vertices = context.consumers().getBuffer(RenderLayer.getLines());
        Map<UUID, Boolean> active = new HashMap<>();
        for (ArcheryArrowEntity arrow : client.world.getEntitiesByClass(ArcheryArrowEntity.class,
                client.player.getBoundingBox().expand(256.0D), entity -> !entity.isRemoved())) {
            active.put(arrow.getUuid(), Boolean.TRUE);
            List<Vec3d> trail = TRAILS.computeIfAbsent(arrow.getUuid(), key -> new ArrayList<>());
            Vec3d current = arrow.getPos();
            if (trail.isEmpty() || trail.getLast().squaredDistanceTo(current) > 0.0025D) {
                trail.add(current);
                if (trail.size() > 100) trail.removeFirst();
            }
            List<Vec3d> path = new ArrayList<>(trail);
            appendPrediction(path, client.world, client.player, current, arrow.getVelocity());
            drawPath(vertices, matrices, camera, path);
        }
        TRAILS.keySet().removeIf(id -> !active.containsKey(id));

        if (isAiming(client.player)) {
            Vec3d start = camera.add(client.player.getRotationVec(1.0F).multiply(0.16D));
            double speed = client.player.getActiveItem().getItem() instanceof CrossbowItem ? 3.15D
                    : 3.0D * bowPull(client.player);
            List<Vec3d> path = new ArrayList<>();
            path.add(start);
            appendPrediction(path, client.world, client.player, start,
                    client.player.getRotationVec(1.0F).multiply(speed));
            drawPath(vertices, matrices, camera, path);
        }
    }

    private static boolean isAiming(PlayerEntity player) {
        return player.isUsingItem() && (player.getActiveItem().getItem() instanceof BowItem
                || player.getActiveItem().getItem() instanceof CrossbowItem);
    }

    private static double bowPull(PlayerEntity player) {
        return Math.min(1.0D, Math.max(0.1D, player.getItemUseTime() / 20.0D));
    }

    private static void appendPrediction(List<Vec3d> path, World world, PlayerEntity player,
                                         Vec3d position, Vec3d velocity) {
        for (int tick = 0; tick < MAX_TICKS; tick++) {
            Vec3d next = position.add(velocity);
            BlockHitResult hit = world.raycast(new RaycastContext(position, next,
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
            if (hit.getType() != HitResult.Type.MISS) {
                path.add(hit.getPos());
                return;
            }
            path.add(next);
            position = next;
            velocity = ArrowPhysicsEngine.applyPreviewPhysics(world, position, velocity);
            if (velocity.lengthSquared() < 1.0E-7D) return;
        }
    }

    private static void drawPath(VertexConsumer vertices, MatrixStack matrices, Vec3d camera, List<Vec3d> path) {
        if (path.size() < 2) return;
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        for (int i = 1; i < path.size(); i++) {
            float progress = i / (float) (path.size() - 1);
            int alpha = (int) (180.0F * (1.0F - progress * 0.55F));
            int red = colourVisualisationEnabled ? (int) (50 + progress * 205) : 55;
            int blue = colourVisualisationEnabled ? (int) (255 - progress * 205) : 225;
            Vec3d a = path.get(i - 1);
            Vec3d b = path.get(i);
            vertices.vertex(matrix, (float) a.x, (float) a.y, (float) a.z).color(red, 160, blue, alpha).normal(0.0F, 1.0F, 0.0F);
            vertices.vertex(matrix, (float) b.x, (float) b.y, (float) b.z).color(red, 160, blue, alpha).normal(0.0F, 1.0F, 0.0F);
        }
        matrices.pop();
    }
}
