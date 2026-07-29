package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class EnderArrowBehavior implements ArrowBehavior {
    @Override
    public void onTick(ArcheryArrowEntity arrow) {
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        teleportAtImpact(arrow, hit.getPos());
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        teleportAtImpact(arrow, hit.getPos());
    }

    private static void teleportAtImpact(ArcheryArrowEntity arrow, Vec3d impactPosition) {
        if (!ConfigManager.isEnderArrowEnabled()) {
            return;
        }

        Entity owner = arrow.getOwner();
        if (!(owner instanceof LivingEntity livingOwner)) {
            arrow.discard();
            return;
        }

        World world = livingOwner.getEntityWorld();
        if (world.isClient()) {
            return;
        }

        Vec3d requestedPosition = new Vec3d(
                impactPosition.x,
                impactPosition.y + 0.5D,
                impactPosition.z
        );
        Vec3d safePosition = findSafePosition(livingOwner, requestedPosition);
        if (safePosition != null) {
            Vec3d oldPosition = new Vec3d(livingOwner.getX(), livingOwner.getY(), livingOwner.getZ());
            spawnEffects(world, oldPosition);
            livingOwner.requestTeleport(safePosition.x, safePosition.y, safePosition.z);
            spawnEffects(world, safePosition);
        }

        arrow.discard();
    }

    private static Vec3d findSafePosition(LivingEntity owner, Vec3d requestedPosition) {
        World world = owner.getEntityWorld();
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
            Vec3d candidate = requestedPosition.add(offset[0], offset[1], offset[2]);
            Box candidateBox = owner.getBoundingBox().offset(
                    candidate.x - owner.getX(),
                    candidate.y - owner.getY(),
                    candidate.z - owner.getZ()
            );
            if (world.isSpaceEmpty(owner, candidateBox)) {
                return candidate;
            }
        }

        return null;
    }

    private static void spawnEffects(World world, Vec3d position) {
        EffectManager.spawnParticles(world, position, ParticleTypes.PORTAL, 32);
        EffectManager.playSound(world, position,
                SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }
}
