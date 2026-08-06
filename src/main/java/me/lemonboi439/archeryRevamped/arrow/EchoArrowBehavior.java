package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/** Applies darkness and seeds nearby sculk when its hit kills an entity. */
public final class EchoArrowBehavior implements ArrowBehavior {
    @Override
    public void onTick(ArcheryArrowEntity arrow) {
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        EffectManager.spawnParticles(arrow.getEntityWorld(), hit.getPos(), ParticleTypes.SCULK_SOUL, 8);
        EffectManager.playSound(arrow.getEntityWorld(), hit.getPos(),
                SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, 0.65F, 0.7F);
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        Entity target = hit.getEntity();
        if (target instanceof LivingEntity living && !arrow.getEntityWorld().isClient()) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 100, 0));
            if (!living.isAlive()) {
                spreadSculk(arrow.getEntityWorld(), hit.getPos());
            }
        }
        EffectManager.spawnParticles(arrow.getEntityWorld(), hit.getPos(), ParticleTypes.SCULK_SOUL, 12);
        EffectManager.playSound(arrow.getEntityWorld(), hit.getPos(),
                SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, 0.7F, 1.25F);
    }

    private static void spreadSculk(net.minecraft.world.World world, Vec3d impact) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        BlockPos center = BlockPos.ofFloored(impact);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (x * x + z * z > 5) {
                    continue;
                }

                BlockPos position = findInfectableGround(serverWorld, center.add(x, 0, z));
                if (position != null) {
                    // Sculk catalyst-style spreading converts the ground itself; it never
                    // fills air above it or overwrites replaceable plants such as grass.
                    serverWorld.setBlockState(position, Blocks.SCULK.getDefaultState());
                }
            }
        }
        EffectManager.spawnParticles(serverWorld, impact, ParticleTypes.SCULK_CHARGE_POP, 16);
    }

    private static BlockPos findInfectableGround(ServerWorld world, BlockPos start) {
        // An entity can die several blocks above its footing. Search down to the nearby
        // terrain, but only ever replace a real solid block already in the world.
        for (int yOffset = 0; yOffset <= 5; yOffset++) {
            BlockPos position = start.down(yOffset);
            BlockState state = world.getBlockState(position);
            if (state.isOf(Blocks.SCULK)) {
                return null;
            }
            if (!state.isAir() && !state.isReplaceable() && state.isSolidBlock(world, position)) {
                return position;
            }
        }
        return null;
    }
}
