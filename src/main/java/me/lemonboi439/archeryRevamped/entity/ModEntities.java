package me.lemonboi439.archeryRevamped.entity;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
    public static EntityType<ArcheryArrowEntity> ARCHERY_ARROW;
    public static EntityType<ShatteringShardEntity> SHATTERING_SHARD;

    private ModEntities() {
    }

    public static void register() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEntities::onRegister);
    }

    private static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.ENTITY_TYPES)) {
            return;
        }

        ResourceLocation arrowId = new ResourceLocation(ArcheryRevamped.MOD_ID, "archery_arrow");
        ResourceLocation shardId = new ResourceLocation(ArcheryRevamped.MOD_ID, "shattering_shard");

        ARCHERY_ARROW = EntityType.Builder.<ArcheryArrowEntity>of(ArcheryArrowEntity::new, MobCategory.MISC)
                .sized(0.5F, 0.5F)
                .clientTrackingRange(4)
                .updateInterval(20)
                .build(arrowId.toString());
        SHATTERING_SHARD = EntityType.Builder.<ShatteringShardEntity>of(ShatteringShardEntity::new, MobCategory.MISC)
                .sized(0.18F, 0.18F)
                .clientTrackingRange(4)
                .updateInterval(10)
                .build(shardId.toString());

        event.register(ForgeRegistries.Keys.ENTITY_TYPES, helper -> {
            helper.register(arrowId, ARCHERY_ARROW);
            helper.register(shardId, SHATTERING_SHARD);
        });
    }
}
