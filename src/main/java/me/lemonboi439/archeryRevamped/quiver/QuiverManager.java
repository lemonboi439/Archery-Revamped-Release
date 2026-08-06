package me.lemonboi439.archeryRevamped.quiver;

import me.lemonboi439.archeryRevamped.component.ModDataComponents;
import me.lemonboi439.archeryRevamped.item.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

/** Persistent 9-stack quiver storage and arrow-priority rules. */
public final class QuiverManager {
    public static final int SLOT_COUNT = 9;

    private QuiverManager() {
    }

    public static ItemStack getActiveQuiver(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.isOf(ModItems.QUIVER)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static List<ItemStack> getContents(ItemStack quiver) {
        DefaultedList<ItemStack> contents = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);
        quiver.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(contents);
        return contents;
    }

    public static void setContents(ItemStack quiver, List<ItemStack> contents) {
        List<ItemStack> normalized = new ArrayList<>(SLOT_COUNT);
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            ItemStack stack = slot < contents.size() ? contents.get(slot) : ItemStack.EMPTY;
            normalized.add(stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
        quiver.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(normalized));
    }

    public static int getSelectedSlot(ItemStack quiver) {
        return MathHelper.clamp(quiver.getOrDefault(ModDataComponents.QUIVER_SELECTED_SLOT, 0), 0, SLOT_COUNT - 1);
    }

    public static void setSelectedSlot(ItemStack quiver, int slot) {
        quiver.set(ModDataComponents.QUIVER_SELECTED_SLOT, MathHelper.clamp(slot, 0, SLOT_COUNT - 1));
    }

    private static int getNextSlot(ItemStack quiver) {
        return MathHelper.floorMod(quiver.getOrDefault(ModDataComponents.QUIVER_NEXT_SLOT, 0), SLOT_COUNT);
    }

    private static void setNextSlot(ItemStack quiver, int slot) {
        quiver.set(ModDataComponents.QUIVER_NEXT_SLOT, MathHelper.floorMod(slot, SLOT_COUNT));
    }

    /** Stores a whole arrow stack in the next empty slot, proceeding clockwise. */
    public static boolean insertNextArrowStack(ItemStack quiver, ItemStack arrowStack) {
        if (!isArrow(arrowStack)) {
            return false;
        }
        List<ItemStack> contents = getContents(quiver);
        int start = getNextSlot(quiver);
        for (int offset = 0; offset < SLOT_COUNT; offset++) {
            int slot = (start + offset) % SLOT_COUNT;
            if (contents.get(slot).isEmpty()) {
                contents.set(slot, arrowStack.copy());
                setContents(quiver, contents);
                setNextSlot(quiver, slot + 1);
                return true;
            }
        }
        return false;
    }

    /** Removes the next stored arrow stack, proceeding clockwise from the queue position. */
    public static ItemStack removeNextArrowStack(ItemStack quiver) {
        List<ItemStack> contents = getContents(quiver);
        int start = getNextSlot(quiver);
        for (int offset = 0; offset < SLOT_COUNT; offset++) {
            int slot = (start + offset) % SLOT_COUNT;
            ItemStack stored = contents.get(slot);
            if (!stored.isEmpty()) {
                contents.set(slot, ItemStack.EMPTY);
                setContents(quiver, contents);
                setNextSlot(quiver, slot + 1);
                return stored;
            }
        }
        return ItemStack.EMPTY;
    }

    public static void cycleSelectedSlot(PlayerEntity player) {
        ItemStack quiver = getActiveQuiver(player);
        if (quiver.isEmpty()) {
            return;
        }
        List<ItemStack> contents = getContents(quiver);
        int start = getSelectedSlot(quiver);
        for (int offset = 1; offset <= SLOT_COUNT; offset++) {
            int slot = (start + offset) % SLOT_COUNT;
            if (isArrow(contents.get(slot))) {
                setSelectedSlot(quiver, slot);
                return;
            }
        }
    }

    public static ItemStack getSelectedArrow(PlayerEntity player) {
        ItemStack quiver = getActiveQuiver(player);
        if (quiver.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack selected = getContents(quiver).get(getSelectedSlot(quiver));
        return isArrow(selected) ? selected.copy() : ItemStack.EMPTY;
    }

    public static boolean isSelectedArrow(PlayerEntity player, ItemStack arrow) {
        ItemStack selected = getSelectedArrow(player);
        return !selected.isEmpty() && ItemStack.areItemsAndComponentsEqual(selected, arrow);
    }

    public static int countArrow(PlayerEntity player, ItemStack arrow) {
        int available = 0;
        ItemStack quiver = getActiveQuiver(player);
        if (!quiver.isEmpty()) {
            for (ItemStack stack : getContents(quiver)) {
                if (ItemStack.areItemsAndComponentsEqual(stack, arrow)) {
                    available += stack.getCount();
                }
            }
        }
        return available;
    }

    /** Consumes matching arrows from the active quiver, selected slot first. */
    public static int consumeArrow(PlayerEntity player, ItemStack arrow, int amount) {
        if (amount <= 0) {
            return 0;
        }
        ItemStack quiver = getActiveQuiver(player);
        if (quiver.isEmpty()) {
            return 0;
        }
        List<ItemStack> contents = getContents(quiver);
        int remaining = amount;
        int selected = getSelectedSlot(quiver);
        remaining = consumeSlot(contents, selected, arrow, remaining);
        for (int slot = 0; slot < SLOT_COUNT && remaining > 0; slot++) {
            if (slot != selected) {
                remaining = consumeSlot(contents, slot, arrow, remaining);
            }
        }
        if (remaining != amount) {
            setContents(quiver, contents);
        }
        return amount - remaining;
    }

    private static int consumeSlot(List<ItemStack> contents, int slot, ItemStack arrow, int remaining) {
        ItemStack candidate = contents.get(slot);
        if (candidate.isEmpty() || !ItemStack.areItemsAndComponentsEqual(candidate, arrow)) {
            return remaining;
        }
        int removed = Math.min(remaining, candidate.getCount());
        candidate.decrement(removed);
        if (candidate.isEmpty()) {
            contents.set(slot, ItemStack.EMPTY);
        }
        return remaining - removed;
    }

    public static boolean isArrow(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ArrowItem;
    }
}
