package me.lemonboi439.archeryRevamped.overdraw;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.enchantment.OverdrawEnchantment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

        DrawState state = STATES.computeIfAbsent(player.getUuid(), uuid -> new DrawState(serverTick));
        state.level = level;
        state.overdrawDuration = drawTime - 20;

        double perTick = ConfigManager.getOverdrawDamageIncreasePerTickPercent() / 100.0D;
        double cap = ConfigManager.getOverdrawMaxDamageBonusPercent() / 100.0D;
        state.damageBonus = Math.min(cap, state.overdrawDuration * perTick * level);

        if (state.overdrawDuration > 0 && state.overdrawDuration % 10 == 0) {
            float pitch = 0.8F + Math.min(0.8F, (float) (state.damageBonus / Math.max(cap, 0.01D)) * 0.8F);
            player.getEntityWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE, SoundCategory.PLAYERS, 0.35F, pitch);
        }

        if (!state.punishmentApplied
                && state.overdrawDuration >= Math.max(1, ConfigManager.getOverdrawAutoFireDelayTicks())) {
            state.punishmentApplied = true;
            applyPunishment(player, activeStack, level);
            activeStack.getItem().onStoppedUsing(activeStack, player.getEntityWorld(), player,
                    player.getItemUseTimeLeft());
            player.stopUsingItem();
        }
    }

    private static int getOverdrawLevel(ServerPlayerEntity player, ItemStack bow) {
        var enchantments = player.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        return enchantments.getOptional(OverdrawEnchantment.KEY)
                .map(entry -> EnchantmentHelper.getLevel(entry, bow))
                .map(level -> Math.min(level, OverdrawEnchantment.MAX_LEVEL))
                .orElse(0);
    }

    private static void applyPunishment(ServerPlayerEntity player, ItemStack bow, int level) {
        if (player.getRandom().nextBoolean()) {
            int durabilityLoss = Math.max(1, (int) Math.ceil(
                    bow.getMaxDamage() * ConfigManager.getOverdrawDurabilityLossPercent() / 100.0D));
            bow.damage(durabilityLoss, player, player.getActiveHand().getEquipmentSlot());
        } else {
            player.damage(player.getEntityWorld(), player.getDamageSources().generic(),
                    (float) (ConfigManager.getOverdrawSelfDamageHearts() * 2.0D * level));
        }
    }

    private static final class DrawState {
        private final long drawStartTick;
        private int level;
        private int overdrawDuration;
        private double damageBonus;
        private boolean punishmentApplied;

        private DrawState(long drawStartTick) {
            this.drawStartTick = drawStartTick;
        }
    }
}
