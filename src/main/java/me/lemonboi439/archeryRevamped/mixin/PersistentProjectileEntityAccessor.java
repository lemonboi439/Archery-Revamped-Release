package me.lemonboi439.archeryRevamped.mixin;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface PersistentProjectileEntityAccessor {
    @Accessor("baseDamage")
    double archeryRevamped$getDamage();

    @Accessor("pickup")
    AbstractArrow.Pickup archeryRevamped$getPickupType();

    @Accessor("pickup")
    void archeryRevamped$setPickupType(AbstractArrow.Pickup pickupType);

    @Invoker("setPierceLevel")
    void archeryRevamped$setPierceLevel(byte level);
}
