package me.lemonboi439.archeryRevamped.debug;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/** Synchronizes the admin trajectory toggle to clients for live aiming previews. */
public final class TrajectoryNetworking {
    private TrajectoryNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(TrajectoryStatePayload.ID, TrajectoryStatePayload.CODEC);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                send(handler.player, TrajectoryVisualizer.isEnabled(),
                        TrajectoryVisualizer.isColourVisualisationEnabled()));
    }

    public static void send(ServerPlayerEntity player, boolean enabled) {
        send(player, enabled, TrajectoryVisualizer.isColourVisualisationEnabled());
    }

    public static void send(ServerPlayerEntity player, boolean enabled, boolean colourVisualisation) {
        if (ServerPlayNetworking.canSend(player, TrajectoryStatePayload.ID)) {
            ServerPlayNetworking.send(player, new TrajectoryStatePayload(enabled, colourVisualisation));
        }
    }

    public record TrajectoryStatePayload(boolean enabled, boolean colourVisualisation) implements CustomPayload {
        public static final CustomPayload.Id<TrajectoryStatePayload> ID =
                new CustomPayload.Id<>(Identifier.of(ArcheryRevamped.MOD_ID, "trajectory_state"));
        public static final PacketCodec<RegistryByteBuf, TrajectoryStatePayload> CODEC =
                PacketCodec.of((value, buf) -> {
                            buf.writeBoolean(value.enabled);
                            buf.writeBoolean(value.colourVisualisation);
                        },
                        buf -> new TrajectoryStatePayload(buf.readBoolean(), buf.readBoolean()));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
