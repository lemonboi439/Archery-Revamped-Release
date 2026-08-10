package me.lemonboi439.archeryRevamped.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import me.lemonboi439.archeryRevamped.item.ModItems;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
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
@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {
    @Unique
    private boolean archeryRevamped$renderingSpecialArrowVariant;

    @Shadow
    protected abstract void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode,
                                       PoseStack matrices, SubmitNodeCollector queue, int light);

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void archeryRevamped$renderLoadedSpecialArrow(
            LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, PoseStack matrices,
            SubmitNodeCollector queue, int light, CallbackInfo ci
    ) {
        if (this.archeryRevamped$renderingSpecialArrowVariant || !(entity instanceof AbstractClientPlayer player)) {
            return;
        }

        Identifier visualModel = archeryRevamped$getVisualModel(player, stack);
        if (visualModel == null) {
            return;
        }

        ItemStack visualStack = stack.copy();
        visualStack.set(DataComponents.ITEM_MODEL, visualModel);
        this.archeryRevamped$renderingSpecialArrowVariant = true;
        try {
            this.renderItem(entity, visualStack, renderMode, matrices, queue, light);
        } finally {
            this.archeryRevamped$renderingSpecialArrowVariant = false;
        }
        ci.cancel();
    }

    @Unique
    private static Identifier archeryRevamped$getVisualModel(AbstractClientPlayer player, ItemStack weapon) {
        ItemStack projectile = ItemStack.EMPTY;
        boolean activelyLoading = player.isUsingItem() && player.getUseItem().getItem() == weapon.getItem();

        if (weapon.getItem() instanceof BowItem) {
            if (!activelyLoading) {
                return null;
            }
            projectile = player.getProjectile(weapon);
        } else if (weapon.getItem() instanceof CrossbowItem) {
            if (CrossbowItem.isCharged(weapon)) {
                ChargedProjectiles charged = weapon.get(DataComponents.CHARGED_PROJECTILES);
                if (charged != null && !charged.isEmpty()) {
                    projectile = charged.itemCopies().getFirst();
                }
            } else if (activelyLoading) {
                projectile = player.getProjectile(weapon);
            }
        } else {
            return null;
        }

        String arrowName = archeryRevamped$getSpecialArrowName(projectile);
        if (arrowName == null) {
            return null;
        }
        String weaponName = weapon.getItem() instanceof BowItem ? "bow" : "crossbow";
        return Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, weaponName + "_" + arrowName + "_arrow");
    }

    @Unique
    private static String archeryRevamped$getSpecialArrowName(ItemStack projectile) {
        if (projectile.is(ModItems.ENDER_ARROW)) return "ender";
        if (projectile.is(ModItems.SHOCKWAVE_ARROW)) return "shockwave";
        if (projectile.is(ModItems.IMPULSE_ARROW)) return "impulse";
        if (projectile.is(ModItems.EXPLOSIVE_ARROW)) return "explosive";
        if (projectile.is(ModItems.TIDAL_ARROW)) return "tidal";
        if (projectile.is(ModItems.SHATTERING_ARROW)) return "shattering";
        if (projectile.is(ModItems.ECHO_ARROW)) return "echo";
        return null;
    }
}
