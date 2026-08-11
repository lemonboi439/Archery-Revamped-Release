package me.lemonboi439.archeryRevamped.client.render;

import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

/** 1.20.1 renderer: inherits Minecraft's crossed-arrow model and swaps only its texture. */
public final class ArcheryArrowEntityRenderer extends ProjectileEntityRenderer<ArcheryArrowEntity> {
    private static final Identifier VANILLA = new Identifier("minecraft", "textures/entity/projectiles/arrow.png");
    private static final Identifier ENDER = new Identifier("archery-revamped", "textures/entity/projectiles/ender_arrow.png");
    private static final Identifier EXPLOSIVE = new Identifier("archery-revamped", "textures/entity/projectiles/explosive_arrow.png");
    private static final Identifier ECHO = new Identifier("archery-revamped", "textures/entity/projectiles/echo_arrow.png");
    private static final Identifier SHATTERING = new Identifier("archery-revamped", "textures/entity/projectiles/shattering_arrow.png");
    private static final Identifier SHOCKWAVE = new Identifier("archery-revamped", "textures/entity/projectiles/shockwave_arrow.png");
    private static final Identifier TIDAL = new Identifier("archery-revamped", "textures/entity/projectiles/tidal_arrow.png");
    private static final Identifier IMPULSE = new Identifier("archery-revamped", "textures/entity/projectiles/impulse_arrow.png");

    public ArcheryArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ArcheryArrowEntity arrow) {
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
