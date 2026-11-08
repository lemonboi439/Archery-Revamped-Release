package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.screen.FletchingTableScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class FletchingTableScreen extends HandledScreen<FletchingTableScreenHandler> {
    private static final int BACKGROUND_WIDTH = 176;
    private static final int BACKGROUND_HEIGHT = 166;
    private static final Identifier TEXTURE = new Identifier(
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
        context.drawTexture(TEXTURE, left, top, 0, 0,
                BACKGROUND_WIDTH,
                BACKGROUND_HEIGHT,
                256,
                256
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // The final texture has no baked title; centre it in the header area.
        context.drawText(this.textRenderer, this.title, 53, 6, 0x404040, false);
        Text recipeName = this.handler.getRecipeName();
        if (!recipeName.getString().isEmpty()) {
            context.drawText(this.textRenderer, recipeName, 8, 70, 0x404040, false);
        }
    }
}
