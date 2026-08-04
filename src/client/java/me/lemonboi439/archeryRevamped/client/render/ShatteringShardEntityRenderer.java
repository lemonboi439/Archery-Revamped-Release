package me.lemonboi439.archeryRevamped.client.render;

import me.lemonboi439.archeryRevamped.entity.ShatteringShardEntity;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;

/** Renders each shattering shard as a small amethyst item projectile. */
public final class ShatteringShardEntityRenderer
        extends EntityRenderer<ShatteringShardEntity, ItemEntityRenderState> {
    private final ItemModelManager itemModelManager;
    private final Random random = Random.create();

    public ShatteringShardEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelManager();
        this.shadowRadius = 0.0F;
    }

    @Override
    public ItemEntityRenderState createRenderState() {
        return new ItemEntityRenderState();
    }

    @Override
    public void updateRenderState(ShatteringShardEntity entity,
                                  ItemEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.update(entity, new ItemStack(Items.AMETHYST_SHARD), itemModelManager);
        state.uniqueOffset = 0.0F;
        state.renderedAmount = 1;
    }

    @Override
    public void render(ItemEntityRenderState state, MatrixStack matrices,
                       OrderedRenderCommandQueue queue, CameraRenderState camera) {
        matrices.push();
        matrices.scale(0.38F, 0.38F, 0.38F);
        ItemEntityRenderer.render(matrices, queue, state.light, state, random);
        matrices.pop();
        super.render(state, matrices, queue, camera);
    }
}
