package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import me.lemonboi439.archeryRevamped.quiver.QuiverNetworking;
import me.lemonboi439.archeryRevamped.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
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
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        List<ItemStack> contents = QuiverManager.getContents(QuiverManager.getActiveQuiver(client.player));
        int selected = getPendingSlot(contents, QuiverManager.getActiveQuiver(client.player));
        int hovered = getHoveredSlot(mouseX, mouseY, centerX, centerY);
        context.fill(centerX - 36, centerY - 33, centerX + 36, centerY + 37, 0xD01B1712);
        context.drawCenteredString(this.font, Component.translatable("container.archery-revamped.quiver"),
                centerX, centerY - 28, 0xFFFFFFFF);
        ItemStack selectedStack = selected >= 0 ? contents.get(selected) : ItemStack.EMPTY;
        if (!selectedStack.isEmpty()) {
            context.renderItem(selectedStack, centerX - 8, centerY - 10);
            drawScaledCenteredText(context, Component.literal(getFittingName(selectedStack, 86)), centerX, centerY + 8,
                    0xFFD5C8AC, 0.75F);
        } else {
            context.drawCenteredString(this.font, Component.literal("No arrow selected"), centerX,
                    centerY + 2, 0xFFD5C8AC);
        }
        drawScaledCenteredText(context, Component.literal("Q <  > E"), centerX, centerY + 21, 0xFFAFA28A, 0.75F);
        drawScaledCenteredText(context, Component.literal("Release V"), centerX, centerY + 29, 0xFFAFA28A, 0.75F);

        for (int slot = 0; slot < QuiverManager.SLOT_COUNT; slot++) {
            double angle = Math.PI * 2.0D * slot / QuiverManager.SLOT_COUNT - Math.PI / 2.0D;
            int x = centerX + (int) Math.round(Math.cos(angle) * RADIUS) - 10;
            int y = centerY + (int) Math.round(Math.sin(angle) * RADIUS) - 10;
            int color = slot == hovered ? 0xFFB88A36 : slot == selected ? 0xFF7B9B53 : 0xFF3D3428;
            context.fill(x - 2, y - 2, x + 20, y + 20, color);
            context.fill(x, y, x + 18, y + 18, 0xFF1D1914);
            ItemStack stack = contents.get(slot);
            if (!stack.isEmpty()) {
                context.renderItem(stack, x + 1, y + 1);
                context.renderItemDecorations(this.font, stack, x + 1, y + 1);
            }
        }
        if (hovered >= 0 && !contents.get(hovered).isEmpty()) {
            context.renderTooltip(this.font, contents.get(hovered).getHoverName(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int slot = getHoveredSlot(mouseX, mouseY, this.width / 2, this.height / 2);
            Minecraft client = Minecraft.getInstance();
            if (slot >= 0 && client.player != null
                    && !QuiverManager.getContents(QuiverManager.getActiveQuiver(client.player)).get(slot).isEmpty()) {
                this.pendingSlot = slot;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_Q) {
            cycleSelection(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_E) {
            cycleSelection(1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_V) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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
                ModNetwork.CHANNEL.sendToServer(new ModNetwork.SelectQuiverSlotPacket(selected));
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

    private void drawScaledCenteredText(GuiGraphics context, Component text, int centerX, int y, int color, float scale) {
        context.pose().pushPose();
        context.pose().translate(centerX, y, 0.0F);
        context.pose().scale(scale, scale, 1.0F);
        context.drawCenteredString(this.font, text, 0, 0, color);
        context.pose().popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
