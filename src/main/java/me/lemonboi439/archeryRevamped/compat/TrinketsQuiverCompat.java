package me.lemonboi439.archeryRevamped.compat;

import dev.emi.trinkets.api.TrinketsApi;
import me.lemonboi439.archeryRevamped.item.ModItems;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Optional Trinkets Updated bridge, isolated so the base mod needs no accessory API at runtime. */
public final class TrinketsQuiverCompat {
    private TrinketsQuiverCompat() {
    }

    public static ItemStack getEquippedQuiver(Player player) {
        if (!FabricLoader.getInstance().isModLoaded("trinkets")) {
            return ItemStack.EMPTY;
        }

        return TrinketsApi.getTrinketComponent(player)
                .flatMap(component -> component.getEquipped(ModItems.QUIVER).stream().findFirst())
                .map(entry -> entry.getB())
                .orElse(ItemStack.EMPTY);
    }
}
