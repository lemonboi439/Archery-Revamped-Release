package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

public final class StickyArrowBehavior implements ArrowBehavior {
    @Override
    public void onTick(ArcheryArrowEntity arrow) {
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        if (!ConfigManager.isStickyArrowEnabled()) {
            return;
        }

        Entity entity = hit.getEntity();
        if (entity instanceof LivingEntity livingEntity && !livingEntity.isRemoved()) {
            int duration = Math.max(1, ConfigManager.getStickyDurationTicks());
            int amplifier = Math.max(0, ConfigManager.getStickySlownessLevel() - 1);
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, amplifier));

            double reduction = Math.max(0.0D, Math.min(1.0D,
                    ConfigManager.getStickyMovementReductionPercent() / 100.0D));
            Vec3d velocity = livingEntity.getVelocity();
            livingEntity.setVelocity(velocity.x * (1.0D - reduction), velocity.y,
                    velocity.z * (1.0D - reduction));

            Vec3d effectPosition = new Vec3d(
                    livingEntity.getX(),
                    livingEntity.getY() + livingEntity.getHeight() * 0.5D,
                    livingEntity.getZ()
            );
            EffectManager.spawnParticles(livingEntity.getEntityWorld(), effectPosition,
                    ParticleTypes.ITEM_SLIME, 12);
            EffectManager.playSound(livingEntity.getEntityWorld(), effectPosition,
                    SoundEvents.ENTITY_SLIME_SQUISH, 0.8F, 1.0F);
        }
        arrow.discard();
    }
}
