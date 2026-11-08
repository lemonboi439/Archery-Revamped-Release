package me.lemonboi439.archeryRevamped.mixin;

import me.lemonboi439.archeryRevamped.arrow.ArrowReplacement;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public abstract class RangedWeaponItemMixin {
    @Inject(method = "createArrow", at = @At("RETURN"), cancellable = true)
    private void archeryRevamped$createRicochetArrow(
            Level world,
            ItemStack projectileStack,
            LivingEntity shooter,
            CallbackInfoReturnable<net.minecraft.world.entity.projectile.AbstractArrow> cir
    ) {
        if (cir.getReturnValue() instanceof ArcheryArrowEntity) {
            return;
        }

        cir.setReturnValue(ArrowReplacement.create(world, projectileStack, shooter));
    }
}
