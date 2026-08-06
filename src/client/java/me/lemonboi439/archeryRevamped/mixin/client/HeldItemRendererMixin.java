package me.lemonboi439.archeryRevamped.mixin.client;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import me.lemonboi439.archeryRevamped.item.ModItems;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Selects a visual-only bow/crossbow model when a special arrow is loaded.
 * The copy exists only for rendering, so it cannot change inventory or network state.
 */
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Unique
    private boolean archeryRevamped$renderingSpecialArrowVariant;

    @Shadow
    protected abstract void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode,
                                       MatrixStack matrices, OrderedRenderCommandQueue queue, int light);

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void archeryRevamped$renderLoadedSpecialArrow(
            LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices,
            OrderedRenderCommandQueue queue, int light, CallbackInfo ci
    ) {
        if (this.archeryRevamped$renderingSpecialArrowVariant || !(entity instanceof AbstractClientPlayerEntity player)) {
            return;
        }

        Identifier visualModel = archeryRevamped$getVisualModel(player, stack);
        if (visualModel == null) {
            return;
        }

        ItemStack visualStack = stack.copy();
        visualStack.set(DataComponentTypes.ITEM_MODEL, visualModel);
        this.archeryRevamped$renderingSpecialArrowVariant = true;
        try {
            this.renderItem(entity, visualStack, renderMode, matrices, queue, light);
        } finally {
            this.archeryRevamped$renderingSpecialArrowVariant = false;
        }
        ci.cancel();
    }

    @Unique
    private static Identifier archeryRevamped$getVisualModel(AbstractClientPlayerEntity player, ItemStack weapon) {
        ItemStack projectile = ItemStack.EMPTY;
        boolean activelyLoading = player.isUsingItem() && player.getActiveItem().getItem() == weapon.getItem();

        if (weapon.getItem() instanceof BowItem) {
            if (!activelyLoading) {
                return null;
            }
            projectile = player.getProjectileType(weapon);
        } else if (weapon.getItem() instanceof CrossbowItem) {
            if (CrossbowItem.isCharged(weapon)) {
                ChargedProjectilesComponent charged = weapon.get(DataComponentTypes.CHARGED_PROJECTILES);
                if (charged != null && !charged.getProjectiles().isEmpty()) {
                    projectile = charged.getProjectiles().getFirst();
                }
            } else if (activelyLoading) {
                projectile = player.getProjectileType(weapon);
            }
        } else {
            return null;
        }

        String arrowName = archeryRevamped$getSpecialArrowName(projectile);
        if (arrowName == null) {
            return null;
        }
        String weaponName = weapon.getItem() instanceof BowItem ? "bow" : "crossbow";
        return Identifier.of(ArcheryRevamped.MOD_ID, weaponName + "_" + arrowName + "_arrow");
    }

    @Unique
    private static String archeryRevamped$getSpecialArrowName(ItemStack projectile) {
        if (projectile.isOf(ModItems.ENDER_ARROW)) return "ender";
        if (projectile.isOf(ModItems.SHOCKWAVE_ARROW)) return "shockwave";
        if (projectile.isOf(ModItems.IMPULSE_ARROW)) return "impulse";
        if (projectile.isOf(ModItems.EXPLOSIVE_ARROW)) return "explosive";
        if (projectile.isOf(ModItems.TIDAL_ARROW)) return "tidal";
        if (projectile.isOf(ModItems.SHATTERING_ARROW)) return "shattering";
        if (projectile.isOf(ModItems.ECHO_ARROW)) return "echo";
        return null;
    }
}
