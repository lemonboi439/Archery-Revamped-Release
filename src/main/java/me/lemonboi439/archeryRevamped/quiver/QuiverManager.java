package me.lemonboi439.archeryRevamped.quiver;

import me.lemonboi439.archeryRevamped.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

/** Persistent nine-stack quiver storage for the pre-data-component 1.20.1 API. */
public final class QuiverManager {
    public static final int SLOT_COUNT = 9;
    private static final String CONTENTS_KEY = "ArcheryRevampedQuiverContents";
    private static final String SELECTED_SLOT_KEY = "ArcheryRevampedQuiverSelectedSlot";
    private static final String NEXT_SLOT_KEY = "ArcheryRevampedQuiverNextSlot";

    private QuiverManager() {
    }

    public static ItemStack getActiveQuiver(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.isOf(ModItems.QUIVER)) return stack;
        }
        return ItemStack.EMPTY;
    }

    public static List<ItemStack> getContents(ItemStack quiver) {
        DefaultedList<ItemStack> contents = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);
        if (!quiver.hasNbt()) return contents;
        NbtList serialized = quiver.getNbt().getList(CONTENTS_KEY, NbtCompound.COMPOUND_TYPE);
        for (int slot = 0; slot < Math.min(SLOT_COUNT, serialized.size()); slot++) {
            contents.set(slot, ItemStack.fromNbt(serialized.getCompound(slot)));
        }
        return contents;
    }

    public static void setContents(ItemStack quiver, List<ItemStack> contents) {
        NbtList serialized = new NbtList();
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            ItemStack stack = slot < contents.size() ? contents.get(slot) : ItemStack.EMPTY;
            NbtCompound saved = new NbtCompound();
            stack.writeNbt(saved);
            serialized.add(saved);
        }
        quiver.getOrCreateNbt().put(CONTENTS_KEY, serialized);
    }

    public static int getSelectedSlot(ItemStack quiver) {
        return MathHelper.clamp(quiver.getOrCreateNbt().getInt(SELECTED_SLOT_KEY), 0, SLOT_COUNT - 1);
    }

    public static void setSelectedSlot(ItemStack quiver, int slot) {
        quiver.getOrCreateNbt().putInt(SELECTED_SLOT_KEY, MathHelper.clamp(slot, 0, SLOT_COUNT - 1));
    }

    private static int getNextSlot(ItemStack quiver) {
        return MathHelper.floorMod(quiver.getOrCreateNbt().getInt(NEXT_SLOT_KEY), SLOT_COUNT);
    }

    private static void setNextSlot(ItemStack quiver, int slot) {
        quiver.getOrCreateNbt().putInt(NEXT_SLOT_KEY, MathHelper.floorMod(slot, SLOT_COUNT));
    }

    public static boolean insertNextArrowStack(ItemStack quiver, ItemStack arrowStack) {
        if (!isArrow(arrowStack)) return false;
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
        if (quiver.isEmpty()) return;
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
        if (quiver.isEmpty()) return ItemStack.EMPTY;
        ItemStack selected = getContents(quiver).get(getSelectedSlot(quiver));
        return isArrow(selected) ? selected.copy() : ItemStack.EMPTY;
    }

    public static boolean isSelectedArrow(PlayerEntity player, ItemStack arrow) {
        ItemStack selected = getSelectedArrow(player);
        return !selected.isEmpty() && ItemStack.canCombine(selected, arrow);
    }

    public static int countArrow(PlayerEntity player, ItemStack arrow) {
        int available = 0;
        ItemStack quiver = getActiveQuiver(player);
        if (!quiver.isEmpty()) for (ItemStack stack : getContents(quiver)) {
            if (ItemStack.canCombine(stack, arrow)) available += stack.getCount();
        }
        return available;
    }

    public static int consumeArrow(PlayerEntity player, ItemStack arrow, int amount) {
        if (amount <= 0) return 0;
        ItemStack quiver = getActiveQuiver(player);
        if (quiver.isEmpty()) return 0;
        List<ItemStack> contents = getContents(quiver);
        int remaining = consumeSlot(contents, getSelectedSlot(quiver), arrow, amount);
        for (int slot = 0; slot < SLOT_COUNT && remaining > 0; slot++) {
            if (slot != getSelectedSlot(quiver)) remaining = consumeSlot(contents, slot, arrow, remaining);
        }
        if (remaining != amount) setContents(quiver, contents);
        return amount - remaining;
    }

    private static int consumeSlot(List<ItemStack> contents, int slot, ItemStack arrow, int remaining) {
        ItemStack candidate = contents.get(slot);
        if (candidate.isEmpty() || !ItemStack.canCombine(candidate, arrow)) return remaining;
        int removed = Math.min(remaining, candidate.getCount());
        candidate.decrement(removed);
        if (candidate.isEmpty()) contents.set(slot, ItemStack.EMPTY);
        return remaining - removed;
    }

    public static boolean isArrow(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ArrowItem;
    }
}
