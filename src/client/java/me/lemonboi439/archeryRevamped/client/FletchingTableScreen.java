package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.screen.FletchingTableScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class FletchingTableScreen extends HandledScreen<FletchingTableScreenHandler> {
    private static final int BACKGROUND_WIDTH = 176;
    private static final int BACKGROUND_HEIGHT = 166;
    private static final Identifier TEXTURE = Identifier.of(
            "archery-revamped", "textures/gui/fletching_table.png"
    );

    public FletchingTableScreen(FletchingTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = BACKGROUND_WIDTH;
        this.backgroundHeight = BACKGROUND_HEIGHT;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int left = this.x;
        int top = this.y;
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                left,
                top,
                0.0F,
                0.0F,
                BACKGROUND_WIDTH,
                BACKGROUND_HEIGHT,
                256,
                256
        );

        int maxProgress = this.handler.getMaxProgress();
        int progress = Math.min(this.handler.getProgress(), maxProgress);
        drawCraftingArrow(context, left + 99, top + 42, progress, maxProgress);
    }

    private static void drawCraftingArrow(DrawContext context, int x, int y, int progress, int maxProgress) {
        context.fill(x, y + 6, x + 24, y + 10, 0xFF4B423B);
        if (maxProgress <= 0 || progress <= 0) {
            return;
        }

        int headX = x + Math.min(18, progress * 18 / maxProgress);
        int color = progress >= maxProgress ? 0xFF8FCB58 : 0xFF6A9E4B;
        context.fill(x, y + 7, headX, y + 9, color);
        context.fill(headX, y + 5, headX + 3, y + 11, color);
        context.fill(headX + 3, y + 3, headX + 5, y + 13, color);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, 8, 6, 0x404040, false);
        Text recipeName = this.handler.getRecipeName();
        if (!recipeName.getString().isEmpty()) {
            context.drawText(this.textRenderer, recipeName, 8, 70, 0x404040, false);
        }
    }
}
