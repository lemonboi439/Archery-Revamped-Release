package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.block.DispenserBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModItems {
    private static final Identifier ENDER_ARROW_ID = Identifier.of(ArcheryRevamped.MOD_ID, "ender_arrow");
    private static final Identifier SHOCKWAVE_ARROW_ID = Identifier.of(ArcheryRevamped.MOD_ID, "shockwave_arrow");
    private static final Identifier IMPULSE_ARROW_ID = Identifier.of(ArcheryRevamped.MOD_ID, "impulse_arrow");
    private static final Identifier EXPLOSIVE_ARROW_ID = Identifier.of(ArcheryRevamped.MOD_ID, "explosive_arrow");
    private static final Identifier TIDAL_ARROW_ID = Identifier.of(ArcheryRevamped.MOD_ID, "tidal_arrow");
    private static final Identifier SHATTERING_ARROW_ID = Identifier.of(ArcheryRevamped.MOD_ID, "shattering_arrow");
    private static final Identifier ECHO_ARROW_ID = Identifier.of(ArcheryRevamped.MOD_ID, "echo_arrow");

    public static final Item ENDER_ARROW = Registry.register(
            Registries.ITEM,
            ENDER_ARROW_ID,
            new EnderArrowItem(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, ENDER_ARROW_ID))
                    .maxCount(64))
    );

    public static final Item SHOCKWAVE_ARROW = Registry.register(
            Registries.ITEM,
            SHOCKWAVE_ARROW_ID,
            new ShockwaveArrowItem(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, SHOCKWAVE_ARROW_ID))
                    .maxCount(64))
    );

    public static final Item IMPULSE_ARROW = Registry.register(
            Registries.ITEM,
            IMPULSE_ARROW_ID,
            new ImpulseArrowItem(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, IMPULSE_ARROW_ID))
                    .maxCount(64))
    );

    public static final Item EXPLOSIVE_ARROW = Registry.register(
            Registries.ITEM,
            EXPLOSIVE_ARROW_ID,
            new ExplosiveArrowItem(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, EXPLOSIVE_ARROW_ID))
                    .maxCount(64))
    );

    public static final Item TIDAL_ARROW = Registry.register(
            Registries.ITEM,
            TIDAL_ARROW_ID,
            new TidalArrowItem(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, TIDAL_ARROW_ID))
                    .maxCount(64))
    );

    public static final Item SHATTERING_ARROW = Registry.register(
            Registries.ITEM,
            SHATTERING_ARROW_ID,
            new ShatteringArrowItem(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, SHATTERING_ARROW_ID))
                    .maxCount(64))
    );

    public static final Item ECHO_ARROW = Registry.register(
            Registries.ITEM,
            ECHO_ARROW_ID,
            new EchoArrowItem(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, ECHO_ARROW_ID))
                    .maxCount(64))
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

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(ENDER_ARROW);
            entries.add(SHOCKWAVE_ARROW);
            entries.add(IMPULSE_ARROW);
            entries.add(EXPLOSIVE_ARROW);
            entries.add(TIDAL_ARROW);
            entries.add(SHATTERING_ARROW);
            entries.add(ECHO_ARROW);
        });
    }
}
