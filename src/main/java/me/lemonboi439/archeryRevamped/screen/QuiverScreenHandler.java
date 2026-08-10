package me.lemonboi439.archeryRevamped.screen;

import me.lemonboi439.archeryRevamped.quiver.QuiverInventory;
import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Nine quiver slots plus the player's standard inventory. Clicking a quiver stack selects it. */
public final class QuiverScreenHandler extends AbstractContainerMenu {
    private static final int QUIVER_SLOT_COUNT = QuiverManager.SLOT_COUNT;
    private final Container quiverInventory;
    private final ContainerData properties;

    public QuiverScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(QUIVER_SLOT_COUNT), new SimpleContainerData(1));
    }

    public QuiverScreenHandler(int syncId, Inventory playerInventory, Player owner, ItemStack quiver) {
        this(syncId, playerInventory, new QuiverInventory(owner, quiver), selectedProperty(quiver));
    }

    private QuiverScreenHandler(int syncId, Inventory playerInventory, Container quiverInventory,
                                ContainerData properties) {
        super(ModScreenHandlers.QUIVER, syncId);
        checkContainerSize(quiverInventory, QUIVER_SLOT_COUNT);
        checkContainerDataCount(properties, 1);
        this.quiverInventory = quiverInventory;
        this.properties = properties;
        addDataSlots(properties);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int slot = column + row * 3;
                this.addSlot(new Slot(quiverInventory, slot, 62 + column * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return QuiverManager.isArrow(stack);
                    }
                });
            }
        }
        addPlayerSlots(playerInventory, 8, 84);
    }

    private static ContainerData selectedProperty(ItemStack quiver) {
        return new ContainerData() {
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
            public int getCount() {
                return 1;
            }
        };
    }

    @Override
    public void clicked(int slotIndex, int button, net.minecraft.world.inventory.ContainerInput actionType,
                            Player player) {
        if (slotIndex >= 0 && slotIndex < QUIVER_SLOT_COUNT && this.getSlot(slotIndex).hasItem()
                && !player.level().isClientSide()) {
            this.properties.set(0, slotIndex);
            broadcastChanges();
        }
        super.clicked(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= this.slots.size()) {
            return ItemStack.EMPTY;
        }
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack source = slot.getItem();
        ItemStack result = source.copy();
        if (slotIndex < QUIVER_SLOT_COUNT) {
            if (!moveItemStackTo(source, QUIVER_SLOT_COUNT, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(source, 0, QUIVER_SLOT_COUNT, false)) {
            return ItemStack.EMPTY;
        }
        if (source.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.quiverInventory.stillValid(player);
    }

    public int getSelectedSlot() {
        return this.properties.get(0);
    }

    private void addPlayerSlots(Inventory inventory, int x, int y) {
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
