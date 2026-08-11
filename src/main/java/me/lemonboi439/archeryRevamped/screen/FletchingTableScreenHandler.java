package me.lemonboi439.archeryRevamped.screen;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

/**
 * A two-input crafting table. The result slot is a preview: ingredients are
 * consumed only after the player successfully takes a crafted batch.
 */
public final class FletchingTableScreenHandler extends ScreenHandler {
    private static final int INPUT_ARROW_SLOT = 0;
    private static final int INPUT_INGREDIENT_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;

    private final SimpleInventory inventory;
    private final ScreenHandlerContext context;
    private boolean refreshingOutput;
    private FletchingRecipeRegistry.FletchingRecipe activeRecipe;

    public FletchingTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public FletchingTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandlers.FLETCHING_TABLE, syncId);
        this.inventory = new SimpleInventory(3);
        this.context = context;

        this.addSlot(new Slot(inventory, INPUT_ARROW_SLOT, 27, 47) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return FletchingRecipeRegistry.isAcceptedArrowInput(stack);
            }
        });
        this.addSlot(new Slot(inventory, INPUT_INGREDIENT_SLOT, 76, 47) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return FletchingRecipeRegistry.isModifier(stack);
            }
        });
        this.addSlot(new Slot(inventory, OUTPUT_SLOT, 134, 47) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });
        addPlayerSlots(playerInventory, 8, 84);
        refreshOutput();
    }

    private FletchingRecipeRegistry.FletchingRecipe getMatchingRecipe() {
        return FletchingRecipeRegistry.match(
                inventory.getStack(INPUT_ARROW_SLOT),
                inventory.getStack(INPUT_INGREDIENT_SLOT)
        );
    }

    private ItemStack getResult(FletchingRecipeRegistry.FletchingRecipe recipe) {
        return recipe == null
                ? ItemStack.EMPTY
                : FletchingRecipeRegistry.createOutput(recipe, inventory.getStack(INPUT_INGREDIENT_SLOT));
    }

    private void refreshOutput() {
        activeRecipe = getMatchingRecipe();
        refreshingOutput = true;
        try {
            inventory.setStack(OUTPUT_SLOT, getResult(activeRecipe));
        } finally {
            refreshingOutput = false;
        }
        sendContentUpdates();
    }

    private void consumeRecipe(FletchingRecipeRegistry.FletchingRecipe recipe) {
        refreshingOutput = true;
        try {
            inventory.removeStack(INPUT_ARROW_SLOT, recipe.arrowCount());
            inventory.removeStack(INPUT_INGREDIENT_SLOT, 1);
        } finally {
            refreshingOutput = false;
        }
        refreshOutput();
    }

    private boolean craftToCursor() {
        FletchingRecipeRegistry.FletchingRecipe recipe = getMatchingRecipe();
        ItemStack result = getResult(recipe);
        if (recipe == null || result.isEmpty()) {
            return false;
        }

        ItemStack cursor = getCursorStack();
        if (!cursor.isEmpty() && !ItemStack.canCombine(cursor, result)) {
            return false;
        }
        if (!cursor.isEmpty() && cursor.getCount() + result.getCount() > cursor.getMaxCount()) {
            return false;
        }

        if (cursor.isEmpty()) {
            setCursorStack(result.copy());
        } else {
            cursor.increment(result.getCount());
            setCursorStack(cursor);
        }
        consumeRecipe(recipe);
        return true;
    }

    private ItemStack craftMaximumToInventory() {
        ItemStack crafted = ItemStack.EMPTY;
        while (true) {
            FletchingRecipeRegistry.FletchingRecipe recipe = getMatchingRecipe();
            ItemStack result = getResult(recipe);
            if (recipe == null || result.isEmpty()) {
                break;
            }

            ItemStack toInsert = result.copy();
            if (!insertItem(toInsert, OUTPUT_SLOT + 1, slots.size(), true) || !toInsert.isEmpty()) {
                break;
            }
            if (crafted.isEmpty()) {
                crafted = result.copy();
            } else {
                crafted.increment(result.getCount());
            }
            consumeRecipe(recipe);
        }
        return crafted;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex == OUTPUT_SLOT) {
            if (actionType == SlotActionType.QUICK_MOVE) {
                craftMaximumToInventory();
            } else if (actionType == SlotActionType.PICKUP) {
                craftToCursor();
            }
            return;
        }

        super.onSlotClick(slotIndex, button, actionType, player);
        if (!refreshingOutput) {
            refreshOutput();
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= slots.size()) {
            return ItemStack.EMPTY;
        }
        if (slotIndex == OUTPUT_SLOT) {
            return craftMaximumToInventory();
        }

        Slot slot = slots.get(slotIndex);
        if (!slot.hasStack()) {
            return ItemStack.EMPTY;
        }
        ItemStack source = slot.getStack();
        ItemStack moved = source.copy();
        if (slotIndex < OUTPUT_SLOT) {
            if (!insertItem(source, OUTPUT_SLOT + 1, slots.size(), false)) {
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
        refreshOutput();
        return moved;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(context, player, Blocks.FLETCHING_TABLE);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        if (!player.getEntityWorld().isClient()) {
            refreshingOutput = true;
            try {
                for (int slot = INPUT_ARROW_SLOT; slot <= INPUT_INGREDIENT_SLOT; slot++) {
                    ItemStack stack = inventory.removeStack(slot);
                    if (!stack.isEmpty()) {
                        player.getInventory().insertStack(stack);
                        if (!stack.isEmpty()) {
                            player.dropItem(stack, false);
                        }
                    }
                }
                inventory.setStack(OUTPUT_SLOT, ItemStack.EMPTY);
            } finally {
                refreshingOutput = false;
            }
        }
        super.onClosed(player);
    }

    /** The table is instant; retained for the existing client art API. */
    public int getProgress() {
        return activeRecipe == null ? 0 : 1;
    }

    public int getMaxProgress() {
        return 1;
    }

    public Text getRecipeName() {
        return activeRecipe == null ? Text.empty() : activeRecipe.displayName();
    }

    private void addPlayerSlots(PlayerInventory playerInventory, int x, int y) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, x + column * 18, y + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, x + column * 18, y + 58));
        }
    }
}
