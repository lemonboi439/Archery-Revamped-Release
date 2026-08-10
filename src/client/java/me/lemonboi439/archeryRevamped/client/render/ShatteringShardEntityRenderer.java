package me.lemonboi439.archeryRevamped.client.render;

import me.lemonboi439.archeryRevamped.entity.ShatteringShardEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

/** Small shard projectiles retain vanilla projectile motion and a simple crystal texture. */
public final class ShatteringShardEntityRenderer extends ProjectileEntityRenderer<ShatteringShardEntity> {
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/item/amethyst_shard.png");

    public ShatteringShardEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ShatteringShardEntity entity) {
        return TEXTURE;
    }
}
