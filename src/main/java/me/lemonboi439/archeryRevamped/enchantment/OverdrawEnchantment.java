package me.lemonboi439.archeryRevamped.enchantment;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public final class OverdrawEnchantment {
    public static final ResourceLocation ID = new ResourceLocation(ArcheryRevamped.MOD_ID, "overdraw");
    public static final int MAX_LEVEL = 2;
    public static Enchantment ENCHANTMENT;

    private OverdrawEnchantment() {
    }
}
