package me.lemonboi439.archeryRevamped;

import me.lemonboi439.archeryRevamped.entity.ModEntities;
import me.lemonboi439.archeryRevamped.item.ModItems;
import me.lemonboi439.archeryRevamped.item.ModItemGroups;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.arrow.ArrowBehaviorRegistry;
import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import me.lemonboi439.archeryRevamped.arrow.EnderArrowBehavior;
import me.lemonboi439.archeryRevamped.arrow.ImpulseArrowBehavior;
import me.lemonboi439.archeryRevamped.arrow.ShockwaveArrowBehavior;
import me.lemonboi439.archeryRevamped.arrow.ExplosiveArrowBehavior;
import me.lemonboi439.archeryRevamped.arrow.TidalArrowBehavior;
import me.lemonboi439.archeryRevamped.arrow.ShatteringArrowBehavior;
import me.lemonboi439.archeryRevamped.arrow.EchoArrowBehavior;
import me.lemonboi439.archeryRevamped.overdraw.OverdrawHandler;
import me.lemonboi439.archeryRevamped.fracture.FractureScheduler;
import me.lemonboi439.archeryRevamped.burst.BurstArrowHandler;
import me.lemonboi439.archeryRevamped.arrow.RicochetBehavior;
import me.lemonboi439.archeryRevamped.screen.ModScreenHandlers;
import me.lemonboi439.archeryRevamped.screen.RecipeViewerCompat;
import me.lemonboi439.archeryRevamped.command.ArcheryCommand;
import me.lemonboi439.archeryRevamped.trade.VillagerTradeManager;
import me.lemonboi439.archeryRevamped.loot.LateGameLootManager;
import me.lemonboi439.archeryRevamped.debug.TrajectoryNetworking;
import me.lemonboi439.archeryRevamped.component.ModDataComponents;
import me.lemonboi439.archeryRevamped.quiver.QuiverNetworking;
import me.lemonboi439.archeryRevamped.enchantment.LegacyEnchantmentRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ArcheryRevamped implements ModInitializer {
    public static final String MOD_ID = "archery-revamped";

    @Override
    public void onInitialize() {
        ConfigManager.load();
        LegacyEnchantmentRegistry.register();
        ModDataComponents.register();
        ModEntities.register();
        ModItems.register();
        ModItemGroups.register();
        RecipeViewerCompat.register();
        VillagerTradeManager.register();
        LateGameLootManager.register();
        ModScreenHandlers.register();
        ArrowBehaviorRegistry.register(ArrowType.NORMAL, new RicochetBehavior());
        ArrowBehaviorRegistry.register(ArrowType.ENDER, new EnderArrowBehavior());
        ArrowBehaviorRegistry.register(ArrowType.SHOCKWAVE, new ShockwaveArrowBehavior());
        ArrowBehaviorRegistry.register(ArrowType.IMPULSE, new ImpulseArrowBehavior());
        ArrowBehaviorRegistry.register(ArrowType.EXPLOSIVE, new ExplosiveArrowBehavior());
        ArrowBehaviorRegistry.register(ArrowType.TIDAL, new TidalArrowBehavior());
        ArrowBehaviorRegistry.register(ArrowType.SHATTERING, new ShatteringArrowBehavior());
        ArrowBehaviorRegistry.register(ArrowType.ECHO, new EchoArrowBehavior());
        OverdrawHandler.register();
        FractureScheduler.register();
        BurstArrowHandler.register();
        TrajectoryNetworking.register();
        QuiverNetworking.register();
        ArcheryCommand.register();
    }
}
