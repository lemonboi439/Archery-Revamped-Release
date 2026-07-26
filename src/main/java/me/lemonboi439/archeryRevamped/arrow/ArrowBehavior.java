package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

public interface ArrowBehavior {
    void onTick(ArcheryArrowEntity arrow);

    void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit);

    void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit);
}
