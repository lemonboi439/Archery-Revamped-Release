package me.lemonboi439.archeryRevamped.screen;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ModScreenHandlers {
    public static MenuType<FletchingTableScreenHandler> FLETCHING_TABLE;
    public static MenuType<QuiverScreenHandler> QUIVER;

    private ModScreenHandlers() {
    }

    public static void register() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModScreenHandlers::onRegister);
    }

    private static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.MENU_TYPES)) {
            return;
        }

        ResourceLocation fletchingId = new ResourceLocation(ArcheryRevamped.MOD_ID, "fletching_table");
        ResourceLocation quiverId = new ResourceLocation(ArcheryRevamped.MOD_ID, "quiver");
        FLETCHING_TABLE = new MenuType<>(FletchingTableScreenHandler::new, FeatureFlags.VANILLA_SET);
        QUIVER = new MenuType<>(QuiverScreenHandler::new, FeatureFlags.VANILLA_SET);

        event.register(ForgeRegistries.Keys.MENU_TYPES, helper -> {
            helper.register(fletchingId, FLETCHING_TABLE);
            helper.register(quiverId, QUIVER);
        });
    }
}
