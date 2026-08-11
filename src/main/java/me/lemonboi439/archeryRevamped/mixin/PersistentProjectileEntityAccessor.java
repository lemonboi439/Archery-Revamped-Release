package me.lemonboi439.archeryRevamped.mixin;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PersistentProjectileEntity.class)
public interface PersistentProjectileEntityAccessor {
    @Accessor("damage")
    double archeryRevamped$getDamage();

    @Accessor("pickupType")
    PersistentProjectileEntity.PickupPermission archeryRevamped$getPickupType();

    @Accessor("pickupType")
    void archeryRevamped$setPickupType(PersistentProjectileEntity.PickupPermission pickupType);

    @Invoker("setPierceLevel")
    void archeryRevamped$setPierceLevel(byte level);
}
