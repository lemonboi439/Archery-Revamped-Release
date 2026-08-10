package me.lemonboi439.archeryRevamped.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import me.lemonboi439.archeryRevamped.entity.ShatteringShardEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Renders each shattering shard as a small amethyst item projectile. */
public final class ShatteringShardEntityRenderer
        extends EntityRenderer<ShatteringShardEntity, ItemEntityRenderState> {
    private final ItemModelResolver itemModelManager;
    private final RandomSource random = RandomSource.create();

    public ShatteringShardEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelResolver();
        this.shadowRadius = 0.0F;
    }

    @Override
    public ItemEntityRenderState createRenderState() {
        return new ItemEntityRenderState();
    }

    @Override
    public void extractRenderState(ShatteringShardEntity entity,
                                  ItemEntityRenderState state, float tickProgress) {
        super.extractRenderState(entity, state, tickProgress);
        state.extractItemGroupRenderState(entity, new ItemStack(Items.AMETHYST_SHARD), itemModelManager);
        state.bobOffset = 0.0F;
        state.count = 1;
    }

    @Override
    public void submit(ItemEntityRenderState state, PoseStack matrices,
                       SubmitNodeCollector queue, CameraRenderState camera) {
        matrices.pushPose();
        matrices.scale(0.38F, 0.38F, 0.38F);
        ItemEntityRenderer.submitMultipleFromCount(matrices, queue, state.lightCoords, state, random);
        matrices.popPose();
        super.submit(state, matrices, queue, camera);
    }
}
