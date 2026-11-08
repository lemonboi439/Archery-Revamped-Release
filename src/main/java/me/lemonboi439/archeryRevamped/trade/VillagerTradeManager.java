package me.lemonboi439.archeryRevamped.trade;

import me.lemonboi439.archeryRevamped.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.MerchantOffer;

/** Adds Archery Revamped materials to the normal villager trade pools. */
public final class VillagerTradeManager {
    private static final int MAX_USES = 12;
    private static final float PRICE_MULTIPLIER = 0.05F;
    private static boolean registered;

    private VillagerTradeManager() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        MinecraftForge.EVENT_BUS.addListener(VillagerTradeManager::onVillagerTrades);
    }

    private static void onVillagerTrades(VillagerTradesEvent event) {
        VillagerProfession profession = event.getType();

        if (profession == VillagerProfession.WEAPONSMITH) {
            add(event, 1, 4, Items.ENDER_PEARL, 1, 5);
            add(event, 2, 2, Items.GUNPOWDER, 2, 10);
            // Wind Charges do not exist in 1.20.1; Firework Stars are the matching shockwave modifier.
            add(event, 3, 3, Items.FIREWORK_STAR, 2, 15);
            add(event, 4, 2, Items.IRON_NUGGET, 8, 20);
            return;
        }

        if (profession == VillagerProfession.CLERIC) {
            event.getTrades().get(2).add((entity, random) -> emeraldTrade(3, waterPotion(), 10));
            add(event, 3, 2, Items.ARROW, 4, 15);
            return;
        }

        if (profession == VillagerProfession.CARTOGRAPHER) {
            add(event, 1, 5, ModItems.ENDER_ARROW, 2, 5);
            add(event, 2, 4, ModItems.SHOCKWAVE_ARROW, 4, 10);
            add(event, 3, 4, ModItems.IMPULSE_ARROW, 4, 15);
            add(event, 4, 5, ModItems.EXPLOSIVE_ARROW, 4, 20);
            return;
        }

        if (profession == VillagerProfession.FLETCHER) {
            add(event, 2, 3, ModItems.SHOCKWAVE_ARROW, 4, 10);
            add(event, 3, 4, ModItems.IMPULSE_ARROW, 4, 15);
            add(event, 4, 5, ModItems.ENDER_ARROW, 2, 20);
            add(event, 5, 5, ModItems.EXPLOSIVE_ARROW, 4, 30);
        }
    }

    private static void add(VillagerTradesEvent event, int level, int emeralds,
                            Item result, int amount, int experience) {
        event.getTrades().computeIfAbsent(level, key -> new java.util.ArrayList<>())
                .add((entity, random) -> emeraldTrade(emeralds, new ItemStack(result, amount), experience));
    }

    private static MerchantOffer emeraldTrade(int emeraldCost, ItemStack result, int experience) {
        return new MerchantOffer(new ItemStack(Items.EMERALD, emeraldCost), result,
                MAX_USES, experience, PRICE_MULTIPLIER);
    }

    private static ItemStack waterPotion() {
        ItemStack potion = new ItemStack(Items.POTION);
        PotionUtils.setPotion(potion, Potions.WATER);
        return potion;
    }
}
