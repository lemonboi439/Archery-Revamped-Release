package me.lemonboi439.archeryRevamped.screen;

import net.fabricmc.loader.api.FabricLoader;

import java.util.List;

/**
 * Optional recipe-viewer compatibility surface.
 *
 * The mod does not hard-depend on JEI, REI, or EMI. Their APIs are client-side
 * and version-specific, so integrations can consume the stable recipe list
 * exposed by {@link FletchingRecipeRegistry} without loading viewer classes
 * when the viewer is absent. The custom arrow items themselves remain normal
 * registered items, so all three viewers can index them safely.
 */
public final class RecipeViewerCompat {
    private static final List<String> VIEWER_IDS = List.of("jei", "roughlyenoughitems", "emi");

    private RecipeViewerCompat() {
    }

    public static void register() {
        // Touching only FabricLoader here is intentional: no optional viewer
        // class is resolved during dedicated-server startup.
        VIEWER_IDS.stream()
                .filter(id -> FabricLoader.getInstance().isModLoaded(id))
                .findFirst();
    }

    public static boolean isViewerLoaded() {
        return VIEWER_IDS.stream().anyMatch(id -> FabricLoader.getInstance().isModLoaded(id));
    }

    public static List<FletchingRecipeRegistry.FletchingRecipe> getFletchingRecipes() {
        return FletchingRecipeRegistry.getRecipes();
    }
}
