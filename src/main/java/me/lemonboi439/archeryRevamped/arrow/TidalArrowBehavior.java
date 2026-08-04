package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/** A water-specialized arrow with a flatter, slower flight and torpedo spin. */
public final class TidalArrowBehavior implements ArrowBehavior {
    private static final double TIDAL_GRAVITY = 0.06D;
    private static final double TIDAL_SPEED_MULTIPLIER = 0.995D;
    private static final float SPIN_PER_TICK = 48.0F;

    @Override
    public void onTick(ArcheryArrowEntity arrow) {
        Vec3d velocity = arrow.getVelocity();
        if (velocity.lengthSquared() >= 1.0E-7D) {
            // PersistentProjectileEntity and the shared physics engine have
            // already applied normal gravity. Apply only the small difference
            // needed for the Tidal Arrow's slightly heavier flight.
            double gravityCorrection = ConfigManager.getGravity() - TIDAL_GRAVITY;
            velocity = velocity.add(0.0D, gravityCorrection, 0.0D)
                    .multiply(TIDAL_SPEED_MULTIPLIER);
            arrow.setVelocity(velocity);
        }

        // Rotate continuously in every medium so the projectile visibly
        // spins like a torpedo throughout its flight.
        arrow.advanceTidalSpin(SPIN_PER_TICK);

        FluidState fluid = arrow.getEntityWorld().getFluidState(BlockPos.ofFloored(arrow.getEntityPos()));
        if (!fluid.isIn(FluidTags.WATER)) {
            return;
        }

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
