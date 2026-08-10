package me.lemonboi439.archeryRevamped.mixin;

import me.lemonboi439.archeryRevamped.enchantment.RicochetEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.LongshotEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.FractureEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.BurstEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.HeadshotEnchantment;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.burst.BurstArrowHandler;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import me.lemonboi439.archeryRevamped.item.ModItems;
import me.lemonboi439.archeryRevamped.overdraw.OverdrawHandler;
import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.item.ProjectileWeaponItem")
public abstract class RangedWeaponItemMixin {
    @Inject(method = "createProjectile", at = @At("RETURN"), cancellable = true)
    private void archeryRevamped$createRicochetArrow(
            Level world,
            LivingEntity shooter,
            ItemStack weaponStack,
            ItemStack projectileStack,
            boolean critical,
            CallbackInfoReturnable<Projectile> cir
    ) {
        if (!(cir.getReturnValue() instanceof AbstractArrow originalArrow)) {
            return;
        }

        ArcheryArrowEntity arrow = new ArcheryArrowEntity(
                world, shooter, projectileStack.copy(), weaponStack.copy()
        );
        var enchantments = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var ricochet = enchantments.get(RicochetEnchantment.KEY);
        int ricochetLevel = ricochet
                .map(entry -> EnchantmentHelper.getItemEnchantmentLevel(entry, weaponStack))
                .orElse(0);
        arrow.setRicochetLevel(ConfigManager.limitEnchantmentLevel(
                ricochetLevel, RicochetEnchantment.MAX_LEVEL));
        var longshot = enchantments.get(LongshotEnchantment.KEY);
        int longshotLevel = longshot
                .map(entry -> EnchantmentHelper.getItemEnchantmentLevel(entry, weaponStack))
                .orElse(0);
        arrow.setLongshotLevel(ConfigManager.limitEnchantmentLevel(
                longshotLevel, LongshotEnchantment.MAX_LEVEL));
        int fractureLevel = 0;
        if (ConfigManager.isFractureEnabled()) {
            var fracture = enchantments.get(FractureEnchantment.KEY);
            fractureLevel = fracture
                    .map(entry -> EnchantmentHelper.getItemEnchantmentLevel(entry, weaponStack))
                    .orElse(0);
        }
        arrow.setFractureLevel(ConfigManager.limitEnchantmentLevel(
                fractureLevel, FractureEnchantment.MAX_LEVEL));
        var burst = enchantments.get(BurstEnchantment.KEY);
        int burstLevel = burst
                .map(entry -> EnchantmentHelper.getItemEnchantmentLevel(entry, weaponStack))
                .orElse(0);
        burstLevel = ConfigManager.limitEnchantmentLevel(burstLevel, BurstEnchantment.MAX_LEVEL);
        var headshot = enchantments.get(HeadshotEnchantment.KEY);
        int headshotLevel = headshot
                .map(entry -> EnchantmentHelper.getItemEnchantmentLevel(entry, weaponStack))
                .orElse(0);
        arrow.setHeadshotLevel(ConfigManager.limitEnchantmentLevel(
                headshotLevel, HeadshotEnchantment.MAX_LEVEL));
        if (projectileStack.is(ModItems.ENDER_ARROW)) {
            arrow.setArrowType(ArrowType.ENDER);
        } else if (projectileStack.is(ModItems.SHOCKWAVE_ARROW)) {
            arrow.setArrowType(ArrowType.SHOCKWAVE);
        } else if (projectileStack.is(ModItems.IMPULSE_ARROW)) {
            arrow.setArrowType(ArrowType.IMPULSE);
        } else if (projectileStack.is(ModItems.EXPLOSIVE_ARROW)) {
            arrow.setArrowType(ArrowType.EXPLOSIVE);
        } else if (projectileStack.is(ModItems.TIDAL_ARROW)) {
            arrow.setArrowType(ArrowType.TIDAL);
        } else if (projectileStack.is(ModItems.SHATTERING_ARROW)) {
            arrow.setArrowType(ArrowType.SHATTERING);
        } else if (projectileStack.is(ModItems.ECHO_ARROW)) {
            arrow.setArrowType(ArrowType.ECHO);
        }
        arrow.setProjectileStack(originalArrow.getPickupItemStackOrigin().copy());
        arrow.setPos(originalArrow.getX(), originalArrow.getY(), originalArrow.getZ());
        arrow.setDeltaMovement(originalArrow.getDeltaMovement());
        arrow.setFractureReleaseSpeed(originalArrow.getDeltaMovement().length());
        arrow.setCritArrow(originalArrow.isCritArrow());
        arrow.setNoPhysics(originalArrow.isNoPhysics());
        boolean extraAmmoFree = shooter.hasInfiniteMaterials()
                || projectileStack.has(DataComponents.INTANGIBLE_PROJECTILE);
        arrow.setExtraAmmoFree(extraAmmoFree);
        if (!world.isClientSide() && shooter instanceof Player player
                && !extraAmmoFree && QuiverManager.isSelectedArrow(player, projectileStack)) {
            // The selected quiver stack is a render/selection copy, so vanilla
            // cannot decrement the component-backed source itself.
            QuiverManager.consumeArrow(player, projectileStack, 1);
        }
        if (shooter instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            BurstArrowHandler.schedule(arrow, serverPlayer, weaponStack, burstLevel);
        }
        double overdrawBonus = shooter instanceof Player player
                ? OverdrawHandler.consumeDamageBonus(player) : 0.0D;
        if (overdrawBonus > 0.0D) {
            arrow.setOverdrawDamageBonus(overdrawBonus);
        }
        cir.setReturnValue(arrow);
    }
}
