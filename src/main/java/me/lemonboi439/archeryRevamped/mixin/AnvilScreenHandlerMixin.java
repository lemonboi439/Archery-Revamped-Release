package me.lemonboi439.archeryRevamped.mixin;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Optional removal of the vanilla anvil's 40-level "Too Expensive" gate.
 * Vanilla still calculates the cost and validates all enchantment rules.
 */
@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {
    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    private void archeryRevamped$allowHighCostOutput(
            PlayerEntity player,
            boolean present,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!ConfigManager.isLimitlessAnvilEnabled() || !present) {
            return;
        }

        AnvilScreenHandler handler = (AnvilScreenHandler) (Object) this;
        cir.setReturnValue(player.isCreative() || player.experienceLevel >= handler.getLevelCost());
    }
}
