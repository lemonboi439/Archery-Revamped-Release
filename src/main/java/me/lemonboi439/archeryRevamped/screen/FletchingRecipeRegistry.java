package me.lemonboi439.archeryRevamped.screen;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import java.util.List;

public final class FletchingRecipeRegistry {
    private static final List<FletchingRecipe> RECIPES = List.of(
            new FletchingRecipe("ender_arrow", Items.ENDER_PEARL, ModItems.ENDER_ARROW, 4, false),
            new FletchingRecipe("shockwave_arrow", Items.WIND_CHARGE, ModItems.SHOCKWAVE_ARROW, 4, false),
            new FletchingRecipe("impulse_arrow", Items.IRON_NUGGET, ModItems.IMPULSE_ARROW, 4, false),
            new FletchingRecipe("explosive_arrow", Items.GUNPOWDER, ModItems.EXPLOSIVE_ARROW, 4, false),
            new FletchingRecipe("tidal_arrow", Items.HEART_OF_THE_SEA, ModItems.TIDAL_ARROW, 4, false),
            new FletchingRecipe("shattering_arrow", Items.AMETHYST_SHARD, ModItems.SHATTERING_ARROW, 4, false),
            new FletchingRecipe("echo_arrow", Items.ECHO_SHARD, ModItems.ECHO_ARROW, 4, false),
            new FletchingRecipe("tipped_arrow", Items.POTION, Items.TIPPED_ARROW, 8, true)
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

    /**
     * Allows any ArrowItem by default, including arrows registered by other
     * Fabric mods. Servers can limit the Fletching Table back to Minecraft's
     * native arrow items without affecting quiver or projectile behaviour.
     */
    public static boolean isAcceptedArrowInput(ItemStack stack) {
        if (!(stack.getItem() instanceof ArrowItem)) {
            return false;
        }
        return ConfigManager.allowsModdedFletchingArrowInputs()
                || "minecraft".equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace());
    }

    /** True only for an item stack that can occupy the modifier slot. */
    public static boolean isModifier(ItemStack stack) {
        for (FletchingRecipe recipe : RECIPES) {
            if (stack.is(recipe.ingredient())
                    && (!recipe.copiesPotion() || stack.has(DataComponents.POTION_CONTENTS))) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack createOutput(FletchingRecipe recipe, ItemStack ingredientStack) {
        // Match vanilla's tipped-arrow conversion: eight arrows plus a potion
        // produces eight tipped arrows. Special-arrow batches remain governed
        // by the Fletching output config (four by default).
        int outputCount = recipe.copiesPotion()
                ? 8
                : Math.max(1, ConfigManager.getFletchingRecipeOutputCount());
        ItemStack output = new ItemStack(recipe.outputItem(), outputCount);
        if (recipe.copiesPotion() && ingredientStack.has(DataComponents.POTION_CONTENTS)) {
            PotionContents potion = ingredientStack.get(DataComponents.POTION_CONTENTS);
            if (potion != null) {
                output.set(DataComponents.POTION_CONTENTS, potion);
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
            if (!isAcceptedArrowInput(arrowStack)
                    || arrowStack.getCount() < arrowCount
                    || !ingredientStack.is(ingredient)) {
                return false;
            }
            return !copiesPotion || ingredientStack.has(DataComponents.POTION_CONTENTS);
        }

        public Component displayName() {
            return Component.translatable("container.archery-revamped.recipe." + name);
        }
    }
}
