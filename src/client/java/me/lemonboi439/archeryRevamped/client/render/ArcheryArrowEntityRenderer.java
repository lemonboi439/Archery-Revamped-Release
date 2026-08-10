package me.lemonboi439.archeryRevamped.client.render;

import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

/** Uses Minecraft 1.21.1's native crossed-arrow renderer with per-type textures. */
public final class ArcheryArrowEntityRenderer extends ProjectileEntityRenderer<ArcheryArrowEntity> {
    private static final Identifier VANILLA_TEXTURE = Identifier.of("minecraft", "textures/entity/projectiles/arrow.png");
    private static final Identifier ENDER_TEXTURE = Identifier.of("archery-revamped", "textures/entity/projectiles/ender_arrow.png");
    private static final Identifier EXPLOSIVE_TEXTURE = Identifier.of("archery-revamped", "textures/entity/projectiles/explosive_arrow.png");
    private static final Identifier ECHO_TEXTURE = Identifier.of("archery-revamped", "textures/entity/projectiles/echo_arrow.png");
    private static final Identifier SHATTERING_TEXTURE = Identifier.of("archery-revamped", "textures/entity/projectiles/shattering_arrow.png");
    private static final Identifier SHOCKWAVE_TEXTURE = Identifier.of("archery-revamped", "textures/entity/projectiles/shockwave_arrow.png");
    private static final Identifier TIDAL_TEXTURE = Identifier.of("archery-revamped", "textures/entity/projectiles/tidal_arrow.png");
    private static final Identifier IMPULSE_TEXTURE = Identifier.of("archery-revamped", "textures/entity/projectiles/impulse_arrow.png");

    public ArcheryArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ArcheryArrowEntity arrow) {
        return switch (arrow.getArrowType()) {
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
