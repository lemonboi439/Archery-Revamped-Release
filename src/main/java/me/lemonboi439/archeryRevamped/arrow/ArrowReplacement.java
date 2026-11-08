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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

/** Shared vanilla-arrow replacement used by normal and tipped ArrowItem factories. */
public final class ArrowReplacement {
    private ArrowReplacement() {
    }

    public static ArcheryArrowEntity create(Level world, ItemStack projectileStack, LivingEntity shooter) {
        ItemStack weaponStack = getFiringWeapon(shooter);
        ArcheryArrowEntity arrow = new ArcheryArrowEntity(world, shooter, projectileStack.copy(), weaponStack.copy());
        arrow.setRicochetLevel(ConfigManager.limitEnchantmentLevel(
                EnchantmentHelper.getItemEnchantmentLevel(RicochetEnchantment.ENCHANTMENT, weaponStack), RicochetEnchantment.MAX_LEVEL));
        arrow.setLongshotLevel(ConfigManager.limitEnchantmentLevel(
                EnchantmentHelper.getItemEnchantmentLevel(LongshotEnchantment.ENCHANTMENT, weaponStack), LongshotEnchantment.MAX_LEVEL));
        int fractureLevel = ConfigManager.isFractureEnabled()
                ? EnchantmentHelper.getItemEnchantmentLevel(FractureEnchantment.ENCHANTMENT, weaponStack) : 0;
        arrow.setFractureLevel(ConfigManager.limitEnchantmentLevel(fractureLevel, FractureEnchantment.MAX_LEVEL));
        int burstLevel = ConfigManager.limitEnchantmentLevel(
                EnchantmentHelper.getItemEnchantmentLevel(BurstEnchantment.ENCHANTMENT, weaponStack), BurstEnchantment.MAX_LEVEL);
        arrow.setHeadshotLevel(ConfigManager.limitEnchantmentLevel(
                EnchantmentHelper.getItemEnchantmentLevel(HeadshotEnchantment.ENCHANTMENT, weaponStack), HeadshotEnchantment.MAX_LEVEL));
        arrow.setArrowType(getArrowType(projectileStack));
        arrow.setProjectileStack(projectileStack.copy());

        // 1.20.1 has no INTANGIBLE_PROJECTILE component; Infinity is the only
        // vanilla case where follow-up Burst/Fracture arrows must not consume ammo.
        boolean infinityArrow = projectileStack.is(Items.ARROW)
                && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, weaponStack) > 0;
        boolean extraAmmoFree = infinityArrow
                || shooter instanceof Player player && player.getAbilities().instabuild;
        arrow.setExtraAmmoFree(extraAmmoFree);
        if (!world.isClientSide() && shooter instanceof Player player
                && !extraAmmoFree && QuiverManager.isSelectedArrow(player, projectileStack)) {
            QuiverManager.consumeArrow(player, projectileStack, 1);
        }
        if (shooter instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            BurstArrowHandler.schedule(arrow, serverPlayer, weaponStack, burstLevel);
        }
        if (shooter instanceof Player player) {
            double overdrawBonus = OverdrawHandler.consumeDamageBonus(player);
            if (overdrawBonus > 0.0D) {
                arrow.setOverdrawDamageBonus(overdrawBonus);
            }
        }
        return arrow;
    }

    public static ArcheryArrowEntity createForDispenser(Level world, ItemStack projectileStack) {
        ArcheryArrowEntity arrow = new ArcheryArrowEntity(world);
        arrow.setArrowType(getArrowType(projectileStack));
        arrow.setProjectileStack(projectileStack.copy());
        return arrow;
    }

    private static ItemStack getFiringWeapon(LivingEntity shooter) {
        ItemStack mainHand = shooter.getMainHandItem();
        if (mainHand.getItem() instanceof ProjectileWeaponItem) {
            return mainHand;
        }
        ItemStack offHand = shooter.getOffhandItem();
        return offHand.getItem() instanceof ProjectileWeaponItem ? offHand : mainHand;
    }

    private static ArrowType getArrowType(ItemStack stack) {
        if (stack.is(ModItems.ENDER_ARROW)) return ArrowType.ENDER;
        if (stack.is(ModItems.SHOCKWAVE_ARROW)) return ArrowType.SHOCKWAVE;
        if (stack.is(ModItems.IMPULSE_ARROW)) return ArrowType.IMPULSE;
        if (stack.is(ModItems.EXPLOSIVE_ARROW)) return ArrowType.EXPLOSIVE;
        if (stack.is(ModItems.TIDAL_ARROW)) return ArrowType.TIDAL;
        if (stack.is(ModItems.SHATTERING_ARROW)) return ArrowType.SHATTERING;
        if (stack.is(ModItems.ECHO_ARROW)) return ArrowType.ECHO;
        return ArrowType.NORMAL;
    }
}
