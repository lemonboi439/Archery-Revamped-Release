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
            "archery-revamped", "textures/entity/projectiles/ender_arrow.png"
    );
    private static final Identifier EXPLOSIVE_TEXTURE = Identifier.of(
            "archery-revamped", "textures/entity/projectiles/explosive_arrow.png"
    );
    private static final Identifier ECHO_TEXTURE = Identifier.of(
            "archery-revamped", "textures/entity/projectiles/echo_arrow.png"
    );
    private static final Identifier SHATTERING_TEXTURE = Identifier.of(
            "archery-revamped", "textures/entity/projectiles/shattering_arrow.png"
    );
    private static final Identifier SHOCKWAVE_TEXTURE = Identifier.of(
            "archery-revamped", "textures/entity/projectiles/shockwave_arrow.png"
    );
    private static final Identifier TIDAL_TEXTURE = Identifier.of(
            "archery-revamped", "textures/entity/projectiles/tidal_arrow.png"
    );
    private static final Identifier IMPULSE_TEXTURE = Identifier.of(
            "archery-revamped", "textures/entity/projectiles/impulse_arrow.png"
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
        // Always use Minecraft's crossed arrow model. The texture is selected
        // per arrow type in updateRenderState, so special arrows keep the
        // vanilla in-flight shape while showing their own fired-arrow texture.
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw - 90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.pitch));
        if (state.tidal) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(state.tidalSpin));
        }
        queue.submitModel(model, state, matrices, RenderLayers.entityCutout(state.texture),
                state.light, OverlayTexture.DEFAULT_UV, state.outlineColor, null);
        matrices.pop();
    }

    @Override
    protected Identifier getTexture(ArcheryArrowEntityRenderState state) {
        return state.texture;
    }

    private static Identifier textureFor(ArrowType arrowType) {
        return switch (arrowType) {
            case ENDER -> ENDER_TEXTURE;
            case EXPLOSIVE -> EXPLOSIVE_TEXTURE;
            case ECHO -> ECHO_TEXTURE;
            case SHATTERING -> SHATTERING_TEXTURE;
            case SHOCKWAVE -> SHOCKWAVE_TEXTURE;
            case IMPULSE -> IMPULSE_TEXTURE;
            case TIDAL -> TIDAL_TEXTURE;
            default -> VANILLA_TEXTURE;
        };
    }

}
