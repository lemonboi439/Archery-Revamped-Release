package me.lemonboi439.archeryRevamped.network;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

/** Forge SimpleChannel replacement for the Fabric packet API. */
public final class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ArcheryRevamped.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int nextId;

    private ModNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(nextId++, TrajectoryStatePacket.class,
                TrajectoryStatePacket::encode, TrajectoryStatePacket::decode,
                TrajectoryStatePacket::handle);
        CHANNEL.registerMessage(nextId++, CycleQuiverPacket.class,
                CycleQuiverPacket::encode, CycleQuiverPacket::decode,
                CycleQuiverPacket::handle);
        CHANNEL.registerMessage(nextId++, SelectQuiverSlotPacket.class,
                SelectQuiverSlotPacket::encode, SelectQuiverSlotPacket::decode,
                SelectQuiverSlotPacket::handle);
    }

    public record TrajectoryStatePacket(boolean enabled, boolean colourVisualisation) {
        public static void encode(TrajectoryStatePacket packet, FriendlyByteBuf buffer) {
            buffer.writeBoolean(packet.enabled);
            buffer.writeBoolean(packet.colourVisualisation);
        }

        public static TrajectoryStatePacket decode(FriendlyByteBuf buffer) {
            return new TrajectoryStatePacket(buffer.readBoolean(), buffer.readBoolean());
        }

        public static void handle(TrajectoryStatePacket packet, Supplier<NetworkEvent.Context> context) {
            NetworkEvent.Context ctx = context.get();
            ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> me.lemonboi439.archeryRevamped.client.ClientPacketHandler.handleTrajectory(
                            packet.enabled, packet.colourVisualisation)));
            ctx.setPacketHandled(true);
        }
    }

    public record CycleQuiverPacket() {
        public static void encode(CycleQuiverPacket packet, FriendlyByteBuf buffer) {
        }

        public static CycleQuiverPacket decode(FriendlyByteBuf buffer) {
            return new CycleQuiverPacket();
        }

        public static void handle(CycleQuiverPacket packet, Supplier<NetworkEvent.Context> context) {
            NetworkEvent.Context ctx = context.get();
            ctx.enqueueWork(() -> {
                ServerPlayer player = ctx.getSender();
                if (player != null) {
                    QuiverManager.cycleSelectedSlot(player);
                }
            });
            ctx.setPacketHandled(true);
        }
    }

    public record SelectQuiverSlotPacket(int slot) {
        public static void encode(SelectQuiverSlotPacket packet, FriendlyByteBuf buffer) {
            buffer.writeVarInt(packet.slot);
        }

        public static SelectQuiverSlotPacket decode(FriendlyByteBuf buffer) {
            return new SelectQuiverSlotPacket(buffer.readVarInt());
        }

        public static void handle(SelectQuiverSlotPacket packet, Supplier<NetworkEvent.Context> context) {
            NetworkEvent.Context ctx = context.get();
            ctx.enqueueWork(() -> {
                ServerPlayer player = ctx.getSender();
                if (player == null) {
                    return;
                }
                var quiver = QuiverManager.getActiveQuiver(player);
                if (!quiver.isEmpty()) {
                    QuiverManager.setSelectedSlot(quiver, packet.slot);
                }
            });
            ctx.setPacketHandled(true);
        }
    }
}
