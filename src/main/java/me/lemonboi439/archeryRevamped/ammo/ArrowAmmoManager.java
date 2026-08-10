package me.lemonboi439.archeryRevamped.ammo;

import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Handles projectile costs for arrows created after vanilla has fired the
 * original projectile. Vanilla has already consumed the first arrow before
 * custom arrow creation is reached, so this class only charges extra arrows.
 */
public final class ArrowAmmoManager {
    private ArrowAmmoManager() {
    }

    /**
     * Returns true when vanilla's rules make an additional projectile free.
     * This preserves creative mode and Infinity/intangible-projectile behavior.
     */
    public static boolean isFreeExtraProjectile(ServerPlayer shooter,
                                                 ServerLevel world,
                                                 ItemStack weaponStack,
                                                 ItemStack projectileStack) {
        return shooter.hasInfiniteMaterials()
                || projectileStack.has(DataComponents.INTANGIBLE_PROJECTILE);
    }

    /**
     * Checks whether the player can pay for all requested additional arrows.
     */
    public static boolean hasExtraArrows(ServerPlayer shooter, ItemStack projectileStack,
                                         int amount) {
        if (amount <= 0 || shooter.hasInfiniteMaterials()) {
            return true;
        }

        Inventory inventory = shooter.getInventory();
        int available = QuiverManager.countArrow(shooter, projectileStack);
        if (available >= amount) {
            return true;
        }
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack candidate = inventory.getItem(slot);
            if (!candidate.isEmpty()
                    && ItemStack.isSameItemSameComponents(candidate, projectileStack)) {
                available += candidate.getCount();
                if (available >= amount) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Charges all requested extra arrows atomically. It returns false without
     * changing the inventory when the player cannot pay the complete amount.
     */
    public static boolean consumeExtraArrows(ServerPlayer shooter, ItemStack projectileStack,
                                             int amount) {
        if (amount <= 0 || shooter.hasInfiniteMaterials()) {
            return true;
        }
        if (!hasExtraArrows(shooter, projectileStack, amount)) {
            return false;
        }

        Inventory inventory = shooter.getInventory();
        int remaining = amount - QuiverManager.consumeArrow(shooter, projectileStack, amount);
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack candidate = inventory.getItem(slot);
            if (candidate.isEmpty()
                    || !ItemStack.isSameItemSameComponents(candidate, projectileStack)) {
                continue;
            }

            int removed = Math.min(remaining, candidate.getCount());
            inventory.removeItem(slot, removed);
            remaining -= removed;
        }
        inventory.setChanged();
        return remaining == 0;
    }
}
