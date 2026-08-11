package me.lemonboi439.archeryRevamped.client.render;

import me.lemonboi439.archeryRevamped.entity.ShatteringShardEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

/** Small shards use the same light projectile model on 1.20.1. */
public final class ShatteringShardEntityRenderer extends ProjectileEntityRenderer<ShatteringShardEntity> {
    private static final Identifier TEXTURE = new Identifier("archery-revamped", "textures/entity/projectiles/shattering_arrow.png");

    public ShatteringShardEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ShatteringShardEntity shard) {
        return TEXTURE;
    }
}
