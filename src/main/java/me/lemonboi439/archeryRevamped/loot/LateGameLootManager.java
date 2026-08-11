package me.lemonboi439.archeryRevamped.loot;

import me.lemonboi439.archeryRevamped.enchantment.BurstEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.FractureEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.HeadshotEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.LongshotEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.OverdrawEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.RicochetEnchantment;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

/** Adds authored enchantment books to late-game loot tables present in 1.20.1. */
public final class LateGameLootManager {
    private static final List<Enchantment> ENCHANTMENTS = List.of(
            RicochetEnchantment.ENCHANTMENT, OverdrawEnchantment.ENCHANTMENT,
            LongshotEnchantment.ENCHANTMENT, FractureEnchantment.ENCHANTMENT,
            BurstEnchantment.ENCHANTMENT, HeadshotEnchantment.ENCHANTMENT);
    private static final Map<Identifier, Float> CHANCES = Map.of(
            LootTables.END_CITY_TREASURE_CHEST, 0.20F,
            LootTables.BASTION_TREASURE_CHEST, 0.14F,
            LootTables.ANCIENT_CITY_CHEST, 0.16F,
            LootTables.NETHER_BRIDGE_CHEST, 0.10F,
            LootTables.STRONGHOLD_LIBRARY_CHEST, 0.10F,
            LootTables.WOODLAND_MANSION_CHEST, 0.08F);
    private static boolean registered;

    private LateGameLootManager() {
    }

    public static void register() {
        if (registered) return;
        registered = true;
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (!source.isBuiltin() || !CHANCES.containsKey(id)) return;
            LootFunction.Builder enchant = EnchantRandomlyLootFunction.builder();
            tableBuilder.pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(RandomChanceLootCondition.builder(CHANCES.get(id)))
                    .with(ItemEntry.builder(Items.BOOK).apply(enchant)));
        });
    }
}
