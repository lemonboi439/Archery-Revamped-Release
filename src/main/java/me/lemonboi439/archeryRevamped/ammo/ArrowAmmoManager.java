package me.lemonboi439.archeryRevamped.ammo;

import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

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
    public static boolean isFreeExtraProjectile(ServerPlayerEntity shooter,
                                                 ServerWorld world,
                                                 ItemStack weaponStack,
                                                 ItemStack projectileStack) {
        return shooter.isInCreativeMode()
                || projectileStack.contains(DataComponentTypes.INTANGIBLE_PROJECTILE);
    }

    /**
     * Checks whether the player can pay for all requested additional arrows.
     */
    public static boolean hasExtraArrows(ServerPlayerEntity shooter, ItemStack projectileStack,
                                         int amount) {
        if (amount <= 0 || shooter.isInCreativeMode()) {
            return true;
        }

        PlayerInventory inventory = shooter.getInventory();
        int available = QuiverManager.countArrow(shooter, projectileStack);
        if (available >= amount) {
            return true;
        }
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack candidate = inventory.getStack(slot);
            if (!candidate.isEmpty()
                    && ItemStack.areItemsAndComponentsEqual(candidate, projectileStack)) {
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
    public static boolean consumeExtraArrows(ServerPlayerEntity shooter, ItemStack projectileStack,
                                             int amount) {
        if (amount <= 0 || shooter.isInCreativeMode()) {
            return true;
        }
        if (!hasExtraArrows(shooter, projectileStack, amount)) {
            return false;
        }

        PlayerInventory inventory = shooter.getInventory();
        int remaining = amount - QuiverManager.consumeArrow(shooter, projectileStack, amount);
        for (int slot = 0; slot < inventory.size() && remaining > 0; slot++) {
            ItemStack candidate = inventory.getStack(slot);
            if (candidate.isEmpty()
                    || !ItemStack.areItemsAndComponentsEqual(candidate, projectileStack)) {
                continue;
            }

            int removed = Math.min(remaining, candidate.getCount());
            inventory.removeStack(slot, removed);
            remaining -= removed;
        }
        inventory.markDirty();
        return remaining == 0;
    }
}
