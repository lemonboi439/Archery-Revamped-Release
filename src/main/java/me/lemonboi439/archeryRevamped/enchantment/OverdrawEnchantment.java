package me.lemonboi439.archeryRevamped.enchantment;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;

public final class OverdrawEnchantment {
    public static final Identifier ID = new Identifier(ArcheryRevamped.MOD_ID, "overdraw");
    public static final int MAX_LEVEL = 2;
    public static final Enchantment ENCHANTMENT = LegacyEnchantmentRegistry.registerRanged(
            ID, Enchantment.Rarity.RARE, MAX_LEVEL, true);

    private OverdrawEnchantment() {
    }
}
