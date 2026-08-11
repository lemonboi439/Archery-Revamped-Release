package me.lemonboi439.archeryRevamped.enchantment;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;

public final class LongshotEnchantment {
    public static final Identifier ID = new Identifier(ArcheryRevamped.MOD_ID, "longshot");
    public static final int MAX_LEVEL = 1;
    public static final Enchantment ENCHANTMENT = LegacyEnchantmentRegistry.registerRanged(
            ID, Enchantment.Rarity.RARE, MAX_LEVEL, false);

    private LongshotEnchantment() {
    }
}
