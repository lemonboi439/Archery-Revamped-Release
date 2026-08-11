package me.lemonboi439.archeryRevamped.trade;

import me.lemonboi439.archeryRevamped.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

/** Villager offers using the 1.20.1 two-argument trade factory API. */
public final class VillagerTradeManager {
    private static final int MAX_USES = 12;
    private static final float PRICE_MULTIPLIER = 0.05F;
    private static boolean registered;

    private VillagerTradeManager() {
    }

    public static void register() {
        if (registered) return;
        registered = true;

        add(VillagerProfession.WEAPONSMITH, 1, 4, Items.ENDER_PEARL, 1, 5);
        add(VillagerProfession.WEAPONSMITH, 2, 2, Items.GUNPOWDER, 2, 10);
        // Wind Charges do not exist in 1.20.1; Firework Stars are the matching shockwave modifier.
        add(VillagerProfession.WEAPONSMITH, 3, 3, Items.FIREWORK_STAR, 2, 15);
        add(VillagerProfession.WEAPONSMITH, 4, 2, Items.IRON_NUGGET, 8, 20);

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CLERIC, 2,
                factories -> factories.add((entity, random) -> emeraldTrade(3, waterPotion(), 10)));
        add(VillagerProfession.CLERIC, 3, 2, Items.ARROW, 4, 15);

        add(VillagerProfession.CARTOGRAPHER, 1, 5, ModItems.ENDER_ARROW, 2, 5);
        add(VillagerProfession.CARTOGRAPHER, 2, 4, ModItems.SHOCKWAVE_ARROW, 4, 10);
        add(VillagerProfession.CARTOGRAPHER, 3, 4, ModItems.IMPULSE_ARROW, 4, 15);
        add(VillagerProfession.CARTOGRAPHER, 4, 5, ModItems.EXPLOSIVE_ARROW, 4, 20);

        add(VillagerProfession.FLETCHER, 2, 3, ModItems.SHOCKWAVE_ARROW, 4, 10);
        add(VillagerProfession.FLETCHER, 3, 4, ModItems.IMPULSE_ARROW, 4, 15);
        add(VillagerProfession.FLETCHER, 4, 5, ModItems.ENDER_ARROW, 2, 20);
        add(VillagerProfession.FLETCHER, 5, 5, ModItems.EXPLOSIVE_ARROW, 4, 30);
    }

    private static void add(VillagerProfession profession, int level, int emeralds,
                            Item result, int amount, int experience) {
        TradeOfferHelper.registerVillagerOffers(profession, level,
                factories -> factories.add((entity, random) ->
                        emeraldTrade(emeralds, new ItemStack(result, amount), experience)));
    }

    private static TradeOffer emeraldTrade(int emeraldCost, ItemStack result, int experience) {
        return new TradeOffer(new ItemStack(Items.EMERALD, emeraldCost), result,
                MAX_USES, experience, PRICE_MULTIPLIER);
    }

    private static ItemStack waterPotion() {
        ItemStack potion = new ItemStack(Items.POTION);
        PotionUtil.setPotion(potion, Potions.WATER);
        return potion;
    }
}
