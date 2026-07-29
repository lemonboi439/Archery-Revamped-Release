package me.lemonboi439.archeryRevamped.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.lemonboi439.archeryRevamped.client.config.ArcheryRevampedConfigScreen;
import net.fabricmc.loader.api.FabricLoader;

/** Optional Mod Menu bridge for the Cloth Config screen. */
public final class ArcheryRevampedModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return null;
        }
        return ArcheryRevampedConfigScreen::create;
    }
}
