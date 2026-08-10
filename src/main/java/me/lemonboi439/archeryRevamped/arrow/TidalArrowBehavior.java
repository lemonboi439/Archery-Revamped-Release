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

/** A water-specialized arrow that keeps vanilla air handling with slightly more gravity. */
public final class TidalArrowBehavior implements ArrowBehavior {
    private static final double TIDAL_GRAVITY = 0.06D;
    private static final float SPIN_PER_TICK = 48.0F;

    @Override
    public void onTick(ArcheryArrowEntity arrow) {
        FluidState fluid = arrow.getEntityWorld().getFluidState(BlockPos.ofFloored(arrow.getPos()));
        if (fluid.isIn(FluidTags.WATER)) {
            // In water the custom drag override preserves speed and the arrow
            // spins like a torpedo. Air flight deliberately remains stable.
            arrow.advanceTidalSpin(SPIN_PER_TICK);
            EffectManager.spawnParticles(arrow.getEntityWorld(), arrow.getPos(), ParticleTypes.BUBBLE, 3);
            return;
        }

        // super.tick() and ArrowPhysicsEngine have already applied the normal
        // arrow gravity. Apply only the extra 0.01 downward acceleration.
        Vec3d velocity = arrow.getVelocity();
        if (velocity.lengthSquared() >= 1.0E-7D) {
            arrow.setVelocity(velocity.add(0.0D, -(TIDAL_GRAVITY - 0.05D), 0.0D));
        }
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
