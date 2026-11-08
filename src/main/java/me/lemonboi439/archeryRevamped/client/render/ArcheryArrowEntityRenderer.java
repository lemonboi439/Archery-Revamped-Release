package me.lemonboi439.archeryRevamped.client.render;

import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/** 1.20.1 renderer: inherits Minecraft's crossed-arrow model and swaps only its texture. */
public final class ArcheryArrowEntityRenderer extends ArrowRenderer<ArcheryArrowEntity> {
    private static final ResourceLocation VANILLA = new ResourceLocation("minecraft", "textures/entity/projectiles/arrow.png");
    private static final ResourceLocation ENDER = new ResourceLocation("archery-revamped", "textures/entity/projectiles/ender_arrow.png");
    private static final ResourceLocation EXPLOSIVE = new ResourceLocation("archery-revamped", "textures/entity/projectiles/explosive_arrow.png");
    private static final ResourceLocation ECHO = new ResourceLocation("archery-revamped", "textures/entity/projectiles/echo_arrow.png");
    private static final ResourceLocation SHATTERING = new ResourceLocation("archery-revamped", "textures/entity/projectiles/shattering_arrow.png");
    private static final ResourceLocation SHOCKWAVE = new ResourceLocation("archery-revamped", "textures/entity/projectiles/shockwave_arrow.png");
    private static final ResourceLocation TIDAL = new ResourceLocation("archery-revamped", "textures/entity/projectiles/tidal_arrow.png");
    private static final ResourceLocation IMPULSE = new ResourceLocation("archery-revamped", "textures/entity/projectiles/impulse_arrow.png");

    public ArcheryArrowEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ArcheryArrowEntity arrow) {
        return switch (arrow.getArrowType()) {
            case ENDER -> ENDER;
            case EXPLOSIVE -> EXPLOSIVE;
            case ECHO -> ECHO;
            case SHATTERING -> SHATTERING;
            case SHOCKWAVE -> SHOCKWAVE;
            case IMPULSE -> IMPULSE;
            case TIDAL -> TIDAL;
            default -> VANILLA;
        };
    }
}
