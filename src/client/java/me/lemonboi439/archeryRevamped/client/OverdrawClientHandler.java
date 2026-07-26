package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.enchantment.OverdrawEnchantment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;

public final class OverdrawClientHandler {
    private static float vignette;
    private static float shakeIntensity;

    private OverdrawClientHandler() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(OverdrawClientHandler::tick);
        HudRenderCallback.EVENT.register(OverdrawClientHandler::renderVignette);
    }

    public static float getShakeIntensity() {
        return shakeIntensity;
    }

    private static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            vignette = 0.0F;
            shakeIntensity = 0.0F;
            return;
        }

        ItemStack activeStack = client.player.getActiveItem();
        if (!client.player.isUsingItem() || activeStack.isEmpty()
                || !(activeStack.getItem() instanceof BowItem)) {
            fadeEffects();
            return;
        }

        var enchantments = client.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        int level = enchantments.getOptional(OverdrawEnchantment.KEY)
                .map(entry -> EnchantmentHelper.getLevel(entry, activeStack))
                .map(value -> Math.min(value, OverdrawEnchantment.MAX_LEVEL))
                .orElse(0);
        int overdrawTicks = client.player.getItemUseTime() - 20;
        if (level <= 0 || overdrawTicks <= 0) {
            fadeEffects();
            return;
        }

        float progress = Math.min(1.0F, overdrawTicks / 100.0F);
        float target = Math.min(1.0F, progress * level);
        vignette += (target - vignette) * 0.2F;
        shakeIntensity += (target * 1.5F - shakeIntensity) * 0.2F;
    }

    private static void fadeEffects() {
        vignette *= 0.8F;
        shakeIntensity *= 0.75F;
        if (vignette < 0.001F) vignette = 0.0F;
        if (shakeIntensity < 0.001F) shakeIntensity = 0.0F;
    }

    private static void renderVignette(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
        if (vignette <= 0.001F) return;

        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        int alpha = Math.min(130, 20 + (int) (vignette * 110.0F));
        int color = (alpha << 24) | 0xAA0000;
        int border = Math.max(8, (int) (Math.min(width, height) * 0.08F));
        context.fill(0, 0, width, border, color);
        context.fill(0, height - border, width, height, color);
        context.fill(0, border, border, height - border, color);
        context.fill(width - border, border, width, height - border, color);
    }
}
