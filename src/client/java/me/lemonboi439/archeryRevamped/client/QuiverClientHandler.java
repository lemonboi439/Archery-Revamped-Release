package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class QuiverClientHandler {
    private static final KeyBinding OPEN_SELECTOR = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.archery-revamped.quiver_selector",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.misc"
    ));

    private QuiverClientHandler() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean selectorHeld = client.getWindow() != null
                    && GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_V) == GLFW.GLFW_PRESS;
            if (client.currentScreen instanceof QuiverRadialScreen selector) {
                if (!selectorHeld) {
                    selector.confirmSelection();
                }
                return;
            }
            if (selectorHeld && client.player != null && client.currentScreen == null
                    && !QuiverManager.getActiveQuiver(client.player).isEmpty()) {
                client.setScreen(new QuiverRadialScreen());
            }
        });
    }
}
