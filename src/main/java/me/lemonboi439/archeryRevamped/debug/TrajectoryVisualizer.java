package me.lemonboi439.archeryRevamped.debug;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import me.lemonboi439.archeryRevamped.entity.ModEntities;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

/** Renders a short, non-invasive server-side prediction using temporary particles. */
public final class TrajectoryVisualizer {
    private static final int PREDICTION_TICKS = 24;
    private static final int LINE_PARTICLES_PER_TICK = 4;
    private static boolean enabled;

    private TrajectoryVisualizer() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(TrajectoryVisualizer::tick);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        TrajectoryVisualizer.enabled = enabled;
    }

    private static void tick(MinecraftServer server) {
        if (!enabled) {
            return;
        }

        for (ServerWorld world : server.getWorlds()) {
            for (ArcheryArrowEntity arrow : world.getEntitiesByType(
                    ModEntities.ARCHERY_ARROW, ArcheryArrowEntity::isAlive)) {
                if (arrow.isArrowInGround() || arrow.isRemoved()) {
                    continue;
                }
                renderPrediction(world, arrow);
            }
        }
    }

    private static void renderPrediction(ServerWorld world, ArcheryArrowEntity arrow) {
        Vec3d position = new Vec3d(arrow.getX(), arrow.getY(), arrow.getZ());
        Vec3d velocity = arrow.getVelocity();
        double gravity = ConfigManager.getGravity();
        double drag = ConfigManager.getDrag();
        double speedMultiplier = ConfigManager.getSpeedMultiplier();
        double terminalVelocity = ConfigManager.getTerminalVelocity();

        for (int tick = 0; tick < PREDICTION_TICKS; tick++) {
            Vec3d start = position;
            Vec3d end = position.add(velocity);
            for (int particle = 1; particle <= LINE_PARTICLES_PER_TICK; particle++) {
                double fraction = particle / (double) LINE_PARTICLES_PER_TICK;
                Vec3d point = start.add(velocity.multiply(fraction));
                world.spawnParticles(ParticleTypes.END_ROD,
                        point.x, point.y, point.z, 1,
                        0.0D, 0.0D, 0.0D, 0.0D);
            }
            position = end;

            velocity = velocity.add(0.0D, -gravity, 0.0D)
                    .multiply(Math.max(0.0D, drag) * speedMultiplier);
            double speedSquared = velocity.lengthSquared();
            if (terminalVelocity >= 0.0D && speedSquared > terminalVelocity * terminalVelocity) {
                velocity = velocity.normalize().multiply(terminalVelocity);
            }
        }
    }
}
