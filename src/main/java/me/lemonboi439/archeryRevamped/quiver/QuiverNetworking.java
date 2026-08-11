package me.lemonboi439.archeryRevamped.quiver;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

/** Client keybind requests for the pre-custom-payload 1.20.1 networking API. */
public final class QuiverNetworking {
    public static final Identifier CYCLE_ID = new Identifier(ArcheryRevamped.MOD_ID, "cycle_quiver");
    public static final Identifier SELECT_ID = new Identifier(ArcheryRevamped.MOD_ID, "select_quiver_slot");

    private QuiverNetworking() {
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(CYCLE_ID, (server, player, handler, buffer, responseSender) ->
                server.execute(() -> QuiverManager.cycleSelectedSlot(player)));
        ServerPlayNetworking.registerGlobalReceiver(SELECT_ID, (server, player, handler, buffer, responseSender) -> {
            int slot = buffer.readVarInt();
            server.execute(() -> {
                var quiver = QuiverManager.getActiveQuiver(player);
                if (!quiver.isEmpty()) QuiverManager.setSelectedSlot(quiver, slot);
            });
        });
    }
}
