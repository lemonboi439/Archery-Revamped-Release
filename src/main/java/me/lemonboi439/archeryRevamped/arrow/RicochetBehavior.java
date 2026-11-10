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
        Vec3 normal = new Vec3(face.getStepX(), face.getStepY(), face.getStepZ());
        double dot = velocity.dot(normal);
        double velocityLoss = ConfigManager.getRicochetVelocityLossPercent() / 100.0D;
        Vec3 reflected = velocity.subtract(normal.scale(2.0D * dot))
                .scale(1.0D - velocityLoss);

        arrow.setDeltaMovement(reflected);
        arrow.incrementBounceCount();

        // Vanilla marks the arrow in-ground and re-reads its position after
        // onHitBlock returns. Re-apply the reflected state at the end of the
        // tick and place the arrow clear of the block face so it does not
        // immediately re-collide, stick and twitch.
        Vec3 exitPosition = hit.getLocation().add(normal.scale(0.05D));
        arrow.queueRicochet(exitPosition, reflected);

        Vec3 bouncePosition = exitPosition;
        EffectManager.spawnParticles(arrow.getCommandSenderWorld(), bouncePosition,
                ParticleTypes.ITEM_SLIME, 12);
        EffectManager.playSound(arrow.getCommandSenderWorld(), bouncePosition,
                SoundEvents.SLIME_JUMP, 0.8F, 1.15F);
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
    }
}
