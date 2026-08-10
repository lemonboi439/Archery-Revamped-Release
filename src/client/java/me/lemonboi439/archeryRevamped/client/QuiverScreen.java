package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.screen.QuiverScreenHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Compact inventory view; click an occupied quiver slot to make it active. */
public final class QuiverScreen extends AbstractContainerScreen<QuiverScreenHandler> {
    public QuiverScreen(QuiverScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        context.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xFF1D1A16);
        context.fill(this.leftPos + 1, this.topPos + 1, this.leftPos + this.imageWidth - 1, this.topPos + this.imageHeight - 1,
                0xFFC6B28A);
        context.fill(this.leftPos + 3, this.topPos + 3, this.leftPos + this.imageWidth - 3, this.topPos + 78, 0xFF534634);
        int selected = this.menu.getSelectedSlot();
        int slotX = this.leftPos + 61 + (selected % 3) * 18;
        int slotY = this.topPos + 17 + (selected / 3) * 18;
        context.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFFF7D95A);
        context.fill(slotX + 2, slotY + 2, slotX + 16, slotY + 16, 0xFF6B5A39);
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        context.drawString(this.font, this.title, 8, 6, 0xFF2E241B, false);
        context.drawString(this.font, Component.translatable("container.archery-revamped.quiver.selected"),
                8, 68, 0xFF2E241B, false);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);
    }
}
