package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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

    public static void apply(ArcheryArrowEntity arrow, Vec3 impact, double radius,
                             double strength, boolean pullInward) {
        applyForce(arrow.getCommandSenderWorld(), arrow, impact, radius, strength, pullInward);
        arrow.discard();
    }

    private static void applyForce(Level world, Entity source, Vec3 impact, double radius,
                                   double strength, boolean pullInward) {
        if (world.isClientSide()) {
            return;
        }

        double forceRadius = WIND_CHARGE_EXPLOSION_RADIUS * Math.max(0.0D, radius);
        double range = forceRadius * 2.0D;
        double forceMultiplier = Math.max(0.0D, strength);
        if (range < 1.0E-7D || forceMultiplier < 1.0E-7D) {
            return;
        }

        AABB area = new AABB(
                impact.x - range - 1.0D, impact.y - range - 1.0D, impact.z - range - 1.0D,
                impact.x + range + 1.0D, impact.y + range + 1.0D, impact.z + range + 1.0D
        );

        for (Entity entity : world.getEntitiesOfClass(
                Entity.class,
                area,
                candidate -> candidate.isAlive()
                        && candidate != source
        )) {
            double normalizedDistance = Math.sqrt(entity.distanceToSqr(impact)) / range;
            if (normalizedDistance > 1.0D) {
                continue;
            }

            Vec3 direction = entity.getEyePosition().subtract(impact).normalize();
            float exposure = Explosion.getSeenPercent(impact, entity);
            double resistance = entity instanceof LivingEntity living
                    ? living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)
                    : 0.0D;
            double windChargeForce = (1.0D - normalizedDistance)
                    * exposure
                    * WIND_CHARGE_KNOCKBACK_MULTIPLIER
                    * Math.max(0.0D, 1.0D - resistance)
                    * forceMultiplier;
            double sign = pullInward ? -1.0D : 1.0D;
            Vec3 force = direction.scale(sign * windChargeForce);
            entity.push(force.x, force.y, force.z);
            entity.hasImpulse = true;
        }

        EffectManager.spawnParticles(world, impact, ParticleTypes.CLOUD, 12);
        EffectManager.playSound(world, impact,
                SoundEvents.PLAYER_ATTACK_KNOCKBACK, 0.7F,
                pullInward ? 0.85F : 1.1F);
    }
}
