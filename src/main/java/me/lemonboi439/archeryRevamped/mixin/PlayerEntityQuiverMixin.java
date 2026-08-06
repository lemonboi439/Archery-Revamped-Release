package me.lemonboi439.archeryRevamped.mixin;

import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Makes the selected active-quiver stack take priority over ordinary inventory ammunition. */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityQuiverMixin {
    @Inject(method = "getProjectileType", at = @At("HEAD"), cancellable = true)
    private void archeryRevamped$useSelectedQuiverArrow(ItemStack weapon,
                                                        CallbackInfoReturnable<ItemStack> cir) {
        if (!(weapon.getItem() instanceof BowItem) && !(weapon.getItem() instanceof CrossbowItem)) {
            return;
        }
        ItemStack selected = QuiverManager.getSelectedArrow((PlayerEntity) (Object) this);
        if (!selected.isEmpty()) {
            cir.setReturnValue(selected);
        }
    }
}
