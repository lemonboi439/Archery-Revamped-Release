package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.screen.FletchingTableScreenHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class FletchingTableScreen extends AbstractContainerScreen<FletchingTableScreenHandler> {
    private static final int BACKGROUND_WIDTH = 176;
    private static final int BACKGROUND_HEIGHT = 166;
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "archery-revamped", "textures/gui/fletching_table.png"
    );

    public FletchingTableScreen(FletchingTableScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageWidth = BACKGROUND_WIDTH;
        this.imageHeight = BACKGROUND_HEIGHT;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;
        context.blit(TEXTURE, left, top, 0, 0,
                BACKGROUND_WIDTH,
                BACKGROUND_HEIGHT,
                256,
                256
        );
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        // The final texture has no baked title; centre it in the header area.
        context.drawString(this.font, this.title, 53, 6, 0x404040, false);
        Component recipeName = this.menu.getRecipeName();
        if (!recipeName.getString().isEmpty()) {
            context.drawString(this.font, recipeName, 8, 70, 0x404040, false);
        }
    }
}
