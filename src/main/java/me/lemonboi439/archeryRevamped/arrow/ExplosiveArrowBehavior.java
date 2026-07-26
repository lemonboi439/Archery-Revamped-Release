package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
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
        World world = arrow.getEntityWorld();
        if (!world.isClient()) {
            world.createExplosion(
                    arrow,
                    arrow.getX(),
                    arrow.getY(),
                    arrow.getZ(),
                    (float) ConfigManager.getExplosiveArrowSize(),
                    World.ExplosionSourceType.BLOCK
            );
        }
        arrow.discard();
    }
}
