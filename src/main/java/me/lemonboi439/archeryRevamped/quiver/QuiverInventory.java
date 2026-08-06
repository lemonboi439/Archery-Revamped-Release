package me.lemonboi439.archeryRevamped.quiver;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

/** Screen-handler inventory backed by a quiver ItemStack's container component. */
public final class QuiverInventory implements Inventory {
    private final PlayerEntity owner;
    private final ItemStack quiver;
    private final DefaultedList<ItemStack> contents;

    public QuiverInventory(PlayerEntity owner, ItemStack quiver) {
        this.owner = owner;
        this.quiver = quiver;
        this.contents = DefaultedList.ofSize(QuiverManager.SLOT_COUNT, ItemStack.EMPTY);
        List<ItemStack> savedContents = QuiverManager.getContents(quiver);
        for (int slot = 0; slot < QuiverManager.SLOT_COUNT; slot++) {
            this.contents.set(slot, savedContents.get(slot));
        }
    }

    @Override
    public int size() {
        return QuiverManager.SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return this.contents.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.contents.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = this.contents.get(slot).split(amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = this.contents.set(slot, ItemStack.EMPTY);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.contents.set(slot, stack);
        markDirty();
    }

    @Override
    public void markDirty() {
        QuiverManager.setContents(this.quiver, this.contents);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player == this.owner && !this.quiver.isEmpty();
    }

    @Override
    public void clear() {
        this.contents.clear();
        for (int slot = 0; slot < QuiverManager.SLOT_COUNT; slot++) {
            this.contents.add(ItemStack.EMPTY);
        }
        markDirty();
    }

    public ItemStack getQuiver() {
        return this.quiver;
    }
}
