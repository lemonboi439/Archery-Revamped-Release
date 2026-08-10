package me.lemonboi439.archeryRevamped.entity;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
    public static final EntityType<ArcheryArrowEntity> ARCHERY_ARROW = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "archery_arrow"),
            EntityType.Builder.<ArcheryArrowEntity>of(ArcheryArrowEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "archery_arrow")))
    );

    public static final EntityType<ShatteringShardEntity> SHATTERING_SHARD = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "shattering_shard"),
            EntityType.Builder.<ShatteringShardEntity>of(ShatteringShardEntity::new, MobCategory.MISC)
                    .sized(0.18F, 0.18F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "shattering_shard")))
    );

    private ModEntities() {
    }

    public static void register() {
    }
}
