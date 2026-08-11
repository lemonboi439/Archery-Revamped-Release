package me.lemonboi439.archeryRevamped.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** Java registration adapter for Minecraft 1.20.1's pre-data-driven enchantment registry. */
public final class LegacyEnchantmentRegistry {
    private LegacyEnchantmentRegistry() {
    }

    public static Enchantment registerRanged(Identifier id, Enchantment.Rarity rarity, int maxLevel, boolean bowOnly) {
        return Registry.register(Registries.ENCHANTMENT, id, new Enchantment(
                rarity, EnchantmentTarget.BOW, new EquipmentSlot[] {EquipmentSlot.MAINHAND}) {
            @Override
            public int getMaxLevel() {
                return maxLevel;
            }

            @Override
            public boolean isAcceptableItem(ItemStack stack) {
                return stack.isOf(Items.BOW) || (!bowOnly && stack.isOf(Items.CROSSBOW));
            }
        });
    }

    public static void register() {
        // Referencing each holder registers every legacy enchantment exactly once.
        RicochetEnchantment.ENCHANTMENT.toString();
        OverdrawEnchantment.ENCHANTMENT.toString();
        LongshotEnchantment.ENCHANTMENT.toString();
        FractureEnchantment.ENCHANTMENT.toString();
        BurstEnchantment.ENCHANTMENT.toString();
        HeadshotEnchantment.ENCHANTMENT.toString();
    }
}
