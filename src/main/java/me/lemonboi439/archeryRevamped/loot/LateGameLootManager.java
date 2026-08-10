package me.lemonboi439.archeryRevamped.loot;

import me.lemonboi439.archeryRevamped.enchantment.BurstEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.FractureEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.LongshotEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.OverdrawEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.RicochetEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.HeadshotEnchantment;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Adds rare Archery Revamped enchantment books to late-game vanilla chests. */
public final class LateGameLootManager {
    private static final List<RegistryKey<Enchantment>> ENCHANTMENT_KEYS = List.of(
            RicochetEnchantment.KEY,
            OverdrawEnchantment.KEY,
            LongshotEnchantment.KEY,
            FractureEnchantment.KEY,
            BurstEnchantment.KEY,
            HeadshotEnchantment.KEY
    );

    private static final Map<RegistryKey<LootTable>, Float> LOOT_TABLE_CHANCES = Map.ofEntries(
            Map.entry(LootTables.END_CITY_TREASURE_CHEST, 0.20F),
            Map.entry(LootTables.BASTION_TREASURE_CHEST, 0.14F),
            Map.entry(LootTables.ANCIENT_CITY_CHEST, 0.16F),
            Map.entry(LootTables.TRIAL_CHAMBERS_REWARD_RARE_CHEST, 0.18F),
            Map.entry(LootTables.TRIAL_CHAMBERS_REWARD_UNIQUE_CHEST, 0.20F),
            Map.entry(LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE_CHEST, 0.22F),
            Map.entry(LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE_CHEST, 0.24F),
            Map.entry(LootTables.NETHER_BRIDGE_CHEST, 0.10F),
            Map.entry(LootTables.STRONGHOLD_LIBRARY_CHEST, 0.10F),
            Map.entry(LootTables.WOODLAND_MANSION_CHEST, 0.08F)
    );

    private static boolean registered;

    private LateGameLootManager() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) {
                return;
            }

            Float chance = LOOT_TABLE_CHANCES.get(key);
            if (chance == null) {
                return;
            }

            addEnchantmentBookPool(tableBuilder, registries, chance);
        });
    }

    private static void addEnchantmentBookPool(
            LootTable.Builder tableBuilder,
            RegistryWrapper.WrapperLookup registries,
            float chance
    ) {
        var enchantmentLookup = registries.getOptionalWrapper(RegistryKeys.ENCHANTMENT);
        if (enchantmentLookup.isEmpty()) {
            return;
        }

        List<RegistryEntry<Enchantment>> availableEnchantments = new ArrayList<>();
        for (RegistryKey<Enchantment> enchantmentKey : ENCHANTMENT_KEYS) {
            enchantmentLookup.get().getOptional(enchantmentKey).ifPresent(availableEnchantments::add);
        }
        if (availableEnchantments.isEmpty()) {
            return;
        }

        EnchantRandomlyLootFunction.Builder enchantmentFunction =
                EnchantRandomlyLootFunction.builder(registries);
        for (RegistryEntry<Enchantment> enchantment : availableEnchantments) {
            enchantmentFunction.option(enchantment);
        }

        LootPool.Builder pool = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .conditionally(RandomChanceLootCondition.builder(chance))
                .with(ItemEntry.builder(Items.BOOK)
                        .weight(1)
                        .apply(enchantmentFunction));
        tableBuilder.pool(pool);
    }
}
