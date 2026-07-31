package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

/** Pushes nearby entities away from the arrow's landing point. */
public final class ShockwaveArrowBehavior implements ArrowBehavior {
    @Override
    public void onTick(ArcheryArrowEntity arrow) {
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        if (ConfigManager.isShockwaveArrowEnabled()) {
            AreaForceArrowBehavior.apply(arrow, hit.getPos(),
                    ConfigManager.getShockwaveRadius(),
                    ConfigManager.getShockwaveStrength(), false);
        }
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        if (ConfigManager.isShockwaveArrowEnabled()) {
            AreaForceArrowBehavior.apply(arrow, hit.getPos(),
                    ConfigManager.getShockwaveRadius(),
                    ConfigManager.getShockwaveStrength(), false);
        }
    }
}
