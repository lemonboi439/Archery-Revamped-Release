package me.lemonboi439.archeryRevamped.enchantment;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;

public final class BurstEnchantment {
    public static final Identifier ID = new Identifier(ArcheryRevamped.MOD_ID, "burst");
    public static final int MAX_LEVEL = 3;
    public static final Enchantment ENCHANTMENT = LegacyEnchantmentRegistry.registerRanged(
            ID, Enchantment.Rarity.RARE, MAX_LEVEL, false);

    private BurstEnchantment() {
    }
}
