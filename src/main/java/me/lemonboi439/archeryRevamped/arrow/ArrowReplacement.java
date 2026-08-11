package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.burst.BurstArrowHandler;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.enchantment.BurstEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.FractureEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.HeadshotEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.LongshotEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.RicochetEnchantment;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import me.lemonboi439.archeryRevamped.item.ModItems;
import me.lemonboi439.archeryRevamped.overdraw.OverdrawHandler;
import me.lemonboi439.archeryRevamped.quiver.QuiverManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.world.World;

/** Shared vanilla-arrow replacement used by normal and tipped ArrowItem factories. */
public final class ArrowReplacement {
    private ArrowReplacement() {
    }

    public static ArcheryArrowEntity create(World world, ItemStack projectileStack, LivingEntity shooter) {
        ItemStack weaponStack = getFiringWeapon(shooter);
        ArcheryArrowEntity arrow = new ArcheryArrowEntity(world, shooter, projectileStack.copy(), weaponStack.copy());
        arrow.setRicochetLevel(ConfigManager.limitEnchantmentLevel(
                EnchantmentHelper.getLevel(RicochetEnchantment.ENCHANTMENT, weaponStack), RicochetEnchantment.MAX_LEVEL));
        arrow.setLongshotLevel(ConfigManager.limitEnchantmentLevel(
                EnchantmentHelper.getLevel(LongshotEnchantment.ENCHANTMENT, weaponStack), LongshotEnchantment.MAX_LEVEL));
        int fractureLevel = ConfigManager.isFractureEnabled()
                ? EnchantmentHelper.getLevel(FractureEnchantment.ENCHANTMENT, weaponStack) : 0;
        arrow.setFractureLevel(ConfigManager.limitEnchantmentLevel(fractureLevel, FractureEnchantment.MAX_LEVEL));
        int burstLevel = ConfigManager.limitEnchantmentLevel(
                EnchantmentHelper.getLevel(BurstEnchantment.ENCHANTMENT, weaponStack), BurstEnchantment.MAX_LEVEL);
        arrow.setHeadshotLevel(ConfigManager.limitEnchantmentLevel(
                EnchantmentHelper.getLevel(HeadshotEnchantment.ENCHANTMENT, weaponStack), HeadshotEnchantment.MAX_LEVEL));
        arrow.setArrowType(getArrowType(projectileStack));
        arrow.setProjectileStack(projectileStack.copy());

        boolean extraAmmoFree = shooter instanceof PlayerEntity player && player.getAbilities().creativeMode;
        arrow.setExtraAmmoFree(extraAmmoFree);
        if (!world.isClient() && shooter instanceof PlayerEntity player
                && !extraAmmoFree && QuiverManager.isSelectedArrow(player, projectileStack)) {
            QuiverManager.consumeArrow(player, projectileStack, 1);
        }
        if (shooter instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
            BurstArrowHandler.schedule(arrow, serverPlayer, weaponStack, burstLevel);
        }
        if (shooter instanceof PlayerEntity player) {
            double overdrawBonus = OverdrawHandler.consumeDamageBonus(player);
            if (overdrawBonus > 0.0D) {
                arrow.setOverdrawDamageBonus(overdrawBonus);
            }
        }
        return arrow;
    }

    public static ArcheryArrowEntity createForDispenser(World world, ItemStack projectileStack) {
        ArcheryArrowEntity arrow = new ArcheryArrowEntity(world);
        arrow.setArrowType(getArrowType(projectileStack));
        arrow.setProjectileStack(projectileStack.copy());
        return arrow;
    }

    private static ItemStack getFiringWeapon(LivingEntity shooter) {
        ItemStack mainHand = shooter.getMainHandStack();
        if (mainHand.getItem() instanceof RangedWeaponItem) {
            return mainHand;
        }
        ItemStack offHand = shooter.getOffHandStack();
        return offHand.getItem() instanceof RangedWeaponItem ? offHand : mainHand;
    }

    private static ArrowType getArrowType(ItemStack stack) {
        if (stack.isOf(ModItems.ENDER_ARROW)) return ArrowType.ENDER;
        if (stack.isOf(ModItems.SHOCKWAVE_ARROW)) return ArrowType.SHOCKWAVE;
        if (stack.isOf(ModItems.IMPULSE_ARROW)) return ArrowType.IMPULSE;
        if (stack.isOf(ModItems.EXPLOSIVE_ARROW)) return ArrowType.EXPLOSIVE;
        if (stack.isOf(ModItems.TIDAL_ARROW)) return ArrowType.TIDAL;
        if (stack.isOf(ModItems.SHATTERING_ARROW)) return ArrowType.SHATTERING;
        if (stack.isOf(ModItems.ECHO_ARROW)) return ArrowType.ECHO;
        return ArrowType.NORMAL;
    }
}
