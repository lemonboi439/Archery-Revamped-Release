package me.lemonboi439.archeryRevamped.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.lemonboi439.archeryRevamped.item.ModItems;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Supplies the old CustomModelData predicate consumed by the 1.20.1 bow and
 * crossbow model overrides. The copied stack is visual-only and never reaches
 * inventory or networking code.
 */
@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {
    @Unique
    private boolean archeryRevamped$renderingVariant;

    @Shadow
    protected abstract void renderItem(LivingEntity entity, ItemStack stack,
                                       ItemDisplayContext renderMode, boolean leftHanded,
                                       PoseStack matrices, MultiBufferSource vertexConsumers, int light);

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void archeryRevamped$renderLoadedSpecialArrow(LivingEntity entity, ItemStack stack,
                                                           ItemDisplayContext renderMode, boolean leftHanded,
                                                           PoseStack matrices,
                                                           MultiBufferSource vertexConsumers, int light,
                                                           CallbackInfo ci) {
        if (archeryRevamped$renderingVariant) {
            return;
        }
        int modelData = archeryRevamped$getSpecialArrowModelData(entity, stack);
        if (modelData == 0) {
            return;
        }

        ItemStack visualStack = stack.copy();
        CompoundTag nbt = visualStack.getOrCreateTag();
        nbt.putInt("CustomModelData", modelData);
        archeryRevamped$renderingVariant = true;
        try {
            this.renderItem(entity, visualStack, renderMode, leftHanded, matrices, vertexConsumers, light);
        } finally {
            archeryRevamped$renderingVariant = false;
        }
        ci.cancel();
    }

    @Unique
    private static int archeryRevamped$getSpecialArrowModelData(LivingEntity entity, ItemStack weapon) {
        ItemStack projectile = ItemStack.EMPTY;
        boolean loading = entity.isUsingItem() && entity.getUseItem().getItem() == weapon.getItem();
        if (weapon.getItem() instanceof BowItem) {
            if (!loading) {
                return 0;
            }
            projectile = entity.getProjectile(weapon);
        } else if (weapon.getItem() instanceof CrossbowItem) {
            if (CrossbowItem.isCharged(weapon)) {
                CompoundTag nbt = weapon.getTag();
                if (nbt != null && nbt.contains("ChargedProjectiles", Tag.TAG_LIST)
                        && !nbt.getList("ChargedProjectiles", Tag.TAG_COMPOUND).isEmpty()) {
                    projectile = ItemStack.of(nbt.getList("ChargedProjectiles", Tag.TAG_COMPOUND).getCompound(0));
                }
            } else if (loading) {
                projectile = entity.getProjectile(weapon);
            }
        }
        return archeryRevamped$modelDataFor(projectile);
    }

    @Unique
    private static int archeryRevamped$modelDataFor(ItemStack projectile) {
        if (projectile.is(ModItems.ENDER_ARROW)) return 9101;
        if (projectile.is(ModItems.SHOCKWAVE_ARROW)) return 9102;
        if (projectile.is(ModItems.IMPULSE_ARROW)) return 9103;
        if (projectile.is(ModItems.EXPLOSIVE_ARROW)) return 9104;
        if (projectile.is(ModItems.TIDAL_ARROW)) return 9105;
        if (projectile.is(ModItems.SHATTERING_ARROW)) return 9106;
        if (projectile.is(ModItems.ECHO_ARROW)) return 9107;
        return 0;
    }
}
