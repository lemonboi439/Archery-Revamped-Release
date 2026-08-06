package me.lemonboi439.archeryRevamped.overdraw;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.enchantment.OverdrawEnchantment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class OverdrawHandler {
    private static final Map<UUID, DrawState> STATES = new HashMap<>();
    private static long serverTick;

    private OverdrawHandler() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(OverdrawHandler::tick);
    }

    public static double consumeDamageBonus(PlayerEntity player) {
        DrawState state = STATES.remove(player.getUuid());
        if (state == null || state.damageBonus <= 0.0D) {
            return 0.0D;
        }
        return state.damageBonus;
    }

    private static void tick(MinecraftServer server) {
        serverTick++;
        Set<UUID> onlinePlayers = new HashSet<>();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            onlinePlayers.add(player.getUuid());
            updatePlayer(player);
        }
        STATES.keySet().removeIf(uuid -> !onlinePlayers.contains(uuid));
    }

    private static void updatePlayer(ServerPlayerEntity player) {
        ItemStack activeStack = player.getActiveItem();
        if (!player.isUsingItem() || activeStack.isEmpty() || !(activeStack.getItem() instanceof BowItem)) {
            STATES.remove(player.getUuid());
            return;
        }

        int level = getOverdrawLevel(player, activeStack);
        if (level <= 0) {
            STATES.remove(player.getUuid());
            return;
        }

        int drawTime = player.getItemUseTime();
        if (drawTime < 20) {
            STATES.remove(player.getUuid());
            return;
        }

        DrawState state = STATES.computeIfAbsent(player.getUuid(), uuid -> new DrawState(
                randomFailureDelayTicks()));
        state.level = level;
        state.overdrawDuration = drawTime - 20;

        double perTick = ConfigManager.getOverdrawDamageIncreasePerTickPercent() / 100.0D;
        double cap = ConfigManager.getOverdrawMaxDamageBonusPercent() / 100.0D;
        state.damageBonus = Math.min(cap, state.overdrawDuration * perTick * level);

        if (state.overdrawDuration > 0 && state.overdrawDuration % 10 == 0) {
            float pitch = 0.8F + Math.min(0.8F, (float) (state.damageBonus / Math.max(cap, 0.01D)) * 0.8F);
            EffectManager.playSound(player.getEntityWorld(),
                    new Vec3d(player.getX(), player.getY(), player.getZ()),
                    SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE, 0.35F, pitch);
        }

        if (!state.punishmentApplied
                && state.overdrawDuration >= state.failureDelayTicks) {
            state.punishmentApplied = true;
            applyPunishment(player, activeStack, level);
            player.getItemCooldownManager().set(activeStack, ConfigManager.getOverdrawBowDisableTicks());
            fireMisfireShot(player, activeStack);
        }
    }

    private static int randomFailureDelayTicks() {
        int minimum = ConfigManager.getOverdrawMinimumFailureDelayTicks();
        int maximum = ConfigManager.getOverdrawMaximumFailureDelayTicks();
        return ThreadLocalRandom.current().nextInt(minimum, maximum + 1);
    }

    private static int getOverdrawLevel(ServerPlayerEntity player, ItemStack bow) {
        var enchantments = player.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        return enchantments.getOptional(OverdrawEnchantment.KEY)
                .map(entry -> EnchantmentHelper.getLevel(entry, bow))
                .map(level -> ConfigManager.limitEnchantmentLevel(level, OverdrawEnchantment.MAX_LEVEL))
                .orElse(0);
    }

    private static void applyPunishment(ServerPlayerEntity player, ItemStack bow, int level) {
        double durabilityPercent = ConfigManager.getOverdrawDurabilityLossPercent();
        if (durabilityPercent > 0.0D) {
            int durabilityLoss = Math.max(1, (int) Math.ceil(
                    bow.getMaxDamage() * durabilityPercent / 100.0D));
            bow.damage(durabilityLoss, player, player.getActiveHand().getEquipmentSlot());
        }
        double selfDamageHearts = ConfigManager.getOverdrawSelfDamageHearts();
        if (selfDamageHearts > 0.0D) {
            player.damage(player.getEntityWorld(), player.getDamageSources().generic(),
                    (float) (selfDamageHearts * 2.0D * level));
        }
    }

    /** Fires the failed release inside a uniformly random cone around the player's original aim. */
    private static void fireMisfireShot(ServerPlayerEntity player, ItemStack bow) {
        Vec3d direction = randomDirectionInCone(player.getRotationVector(),
                ConfigManager.getOverdrawMisfireAngleDegrees());
        float originalYaw = player.getYaw();
        float originalPitch = player.getPitch();
        player.setYaw((float) Math.toDegrees(Math.atan2(-direction.x, direction.z)));
        player.setPitch((float) Math.toDegrees(-Math.asin(direction.y)));
        try {
            bow.getItem().onStoppedUsing(bow, player.getEntityWorld(), player, player.getItemUseTimeLeft());
        } finally {
            player.setYaw(originalYaw);
            player.setPitch(originalPitch);
            player.stopUsingItem();
            STATES.remove(player.getUuid());
        }
    }

    private static Vec3d randomDirectionInCone(Vec3d forward, double angleDegrees) {
        Vec3d normalizedForward = forward.normalize();
        double maxAngleRadians = Math.toRadians(angleDegrees);
        double cosTheta = 1.0D - ThreadLocalRandom.current().nextDouble()
                * (1.0D - Math.cos(maxAngleRadians));
        double sinTheta = Math.sqrt(Math.max(0.0D, 1.0D - cosTheta * cosTheta));
        double azimuth = ThreadLocalRandom.current().nextDouble() * Math.PI * 2.0D;

        Vec3d reference = Math.abs(normalizedForward.y) > 0.99D
                ? new Vec3d(1.0D, 0.0D, 0.0D)
                : new Vec3d(0.0D, 1.0D, 0.0D);
        Vec3d right = new Vec3d(
                normalizedForward.y * reference.z - normalizedForward.z * reference.y,
                normalizedForward.z * reference.x - normalizedForward.x * reference.z,
                normalizedForward.x * reference.y - normalizedForward.y * reference.x
        ).normalize();
        Vec3d up = new Vec3d(
                right.y * normalizedForward.z - right.z * normalizedForward.y,
                right.z * normalizedForward.x - right.x * normalizedForward.z,
                right.x * normalizedForward.y - right.y * normalizedForward.x
        ).normalize();
        return normalizedForward.multiply(cosTheta)
                .add(right.multiply(Math.cos(azimuth) * sinTheta))
                .add(up.multiply(Math.sin(azimuth) * sinTheta))
                .normalize();
    }

    private static final class DrawState {
        private final int failureDelayTicks;
        private int level;
        private int overdrawDuration;
        private double damageBonus;
        private boolean punishmentApplied;

        private DrawState(int failureDelayTicks) {
            this.failureDelayTicks = failureDelayTicks;
        }
    }
}
