package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.client.render.ArcheryArrowEntityRenderer;
import me.lemonboi439.archeryRevamped.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ArcheryRevampedClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.ARCHERY_ARROW, ArcheryArrowEntityRenderer::new);
        OverdrawClientHandler.register();
    }
}
