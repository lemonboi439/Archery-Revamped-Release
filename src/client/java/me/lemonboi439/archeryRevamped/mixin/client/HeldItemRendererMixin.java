package me.lemonboi439.archeryRevamped.mixin.client;

import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Kept as a compatibility placeholder for the shared mixin configuration.
 * 1.21.1 predates data-component item-model overrides; bow loading still uses
 * vanilla geometry while every fired special arrow keeps its own texture.
 */
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
}
