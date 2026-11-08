package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import me.lemonboi439.archeryRevamped.client.config.ArcheryRevampedConfigScreen;
import me.lemonboi439.archeryRevamped.client.render.ArcheryArrowEntityRenderer;
import me.lemonboi439.archeryRevamped.client.render.ClientTrajectoryPreview;
import me.lemonboi439.archeryRevamped.client.render.ShatteringShardEntityRenderer;
import me.lemonboi439.archeryRevamped.entity.ModEntities;
import me.lemonboi439.archeryRevamped.screen.ModScreenHandlers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = ArcheryRevamped.FORGE_MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class ArcheryRevampedClient {
    private ArcheryRevampedClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModScreenHandlers.FLETCHING_TABLE, FletchingTableScreen::new);
            MenuScreens.register(ModScreenHandlers.QUIVER, QuiverScreen::new);
            QuiverClientHandler.register();
            OverdrawClientHandler.register();
            ClientTrajectoryPreview.register();

            if (ModList.get().isLoaded("cloth_config")) {
                ModLoadingContext.get().registerExtensionPoint(
                        ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory(ArcheryRevampedConfigScreen::create));
            }
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ARCHERY_ARROW, ArcheryArrowEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.SHATTERING_SHARD, ShatteringShardEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        QuiverClientHandler.registerKeyMapping(event);
    }
}
