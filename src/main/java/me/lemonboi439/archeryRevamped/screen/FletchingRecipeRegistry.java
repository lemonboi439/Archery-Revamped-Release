package me.lemonboi439.archeryRevamped.screen;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.item.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.List;

public final class FletchingRecipeRegistry {
    private static final List<FletchingRecipe> RECIPES = List.of(
            new FletchingRecipe("ender_arrow", Items.ENDER_PEARL, ModItems.ENDER_ARROW, 1, false),
            new FletchingRecipe("shockwave_arrow", Items.WIND_CHARGE, ModItems.SHOCKWAVE_ARROW, 1, false),
            new FletchingRecipe("impulse_arrow", Items.IRON_NUGGET, ModItems.IMPULSE_ARROW, 1, false),
            new FletchingRecipe("explosive_arrow", Items.GUNPOWDER, ModItems.EXPLOSIVE_ARROW, 1, false),
            new FletchingRecipe("tidal_arrow", Items.HEART_OF_THE_SEA, ModItems.TIDAL_ARROW, 1, false),
            new FletchingRecipe("shattering_arrow", Items.AMETHYST_SHARD, ModItems.SHATTERING_ARROW, 1, false),
            new FletchingRecipe("echo_arrow", Items.ECHO_SHARD, ModItems.ECHO_ARROW, 1, false),
            new FletchingRecipe("tipped_arrow", Items.POTION, Items.TIPPED_ARROW, 4, true)
    );

    private FletchingRecipeRegistry() {
    }

    /**
     * Stable read-only view for optional recipe viewers. JEI, REI, and EMI
     * integrations can consume the same recipe definitions without making
     * any of those client-only mods a required dependency of this mod.
     */
    public static List<FletchingRecipe> getRecipes() {
        return RECIPES;
    }

    public static FletchingRecipe match(ItemStack arrowStack, ItemStack ingredientStack) {
        for (FletchingRecipe recipe : RECIPES) {
            if (recipe.matches(arrowStack, ingredientStack)) {
                return recipe;
            }
        }
        return null;
    }

    public static ItemStack createOutput(FletchingRecipe recipe, ItemStack ingredientStack) {
        int outputCount = Math.max(1, ConfigManager.getFletchingRecipeOutputCount());
        ItemStack output = new ItemStack(recipe.outputItem(), outputCount);
        if (recipe.copiesPotion() && ingredientStack.contains(DataComponentTypes.POTION_CONTENTS)) {
            PotionContentsComponent potion = ingredientStack.get(DataComponentTypes.POTION_CONTENTS);
            if (potion != null) {
                output.set(DataComponentTypes.POTION_CONTENTS, potion);
            }
        }
        return output;
    }

    public record FletchingRecipe(
            String name,
            Item ingredient,
            Item outputItem,
            int arrowCount,
            boolean copiesPotion
    ) {
        public boolean matches(ItemStack arrowStack, ItemStack ingredientStack) {
            if (!(arrowStack.getItem() instanceof ArrowItem)
                    || arrowStack.getCount() < arrowCount
                    || !ingredientStack.isOf(ingredient)) {
                return false;
            }
            return !copiesPotion || ingredientStack.contains(DataComponentTypes.POTION_CONTENTS);
        }

        public Text displayName() {
            return Text.translatable("container.archery-revamped.recipe." + name);
        }
    }
}
