package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** A nine-stack, arrow-only container. */
public final class QuiverItem extends Item {
    public QuiverItem(Properties settings) {
        super(settings);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack quiver, ItemStack cursorStack, Slot slot, ClickAction clickType,
                             Player player, SlotAccess cursorStackReference) {
        if (QuiverManager.isArrow(cursorStack) && QuiverManager.insertNextArrowStack(quiver, cursorStack)) {
            cursorStackReference.set(ItemStack.EMPTY);
            return true;
        }
        if (clickType == ClickAction.SECONDARY && cursorStack.isEmpty()) {
            ItemStack released = QuiverManager.removeNextArrowStack(quiver);
            if (!released.isEmpty()) {
                cursorStackReference.set(released);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int arrows = QuiverManager.getContents(stack).stream().mapToInt(ItemStack::getCount).sum();
        return Math.min(13, arrows * 13 / (QuiverManager.SLOT_COUNT * 64));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xA56A36;
    }
}
