package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
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
            // KeyBinding#isPressed reads a flag that opening a screen clears, which would close and
            // reopen the selector every other tick. Poll the physical state of the configured binding
            // so the Controls screen drives the selector instead of a hardcoded V. A mouse binding
            // falls back to the flag, since only keyboard state can be polled here.
            boolean selectorHeld = isSelectorHeld(client);
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

    private static boolean isSelectorHeld(MinecraftClient client) {
        InputUtil.Key bound = KeyBindingHelper.getBoundKeyOf(OPEN_SELECTOR);
        if (client.getWindow() == null || bound.getCategory() != InputUtil.Type.KEYSYM) {
            return OPEN_SELECTOR.isPressed();
        }
        return InputUtil.isKeyPressed(client.getWindow().getHandle(), bound.getCode());
    }
}
