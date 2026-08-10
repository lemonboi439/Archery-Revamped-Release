package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.client.render.ArcheryArrowEntityRenderer;
import me.lemonboi439.archeryRevamped.client.render.ShatteringShardEntityRenderer;
import me.lemonboi439.archeryRevamped.client.render.ClientTrajectoryPreview;
import me.lemonboi439.archeryRevamped.debug.TrajectoryNetworking;
import me.lemonboi439.archeryRevamped.entity.ModEntities;
import me.lemonboi439.archeryRevamped.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class ArcheryRevampedClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.ARCHERY_ARROW, ArcheryArrowEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.SHATTERING_SHARD, ShatteringShardEntityRenderer::new);
        MenuScreens.register(ModScreenHandlers.FLETCHING_TABLE, FletchingTableScreen::new);
        QuiverClientHandler.register();
        ClientTrajectoryPreview.register();
        ClientPlayNetworking.registerGlobalReceiver(TrajectoryNetworking.TrajectoryStatePayload.ID,
                (payload, context) -> context.client().execute(() ->
                        ClientTrajectoryPreview.setTrajectoryState(payload.enabled(), payload.colourVisualisation())));
    }
}
