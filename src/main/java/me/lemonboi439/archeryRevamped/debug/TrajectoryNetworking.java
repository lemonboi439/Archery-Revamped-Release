package me.lemonboi439.archeryRevamped.debug;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/** Synchronizes the admin trajectory toggle using Fabric's 1.20.1 packet API. */
public final class TrajectoryNetworking {
    public static final Identifier ID = new Identifier(ArcheryRevamped.MOD_ID, "trajectory_state");

    private TrajectoryNetworking() {
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                send(handler.player, TrajectoryVisualizer.isEnabled(),
                        TrajectoryVisualizer.isColourVisualisationEnabled()));
    }

    public static void send(ServerPlayerEntity player, boolean enabled) {
        send(player, enabled, TrajectoryVisualizer.isColourVisualisationEnabled());
    }

    public static void send(ServerPlayerEntity player, boolean enabled, boolean colourVisualisation) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBoolean(enabled);
        buffer.writeBoolean(colourVisualisation);
        ServerPlayNetworking.send(player, ID, buffer);
    }
}
