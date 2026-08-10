package me.lemonboi439.archeryRevamped.mixin;

import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface PersistentProjectileEntityAccessor {
    @Accessor("baseDamage")
    double archeryRevamped$getDamage();

    @Accessor("firedFromWeapon")
    ItemStack archeryRevamped$getWeapon();

    @Accessor("firedFromWeapon")
    void archeryRevamped$setWeapon(ItemStack weapon);

    @Accessor("pickup")
    AbstractArrow.Pickup archeryRevamped$getPickupType();

    @Accessor("pickup")
    void archeryRevamped$setPickupType(AbstractArrow.Pickup pickupType);

    @Invoker("setPierceLevel")
    void archeryRevamped$setPierceLevel(byte level);
}
