package me.lemonboi439.archeryRevamped.debug;

import me.lemonboi439.archeryRevamped.network.ModNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.PacketDistributor;

/** Synchronizes the admin trajectory toggle to clients for live aiming previews. */
public final class TrajectoryNetworking {
    private TrajectoryNetworking() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                send(player, TrajectoryVisualizer.isEnabled(),
                        TrajectoryVisualizer.isColourVisualisationEnabled());
            }
        });
    }

    public static void send(ServerPlayer player, boolean enabled) {
        send(player, enabled, TrajectoryVisualizer.isColourVisualisationEnabled());
    }

    public static void send(ServerPlayer player, boolean enabled, boolean colourVisualisation) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new ModNetwork.TrajectoryStatePacket(enabled, colourVisualisation));
    }
}
