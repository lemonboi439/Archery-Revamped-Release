package me.lemonboi439.archeryRevamped.client.render;

import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.ArrowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public final class ArcheryArrowEntityRenderer extends ProjectileEntityRenderer<
        ArcheryArrowEntity, ArcheryArrowEntityRenderState> {
    private static final Identifier VANILLA_TEXTURE = Identifier.of(
            "minecraft", "textures/entity/projectiles/arrow.png"
    );
    private static final Identifier ENDER_TEXTURE = Identifier.of(
            "archery-revamped", "textures/item/ender_arrow.png"
    );
    private final ArrowEntityModel model;

    public ArcheryArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new ArrowEntityModel(context.getPart(EntityModelLayers.ARROW));
    }

    @Override
    public ArcheryArrowEntityRenderState createRenderState() {
        ArcheryArrowEntityRenderState state = new ArcheryArrowEntityRenderState();
        state.texture = VANILLA_TEXTURE;
        return state;
    }

    @Override
    public void updateRenderState(ArcheryArrowEntity entity,
                                  ArcheryArrowEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.texture = textureFor(entity.getArrowType());
        state.tidal = entity.getArrowType() == ArrowType.TIDAL;
        state.tidalSpin = entity.getTidalSpin();
    }

    @Override
    public void render(ArcheryArrowEntityRenderState state, MatrixStack matrices,
                       OrderedRenderCommandQueue queue, CameraRenderState camera) {
        if (!state.tidal) {
            super.render(state, matrices, queue, camera);
            return;
        }

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw - 90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.pitch));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(state.tidalSpin));
        queue.submitModel(model, state, matrices, RenderLayers.entityCutout(state.texture),
                state.light, OverlayTexture.DEFAULT_UV, state.outlineColor, null);
        matrices.pop();
    }

    @Override
    protected Identifier getTexture(ArcheryArrowEntityRenderState state) {
        return state.texture;
    }

    private static Identifier textureFor(ArrowType arrowType) {
        return arrowType == ArrowType.ENDER ? ENDER_TEXTURE : VANILLA_TEXTURE;
    }

}
