package me.lemonboi439.archeryRevamped.physics;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

public final class ArrowPhysicsEngine {
    private ArrowPhysicsEngine() {
    }

    public static void applyPhysics(ArcheryArrowEntity arrow) {
        arrow.updateDistanceTravelled();
        int age = arrow.advancePhysicsAge();
        if (age > ConfigManager.getMaxLifetimeTicks()) {
            arrow.discard();
            return;
        }

        arrow.setVelocity(applyConfiguredPhysics(arrow.getVelocity()));
    }

    /**
     * Simulates the same velocity update that follows a projectile's vanilla
     * movement step. The client trajectory renderer uses this instead of
     * duplicating the configurable physics formula.
     */
    public static Vec3d applyPreviewPhysics(World world, Vec3d position, Vec3d velocity) {
        FluidState fluidState = world.getFluidState(BlockPos.ofFloored(position));
        double vanillaDrag = fluidState.isIn(FluidTags.WATER) ? 0.6D : 0.99D;
        Vec3d afterVanillaPhysics = velocity.multiply(vanillaDrag)
                .add(0.0D, -0.05D, 0.0D);
        return applyConfiguredPhysics(afterVanillaPhysics);
    }

    private static Vec3d applyConfiguredPhysics(Vec3d velocity) {
        double gravity = ConfigManager.getGravity();
        double drag = ConfigManager.getDrag();
        double speedMultiplier = ConfigManager.getSpeedMultiplier();

        // PersistentProjectileEntity already applies vanilla gravity and drag in super.tick().
        // Keep the defaults vanilla-compatible; non-default values are applied as deltas.
        if (Double.compare(gravity, 0.05D) != 0) {
            velocity = velocity.add(0.0D, -(gravity - 0.05D), 0.0D);
        }
        if (Double.compare(drag, 0.99D) != 0 && drag >= 0.0D) {
            // Drag is exposed as air resistance: increasing the value must
            // reduce retained velocity.  0.99 is the neutral vanilla value.
            // Values below the vanilla point allow less resistance, capped to
            // keep the setting useful without runaway acceleration.
            double dragMultiplier = drag <= 0.0D
                    ? 1.25D
                    : Math.min(1.25D, 0.99D / drag);
            velocity = velocity.multiply(dragMultiplier);
        }
        if (Double.compare(speedMultiplier, 1.0D) != 0) {
            velocity = velocity.multiply(speedMultiplier);
        }

        double randomness = ConfigManager.getRandomness();
        if (randomness != 0.0D) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            velocity = velocity.add(
                    random.nextDouble(-randomness, randomness),
                    random.nextDouble(-randomness, randomness),
                    random.nextDouble(-randomness, randomness)
            );
        }

        double terminalVelocity = ConfigManager.getTerminalVelocity();
        double speedSquared = velocity.lengthSquared();
        if (terminalVelocity >= 0.0D && speedSquared > terminalVelocity * terminalVelocity) {
            velocity = velocity.normalize().multiply(terminalVelocity);
        }

        return velocity;
    }
}
