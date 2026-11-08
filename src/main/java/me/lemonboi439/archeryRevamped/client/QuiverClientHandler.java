package me.lemonboi439.archeryRevamped.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

public final class QuiverClientHandler {
    private static final KeyMapping OPEN_SELECTOR = new KeyMapping(
            "key.archery-revamped.quiver_selector",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.misc"
    );

    private QuiverClientHandler() {
    }

    public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
        event.register(OPEN_SELECTOR);
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                tick(Minecraft.getInstance());
            }
        });
    }

    private static void tick(Minecraft client) {
        // KeyMapping#isPressed reads a flag that opening a screen clears, which would close and
        // reopen the selector every other tick. Poll the physical state of the configured binding
        // so the Controls screen drives the selector instead of a hardcoded V. A mouse binding
        // falls back to the flag, since only keyboard state can be polled here.
        boolean selectorHeld = isSelectorHeld(client);
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
    }

    private static boolean isSelectorHeld(Minecraft client) {
        InputConstants.Key bound = OPEN_SELECTOR.getKey();
        if (client.getWindow() == null || bound.getType() != InputConstants.Type.KEYSYM) {
            return OPEN_SELECTOR.isDown();
        }
        return InputConstants.isKeyDown(client.getWindow().getWindow(), bound.getValue());
    }
}
