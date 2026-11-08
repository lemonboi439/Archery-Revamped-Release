package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
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
    public AbstractArrow createArrow(Level world, ItemStack stack, LivingEntity shooter) {
        ItemStack pickupStack = stack.copy();
        pickupStack.setCount(1);
        ArcheryArrowEntity arrow = new ArcheryArrowEntity(
                world,
                shooter,
                pickupStack,
                shooter.getUseItem().copy()
        );
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        arrow.setArrowType(arrowType());
        return arrow;
    }
}
