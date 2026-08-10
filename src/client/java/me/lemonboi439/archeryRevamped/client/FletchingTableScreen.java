package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.screen.FletchingTableScreenHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class FletchingTableScreen extends AbstractContainerScreen<FletchingTableScreenHandler> {
    private static final int BACKGROUND_WIDTH = 176;
    private static final int BACKGROUND_HEIGHT = 166;
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            "archery-revamped", "textures/gui/fletching_table.png"
    );

    public FletchingTableScreen(FletchingTableScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractBackground(context, mouseX, mouseY, delta);
        context.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.leftPos,
                this.topPos,
                0.0F,
                0.0F,
                BACKGROUND_WIDTH,
                BACKGROUND_HEIGHT,
                256,
                256
        );
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor context, int mouseX, int mouseY) {
        // The texture intentionally has no baked title, so it stays legible
        // in every language and remains independent from texture edits.
        context.text(this.font, Component.translatable("container.archery-revamped.fletching_table"),
                53, 6, 0x404040, false);
        Component recipeName = this.menu.getRecipeName();
        if (!recipeName.getString().isEmpty()) {
            context.text(this.font, recipeName, 8, 70, 0x404040, false);
        }
    }
}
