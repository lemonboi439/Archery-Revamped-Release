package me.lemonboi439.archeryRevamped.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/**
 * A two-input crafting table: an arrow stack and one modifier preview a result.
 * Results are only created when the output is clicked, never on a server timer.
 */
public final class FletchingTableScreenHandler extends AbstractContainerMenu {
    private static final int INPUT_ARROW_SLOT = 0;
    private static final int INPUT_INGREDIENT_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;

    private final SimpleContainer inventory;
    private final ContainerLevelAccess context;
    private boolean refreshingOutput;
    private FletchingRecipeRegistry.FletchingRecipe activeRecipe;

    public FletchingTableScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public FletchingTableScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(ModScreenHandlers.FLETCHING_TABLE, syncId);
        this.inventory = new SimpleContainer(3);
        this.context = context;

        this.addSlot(new Slot(this.inventory, INPUT_ARROW_SLOT, 27, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FletchingRecipeRegistry.isAcceptedArrowInput(stack);
            }
        });
        this.addSlot(new Slot(this.inventory, INPUT_INGREDIENT_SLOT, 76, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FletchingRecipeRegistry.isModifier(stack);
            }
        });
        this.addSlot(new Slot(this.inventory, OUTPUT_SLOT, 134, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addPlayerSlots(playerInventory, 8, 84);
        refreshOutput();
    }

    private FletchingRecipeRegistry.FletchingRecipe getMatchingRecipe() {
        return FletchingRecipeRegistry.match(
                inventory.getItem(INPUT_ARROW_SLOT),
                inventory.getItem(INPUT_INGREDIENT_SLOT)
        );
    }

    private ItemStack getResult(FletchingRecipeRegistry.FletchingRecipe recipe) {
        return recipe == null ? ItemStack.EMPTY
                : FletchingRecipeRegistry.createOutput(recipe, inventory.getItem(INPUT_INGREDIENT_SLOT));
    }

    private void refreshOutput() {
        FletchingRecipeRegistry.FletchingRecipe recipe = getMatchingRecipe();
        this.activeRecipe = recipe;
        this.refreshingOutput = true;
        try {
            inventory.setItem(OUTPUT_SLOT, getResult(recipe));
        } finally {
            this.refreshingOutput = false;
        }
        broadcastChanges();
    }

    /** Consumes one recipe only after the destination has accepted its result. */
    private void consumeRecipe(FletchingRecipeRegistry.FletchingRecipe recipe) {
        this.refreshingOutput = true;
        try {
            inventory.removeItem(INPUT_ARROW_SLOT, recipe.arrowCount());
            inventory.removeItem(INPUT_INGREDIENT_SLOT, 1);
        } finally {
            this.refreshingOutput = false;
        }
        refreshOutput();
    }

    private boolean craftToCursor() {
        FletchingRecipeRegistry.FletchingRecipe recipe = getMatchingRecipe();
        ItemStack result = getResult(recipe);
        if (recipe == null || result.isEmpty()) {
            return false;
        }

        ItemStack cursor = getCarried();
        if (!cursor.isEmpty() && !ItemStack.isSameItemSameComponents(cursor, result)) {
            return false;
        }
        if (!cursor.isEmpty() && cursor.getCount() + result.getCount() > cursor.getMaxStackSize()) {
            return false;
        }

        if (cursor.isEmpty()) {
            setCarried(result.copy());
        } else {
            cursor.grow(result.getCount());
            setCarried(cursor);
        }
        consumeRecipe(recipe);
        return true;
    }

    /** Shift-click crafting repeats until either ingredients or inventory space runs out. */
    private ItemStack craftMaximumToInventory() {
        ItemStack crafted = ItemStack.EMPTY;
        while (true) {
            FletchingRecipeRegistry.FletchingRecipe recipe = getMatchingRecipe();
            ItemStack result = getResult(recipe);
            if (recipe == null || result.isEmpty()) {
                break;
            }

            ItemStack toInsert = result.copy();
            if (!moveItemStackTo(toInsert, OUTPUT_SLOT + 1, slots.size(), true) || !toInsert.isEmpty()) {
                break;
            }
            if (crafted.isEmpty()) {
                crafted = result.copy();
            } else {
                crafted.grow(result.getCount());
            }
            consumeRecipe(recipe);
        }
        return crafted;
    }

    @Override
    public void clicked(int slotIndex, int button, ContainerInput actionType, Player player) {
        if (slotIndex == OUTPUT_SLOT) {
            if (actionType == ContainerInput.QUICK_MOVE) {
                craftMaximumToInventory();
                return;
            }
            if (actionType == ContainerInput.PICKUP) {
                craftToCursor();
                return;
            }
            // The result is a virtual preview, never a movable inventory
            // stack. Ignore drag, swap, throw, clone, and double-click paths.
            return;
        }
        super.clicked(slotIndex, button, actionType, player);
        if (!this.refreshingOutput) {
            refreshOutput();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= this.slots.size()) {
            return ItemStack.EMPTY;
        }
        if (slotIndex == OUTPUT_SLOT) {
            return craftMaximumToInventory();
        }

        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack source = slot.getItem();
        ItemStack moved = source.copy();
        if (slotIndex < OUTPUT_SLOT) {
            if (!moveItemStackTo(source, OUTPUT_SLOT + 1, this.slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(source, INPUT_ARROW_SLOT, OUTPUT_SLOT, false)) {
            return ItemStack.EMPTY;
        }

        if (source.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return moved;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.context, player, Blocks.FLETCHING_TABLE);
    }

    @Override
    public void removed(Player player) {
        if (player.level().isClientSide()) {
            super.removed(player);
            return;
        }

        this.refreshingOutput = true;
        try {
            // Output is only a preview and must never be returned separately.
            for (int slot = INPUT_ARROW_SLOT; slot <= INPUT_INGREDIENT_SLOT; slot++) {
                ItemStack stack = this.inventory.removeItemNoUpdate(slot);
                if (!stack.isEmpty()) {
                    // Insert directly into the real player inventory. The
                    // handler's own slots are being torn down at this point.
                    player.getInventory().add(stack);
                    if (!stack.isEmpty()) {
                        player.drop(stack, false);
                    }
                }
            }
            inventory.setItem(OUTPUT_SLOT, ItemStack.EMPTY);
        } finally {
            this.refreshingOutput = false;
        }
        super.removed(player);
    }

    public Component getRecipeName() {
        return activeRecipe == null ? Component.empty() : activeRecipe.displayName();
    }

    private void addPlayerSlots(Inventory playerInventory, int x, int y) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, x + column * 18, y + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, x + column * 18, y + 58));
        }
    }
}
