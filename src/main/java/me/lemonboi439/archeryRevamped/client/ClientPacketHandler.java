package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.client.render.ClientTrajectoryPreview;
import net.minecraft.client.Minecraft;

/** Client-only packet handlers, loaded only when the client receives a packet. */
public final class ClientPacketHandler {
    private ClientPacketHandler() {
    }

    public static void handleTrajectory(boolean enabled, boolean colourVisualisation) {
        Minecraft.getInstance().execute(
                () -> ClientTrajectoryPreview.setTrajectoryState(enabled, colourVisualisation));
    }
}
