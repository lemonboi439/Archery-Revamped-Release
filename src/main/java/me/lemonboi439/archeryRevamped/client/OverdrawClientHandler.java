package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.enchantment.OverdrawEnchantment;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class OverdrawClientHandler {
    private static float vignette;
    private static float shakeIntensity;

    private OverdrawClientHandler() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                tick(Minecraft.getInstance());
            }
        });
        MinecraftForge.EVENT_BUS.addListener((RenderGuiEvent.Post event) ->
                renderVignette(event.getGuiGraphics()));
    }

    public static float getShakeIntensity() {
        return shakeIntensity;
    }

    private static void tick(Minecraft client) {
        if (client.player == null || client.level == null) {
            vignette = 0.0F;
            shakeIntensity = 0.0F;
            return;
        }

        ItemStack activeStack = client.player.getUseItem();
        if (!client.player.isUsingItem() || activeStack.isEmpty()
                || !(activeStack.getItem() instanceof BowItem)) {
            fadeEffects();
            return;
        }

        int level = ConfigManager.limitEnchantmentLevel(
                EnchantmentHelper.getItemEnchantmentLevel(OverdrawEnchantment.ENCHANTMENT, activeStack),
                OverdrawEnchantment.MAX_LEVEL);
        int overdrawTicks = client.player.getTicksUsingItem() - 20;
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

    private static void renderVignette(GuiGraphics context) {
        if (vignette <= 0.001F) return;

        int width = context.guiWidth();
        int height = context.guiHeight();
        int alpha = Math.min(130, 20 + (int) (vignette * 110.0F));
        int color = (alpha << 24) | 0xAA0000;
        int border = Math.max(8, (int) (Math.min(width, height) * 0.08F));
        context.fill(0, 0, width, border, color);
        context.fill(0, height - border, width, height, color);
        context.fill(0, border, border, height - border, color);
        context.fill(width - border, border, width, height - border, color);
    }
}
