package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class ImpulseArrowBehavior implements ArrowBehavior {
    @Override
    public void onTick(ArcheryArrowEntity arrow) {
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        triggerBlast(arrow);
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        triggerBlast(arrow);
    }

    private static void triggerBlast(ArcheryArrowEntity arrow) {
        World world = arrow.getEntityWorld();
        if (world.isClient()) {
            return;
        }

        double radius = ConfigManager.getImpulseBlastRadius();
        double strength = ConfigManager.getImpulseKnockbackStrength();
        Vec3d impact = new Vec3d(arrow.getX(), arrow.getY(), arrow.getZ());
        Entity owner = arrow.getOwner();

        for (Entity entity : world.getEntitiesByClass(
                Entity.class,
                arrow.getBoundingBox().expand(radius),
                candidate -> candidate.isAlive() && candidate != arrow && candidate != owner
        )) {
            Vec3d direction = new Vec3d(
                    entity.getX() - impact.x,
                    entity.getY() + entity.getHeight() * 0.5D - impact.y,
                    entity.getZ() - impact.z
            );
            if (direction.lengthSquared() < 1.0E-7D) {
                continue;
            }

            double distance = direction.length();
            double falloff = Math.max(0.0D, 1.0D - distance / radius);
            Vec3d knockback = direction.normalize().multiply(strength * falloff);
            entity.addVelocity(knockback.x, knockback.y, knockback.z);
        }

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    ParticleTypes.EXPLOSION,
                    impact.x, impact.y, impact.z,
                    1, 0.0D, 0.0D, 0.0D, 0.0D
            );
        }
        world.playSound(
                null,
                impact.x, impact.y, impact.z,
                SoundEvents.ENTITY_PLAYER_SPLASH,
                SoundCategory.PLAYERS,
                0.45F,
                1.15F
        );
        arrow.discard();
    }
}
