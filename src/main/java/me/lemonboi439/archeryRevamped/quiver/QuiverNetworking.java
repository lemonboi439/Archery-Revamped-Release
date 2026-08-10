package me.lemonboi439.archeryRevamped.quiver;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Client keybind request for cycling the selected quiver slot. */
public final class QuiverNetworking {
    private QuiverNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(CycleQuiverPayload.ID, CycleQuiverPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SelectQuiverSlotPayload.ID, SelectQuiverSlotPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(CycleQuiverPayload.ID,
                (payload, context) -> context.server().execute(
                        () -> QuiverManager.cycleSelectedSlot(context.player())));
        ServerPlayNetworking.registerGlobalReceiver(SelectQuiverSlotPayload.ID,
                (payload, context) -> context.server().execute(() -> {
                    var quiver = QuiverManager.getActiveQuiver(context.player());
                    if (!quiver.isEmpty()) {
                        QuiverManager.setSelectedSlot(quiver, payload.slot());
                    }
                }));
    }

    public record CycleQuiverPayload() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<CycleQuiverPayload> ID =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "cycle_quiver"));
        public static final StreamCodec<RegistryFriendlyByteBuf, CycleQuiverPayload> CODEC =
                StreamCodec.ofMember((value, buf) -> { }, buf -> new CycleQuiverPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }

    public record SelectQuiverSlotPayload(int slot) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<SelectQuiverSlotPayload> ID =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "select_quiver_slot"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SelectQuiverSlotPayload> CODEC =
                StreamCodec.ofMember((value, buf) -> buf.writeVarInt(value.slot),
                        buf -> new SelectQuiverSlotPayload(buf.readVarInt()));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }
}
