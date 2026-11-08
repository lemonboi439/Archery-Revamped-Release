package me.lemonboi439.archeryRevamped.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/**
 * A two-input crafting table. The result slot is a preview: ingredients are
 * consumed only after the player successfully takes a crafted batch.
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

        this.addSlot(new Slot(inventory, INPUT_ARROW_SLOT, 27, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FletchingRecipeRegistry.isAcceptedArrowInput(stack);
            }
        });
        this.addSlot(new Slot(inventory, INPUT_INGREDIENT_SLOT, 76, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FletchingRecipeRegistry.isModifier(stack);
            }
        });
        this.addSlot(new Slot(inventory, OUTPUT_SLOT, 134, 47) {
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
        return recipe == null
                ? ItemStack.EMPTY
                : FletchingRecipeRegistry.createOutput(recipe, inventory.getItem(INPUT_INGREDIENT_SLOT));
    }

    private void refreshOutput() {
        activeRecipe = getMatchingRecipe();
        refreshingOutput = true;
        try {
            inventory.setItem(OUTPUT_SLOT, getResult(activeRecipe));
        } finally {
            refreshingOutput = false;
        }
        broadcastChanges();
    }

    private void consumeRecipe(FletchingRecipeRegistry.FletchingRecipe recipe) {
        refreshingOutput = true;
        try {
            inventory.removeItem(INPUT_ARROW_SLOT, recipe.arrowCount());
            inventory.removeItem(INPUT_INGREDIENT_SLOT, 1);
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

        ItemStack cursor = getCarried();
        if (!cursor.isEmpty() && !ItemStack.isSameItemSameTags(cursor, result)) {
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
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        if (slotIndex == OUTPUT_SLOT) {
            if (actionType == ClickType.QUICK_MOVE) {
                craftMaximumToInventory();
            } else if (actionType == ClickType.PICKUP) {
                craftToCursor();
            }
            return;
        }

        super.clicked(slotIndex, button, actionType, player);
        if (!refreshingOutput) {
            refreshOutput();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= slots.size()) {
            return ItemStack.EMPTY;
        }
        if (slotIndex == OUTPUT_SLOT) {
            return craftMaximumToInventory();
        }

        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack source = slot.getItem();
        ItemStack moved = source.copy();
        if (slotIndex < OUTPUT_SLOT) {
            if (!moveItemStackTo(source, OUTPUT_SLOT + 1, slots.size(), false)) {
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
        refreshOutput();
        return moved;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(context, player, Blocks.FLETCHING_TABLE);
    }

    @Override
    public void removed(Player player) {
        if (!player.getCommandSenderWorld().isClientSide()) {
            refreshingOutput = true;
            try {
                for (int slot = INPUT_ARROW_SLOT; slot <= INPUT_INGREDIENT_SLOT; slot++) {
                    ItemStack stack = inventory.removeItemNoUpdate(slot);
                    if (!stack.isEmpty()) {
                        player.getInventory().add(stack);
                        if (!stack.isEmpty()) {
                            player.drop(stack, false);
                        }
                    }
                }
                inventory.setItem(OUTPUT_SLOT, ItemStack.EMPTY);
            } finally {
                refreshingOutput = false;
            }
        }
        super.removed(player);
    }

    /** The table is instant; retained for the existing client art API. */
    public int getProgress() {
        return activeRecipe == null ? 0 : 1;
    }

    public int getMaxProgress() {
        return 1;
    }

    public Component getRecipeName() {
        return activeRecipe == null ? Component.empty() : activeRecipe.displayName();
    }

    private void addPlayerSlots(Inventory playerInventory, int x, int y) {
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
