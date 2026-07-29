package me.lemonboi439.archeryRevamped.screen;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public final class ModScreenHandlers {
    public static final ScreenHandlerType<FletchingTableScreenHandler> FLETCHING_TABLE = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(ArcheryRevamped.MOD_ID, "fletching_table"),
            new ScreenHandlerType<>(FletchingTableScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
    );

    private ModScreenHandlers() {
    }

    public static void register() {
    }
}
