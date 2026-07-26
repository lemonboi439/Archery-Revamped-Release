package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModItems {
    private static final Identifier ENDER_ARROW_ID = Identifier.of(ArcheryRevamped.MOD_ID, "ender_arrow");
    private static final Identifier IMPULSE_ARROW_ID = Identifier.of(ArcheryRevamped.MOD_ID, "impulse_arrow");
    private static final Identifier EXPLOSIVE_ARROW_ID = Identifier.of(ArcheryRevamped.MOD_ID, "explosive_arrow");
    private static final Identifier STICKY_ARROW_ID = Identifier.of(ArcheryRevamped.MOD_ID, "sticky_arrow");

    public static final Item ENDER_ARROW = Registry.register(
            Registries.ITEM,
            ENDER_ARROW_ID,
            new EnderArrowItem(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, ENDER_ARROW_ID))
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

    public static final Item STICKY_ARROW = Registry.register(
            Registries.ITEM,
            STICKY_ARROW_ID,
            new StickyArrowItem(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, STICKY_ARROW_ID))
                    .maxCount(64))
    );

    private ModItems() {
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(ENDER_ARROW);
            entries.add(IMPULSE_ARROW);
            entries.add(EXPLOSIVE_ARROW);
            entries.add(STICKY_ARROW);
        });
    }
}
