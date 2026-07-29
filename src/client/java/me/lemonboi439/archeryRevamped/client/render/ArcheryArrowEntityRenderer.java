package me.lemonboi439.archeryRevamped.client.render;

import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public final class ArcheryArrowEntityRenderer extends ProjectileEntityRenderer<
        ArcheryArrowEntity, ArcheryArrowEntityRenderState> {
    private static final Identifier VANILLA_TEXTURE = Identifier.of(
            "minecraft", "textures/entity/projectiles/arrow.png"
    );
    private static final Identifier ENDER_TEXTURE = Identifier.of(
            "archery-revamped", "textures/item/ender_arrow.png"
    );

    public ArcheryArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
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
    }

    @Override
    protected Identifier getTexture(ArcheryArrowEntityRenderState state) {
        return state.texture;
    }

    private static Identifier textureFor(ArrowType arrowType) {
        return arrowType == ArrowType.ENDER ? ENDER_TEXTURE : VANILLA_TEXTURE;
    }

}
