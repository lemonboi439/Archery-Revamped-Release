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
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.item.RangedWeaponItem")
public abstract class RangedWeaponItemMixin {
    @Inject(method = "createArrowEntity", at = @At("RETURN"), cancellable = true)
    private void archeryRevamped$createRicochetArrow(
            World world,
            LivingEntity shooter,
            ItemStack weaponStack,
            ItemStack projectileStack,
            boolean critical,
            CallbackInfoReturnable<ProjectileEntity> cir
    ) {
        if (!(cir.getReturnValue() instanceof PersistentProjectileEntity originalArrow)) {
            return;
        }

        ArcheryArrowEntity arrow = new ArcheryArrowEntity(
                world, shooter, projectileStack.copy(), weaponStack.copy()
        );
        var enchantments = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        var ricochet = enchantments.getOptional(RicochetEnchantment.KEY);
        int ricochetLevel = ricochet
                .map(entry -> EnchantmentHelper.getLevel(entry, weaponStack))
                .orElse(0);
        arrow.setRicochetLevel(ConfigManager.limitEnchantmentLevel(
                ricochetLevel, RicochetEnchantment.MAX_LEVEL));
        var longshot = enchantments.getOptional(LongshotEnchantment.KEY);
        int longshotLevel = longshot
                .map(entry -> EnchantmentHelper.getLevel(entry, weaponStack))
                .orElse(0);
        arrow.setLongshotLevel(ConfigManager.limitEnchantmentLevel(
                longshotLevel, LongshotEnchantment.MAX_LEVEL));
        int fractureLevel = 0;
        if (ConfigManager.isFractureEnabled()) {
            var fracture = enchantments.getOptional(FractureEnchantment.KEY);
            fractureLevel = fracture
                    .map(entry -> EnchantmentHelper.getLevel(entry, weaponStack))
                    .orElse(0);
        }
        arrow.setFractureLevel(ConfigManager.limitEnchantmentLevel(
                fractureLevel, FractureEnchantment.MAX_LEVEL));
        var burst = enchantments.getOptional(BurstEnchantment.KEY);
        int burstLevel = burst
                .map(entry -> EnchantmentHelper.getLevel(entry, weaponStack))
                .orElse(0);
        burstLevel = ConfigManager.limitEnchantmentLevel(burstLevel, BurstEnchantment.MAX_LEVEL);
        var headshot = enchantments.getOptional(HeadshotEnchantment.KEY);
        int headshotLevel = headshot
                .map(entry -> EnchantmentHelper.getLevel(entry, weaponStack))
                .orElse(0);
        arrow.setHeadshotLevel(ConfigManager.limitEnchantmentLevel(
                headshotLevel, HeadshotEnchantment.MAX_LEVEL));
        if (projectileStack.isOf(ModItems.ENDER_ARROW)) {
            arrow.setArrowType(ArrowType.ENDER);
        } else if (projectileStack.isOf(ModItems.SHOCKWAVE_ARROW)) {
            arrow.setArrowType(ArrowType.SHOCKWAVE);
        } else if (projectileStack.isOf(ModItems.IMPULSE_ARROW)) {
            arrow.setArrowType(ArrowType.IMPULSE);
        } else if (projectileStack.isOf(ModItems.EXPLOSIVE_ARROW)) {
            arrow.setArrowType(ArrowType.EXPLOSIVE);
        } else if (projectileStack.isOf(ModItems.TIDAL_ARROW)) {
            arrow.setArrowType(ArrowType.TIDAL);
        } else if (projectileStack.isOf(ModItems.SHATTERING_ARROW)) {
            arrow.setArrowType(ArrowType.SHATTERING);
        } else if (projectileStack.isOf(ModItems.ECHO_ARROW)) {
            arrow.setArrowType(ArrowType.ECHO);
        }
        arrow.setProjectileStack(originalArrow.getItemStack().copy());
        arrow.setPosition(originalArrow.getX(), originalArrow.getY(), originalArrow.getZ());
        arrow.setVelocity(originalArrow.getVelocity());
        arrow.setFractureReleaseSpeed(originalArrow.getVelocity().length());
        arrow.setCritical(originalArrow.isCritical());
        arrow.setNoClip(originalArrow.isNoClip());
        boolean extraAmmoFree = shooter.isInCreativeMode()
                || projectileStack.contains(DataComponentTypes.INTANGIBLE_PROJECTILE);
        arrow.setExtraAmmoFree(extraAmmoFree);
        if (shooter instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
            BurstArrowHandler.schedule(arrow, serverPlayer, weaponStack, burstLevel);
        }
        double overdrawBonus = shooter instanceof PlayerEntity player
                ? OverdrawHandler.consumeDamageBonus(player) : 0.0D;
        if (overdrawBonus > 0.0D) {
            arrow.setOverdrawDamageBonus(overdrawBonus);
        }
        cir.setReturnValue(arrow);
    }
}
