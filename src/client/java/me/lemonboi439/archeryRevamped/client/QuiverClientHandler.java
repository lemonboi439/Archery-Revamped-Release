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
            "key.categories.archery-revamped"
    ));

    private QuiverClientHandler() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_SELECTOR.wasPressed()) {
                if (client.player != null && client.currentScreen == null
                        && !QuiverManager.getActiveQuiver(client.player).isEmpty()) {
                    client.setScreen(new QuiverRadialScreen());
                }
            }
        });
    }
}
