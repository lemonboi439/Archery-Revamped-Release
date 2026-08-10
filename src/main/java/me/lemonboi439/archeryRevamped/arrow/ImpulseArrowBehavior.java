package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.concurrent.ThreadLocalRandom;

public final class ImpulseArrowBehavior implements ArrowBehavior {
    @Override
    public void onTick(ArcheryArrowEntity arrow) {
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        if (ConfigManager.isImpulseArrowEnabled()) {
            Direction normal = hit.getDirection();
            arrow.scheduleDelayedImpact(ArrowType.IMPULSE,
                    hit.getLocation().add(normal.getUnitVec3().scale(0.25D)), activationDelay());
        }
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        if (ConfigManager.isImpulseArrowEnabled()) {
            arrow.scheduleDelayedImpact(ArrowType.IMPULSE, hit.getLocation(), activationDelay());
        }
    }

    @Override
    public void onDelayedImpact(ArcheryArrowEntity arrow, Vec3 impact) {
        if (ConfigManager.isImpulseArrowEnabled()) {
            AreaForceArrowBehavior.apply(arrow, impact, ConfigManager.getImpulseRadius(),
                    ConfigManager.getImpulseStrength(), true);
        }
    }

    private static int activationDelay() {
        return ThreadLocalRandom.current().nextInt(2, 6);
    }
}
