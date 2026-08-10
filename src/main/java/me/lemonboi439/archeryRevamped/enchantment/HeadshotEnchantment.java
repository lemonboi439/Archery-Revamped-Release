package me.lemonboi439.archeryRevamped.enchantment;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public final class HeadshotEnchantment {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "headshot");
    public static final ResourceKey<Enchantment> KEY = ResourceKey.create(Registries.ENCHANTMENT, ID);
    public static final int MAX_LEVEL = 3;

    private HeadshotEnchantment() {
    }
}
