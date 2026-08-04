package me.lemonboi439.archeryRevamped.entity;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModEntities {
    public static final EntityType<ArcheryArrowEntity> ARCHERY_ARROW = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(ArcheryRevamped.MOD_ID, "archery_arrow"),
            EntityType.Builder.<ArcheryArrowEntity>create(ArcheryArrowEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(20)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE,
                            Identifier.of(ArcheryRevamped.MOD_ID, "archery_arrow")))
    );

    public static final EntityType<ShatteringShardEntity> SHATTERING_SHARD = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(ArcheryRevamped.MOD_ID, "shattering_shard"),
            EntityType.Builder.<ShatteringShardEntity>create(ShatteringShardEntity::new, SpawnGroup.MISC)
                    .dimensions(0.18F, 0.18F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE,
                            Identifier.of(ArcheryRevamped.MOD_ID, "shattering_shard")))
    );

    private ModEntities() {
    }

    public static void register() {
    }
}
