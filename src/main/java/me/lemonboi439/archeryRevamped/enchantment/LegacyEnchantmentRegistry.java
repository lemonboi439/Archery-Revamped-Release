package me.lemonboi439.archeryRevamped.enchantment;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/** Forge registration adapter for Minecraft 1.20.1's pre-data-driven enchantment registry. */
public final class LegacyEnchantmentRegistry {
    private LegacyEnchantmentRegistry() {
    }

    public static void register() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(LegacyEnchantmentRegistry::onRegister);
    }

    private static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.ENCHANTMENTS)) {
            return;
        }

        event.register(ForgeRegistries.Keys.ENCHANTMENTS, helper -> {
            RicochetEnchantment.ENCHANTMENT = registerRanged(helper, RicochetEnchantment.ID,
                    Enchantment.Rarity.RARE, RicochetEnchantment.MAX_LEVEL, false);
            OverdrawEnchantment.ENCHANTMENT = registerRanged(helper, OverdrawEnchantment.ID,
                    Enchantment.Rarity.RARE, OverdrawEnchantment.MAX_LEVEL, true);
            LongshotEnchantment.ENCHANTMENT = registerRanged(helper, LongshotEnchantment.ID,
                    Enchantment.Rarity.RARE, LongshotEnchantment.MAX_LEVEL, false);
            FractureEnchantment.ENCHANTMENT = registerRanged(helper, FractureEnchantment.ID,
                    Enchantment.Rarity.VERY_RARE, FractureEnchantment.MAX_LEVEL, false);
            BurstEnchantment.ENCHANTMENT = registerRanged(helper, BurstEnchantment.ID,
                    Enchantment.Rarity.RARE, BurstEnchantment.MAX_LEVEL, false);
            HeadshotEnchantment.ENCHANTMENT = registerRanged(helper, HeadshotEnchantment.ID,
                    Enchantment.Rarity.RARE, HeadshotEnchantment.MAX_LEVEL, false);
        });
    }

    private static Enchantment registerRanged(RegisterEvent.RegisterHelper<Enchantment> helper,
                                              ResourceLocation id, Enchantment.Rarity rarity,
                                              int maxLevel, boolean bowOnly) {
        Enchantment enchantment = new Enchantment(
                rarity, EnchantmentCategory.BOW, new EquipmentSlot[] {EquipmentSlot.MAINHAND}) {
            @Override
            public int getMaxLevel() {
                return maxLevel;
            }

            @Override
            public boolean canEnchant(ItemStack stack) {
                return stack.is(Items.BOW) || (!bowOnly && stack.is(Items.CROSSBOW));
            }
        };
        helper.register(id, enchantment);
        return enchantment;
    }
}
