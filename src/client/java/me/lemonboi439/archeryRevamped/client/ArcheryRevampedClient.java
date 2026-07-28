package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.client.render.ArcheryArrowEntityRenderer;
import me.lemonboi439.archeryRevamped.entity.ModEntities;
import me.lemonboi439.archeryRevamped.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ArcheryRevampedClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.ARCHERY_ARROW, ArcheryArrowEntityRenderer::new);
        HandledScreens.register(ModScreenHandlers.FLETCHING_TABLE, FletchingTableScreen::new);
        OverdrawClientHandler.register();
    }
}
