package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public final class EnderArrowBehavior implements ArrowBehavior {
    @Override
    public void onTick(ArcheryArrowEntity arrow) {
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        teleportAtImpact(arrow, hit.getLocation());
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        teleportAtImpact(arrow, hit.getLocation());
    }

    private static void teleportAtImpact(ArcheryArrowEntity arrow, Vec3 impactPosition) {
        if (!ConfigManager.isEnderArrowEnabled()) {
            return;
        }

        Entity owner = arrow.getOwner();
        if (!(owner instanceof LivingEntity livingOwner)) {
            arrow.discard();
            return;
        }

        Level world = livingOwner.level();
        if (world.isClientSide()) {
            return;
        }

        Vec3 requestedPosition = new Vec3(
                impactPosition.x,
                impactPosition.y + 0.5D,
                impactPosition.z
        );
        Vec3 safePosition = findSafePosition(livingOwner, requestedPosition);
        if (safePosition != null) {
            Vec3 oldPosition = new Vec3(livingOwner.getX(), livingOwner.getY(), livingOwner.getZ());
            spawnEffects(world, oldPosition);
            livingOwner.teleportTo(safePosition.x, safePosition.y, safePosition.z);
            spawnEffects(world, safePosition);
        }

        arrow.discard();
    }

    private static Vec3 findSafePosition(LivingEntity owner, Vec3 requestedPosition) {
        Level world = owner.level();
        double[][] offsets = {
                {0.0D, 0.0D, 0.0D},
                {0.0D, 1.0D, 0.0D},
                {0.0D, 2.0D, 0.0D},
                {1.0D, 0.0D, 0.0D},
                {-1.0D, 0.0D, 0.0D},
                {0.0D, 0.0D, 1.0D},
                {0.0D, 0.0D, -1.0D},
                {1.0D, 1.0D, 0.0D},
                {-1.0D, 1.0D, 0.0D},
                {0.0D, 1.0D, 1.0D},
                {0.0D, 1.0D, -1.0D}
        };

        for (double[] offset : offsets) {
            Vec3 candidate = requestedPosition.add(offset[0], offset[1], offset[2]);
            AABB candidateBox = owner.getBoundingBox().move(
                    candidate.x - owner.getX(),
                    candidate.y - owner.getY(),
                    candidate.z - owner.getZ()
            );
            if (world.noCollision(owner, candidateBox)) {
                return candidate;
            }
        }

        return null;
    }

    private static void spawnEffects(Level world, Vec3 position) {
        EffectManager.spawnParticles(world, position, ParticleTypes.PORTAL, 32);
        EffectManager.playSound(world, position,
                SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }
}
