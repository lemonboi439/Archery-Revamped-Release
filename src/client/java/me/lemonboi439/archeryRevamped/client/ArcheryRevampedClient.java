package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.client.render.ArcheryArrowEntityRenderer;
import me.lemonboi439.archeryRevamped.client.render.ShatteringShardEntityRenderer;
import me.lemonboi439.archeryRevamped.client.render.ClientTrajectoryPreview;
import me.lemonboi439.archeryRevamped.debug.TrajectoryNetworking;
import me.lemonboi439.archeryRevamped.entity.ModEntities;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import me.lemonboi439.archeryRevamped.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ArcheryRevampedClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.ARCHERY_ARROW, ArcheryArrowEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.SHATTERING_SHARD, ShatteringShardEntityRenderer::new);
        HandledScreens.register(ModScreenHandlers.FLETCHING_TABLE, FletchingTableScreen::new);
        HandledScreens.register(ModScreenHandlers.QUIVER, QuiverScreen::new);
        QuiverClientHandler.register();
        OverdrawClientHandler.register();
        ClientTrajectoryPreview.register();
        ClientPlayNetworking.registerGlobalReceiver(TrajectoryNetworking.ID,
                (client, handler, buffer, responseSender) -> {
                    boolean enabled = buffer.readBoolean();
                    boolean colourVisualisation = buffer.readBoolean();
                    client.execute(() -> ClientTrajectoryPreview.setTrajectoryState(enabled, colourVisualisation));
                });
    }
}
