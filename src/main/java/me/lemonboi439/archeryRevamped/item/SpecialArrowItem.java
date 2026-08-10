package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Shared projectile factory for special arrows, including dispenser shots. */
public abstract class SpecialArrowItem extends ArrowItem {
    protected SpecialArrowItem(Item.Properties settings) {
        super(settings);
    }

    protected abstract ArrowType arrowType();

    @Override
    public Projectile asProjectile(Level world, Position position, ItemStack stack, Direction direction) {
        ArcheryArrowEntity arrow = new ArcheryArrowEntity(
                world,
                position.x(),
                position.y(),
                position.z(),
                stack.copyWithCount(1),
                null
        );
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        arrow.setArrowType(arrowType());
        return arrow;
    }
}
