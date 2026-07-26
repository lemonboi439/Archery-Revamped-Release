package me.lemonboi439.archeryRevamped.physics;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.util.math.Vec3d;

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

        Vec3d velocity = arrow.getVelocity();
        double gravity = ConfigManager.getGravity();
        double drag = ConfigManager.getDrag();
        double speedMultiplier = ConfigManager.getSpeedMultiplier();

        // PersistentProjectileEntity already applies vanilla gravity and drag in super.tick().
        // Keep the defaults vanilla-compatible; non-default values are applied as deltas.
        if (Double.compare(gravity, 0.05D) != 0) {
            velocity = velocity.add(0.0D, -(gravity - 0.05D), 0.0D);
        }
        if (Double.compare(drag, 0.99D) != 0 && drag >= 0.0D) {
            velocity = velocity.multiply(drag / 0.99D);
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

        if (!velocity.equals(arrow.getVelocity())) {
            arrow.setVelocity(velocity);
        }
    }
}
