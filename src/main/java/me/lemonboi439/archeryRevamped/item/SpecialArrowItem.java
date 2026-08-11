package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Shared projectile factory for special arrows, including dispenser shots. */
public abstract class SpecialArrowItem extends ArrowItem {
    protected SpecialArrowItem(Item.Settings settings) {
        super(settings);
    }

    protected abstract ArrowType arrowType();

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        ItemStack pickupStack = stack.copy();
        pickupStack.setCount(1);
        ArcheryArrowEntity arrow = new ArcheryArrowEntity(
                world,
                shooter,
                pickupStack,
                shooter.getActiveItem().copy()
        );
        arrow.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        arrow.setArrowType(arrowType());
        return arrow;
    }
}
