package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;

/** A nine-stack, arrow-only container. */
public final class QuiverItem extends Item {
    public QuiverItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onClicked(ItemStack quiver, ItemStack cursorStack, Slot slot, ClickType clickType,
                             PlayerEntity player, StackReference cursorStackReference) {
        if (QuiverManager.isArrow(cursorStack) && QuiverManager.insertNextArrowStack(quiver, cursorStack)) {
            cursorStackReference.set(ItemStack.EMPTY);
            return true;
        }
        if (clickType == ClickType.RIGHT && cursorStack.isEmpty()) {
            ItemStack released = QuiverManager.removeNextArrowStack(quiver);
            if (!released.isEmpty()) {
                cursorStackReference.set(released);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onStackClicked(ItemStack quiver, Slot slot, ClickType clickType, PlayerEntity player) {
        ItemStack slotStack = slot.getStack();
        if (QuiverManager.isArrow(slotStack) && QuiverManager.insertNextArrowStack(quiver, slotStack)) {
            slot.setStack(ItemStack.EMPTY);
            slot.markDirty();
            return true;
        }
        if (clickType == ClickType.RIGHT && slotStack.isEmpty()) {
            ItemStack released = QuiverManager.removeNextArrowStack(quiver);
            if (!released.isEmpty()) {
                slot.setStack(released);
                slot.markDirty();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int arrows = QuiverManager.getContents(stack).stream().mapToInt(ItemStack::getCount).sum();
        return Math.min(13, arrows * 13 / (QuiverManager.SLOT_COUNT * 64));
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0xA56A36;
    }
}
