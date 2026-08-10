package me.lemonboi439.archeryRevamped.client;

import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import me.lemonboi439.archeryRevamped.quiver.QuiverNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/** Original nine-way radial selector inspired by builder-mod usability, not copied code or assets. */
public final class QuiverRadialScreen extends Screen {
    private static final int RADIUS = 62;
    private int pendingSlot = -1;

    public QuiverRadialScreen() {
        super(Text.translatable("container.archery-revamped.quiver"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        List<ItemStack> contents = QuiverManager.getContents(QuiverManager.getActiveQuiver(client.player));
        int selected = getPendingSlot(contents, QuiverManager.getActiveQuiver(client.player));
        int hovered = getHoveredSlot(mouseX, mouseY, centerX, centerY);
        context.fill(centerX - 43, centerY - 36, centerX + 43, centerY + 41, 0xD01B1712);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("container.archery-revamped.quiver"),
                centerX, centerY - 31, 0xFFFFFFFF);
        ItemStack selectedStack = selected >= 0 ? contents.get(selected) : ItemStack.EMPTY;
        if (!selectedStack.isEmpty()) {
            context.drawItem(selectedStack, centerX - 8, centerY - 13);
            context.drawCenteredTextWithShadow(this.textRenderer, selectedStack.getName(), centerX, centerY + 8,
                    0xFFD5C8AC);
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("No arrow selected"), centerX,
                    centerY + 2, 0xFFD5C8AC);
        }
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Q / E: select   V: confirm"), centerX,
                centerY + 25, 0xFFAFA28A);

        for (int slot = 0; slot < QuiverManager.SLOT_COUNT; slot++) {
            double angle = Math.PI * 2.0D * slot / QuiverManager.SLOT_COUNT - Math.PI / 2.0D;
            int x = centerX + (int) Math.round(Math.cos(angle) * RADIUS) - 10;
            int y = centerY + (int) Math.round(Math.sin(angle) * RADIUS) - 10;
            int color = slot == hovered ? 0xFFB88A36 : slot == selected ? 0xFF7B9B53 : 0xFF3D3428;
            context.fill(x - 2, y - 2, x + 20, y + 20, color);
            context.fill(x, y, x + 18, y + 18, 0xFF1D1914);
            ItemStack stack = contents.get(slot);
            if (!stack.isEmpty()) {
                context.drawItem(stack, x + 1, y + 1);
                context.drawText(this.textRenderer, Text.literal(Integer.toString(stack.getCount())), x + 10, y + 10,
                        0xFFFFFFFF, true);
            }
        }
        if (hovered >= 0 && !contents.get(hovered).isEmpty()) {
            context.drawTooltip(this.textRenderer, contents.get(hovered).getName(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int slot = getHoveredSlot(mouseX, mouseY, this.width / 2, this.height / 2);
            MinecraftClient client = MinecraftClient.getInstance();
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
            confirmSelection();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
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
        MinecraftClient client = MinecraftClient.getInstance();
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

    private void confirmSelection() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            ItemStack quiver = QuiverManager.getActiveQuiver(client.player);
            List<ItemStack> contents = QuiverManager.getContents(quiver);
            int selected = getPendingSlot(contents, quiver);
            if (selected >= 0 && !contents.get(selected).isEmpty()) {
                ClientPlayNetworking.send(new QuiverNetworking.SelectQuiverSlotPayload(selected));
            }
        }
        close();
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

    @Override
    public boolean shouldPause() {
        return false;
    }
}
