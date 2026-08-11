package me.lemonboi439.archeryRevamped.mixin;

import me.lemonboi439.archeryRevamped.arrow.ArrowReplacement;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public abstract class RangedWeaponItemMixin {
    @Inject(method = "createArrow", at = @At("RETURN"), cancellable = true)
    private void archeryRevamped$createRicochetArrow(
            World world,
            ItemStack projectileStack,
            LivingEntity shooter,
            CallbackInfoReturnable<net.minecraft.entity.projectile.PersistentProjectileEntity> cir
    ) {
        if (cir.getReturnValue() instanceof ArcheryArrowEntity) {
            return;
        }

        cir.setReturnValue(ArrowReplacement.create(world, projectileStack, shooter));
    }
}
