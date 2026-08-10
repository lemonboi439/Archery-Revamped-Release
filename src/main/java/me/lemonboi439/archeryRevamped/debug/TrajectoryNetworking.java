package me.lemonboi439.archeryRevamped.debug;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/** Synchronizes the admin trajectory toggle to clients for live aiming previews. */
public final class TrajectoryNetworking {
    private TrajectoryNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(TrajectoryStatePayload.ID, TrajectoryStatePayload.CODEC);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                send(handler.player, TrajectoryVisualizer.isEnabled(),
                        TrajectoryVisualizer.isColourVisualisationEnabled()));
    }

    public static void send(ServerPlayer player, boolean enabled) {
        send(player, enabled, TrajectoryVisualizer.isColourVisualisationEnabled());
    }

    public static void send(ServerPlayer player, boolean enabled, boolean colourVisualisation) {
        if (ServerPlayNetworking.canSend(player, TrajectoryStatePayload.ID)) {
            ServerPlayNetworking.send(player, new TrajectoryStatePayload(enabled, colourVisualisation));
        }
    }

    public record TrajectoryStatePayload(boolean enabled, boolean colourVisualisation) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<TrajectoryStatePayload> ID =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "trajectory_state"));
        public static final StreamCodec<RegistryFriendlyByteBuf, TrajectoryStatePayload> CODEC =
                StreamCodec.ofMember((value, buf) -> {
                            buf.writeBoolean(value.enabled);
                            buf.writeBoolean(value.colourVisualisation);
                        },
                        buf -> new TrajectoryStatePayload(buf.readBoolean(), buf.readBoolean()));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }
}
