package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class ExplosiveArrowBehavior implements ArrowBehavior {
    @Override
    public void onTick(ArcheryArrowEntity arrow) {
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        explode(arrow);
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        explode(arrow);
    }

    private static void explode(ArcheryArrowEntity arrow) {
        if (!ConfigManager.isExplosiveArrowEnabled()) {
            return;
        }

        World world = arrow.getEntityWorld();
        Vec3d impact = new Vec3d(arrow.getX(), arrow.getY(), arrow.getZ());
        EffectManager.spawnParticles(world, impact, ParticleTypes.EXPLOSION_EMITTER, 1);
        EffectManager.playSound(world, impact, SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
        if (!world.isClient()) {
            world.createExplosion(
                    arrow,
                    arrow.getX(),
                    arrow.getY(),
                    arrow.getZ(),
                    (float) ConfigManager.getExplosiveArrowSize(),
                    ConfigManager.isExplosiveArrowAntiGriefEnabled()
                            ? World.ExplosionSourceType.NONE
                            : World.ExplosionSourceType.BLOCK
            );
        }
        arrow.discard();
    }
}
