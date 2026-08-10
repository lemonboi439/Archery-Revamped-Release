package me.lemonboi439.archeryRevamped.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Keeps tipped-arrow conversion exclusive to the Fletching Table. */
@Mixin(CraftingScreenHandler.class)
public abstract class CraftingScreenHandlerMixin {
    @Inject(method = "updateResult", at = @At("TAIL"))
    private static void archeryRevamped$disableCraftingTableTippedArrows(
            ScreenHandler handler,
            World world,
            PlayerEntity player,
            RecipeInputInventory input,
            CraftingResultInventory result,
            RecipeEntry<CraftingRecipe> recipe,
            CallbackInfo ci
    ) {
        if (result.getStack(0).isOf(Items.TIPPED_ARROW)) {
            result.setStack(0, ItemStack.EMPTY);
        }
    }
}
