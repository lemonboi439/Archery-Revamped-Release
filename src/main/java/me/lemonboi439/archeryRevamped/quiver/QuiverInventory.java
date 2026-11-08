package me.lemonboi439.archeryRevamped.quiver;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Screen-handler inventory backed by a quiver ItemStack's container component. */
public final class QuiverInventory implements Container {
    private final Player owner;
    private final ItemStack quiver;
    private final NonNullList<ItemStack> contents;

    public QuiverInventory(Player owner, ItemStack quiver) {
        this.owner = owner;
        this.quiver = quiver;
        this.contents = NonNullList.withSize(QuiverManager.SLOT_COUNT, ItemStack.EMPTY);
        List<ItemStack> savedContents = QuiverManager.getContents(quiver);
        for (int slot = 0; slot < QuiverManager.SLOT_COUNT; slot++) {
            this.contents.set(slot, savedContents.get(slot));
        }
    }

    @Override
    public int getContainerSize() {
        return QuiverManager.SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return this.contents.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.contents.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = this.contents.get(slot).split(amount);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = this.contents.set(slot, ItemStack.EMPTY);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.contents.set(slot, stack);
        setChanged();
    }

    @Override
    public void setChanged() {
        QuiverManager.setContents(this.quiver, this.contents);
    }

    @Override
    public boolean stillValid(Player player) {
        return player == this.owner && !this.quiver.isEmpty();
    }

    @Override
    public void clearContent() {
        this.contents.clear();
        for (int slot = 0; slot < QuiverManager.SLOT_COUNT; slot++) {
            this.contents.add(ItemStack.EMPTY);
        }
        setChanged();
    }

    public ItemStack getQuiver() {
        return this.quiver;
    }
}
