package me.lemonboi439.archeryRevamped.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.projectile.ArrowModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/** Draws every custom arrow with Minecraft's crossed-arrow model and its matching texture. */
public final class ArcheryArrowEntityRenderer extends ArrowRenderer<ArcheryArrowEntity, ArcheryArrowEntityRenderState> {
    private static final Identifier VANILLA_TEXTURE = texture("minecraft", "arrow");
    private static final Identifier TIPPED_TEXTURE = texture("minecraft", "tipped_arrow");
    private static final Identifier ENDER_TEXTURE = texture("archery-revamped", "ender_arrow");
    private static final Identifier EXPLOSIVE_TEXTURE = texture("archery-revamped", "explosive_arrow");
    private static final Identifier ECHO_TEXTURE = texture("archery-revamped", "echo_arrow");
    private static final Identifier SHATTERING_TEXTURE = texture("archery-revamped", "shattering_arrow");
    private static final Identifier SHOCKWAVE_TEXTURE = texture("archery-revamped", "shockwave_arrow");
    private static final Identifier TIDAL_TEXTURE = texture("archery-revamped", "tidal_arrow");
    private static final Identifier IMPULSE_TEXTURE = texture("archery-revamped", "impulse_arrow");
    private static final Map<Integer, Identifier> TIPPED_TEXTURES = new HashMap<>();

    private final ArrowModel model;

    public ArcheryArrowEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ArrowModel(context.bakeLayer(ModelLayers.ARROW));
    }

    @Override
    public ArcheryArrowEntityRenderState createRenderState() {
        ArcheryArrowEntityRenderState state = new ArcheryArrowEntityRenderState();
        state.texture = VANILLA_TEXTURE;
        return state;
    }

    @Override
    public void extractRenderState(ArcheryArrowEntity arrow, ArcheryArrowEntityRenderState state, float tickProgress) {
        super.extractRenderState(arrow, state, tickProgress);
        state.tipped = arrow.getArrowType() == ArrowType.NORMAL && arrow.hasPotionContents();
        state.potionColor = arrow.getPotionColor();
        state.texture = state.tipped ? tippedTextureFor(state.potionColor) : textureFor(arrow.getArrowType());
        state.tidal = arrow.getArrowType() == ArrowType.TIDAL;
        state.tidalSpin = arrow.getTidalSpin();
    }

    @Override
    public void submit(ArcheryArrowEntityRenderState state, PoseStack matrices,
                       SubmitNodeCollector queue, CameraRenderState camera) {
        if (!state.tidal) {
            super.submit(state, matrices, queue, camera);
            return;
        }

        // ArrowRenderer has no axial rotation hook. Reproduce its normal pose
        // and add a roll around the arrow's travel axis for the tidal torpedo.
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        matrices.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        matrices.mulPose(Axis.XP.rotationDegrees(state.tidalSpin));
        queue.submitModel(model, state, matrices, state.texture, state.lightCoords,
                OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        matrices.popPose();
    }

    @Override
    protected Identifier getTextureLocation(ArcheryArrowEntityRenderState state) {
        return state.texture;
    }

    private static Identifier texture(String namespace, String name) {
        return Identifier.fromNamespaceAndPath(namespace, "textures/entity/projectiles/" + name + ".png");
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

    private static Identifier tippedTextureFor(int potionColor) {
        // Potion colours are opaque ARGB values. Their alpha byte makes most
        // valid colours negative as signed ints; only -1 is our no-potion
        // sentinel from ArcheryArrowEntity.
        if (potionColor == -1) {
            return TIPPED_TEXTURE;
        }
        return TIPPED_TEXTURES.computeIfAbsent(potionColor, ArcheryArrowEntityRenderer::createTippedTexture);
    }

    private static Identifier createTippedTexture(int potionColor) {
        Identifier identifier = Identifier.fromNamespaceAndPath("archery-revamped",
                "dynamic/tipped_arrow_" + Integer.toUnsignedString(potionColor, 16));
        Minecraft client = Minecraft.getInstance();
        try {
            var resource = client.getResourceManager().getResource(VANILLA_TEXTURE).orElse(null);
            if (resource == null) {
                return TIPPED_TEXTURE;
            }

            try (InputStream input = resource.open(); NativeImage source = NativeImage.read(input)) {
                NativeImage tinted = new NativeImage(source.getWidth(), source.getHeight(), true);
                int tintRed = potionColor >> 16 & 0xFF;
                int tintGreen = potionColor >> 8 & 0xFF;
                int tintBlue = potionColor & 0xFF;

                for (int y = 0; y < source.getHeight(); y++) {
                    for (int x = 0; x < source.getWidth(); x++) {
                        int pixel = source.getPixel(x, y);
                        int alpha = pixel & 0xFF000000;
                        if (alpha == 0) {
                            continue;
                        }
                        float brightness = ((pixel & 0xFF) + (pixel >> 8 & 0xFF) + (pixel >> 16 & 0xFF)) / 765.0F;
                        float shade = 0.35F + brightness * 0.65F;
                        int red = Math.round(tintRed * shade);
                        int green = Math.round(tintGreen * shade);
                        int blue = Math.round(tintBlue * shade);
                        tinted.setPixel(x, y, alpha | red << 16 | green << 8 | blue);
                    }
                }

                DynamicTexture texture = new DynamicTexture(
                        () -> "archery-revamped tipped arrow", tinted);
                // TextureManager#register only stores a DynamicTexture in
                // 26.1; it no longer uploads its pixels. Upload first so the
                // arrow model never samples an uninitialised GPU texture.
                texture.upload();
                client.getTextureManager().register(identifier, texture);
                return identifier;
            }
        } catch (IOException exception) {
            return TIPPED_TEXTURE;
        }
    }
}
