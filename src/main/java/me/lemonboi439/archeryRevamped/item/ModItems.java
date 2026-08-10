package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;

public final class ModItems {
    private static final Identifier ENDER_ARROW_ID = Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "ender_arrow");
    private static final Identifier SHOCKWAVE_ARROW_ID = Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "shockwave_arrow");
    private static final Identifier IMPULSE_ARROW_ID = Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "impulse_arrow");
    private static final Identifier EXPLOSIVE_ARROW_ID = Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "explosive_arrow");
    private static final Identifier TIDAL_ARROW_ID = Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "tidal_arrow");
    private static final Identifier SHATTERING_ARROW_ID = Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "shattering_arrow");
    private static final Identifier ECHO_ARROW_ID = Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "echo_arrow");
    private static final Identifier QUIVER_ID = Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "quiver");

    public static final Item QUIVER = Registry.register(
            BuiltInRegistries.ITEM,
            QUIVER_ID,
            new QuiverItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, QUIVER_ID))
                    .stacksTo(1))
    );

    public static final Item ENDER_ARROW = Registry.register(
            BuiltInRegistries.ITEM,
            ENDER_ARROW_ID,
            new EnderArrowItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, ENDER_ARROW_ID))
                    .stacksTo(64))
    );

    public static final Item SHOCKWAVE_ARROW = Registry.register(
            BuiltInRegistries.ITEM,
            SHOCKWAVE_ARROW_ID,
            new ShockwaveArrowItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, SHOCKWAVE_ARROW_ID))
                    .stacksTo(64))
    );

    public static final Item IMPULSE_ARROW = Registry.register(
            BuiltInRegistries.ITEM,
            IMPULSE_ARROW_ID,
            new ImpulseArrowItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, IMPULSE_ARROW_ID))
                    .stacksTo(64))
    );

    public static final Item EXPLOSIVE_ARROW = Registry.register(
            BuiltInRegistries.ITEM,
            EXPLOSIVE_ARROW_ID,
            new ExplosiveArrowItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, EXPLOSIVE_ARROW_ID))
                    .stacksTo(64))
    );

    public static final Item TIDAL_ARROW = Registry.register(
            BuiltInRegistries.ITEM,
            TIDAL_ARROW_ID,
            new TidalArrowItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, TIDAL_ARROW_ID))
                    .stacksTo(64))
    );

    public static final Item SHATTERING_ARROW = Registry.register(
            BuiltInRegistries.ITEM,
            SHATTERING_ARROW_ID,
            new ShatteringArrowItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, SHATTERING_ARROW_ID))
                    .stacksTo(64))
    );

    public static final Item ECHO_ARROW = Registry.register(
            BuiltInRegistries.ITEM,
            ECHO_ARROW_ID,
            new EchoArrowItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, ECHO_ARROW_ID))
                    .stacksTo(64))
    );

    private ModItems() {
    }

    public static void register() {
        DispenserBlock.registerProjectileBehavior(ENDER_ARROW);
        DispenserBlock.registerProjectileBehavior(SHOCKWAVE_ARROW);
        DispenserBlock.registerProjectileBehavior(IMPULSE_ARROW);
        DispenserBlock.registerProjectileBehavior(EXPLOSIVE_ARROW);
        DispenserBlock.registerProjectileBehavior(TIDAL_ARROW);
        DispenserBlock.registerProjectileBehavior(SHATTERING_ARROW);
        DispenserBlock.registerProjectileBehavior(ECHO_ARROW);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.accept(QUIVER);
            entries.accept(ENDER_ARROW);
            entries.accept(SHOCKWAVE_ARROW);
            entries.accept(IMPULSE_ARROW);
            entries.accept(EXPLOSIVE_ARROW);
            entries.accept(TIDAL_ARROW);
            entries.accept(SHATTERING_ARROW);
            entries.accept(ECHO_ARROW);
        });
    }
}
