package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ThreadLocalRandom;

public final class ImpulseArrowBehavior implements ArrowBehavior {
    @Override
    public void onTick(ArcheryArrowEntity arrow) {
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        if (ConfigManager.isImpulseArrowEnabled()) {
            arrow.scheduleDelayedImpact(ArrowType.IMPULSE, hit.getPos(), activationDelay());
        }
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        if (ConfigManager.isImpulseArrowEnabled()) {
            arrow.scheduleDelayedImpact(ArrowType.IMPULSE, hit.getPos(), activationDelay());
        }
    }

    @Override
    public void onDelayedImpact(ArcheryArrowEntity arrow, Vec3d impact) {
        if (ConfigManager.isImpulseArrowEnabled()) {
            AreaForceArrowBehavior.apply(arrow, impact, ConfigManager.getImpulseRadius(),
                    ConfigManager.getImpulseStrength(), true);
        }
    }

    private static int activationDelay() {
        return ThreadLocalRandom.current().nextInt(2, 6);
    }
}
