package me.lemonboi439.archeryRevamped.loot;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import java.util.Map;

/** Adds authored enchantment books to late-game loot tables present in 1.20.1. */
public final class LateGameLootManager {
    private static final Map<ResourceLocation, Float> CHANCES = Map.of(
            BuiltInLootTables.END_CITY_TREASURE, 0.20F,
            BuiltInLootTables.BASTION_TREASURE, 0.14F,
            BuiltInLootTables.ANCIENT_CITY, 0.16F,
            BuiltInLootTables.NETHER_BRIDGE, 0.10F,
            BuiltInLootTables.STRONGHOLD_LIBRARY, 0.10F,
            BuiltInLootTables.WOODLAND_MANSION, 0.08F);
    private static boolean registered;

    private LateGameLootManager() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        MinecraftForge.EVENT_BUS.addListener(LateGameLootManager::onLootTableLoad);
    }

    private static void onLootTableLoad(LootTableLoadEvent event) {
        Float chance = CHANCES.get(event.getName());
        if (chance == null) {
            return;
        }

        LootItemFunction.Builder enchant = EnchantRandomlyFunction.randomApplicableEnchantment();
        event.getTable().addPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(Items.BOOK).apply(enchant)).build());
    }
}
