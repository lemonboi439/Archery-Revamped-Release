package me.lemonboi439.archeryRevamped.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class QuiverClientHandler {
    private static final KeyMapping OPEN_SELECTOR = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.archery-revamped.quiver_selector",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "archery_revamped"))
    ));

    private QuiverClientHandler() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Query the physical key state. KeyBinding#isPressed can be cleared while
            // a Screen receives the same V key event, causing the selector to
            // close and reopen every other client tick (a visible flash).
            boolean selectorHeld = client.getWindow() != null
                    && GLFW.glfwGetKey(client.getWindow().handle(), GLFW.GLFW_KEY_V) == GLFW.GLFW_PRESS;
            if (client.screen instanceof QuiverRadialScreen selector) {
                if (!selectorHeld) {
                    selector.confirmSelection();
                }
                return;
            }

            if (selectorHeld && client.player != null && client.screen == null
                    && !QuiverManager.getActiveQuiver(client.player).isEmpty()) {
                client.setScreen(new QuiverRadialScreen());
            }
        });
    }
}
