package me.lemonboi439.archeryRevamped.mixin;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Optional removal of the vanilla anvil's 40-level "Too Expensive" gate.
 * Vanilla still calculates the cost and validates all enchantment rules.
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin {
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void archeryRevamped$allowHighCostOutput(
            Player player,
            boolean present,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!ConfigManager.isLimitlessAnvilEnabled() || !present) {
            return;
        }

        AnvilMenu handler = (AnvilMenu) (Object) this;
        cir.setReturnValue(player.isCreative() || player.experienceLevel >= handler.getCost());
    }
}
