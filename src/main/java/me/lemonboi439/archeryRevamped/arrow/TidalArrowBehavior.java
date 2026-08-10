package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** A water-specialized arrow that keeps vanilla air handling with slightly more gravity. */
public final class TidalArrowBehavior implements ArrowBehavior {
    private static final double TIDAL_GRAVITY = 0.06D;
    private static final float SPIN_PER_TICK = 48.0F;

    @Override
    public void onTick(ArcheryArrowEntity arrow) {
        FluidState fluid = arrow.level().getFluidState(BlockPos.containing(arrow.position()));
        if (fluid.is(FluidTags.WATER)) {
            // In water the custom drag override preserves speed and the arrow
            // spins like a torpedo. Air flight deliberately remains stable.
            arrow.advanceTidalSpin(SPIN_PER_TICK);
            EffectManager.spawnParticles(arrow.level(), arrow.position(), ParticleTypes.BUBBLE, 3);
            return;
        }

        // super.tick() and ArrowPhysicsEngine have already applied the normal
        // arrow gravity. Apply only the extra 0.01 downward acceleration.
        Vec3 velocity = arrow.getDeltaMovement();
        if (velocity.lengthSqr() >= 1.0E-7D) {
            arrow.setDeltaMovement(velocity.add(0.0D, -(TIDAL_GRAVITY - 0.05D), 0.0D));
        }
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        EffectManager.playSound(arrow.level(), hit.getLocation(),
                SoundEvents.WATER_AMBIENT, 0.45F, 1.35F);
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        EffectManager.spawnParticles(arrow.level(), hit.getLocation(), ParticleTypes.BUBBLE, 8);
    }
}
