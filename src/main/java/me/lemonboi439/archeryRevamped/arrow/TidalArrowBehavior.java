package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/** A water-specialized arrow that accelerates underwater and leaves bubbles. */
public final class TidalArrowBehavior implements ArrowBehavior {
    private static final double UNDERWATER_SPEED_MULTIPLIER = 1.08D;

    @Override
    public void onTick(ArcheryArrowEntity arrow) {
        FluidState fluid = arrow.getEntityWorld().getFluidState(BlockPos.ofFloored(arrow.getEntityPos()));
        if (!fluid.isIn(FluidTags.WATER) || arrow.getVelocity().lengthSquared() < 1.0E-7D) {
            return;
        }

        arrow.setVelocity(arrow.getVelocity().multiply(UNDERWATER_SPEED_MULTIPLIER));
        arrow.advanceTidalSpin(32.0F);
        Vec3d position = arrow.getEntityPos();
        EffectManager.spawnParticles(arrow.getEntityWorld(), position, ParticleTypes.BUBBLE, 3);
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        EffectManager.playSound(arrow.getEntityWorld(), hit.getPos(),
                SoundEvents.BLOCK_WATER_AMBIENT, 0.45F, 1.35F);
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        EffectManager.spawnParticles(arrow.getEntityWorld(), hit.getPos(), ParticleTypes.BUBBLE, 8);
    }
}
