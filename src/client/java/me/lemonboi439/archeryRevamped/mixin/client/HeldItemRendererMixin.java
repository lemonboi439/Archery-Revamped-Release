package me.lemonboi439.archeryRevamped.mixin.client;

import me.lemonboi439.archeryRevamped.item.ModItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Unique
    private boolean archeryRevamped$renderingVariant;

    @Shadow
    protected abstract void renderItem(LivingEntity entity, ItemStack stack,
                                       ModelTransformationMode renderMode, boolean leftHanded,
                                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void archeryRevamped$renderLoadedSpecialArrow(LivingEntity entity, ItemStack stack,
                                                           ModelTransformationMode renderMode, boolean leftHanded,
                                                           MatrixStack matrices,
                                                           VertexConsumerProvider vertexConsumers, int light,
                                                           CallbackInfo ci) {
        if (archeryRevamped$renderingVariant) {
            return;
        }
        int modelData = archeryRevamped$getSpecialArrowModelData(entity, stack);
        if (modelData == 0) {
            return;
        }

        ItemStack visualStack = stack.copy();
        NbtCompound nbt = visualStack.getOrCreateNbt();
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
        boolean loading = entity.isUsingItem() && entity.getActiveItem().getItem() == weapon.getItem();
        if (weapon.getItem() instanceof BowItem) {
            if (!loading) {
                return 0;
            }
            projectile = entity.getProjectileType(weapon);
        } else if (weapon.getItem() instanceof CrossbowItem) {
            if (CrossbowItem.isCharged(weapon)) {
                NbtCompound nbt = weapon.getNbt();
                if (nbt != null && nbt.contains("ChargedProjectiles", NbtElement.LIST_TYPE)
                        && !nbt.getList("ChargedProjectiles", NbtElement.COMPOUND_TYPE).isEmpty()) {
                    projectile = ItemStack.fromNbt(nbt.getList("ChargedProjectiles", NbtElement.COMPOUND_TYPE).getCompound(0));
                }
            } else if (loading) {
                projectile = entity.getProjectileType(weapon);
            }
        }
        return archeryRevamped$modelDataFor(projectile);
    }

    @Unique
    private static int archeryRevamped$modelDataFor(ItemStack projectile) {
        if (projectile.isOf(ModItems.ENDER_ARROW)) return 9101;
        if (projectile.isOf(ModItems.SHOCKWAVE_ARROW)) return 9102;
        if (projectile.isOf(ModItems.IMPULSE_ARROW)) return 9103;
        if (projectile.isOf(ModItems.EXPLOSIVE_ARROW)) return 9104;
        if (projectile.isOf(ModItems.TIDAL_ARROW)) return 9105;
        if (projectile.isOf(ModItems.SHATTERING_ARROW)) return 9106;
        if (projectile.isOf(ModItems.ECHO_ARROW)) return 9107;
        return 0;
    }
}
