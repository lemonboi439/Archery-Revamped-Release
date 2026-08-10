package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import me.lemonboi439.archeryRevamped.quiver.QuiverNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/** Original nine-way radial selector inspired by builder-mod usability, not copied code or assets. */
public final class QuiverRadialScreen extends Screen {
    private static final int RADIUS = 62;
    private int pendingSlot = -1;

    public QuiverRadialScreen() {
        super(Component.translatable("container.archery-revamped.quiver"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        List<ItemStack> contents = QuiverManager.getContents(QuiverManager.getActiveQuiver(client.player));
        int selected = getPendingSlot(contents, QuiverManager.getActiveQuiver(client.player));
        int hovered = getHoveredSlot(mouseX, mouseY, centerX, centerY);
        // Keep the centre panel intentionally compact so it cannot overlap the radial slots.
        context.fill(centerX - 36, centerY - 33, centerX + 36, centerY + 37, 0xD01B1712);
        context.centeredText(this.font, Component.translatable("container.archery-revamped.quiver"),
                centerX, centerY - 28, 0xFFFFFFFF);
        ItemStack selectedStack = selected >= 0 ? contents.get(selected) : ItemStack.EMPTY;
        if (!selectedStack.isEmpty()) {
            context.item(selectedStack, centerX - 8, centerY - 10);
            drawScaledCenteredText(context, Component.literal(getFittingName(selectedStack, 86)), centerX, centerY + 8,
                    0xFFD5C8AC, 0.75F);
        } else {
            context.centeredText(this.font, Component.literal("No arrow selected"), centerX,
                    centerY + 2, 0xFFD5C8AC);
        }
        drawScaledCenteredText(context, Component.literal("Q <  > E"), centerX,
                centerY + 21, 0xFFAFA28A, 0.75F);
        drawScaledCenteredText(context, Component.literal("Release V"), centerX,
                centerY + 29, 0xFFAFA28A, 0.75F);

        for (int slot = 0; slot < QuiverManager.SLOT_COUNT; slot++) {
            double angle = Math.PI * 2.0D * slot / QuiverManager.SLOT_COUNT - Math.PI / 2.0D;
            int x = centerX + (int) Math.round(Math.cos(angle) * RADIUS) - 10;
            int y = centerY + (int) Math.round(Math.sin(angle) * RADIUS) - 10;
            int color = slot == hovered ? 0xFFB88A36 : slot == selected ? 0xFF7B9B53 : 0xFF3D3428;
            context.fill(x - 2, y - 2, x + 20, y + 20, color);
            context.fill(x, y, x + 18, y + 18, 0xFF1D1914);
            ItemStack stack = contents.get(slot);
            if (!stack.isEmpty()) {
                context.item(stack, x + 1, y + 1);
                context.itemDecorations(this.font, stack, x + 1, y + 1);
            }
        }
        if (hovered >= 0 && !contents.get(hovered).isEmpty()) {
            context.setTooltipForNextFrame(this.font, contents.get(hovered).getHoverName(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (click.button() == 0) {
            int slot = getHoveredSlot(click.x(), click.y(), this.width / 2, this.height / 2);
            Minecraft client = Minecraft.getInstance();
            if (slot >= 0 && client.player != null
                    && !QuiverManager.getContents(QuiverManager.getActiveQuiver(client.player)).get(slot).isEmpty()) {
                this.pendingSlot = slot;
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == GLFW.GLFW_KEY_Q) {
            cycleSelection(-1);
            return true;
        }
        if (input.key() == GLFW.GLFW_KEY_E) {
            cycleSelection(1);
            return true;
        }
        if (input.key() == GLFW.GLFW_KEY_V) {
            return true;
        }
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        return super.keyPressed(input);
    }

    private int getPendingSlot(List<ItemStack> contents, ItemStack quiver) {
        if (this.pendingSlot >= 0 && this.pendingSlot < contents.size() && !contents.get(this.pendingSlot).isEmpty()) {
            return this.pendingSlot;
        }
        this.pendingSlot = QuiverManager.getSelectedSlot(quiver);
        return this.pendingSlot;
    }

    private void cycleSelection(int direction) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }
        ItemStack quiver = QuiverManager.getActiveQuiver(client.player);
        List<ItemStack> contents = QuiverManager.getContents(quiver);
        int start = getPendingSlot(contents, quiver);
        for (int offset = 1; offset <= QuiverManager.SLOT_COUNT; offset++) {
            int slot = Math.floorMod(start + direction * offset, QuiverManager.SLOT_COUNT);
            if (!contents.get(slot).isEmpty()) {
                this.pendingSlot = slot;
                return;
            }
        }
    }

    void confirmSelection() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            ItemStack quiver = QuiverManager.getActiveQuiver(client.player);
            List<ItemStack> contents = QuiverManager.getContents(quiver);
            int selected = getPendingSlot(contents, quiver);
            if (selected >= 0 && !contents.get(selected).isEmpty()) {
                ClientPlayNetworking.send(new QuiverNetworking.SelectQuiverSlotPayload(selected));
            }
        }
        onClose();
    }

    private static int getHoveredSlot(double mouseX, double mouseY, int centerX, int centerY) {
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance < RADIUS - 19 || distance > RADIUS + 20) {
            return -1;
        }
        double angle = Math.atan2(dy, dx) + Math.PI / 2.0D;
        if (angle < 0.0D) {
            angle += Math.PI * 2.0D;
        }
        return (int) Math.floor((angle + Math.PI / QuiverManager.SLOT_COUNT)
                / (Math.PI * 2.0D) * QuiverManager.SLOT_COUNT) % QuiverManager.SLOT_COUNT;
    }

    private String getFittingName(ItemStack stack, int maxWidth) {
        String name = stack.getHoverName().getString();
        if (this.font.width(name) <= maxWidth) {
            return name;
        }

        String suffix = "...";
        int end = name.length();
        while (end > 0 && this.font.width(name.substring(0, end) + suffix) > maxWidth) {
            end--;
        }
        return name.substring(0, end) + suffix;
    }

    private void drawScaledCenteredText(GuiGraphicsExtractor context, Component text, int centerX, int y, int color, float scale) {
        context.pose().pushMatrix();
        context.pose().translate(centerX, y);
        context.pose().scale(scale, scale);
        context.centeredText(this.font, text, 0, 0, color);
        context.pose().popMatrix();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
