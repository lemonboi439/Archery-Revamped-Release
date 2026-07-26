package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

public final class RicochetBehavior implements ArrowBehavior {
    @Override
    public void onTick(ArcheryArrowEntity arrow) {
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        applyRicochet(arrow, hit);
    }

    public static void applyRicochet(ArcheryArrowEntity arrow, BlockHitResult hit) {
        if (!arrow.canRicochet() || arrow.isArrowInGround()) {
            return;
        }

        Vec3d velocity = arrow.getVelocity();
        Direction face = hit.getSide();
        Vec3d normal = face.getDoubleVector();
        double dot = velocity.dotProduct(normal);
        double velocityLoss = ConfigManager.getRicochetVelocityLossPercent() / 100.0D;
        Vec3d reflected = velocity.subtract(normal.multiply(2.0D * dot))
                .multiply(1.0D - velocityLoss);

        arrow.setVelocity(reflected);
        arrow.clearInGround();
        arrow.incrementBounceCount();
        arrow.setPosition(
                arrow.getX() + normal.x * 0.01D,
                arrow.getY() + normal.y * 0.01D,
                arrow.getZ() + normal.z * 0.01D
        );
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
    }
}
