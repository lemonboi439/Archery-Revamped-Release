package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

/**
 * Shared landing-point force logic for Shockwave and Impulse arrows.
 *
 * <p>This mirrors vanilla wind-charge launch maths: a 1.2 power blast reaches
 * entities up to twice that radius, honours obstruction exposure and explosion
 * knockback resistance, and uses the wind charge's 1.22 knockback factor.
 * Impulse uses that same vector in reverse.</p>
 */
public final class AreaForceArrowBehavior {
    private static final double WIND_CHARGE_EXPLOSION_RADIUS = 1.2D;
    private static final double WIND_CHARGE_KNOCKBACK_MULTIPLIER = 1.22D;

    private AreaForceArrowBehavior() {
    }

    public static void apply(ArcheryArrowEntity arrow, Vec3d impact, double radius,
                             double strength, boolean pullInward) {
        applyForce(arrow.getEntityWorld(), arrow, impact, radius, strength, pullInward);
        arrow.discard();
    }

    private static void applyForce(World world, Entity source, Vec3d impact, double radius,
                                   double strength, boolean pullInward) {
        if (world.isClient()) {
            return;
        }

        double forceRadius = WIND_CHARGE_EXPLOSION_RADIUS * Math.max(0.0D, radius);
        double range = forceRadius * 2.0D;
        double forceMultiplier = Math.max(0.0D, strength);
        if (range < 1.0E-7D || forceMultiplier < 1.0E-7D) {
            return;
        }

        Box area = new Box(
                impact.x - range - 1.0D, impact.y - range - 1.0D, impact.z - range - 1.0D,
                impact.x + range + 1.0D, impact.y + range + 1.0D, impact.z + range + 1.0D
        );

        for (Entity entity : world.getEntitiesByClass(
                Entity.class,
                area,
                candidate -> candidate.isAlive()
                        && candidate != source
        )) {
            double normalizedDistance = Math.sqrt(entity.squaredDistanceTo(impact)) / range;
            if (normalizedDistance > 1.0D) {
                continue;
            }

            Vec3d direction = entity.getEyePos().subtract(impact).normalize();
            float exposure = Explosion.getExposure(impact, entity);
            double resistance = entity instanceof LivingEntity living
                    ? living.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)
                    : 0.0D;
            double windChargeForce = (1.0D - normalizedDistance)
                    * exposure
                    * WIND_CHARGE_KNOCKBACK_MULTIPLIER
                    * Math.max(0.0D, 1.0D - resistance)
                    * forceMultiplier;
            double sign = pullInward ? -1.0D : 1.0D;
            Vec3d force = direction.multiply(sign * windChargeForce);
            entity.addVelocity(force.x, force.y, force.z);
            entity.velocityDirty = true;
        }

        EffectManager.spawnParticles(world, impact, ParticleTypes.CLOUD, 12);
        EffectManager.playSound(world, impact,
                SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, 0.7F,
                pullInward ? 0.85F : 1.1F);
    }
}
