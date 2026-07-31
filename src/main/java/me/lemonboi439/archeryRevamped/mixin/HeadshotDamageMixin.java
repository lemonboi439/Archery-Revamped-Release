package me.lemonboi439.archeryRevamped.mixin;

import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Applies Headshot after PersistentProjectileEntity has computed final damage. */
@Mixin(PersistentProjectileEntity.class)
public abstract class HeadshotDamageMixin {
    @ModifyArg(
            method = "onEntityHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;sidedDamage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            ),
            index = 1
    )
    private float archeryRevamped$applyHeadshotMultiplier(float damage) {
        if ((Object) this instanceof ArcheryArrowEntity arrow) {
            return (float) (damage * arrow.consumeHeadshotDamageMultiplier());
        }
        return damage;
    }
}
