package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

/** Shared projectile factory for special arrows, including dispenser shots. */
public abstract class SpecialArrowItem extends ArrowItem {
    protected SpecialArrowItem(Item.Settings settings) {
        super(settings);
    }

    protected abstract ArrowType arrowType();

    @Override
    public ProjectileEntity createEntity(World world, Position position, ItemStack stack, Direction direction) {
        ArcheryArrowEntity arrow = new ArcheryArrowEntity(
                world,
                position.getX(),
                position.getY(),
                position.getZ(),
                stack.copyWithCount(1),
                null
        );
        arrow.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        arrow.setArrowType(arrowType());
        return arrow;
    }
}
