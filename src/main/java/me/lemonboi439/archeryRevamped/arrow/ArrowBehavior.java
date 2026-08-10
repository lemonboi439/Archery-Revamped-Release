package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public interface ArrowBehavior {
    void onTick(ArcheryArrowEntity arrow);

    void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit);

    void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit);

    default void onDelayedImpact(ArcheryArrowEntity arrow, Vec3 impact) {
    }
}
