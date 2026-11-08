package me.lemonboi439.archeryRevamped.enchantment;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public final class RicochetEnchantment {
    public static final ResourceLocation ID = new ResourceLocation(ArcheryRevamped.MOD_ID, "ricochet");
    public static final int MAX_LEVEL = 5;
    public static Enchantment ENCHANTMENT;

    private RicochetEnchantment() {
    }
}
