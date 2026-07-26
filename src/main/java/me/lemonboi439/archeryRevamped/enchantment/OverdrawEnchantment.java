package me.lemonboi439.archeryRevamped.enchantment;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class OverdrawEnchantment {
    public static final Identifier ID = Identifier.of(ArcheryRevamped.MOD_ID, "overdraw");
    public static final RegistryKey<Enchantment> KEY = RegistryKey.of(RegistryKeys.ENCHANTMENT, ID);
    public static final int MAX_LEVEL = 2;

    private OverdrawEnchantment() {
    }
}
