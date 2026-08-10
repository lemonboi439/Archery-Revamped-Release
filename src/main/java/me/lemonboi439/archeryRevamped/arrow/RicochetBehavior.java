package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public final class RicochetBehavior implements ArrowBehavior {
    @Override
    public void onTick(ArcheryArrowEntity arrow) {
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        applyRicochet(arrow, hit);
    }

    public static void applyRicochet(ArcheryArrowEntity arrow, BlockHitResult hit) {
        if (!arrow.canRicochet() || arrow.isArrowInGround()) {
            return;
        }

        Vec3 velocity = arrow.getDeltaMovement();
        Direction face = hit.getDirection();
        Vec3 normal = face.getUnitVec3();
        double dot = velocity.dot(normal);
        double velocityLoss = ConfigManager.getRicochetVelocityLossPercent() / 100.0D;
        Vec3 reflected = velocity.subtract(normal.scale(2.0D * dot))
                .scale(1.0D - velocityLoss);

        arrow.setDeltaMovement(reflected);
        arrow.clearInGround();
        arrow.incrementBounceCount();
        arrow.setPos(
                arrow.getX() + normal.x * 0.01D,
                arrow.getY() + normal.y * 0.01D,
                arrow.getZ() + normal.z * 0.01D
        );

        Vec3 bouncePosition = new Vec3(arrow.getX(), arrow.getY(), arrow.getZ());
        EffectManager.spawnParticles(arrow.level(), bouncePosition,
                ParticleTypes.ITEM_SLIME, 12);
        EffectManager.playSound(arrow.level(), bouncePosition,
                SoundEvents.SLIME_JUMP, 0.8F, 1.15F);
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
    }
}
