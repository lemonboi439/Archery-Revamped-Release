package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** Shared landing-point force logic for Shockwave and Impulse arrows. */
public final class AreaForceArrowBehavior {
    private AreaForceArrowBehavior() {
    }

    public static void apply(ArcheryArrowEntity arrow, Vec3d impact, double radius,
                             double strength, boolean pullInward) {
        World world = arrow.getEntityWorld();
        if (world.isClient()) {
            return;
        }

        double safeRadius = Math.max(0.01D, radius);
        double safeStrength = Math.max(0.0D, strength);
        Box area = new Box(
                impact.x - safeRadius, impact.y - safeRadius, impact.z - safeRadius,
                impact.x + safeRadius, impact.y + safeRadius, impact.z + safeRadius
        );

        for (Entity entity : world.getEntitiesByClass(
                Entity.class,
                area,
                candidate -> candidate.isAlive()
                        && candidate != arrow
                        && candidate != arrow.getOwner()
        )) {
            Vec3d direction = new Vec3d(
                    entity.getX() - impact.x,
                    entity.getY() + entity.getHeight() * 0.5D - impact.y,
                    entity.getZ() - impact.z
            );
            double distance = direction.length();
            if (distance < 1.0E-7D) {
                continue;
            }

            // Keep the outer part of the area useful instead of reducing the
            // force to almost nothing before the entity reaches the edge.
            double falloff = 0.35D + 0.65D * Math.max(0.0D, 1.0D - distance / safeRadius);
            double playerMultiplier = entity instanceof PlayerEntity ? 4.0D : 1.0D;
            double sign = pullInward ? -1.0D : 1.0D;
            Vec3d force = direction.normalize().multiply(sign * safeStrength * falloff * playerMultiplier);
            entity.addVelocity(force.x, force.y, force.z);
        }

        EffectManager.spawnParticles(world, impact, ParticleTypes.GUST, 12);
        EffectManager.playSound(world, impact,
                SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST, 0.7F,
                pullInward ? 0.85F : 1.1F);
        arrow.discard();
    }
}
