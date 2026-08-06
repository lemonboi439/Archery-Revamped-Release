package me.lemonboi439.archeryRevamped.quiver;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Client keybind request for cycling the selected quiver slot. */
public final class QuiverNetworking {
    private QuiverNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(CycleQuiverPayload.ID, CycleQuiverPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SelectQuiverSlotPayload.ID, SelectQuiverSlotPayload.CODEC);
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

    public record CycleQuiverPayload() implements CustomPayload {
        public static final CustomPayload.Id<CycleQuiverPayload> ID =
                new CustomPayload.Id<>(Identifier.of(ArcheryRevamped.MOD_ID, "cycle_quiver"));
        public static final PacketCodec<RegistryByteBuf, CycleQuiverPayload> CODEC =
                PacketCodec.of((value, buf) -> { }, buf -> new CycleQuiverPayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record SelectQuiverSlotPayload(int slot) implements CustomPayload {
        public static final CustomPayload.Id<SelectQuiverSlotPayload> ID =
                new CustomPayload.Id<>(Identifier.of(ArcheryRevamped.MOD_ID, "select_quiver_slot"));
        public static final PacketCodec<RegistryByteBuf, SelectQuiverSlotPayload> CODEC =
                PacketCodec.of((value, buf) -> buf.writeVarInt(value.slot),
                        buf -> new SelectQuiverSlotPayload(buf.readVarInt()));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
