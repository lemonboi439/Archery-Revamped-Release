package me.lemonboi439.archeryRevamped.screen;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ModScreenHandlers {
    public static final MenuType<FletchingTableScreenHandler> FLETCHING_TABLE = Registry.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "fletching_table"),
            new MenuType<>(FletchingTableScreenHandler::new, FeatureFlags.VANILLA_SET)
    );
    public static final MenuType<QuiverScreenHandler> QUIVER = Registry.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "quiver"),
            new MenuType<>(QuiverScreenHandler::new, FeatureFlags.VANILLA_SET)
    );

    private ModScreenHandlers() {
    }

    public static void register() {
    }
}
