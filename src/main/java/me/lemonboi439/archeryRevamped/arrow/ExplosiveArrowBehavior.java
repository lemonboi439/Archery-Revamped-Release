package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

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

        Level world = arrow.level();
        Vec3 impact = new Vec3(arrow.getX(), arrow.getY(), arrow.getZ());
        EffectManager.spawnParticles(world, impact, ParticleTypes.EXPLOSION_EMITTER, 1);
        EffectManager.playSound(world, impact, SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F);
        if (!world.isClientSide()) {
            world.explode(
                    arrow,
                    arrow.getX(),
                    arrow.getY(),
                    arrow.getZ(),
                    (float) ConfigManager.getExplosiveArrowSize(),
                    ConfigManager.isExplosiveArrowAntiGriefEnabled()
                            ? Level.ExplosionInteraction.NONE
                            : Level.ExplosionInteraction.BLOCK
            );
        }
        arrow.discard();
    }
}
