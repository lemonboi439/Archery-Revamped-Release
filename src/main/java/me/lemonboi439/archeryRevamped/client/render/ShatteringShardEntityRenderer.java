package me.lemonboi439.archeryRevamped.client.render;

import me.lemonboi439.archeryRevamped.entity.ShatteringShardEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/** Small shards use the same light projectile model on 1.20.1. */
public final class ShatteringShardEntityRenderer extends ArrowRenderer<ShatteringShardEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("archery-revamped", "textures/entity/projectiles/shattering_arrow.png");

    public ShatteringShardEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ShatteringShardEntity shard) {
        return TEXTURE;
    }
}
