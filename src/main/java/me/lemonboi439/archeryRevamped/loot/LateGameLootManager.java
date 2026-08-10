package me.lemonboi439.archeryRevamped.loot;

import me.lemonboi439.archeryRevamped.enchantment.BurstEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.FractureEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.LongshotEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.OverdrawEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.RicochetEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.HeadshotEnchantment;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Adds rare Archery Revamped enchantment books to late-game vanilla chests. */
public final class LateGameLootManager {
    private static final List<ResourceKey<Enchantment>> ENCHANTMENT_KEYS = List.of(
            RicochetEnchantment.KEY,
            OverdrawEnchantment.KEY,
            LongshotEnchantment.KEY,
            FractureEnchantment.KEY,
            BurstEnchantment.KEY,
            HeadshotEnchantment.KEY
    );

    private static final Map<ResourceKey<LootTable>, Float> LOOT_TABLE_CHANCES = Map.ofEntries(
            Map.entry(BuiltInLootTables.END_CITY_TREASURE, 0.20F),
            Map.entry(BuiltInLootTables.BASTION_TREASURE, 0.14F),
            Map.entry(BuiltInLootTables.ANCIENT_CITY, 0.16F),
            Map.entry(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_RARE, 0.18F),
            Map.entry(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_UNIQUE, 0.20F),
            Map.entry(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE, 0.22F),
            Map.entry(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE, 0.24F),
            Map.entry(BuiltInLootTables.NETHER_BRIDGE, 0.10F),
            Map.entry(BuiltInLootTables.STRONGHOLD_LIBRARY, 0.10F),
            Map.entry(BuiltInLootTables.WOODLAND_MANSION, 0.08F)
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
            HolderLookup.Provider registries,
            float chance
    ) {
        var enchantmentLookup = registries.lookup(Registries.ENCHANTMENT);
        if (enchantmentLookup.isEmpty()) {
            return;
        }

        List<Holder<Enchantment>> availableEnchantments = new ArrayList<>();
        for (ResourceKey<Enchantment> enchantmentKey : ENCHANTMENT_KEYS) {
            enchantmentLookup.get().get(enchantmentKey).ifPresent(availableEnchantments::add);
        }
        if (availableEnchantments.isEmpty()) {
            return;
        }

        EnchantRandomlyFunction.Builder enchantmentFunction =
                EnchantRandomlyFunction.randomApplicableEnchantment(registries);
        for (Holder<Enchantment> enchantment : availableEnchantments) {
            enchantmentFunction.withEnchantment(enchantment);
        }

        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(Items.BOOK)
                        .setWeight(1)
                        .apply(enchantmentFunction));
        tableBuilder.withPool(pool);
    }
}
