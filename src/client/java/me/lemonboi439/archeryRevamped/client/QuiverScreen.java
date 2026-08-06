package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.screen.QuiverScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/** Compact inventory view; click an occupied quiver slot to make it active. */
public final class QuiverScreen extends HandledScreen<QuiverScreenHandler> {
    public QuiverScreen(QuiverScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.fill(this.x, this.y, this.x + this.backgroundWidth, this.y + this.backgroundHeight, 0xFF1D1A16);
        context.fill(this.x + 1, this.y + 1, this.x + this.backgroundWidth - 1, this.y + this.backgroundHeight - 1,
                0xFFC6B28A);
        context.fill(this.x + 3, this.y + 3, this.x + this.backgroundWidth - 3, this.y + 78, 0xFF534634);
        int selected = this.handler.getSelectedSlot();
        int slotX = this.x + 61 + (selected % 3) * 18;
        int slotY = this.y + 17 + (selected / 3) * 18;
        context.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFFF7D95A);
        context.fill(slotX + 2, slotY + 2, slotX + 16, slotY + 16, 0xFF6B5A39);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, 8, 6, 0xFF2E241B, false);
        context.drawText(this.textRenderer, Text.translatable("container.archery-revamped.quiver.selected"),
                8, 68, 0xFF2E241B, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
