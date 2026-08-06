package me.lemonboi439.archeryRevamped.screen;

import me.lemonboi439.archeryRevamped.quiver.QuiverInventory;
import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/** Nine quiver slots plus the player's standard inventory. Clicking a quiver stack selects it. */
public final class QuiverScreenHandler extends ScreenHandler {
    private static final int QUIVER_SLOT_COUNT = QuiverManager.SLOT_COUNT;
    private final Inventory quiverInventory;
    private final PropertyDelegate properties;

    public QuiverScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(QUIVER_SLOT_COUNT), new ArrayPropertyDelegate(1));
    }

    public QuiverScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity owner, ItemStack quiver) {
        this(syncId, playerInventory, new QuiverInventory(owner, quiver), selectedProperty(quiver));
    }

    private QuiverScreenHandler(int syncId, PlayerInventory playerInventory, Inventory quiverInventory,
                                PropertyDelegate properties) {
        super(ModScreenHandlers.QUIVER, syncId);
        checkSize(quiverInventory, QUIVER_SLOT_COUNT);
        checkDataCount(properties, 1);
        this.quiverInventory = quiverInventory;
        this.properties = properties;
        addProperties(properties);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int slot = column + row * 3;
                this.addSlot(new Slot(quiverInventory, slot, 62 + column * 18, 18 + row * 18) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return QuiverManager.isArrow(stack);
                    }
                });
            }
        }
        addPlayerSlots(playerInventory, 8, 84);
    }

    private static PropertyDelegate selectedProperty(ItemStack quiver) {
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return index == 0 ? QuiverManager.getSelectedSlot(quiver) : 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    QuiverManager.setSelectedSlot(quiver, value);
                }
            }

            @Override
            public int size() {
                return 1;
            }
        };
    }

    @Override
    public void onSlotClick(int slotIndex, int button, net.minecraft.screen.slot.SlotActionType actionType,
                            PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < QUIVER_SLOT_COUNT && this.getSlot(slotIndex).hasStack()
                && !player.getEntityWorld().isClient()) {
            this.properties.set(0, slotIndex);
            sendContentUpdates();
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= this.slots.size()) {
            return ItemStack.EMPTY;
        }
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasStack()) {
            return ItemStack.EMPTY;
        }
        ItemStack source = slot.getStack();
        ItemStack result = source.copy();
        if (slotIndex < QUIVER_SLOT_COUNT) {
            if (!insertItem(source, QUIVER_SLOT_COUNT, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!insertItem(source, 0, QUIVER_SLOT_COUNT, false)) {
            return ItemStack.EMPTY;
        }
        if (source.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }
        return result;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.quiverInventory.canPlayerUse(player);
    }

    public int getSelectedSlot() {
        return this.properties.get(0);
    }

    private void addPlayerSlots(PlayerInventory inventory, int x, int y) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, x + column * 18, y + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column, x + column * 18, y + 58));
        }
    }
}
