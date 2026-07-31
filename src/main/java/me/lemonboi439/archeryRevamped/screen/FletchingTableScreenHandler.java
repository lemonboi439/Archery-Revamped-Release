package me.lemonboi439.archeryRevamped.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.block.Blocks;

public class FletchingTableScreenHandler extends ScreenHandler implements InventoryChangedListener {
    private static final int INPUT_ARROW_SLOT = 0;
    private static final int INPUT_INGREDIENT_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private static final int PROGRESS_INDEX = 0;
    private static final int MAX_PROGRESS_INDEX = 1;

    private final SimpleInventory inventory;
    private final ScreenHandlerContext context;
    private final PropertyDelegate progress;
    private FletchingRecipeRegistry.FletchingRecipe activeRecipe;
    private boolean updatingOutput;
    private ItemStack lastArrowInput = ItemStack.EMPTY;
    private ItemStack lastIngredientInput = ItemStack.EMPTY;

    public FletchingTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public FletchingTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        this(syncId, playerInventory, context, new SimpleInventory(3), new ArrayPropertyDelegate(2));
    }

    private FletchingTableScreenHandler(
            int syncId,
            PlayerInventory playerInventory,
            ScreenHandlerContext context,
            SimpleInventory inventory,
            PropertyDelegate progress
    ) {
        super(ModScreenHandlers.FLETCHING_TABLE, syncId);
        checkSize(inventory, 3);
        checkDataCount(progress, 2);
        this.inventory = inventory;
        this.context = context;
        this.progress = progress;
        this.inventory.addListener(this);
        this.progress.set(MAX_PROGRESS_INDEX, 1);
        addProperties(this.progress);

        this.addSlot(new Slot(this.inventory, INPUT_ARROW_SLOT, 27, 47) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof ArrowItem;
            }
        });
        this.addSlot(new Slot(this.inventory, INPUT_INGREDIENT_SLOT, 76, 47));
        this.addSlot(new Slot(this.inventory, OUTPUT_SLOT, 134, 47) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

        });
        addPlayerSlots(playerInventory, 8, 84);
        updateRecipeOutput();
    }

    public static void tickServer(MinecraftServer server) {
        for (var player : server.getPlayerManager().getPlayerList()) {
            if (player.currentScreenHandler instanceof FletchingTableScreenHandler handler) {
                handler.tickCrafting();
            }
        }
    }

    private void tickCrafting() {
        FletchingRecipeRegistry.FletchingRecipe recipe = getMatchingRecipe();
        if (recipe == null) {
            progress.set(PROGRESS_INDEX, 0);
            progress.set(MAX_PROGRESS_INDEX, 1);
            activeRecipe = null;
            return;
        }

        if (inputsChanged() || activeRecipe != recipe) {
            activeRecipe = recipe;
            progress.set(PROGRESS_INDEX, 0);
        }

        progress.set(MAX_PROGRESS_INDEX, 1);
        ItemStack result = FletchingRecipeRegistry.createOutput(
                recipe, inventory.getStack(INPUT_INGREDIENT_SLOT));
        if (!canAcceptOutput(result)) {
            return;
        }

        // Fletching is intentionally instantaneous: one valid batch is made
        // on each server tick while inputs and output space are available.
        progress.set(PROGRESS_INDEX, 1);
        craftBatch(recipe, result);
    }

    private FletchingRecipeRegistry.FletchingRecipe getMatchingRecipe() {
        return FletchingRecipeRegistry.match(
                inventory.getStack(INPUT_ARROW_SLOT),
                inventory.getStack(INPUT_INGREDIENT_SLOT)
        );
    }

    private void updateRecipeOutput() {
        FletchingRecipeRegistry.FletchingRecipe recipe = getMatchingRecipe();
        boolean changed = inputsChanged();
        if (changed || recipe != activeRecipe) {
            progress.set(PROGRESS_INDEX, 0);
        }
        activeRecipe = recipe;
        progress.set(MAX_PROGRESS_INDEX, 1);
    }

    private boolean inputsChanged() {
        ItemStack arrowInput = inventory.getStack(INPUT_ARROW_SLOT);
        ItemStack ingredientInput = inventory.getStack(INPUT_INGREDIENT_SLOT);
        boolean changed = !ItemStack.areEqual(this.lastArrowInput, arrowInput)
                || !ItemStack.areEqual(this.lastIngredientInput, ingredientInput);
        this.lastArrowInput = arrowInput.copy();
        this.lastIngredientInput = ingredientInput.copy();
        return changed;
    }

    private void setOutput(ItemStack output) {
        if (ItemStack.areEqual(inventory.getStack(OUTPUT_SLOT), output)) {
            return;
        }

        this.updatingOutput = true;
        try {
            inventory.setStack(OUTPUT_SLOT, output);
        } finally {
            this.updatingOutput = false;
        }
    }

    private boolean canAcceptOutput(ItemStack result) {
        ItemStack output = inventory.getStack(OUTPUT_SLOT);
        if (output.isEmpty()) {
            return true;
        }
        return ItemStack.areItemsAndComponentsEqual(output, result)
                && output.getCount() + result.getCount() <= output.getMaxCount();
    }

    private void craftBatch(FletchingRecipeRegistry.FletchingRecipe recipe, ItemStack result) {
        if (!canAcceptOutput(result)) {
            return;
        }

        this.updatingOutput = true;
        try {
            inventory.removeStack(INPUT_ARROW_SLOT, recipe.arrowCount());
            inventory.removeStack(INPUT_INGREDIENT_SLOT, 1);

            ItemStack output = inventory.getStack(OUTPUT_SLOT);
            if (output.isEmpty()) {
                inventory.setStack(OUTPUT_SLOT, result);
            } else {
                output.increment(result.getCount());
                inventory.setStack(OUTPUT_SLOT, output);
            }
        } finally {
            this.updatingOutput = false;
        }

        progress.set(PROGRESS_INDEX, 0);
    }

    @Override
    public void onInventoryChanged(Inventory inventory) {
        if (!this.updatingOutput) {
            onContentChanged(inventory);
        }
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if (inventory == this.inventory) {
            FletchingRecipeRegistry.FletchingRecipe recipe = getMatchingRecipe();
            if (recipe != activeRecipe) {
                progress.set(PROGRESS_INDEX, 0);
            }
            updateRecipeOutput();
        }
        super.onContentChanged(inventory);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack moved = ItemStack.EMPTY;
        if (slotIndex < 0 || slotIndex >= this.slots.size()) {
            return moved;
        }

        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasStack()) {
            return moved;
        }

        ItemStack source = slot.getStack();
        moved = source.copy();
        if (slotIndex == OUTPUT_SLOT) {
            if (!slot.canTakeItems(player) || !insertItem(source, OUTPUT_SLOT + 1, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, moved);
        } else if (slotIndex < OUTPUT_SLOT) {
            if (!insertItem(source, OUTPUT_SLOT + 1, this.slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (!insertItem(source, INPUT_ARROW_SLOT, OUTPUT_SLOT, false)) {
            return ItemStack.EMPTY;
        }

        if (source.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }
        return moved;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, Blocks.FLETCHING_TABLE);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (!player.getEntityWorld().isClient()) {
            // Return all table contents to the player first. Only items that
            // do not fit are dropped, so closing the screen never silently
            // deletes ingredients or completed output.
            this.updatingOutput = true;
            try {
                for (int slot = 0; slot < OUTPUT_SLOT + 1; slot++) {
                    ItemStack stack = this.inventory.removeStack(slot);
                    if (!stack.isEmpty()) {
                        insertItem(stack, OUTPUT_SLOT + 1, this.slots.size(), true);
                        if (!stack.isEmpty()) {
                            player.dropItem(stack, false);
                        }
                    }
                }
            } finally {
                this.updatingOutput = false;
            }
        }
    }

    public int getProgress() {
        return progress.get(PROGRESS_INDEX);
    }

    public int getMaxProgress() {
        return Math.max(1, progress.get(MAX_PROGRESS_INDEX));
    }

    public Text getRecipeName() {
        return activeRecipe == null ? Text.empty() : activeRecipe.displayName();
    }
}
