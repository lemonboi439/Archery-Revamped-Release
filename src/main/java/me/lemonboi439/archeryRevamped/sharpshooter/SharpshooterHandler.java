package me.lemonboi439.archeryRevamped.sharpshooter;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.enchantment.SharpshooterEnchantment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UseEffectsComponent;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class SharpshooterHandler {
    private static final Identifier MOVEMENT_MODIFIER_ID =
            Identifier.of(ArcheryRevamped.MOD_ID, "sharpshooter_draw_speed");

    private SharpshooterHandler() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(SharpshooterHandler::tick);
    }

    private static void tick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            updatePlayer(player);
        }
    }

    private static void updatePlayer(ServerPlayerEntity player) {
        var movementAttribute = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (movementAttribute == null) {
            return;
        }

        if (!ConfigManager.isSharpshooterEnabled()) {
            movementAttribute.removeModifier(MOVEMENT_MODIFIER_ID);
            return;
        }

        int level = isDrawingRangedWeapon(player) ? getSharpshooterLevel(player) : 0;
        movementAttribute.removeModifier(MOVEMENT_MODIFIER_ID);
        if (level <= 0) {
            return;
        }

        double reduction = ConfigManager.getSharpshooterReductionPercent(level);
        if (reduction <= 0.0D) {
            return;
        }

        ItemStack activeItem = player.getActiveItem();
        double useSpeedMultiplier = activeItem
                .getOrDefault(DataComponentTypes.USE_EFFECTS, UseEffectsComponent.DEFAULT)
                .speedMultiplier();
        double clampedReduction = Math.max(0.0D, Math.min(100.0D, reduction)) / 100.0D;
        double targetSpeedMultiplier = useSpeedMultiplier
                + (1.0D - useSpeedMultiplier) * clampedReduction;
        double attributeModifier = useSpeedMultiplier > 1.0E-6D
                ? targetSpeedMultiplier / useSpeedMultiplier - 1.0D
                : 0.0D;

        movementAttribute.addTemporaryModifier(new EntityAttributeModifier(
                MOVEMENT_MODIFIER_ID,
                attributeModifier,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ));
    }

    private static boolean isDrawingRangedWeapon(ServerPlayerEntity player) {
        if (!player.isUsingItem()) {
            return false;
        }

        ItemStack activeItem = player.getActiveItem();
        return !activeItem.isEmpty()
                && (activeItem.getItem() instanceof BowItem || activeItem.getItem() instanceof CrossbowItem);
    }

    private static int getSharpshooterLevel(ServerPlayerEntity player) {
        ItemStack leggings = player.getEquippedStack(EquipmentSlot.LEGS);
        var enchantments = player.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        return enchantments.getOptional(SharpshooterEnchantment.KEY)
                .map(entry -> EnchantmentHelper.getLevel(entry, leggings))
                .map(level -> ConfigManager.limitEnchantmentLevel(level, SharpshooterEnchantment.MAX_LEVEL))
                .orElse(0);
    }
}
