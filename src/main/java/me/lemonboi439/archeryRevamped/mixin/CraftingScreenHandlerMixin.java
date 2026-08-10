package me.lemonboi439.archeryRevamped.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Reserves tipped-arrow conversion for Archery Revamped's Fletching Table. */
@Mixin(CraftingMenu.class)
public abstract class CraftingScreenHandlerMixin {
    @Inject(method = "slotChangedCraftingGrid", at = @At("TAIL"))
    private static void archeryRevamped$disableCraftingTableTippedArrows(
            AbstractContainerMenu handler,
            ServerLevel world,
            Player player,
            CraftingContainer input,
            ResultContainer result,
            RecipeHolder<CraftingRecipe> recipe,
            CallbackInfo ci
    ) {
        if (result.getItem(0).is(Items.TIPPED_ARROW)) {
            result.setItem(0, ItemStack.EMPTY);
        }
    }
}
