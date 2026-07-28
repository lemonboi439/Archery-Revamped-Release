package me.lemonboi439.archeryRevamped.trade;

import me.lemonboi439.archeryRevamped.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;

/**
 * Adds Archery Revamped materials to the normal villager trade pools.
 *
 * <p>Fabric 1.21.11 exposes the villager trade event through
 * {@link TradeOfferHelper}; each factory is attached to a profession level
 * and is therefore applied when that villager generates its offers.</p>
 */
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

        registerWeaponsmithTrades();
        registerClericTrades();
        registerCartographerTrades();
    }

    private static void registerWeaponsmithTrades() {
        // Novice: a reliable source of Ender Pearls for Ender Arrow recipes.
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 1, factories ->
                factories.add((world, entity, random) -> emeraldTrade(
                        4, new ItemStack(Items.ENDER_PEARL), 5
                ))
        );

        // Apprentice: Gunpowder for Explosive Arrows.
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 2, factories ->
                factories.add((world, entity, random) -> emeraldTrade(
                        2, new ItemStack(Items.GUNPOWDER, 2), 10
                ))
        );

        // Journeyman: Honeycomb for Sticky Arrows.
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 3, factories ->
                factories.add((world, entity, random) -> emeraldTrade(
                        3, new ItemStack(Items.HONEYCOMB, 2), 15
                ))
        );
    }

    private static void registerClericTrades() {
        // A water potion is a valid fletching-table potion ingredient and
        // keeps the trade useful without choosing a particular tipped effect.
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CLERIC, 2, factories ->
                factories.add((world, entity, random) -> emeraldTrade(
                        3, createWaterPotionStack(), 10
                ))
        );

        // Clerics also provide the arrows needed by the tipped-arrow recipe.
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CLERIC, 3, factories ->
                factories.add((world, entity, random) -> emeraldTrade(
                        2, new ItemStack(Items.ARROW, 4), 15
                ))
        );
    }

    private static void registerCartographerTrades() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CARTOGRAPHER, 1, factories ->
                factories.add((world, entity, random) -> emeraldTrade(
                        5, new ItemStack(ModItems.ENDER_ARROW, 2), 5
                ))
        );

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CARTOGRAPHER, 2, factories ->
                factories.add((world, entity, random) -> emeraldTrade(
                        4, new ItemStack(ModItems.IMPULSE_ARROW, 4), 10
                ))
        );

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CARTOGRAPHER, 3, factories ->
                factories.add((world, entity, random) -> emeraldTrade(
                        5, new ItemStack(ModItems.EXPLOSIVE_ARROW, 4), 15
                ))
        );

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CARTOGRAPHER, 4, factories ->
                factories.add((world, entity, random) -> emeraldTrade(
                        6, new ItemStack(ModItems.STICKY_ARROW, 4), 20
                ))
        );
    }

    private static TradeOffer emeraldTrade(int emeraldCost, ItemStack result, int experience) {
        return new TradeOffer(
                new TradedItem(Items.EMERALD, emeraldCost),
                result,
                MAX_USES,
                experience,
                PRICE_MULTIPLIER
        );
    }

    private static ItemStack createWaterPotionStack() {
        ItemStack potion = new ItemStack(Items.POTION);
        potion.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.WATER));
        return potion;
    }
}
