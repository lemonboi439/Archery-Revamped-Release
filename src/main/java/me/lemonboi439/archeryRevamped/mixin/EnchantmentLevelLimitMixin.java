package me.lemonboi439.archeryRevamped.mixin;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Makes the vanilla max-level check unbounded for this mod's enchantments. */
@Mixin(Enchantment.class)
public abstract class EnchantmentLevelLimitMixin {
    @Inject(method = "getMaxLevel", at = @At("RETURN"), cancellable = true)
    private void archeryRevamped$allowInfiniteLevels(CallbackInfoReturnable<Integer> callback) {
        if (!ConfigManager.isInfiniteLevels()) {
            return;
        }

        Enchantment enchantment = (Enchantment) (Object) this;
        if (enchantment.description().getContent() instanceof TranslatableTextContent content
                && content.getKey().startsWith("enchantment.archery-revamped.")) {
            callback.setReturnValue(Integer.MAX_VALUE);
        }
    }
}
