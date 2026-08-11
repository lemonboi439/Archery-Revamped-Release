package me.lemonboi439.archeryRevamped.enchantment;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;

public final class RicochetEnchantment {
    public static final Identifier ID = new Identifier(ArcheryRevamped.MOD_ID, "ricochet");
    public static final int MAX_LEVEL = 5;
    public static final Enchantment ENCHANTMENT = LegacyEnchantmentRegistry.registerRanged(
            ID, Enchantment.Rarity.RARE, MAX_LEVEL, false);

    private RicochetEnchantment() {
    }
}
