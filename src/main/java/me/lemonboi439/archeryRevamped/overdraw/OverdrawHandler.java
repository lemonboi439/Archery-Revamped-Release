package me.lemonboi439.archeryRevamped.overdraw;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.enchantment.OverdrawEnchantment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
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

    public static double consumeDamageBonus(Player player) {
        DrawState state = STATES.remove(player.getUUID());
        if (state == null || state.damageBonus <= 0.0D) {
            return 0.0D;
        }
        return state.damageBonus;
    }

    private static void tick(MinecraftServer server) {
        serverTick++;
        Set<UUID> onlinePlayers = new HashSet<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            onlinePlayers.add(player.getUUID());
            updatePlayer(player);
        }
        STATES.keySet().removeIf(uuid -> !onlinePlayers.contains(uuid));
    }

    private static void updatePlayer(ServerPlayer player) {
        ItemStack activeStack = player.getUseItem();
        if (!player.isUsingItem() || activeStack.isEmpty() || !(activeStack.getItem() instanceof BowItem)) {
            STATES.remove(player.getUUID());
            return;
        }

        int level = getOverdrawLevel(player, activeStack);
        if (level <= 0) {
            STATES.remove(player.getUUID());
            return;
        }

        int drawTime = player.getTicksUsingItem();
        if (drawTime < 20) {
            STATES.remove(player.getUUID());
            return;
        }

        DrawState state = STATES.computeIfAbsent(player.getUUID(), uuid -> new DrawState(
                randomFailureDelayTicks()));
        state.level = level;
        state.overdrawDuration = drawTime - 20;

        double perTick = ConfigManager.getOverdrawDamageIncreasePerTickPercent() / 100.0D;
        double cap = ConfigManager.getOverdrawMaxDamageBonusPercent() / 100.0D;
        state.damageBonus = Math.min(cap, state.overdrawDuration * perTick * level);

        if (state.overdrawDuration > 0 && state.overdrawDuration % 10 == 0) {
            float pitch = 0.8F + Math.min(0.8F, (float) (state.damageBonus / Math.max(cap, 0.01D)) * 0.8F);
            EffectManager.playSound(player.level(),
                    new Vec3(player.getX(), player.getY(), player.getZ()),
                    SoundEvents.CROSSBOW_LOADING_MIDDLE, 0.35F, pitch);
        }

        if (!state.punishmentApplied
                && state.overdrawDuration >= state.failureDelayTicks) {
            state.punishmentApplied = true;
            applyPunishment(player, activeStack, level);
            player.getCooldowns().addCooldown(activeStack, ConfigManager.getOverdrawBowDisableTicks());
            fireMisfireShot(player, activeStack);
        }
    }

    private static int randomFailureDelayTicks() {
        int minimum = ConfigManager.getOverdrawMinimumFailureDelayTicks();
        int maximum = ConfigManager.getOverdrawMaximumFailureDelayTicks();
        return ThreadLocalRandom.current().nextInt(minimum, maximum + 1);
    }

    private static int getOverdrawLevel(ServerPlayer player, ItemStack bow) {
        var enchantments = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return enchantments.get(OverdrawEnchantment.KEY)
                .map(entry -> EnchantmentHelper.getItemEnchantmentLevel(entry, bow))
                .map(level -> ConfigManager.limitEnchantmentLevel(level, OverdrawEnchantment.MAX_LEVEL))
                .orElse(0);
    }

    private static void applyPunishment(ServerPlayer player, ItemStack bow, int level) {
        double durabilityPercent = ConfigManager.getOverdrawDurabilityLossPercent();
        if (durabilityPercent > 0.0D) {
            int durabilityLoss = Math.max(1, (int) Math.ceil(
                    bow.getMaxDamage() * durabilityPercent / 100.0D));
            bow.hurtAndBreak(durabilityLoss, player, player.getUsedItemHand().asEquipmentSlot());
        }
        double selfDamageHearts = ConfigManager.getOverdrawSelfDamageHearts();
        if (selfDamageHearts > 0.0D) {
            player.hurtServer(player.level(), player.damageSources().generic(),
                    (float) (selfDamageHearts * 2.0D * level));
        }
    }

    /** Fires the failed release inside a uniformly random cone around the player's original aim. */
    private static void fireMisfireShot(ServerPlayer player, ItemStack bow) {
        Vec3 direction = randomDirectionInCone(player.getLookAngle(),
                ConfigManager.getOverdrawMisfireAngleDegrees());
        float originalYaw = player.getYRot();
        float originalPitch = player.getXRot();
        player.setYRot((float) Math.toDegrees(Math.atan2(-direction.x, direction.z)));
        player.setXRot((float) Math.toDegrees(-Math.asin(direction.y)));
        try {
            bow.getItem().releaseUsing(bow, player.level(), player, player.getUseItemRemainingTicks());
        } finally {
            player.setYRot(originalYaw);
            player.setXRot(originalPitch);
            player.releaseUsingItem();
            STATES.remove(player.getUUID());
        }
    }

    private static Vec3 randomDirectionInCone(Vec3 forward, double angleDegrees) {
        Vec3 normalizedForward = forward.normalize();
        double maxAngleRadians = Math.toRadians(angleDegrees);
        double cosTheta = 1.0D - ThreadLocalRandom.current().nextDouble()
                * (1.0D - Math.cos(maxAngleRadians));
        double sinTheta = Math.sqrt(Math.max(0.0D, 1.0D - cosTheta * cosTheta));
        double azimuth = ThreadLocalRandom.current().nextDouble() * Math.PI * 2.0D;

        Vec3 reference = Math.abs(normalizedForward.y) > 0.99D
                ? new Vec3(1.0D, 0.0D, 0.0D)
                : new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 right = new Vec3(
                normalizedForward.y * reference.z - normalizedForward.z * reference.y,
                normalizedForward.z * reference.x - normalizedForward.x * reference.z,
                normalizedForward.x * reference.y - normalizedForward.y * reference.x
        ).normalize();
        Vec3 up = new Vec3(
                right.y * normalizedForward.z - right.z * normalizedForward.y,
                right.z * normalizedForward.x - right.x * normalizedForward.z,
                right.x * normalizedForward.y - right.y * normalizedForward.x
        ).normalize();
        return normalizedForward.scale(cosTheta)
                .add(right.scale(Math.cos(azimuth) * sinTheta))
                .add(up.scale(Math.sin(azimuth) * sinTheta))
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
