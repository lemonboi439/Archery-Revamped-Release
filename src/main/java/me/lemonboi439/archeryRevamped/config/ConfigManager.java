package me.lemonboi439.archeryRevamped.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Central JSON configuration for Archery Revamped. */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "archery_revamped-config.json";
    private static PhysicsConfig config = new PhysicsConfig();

    private ConfigManager() {
    }

    public static void load() {
        Path configDirectory = FabricLoader.getInstance().getConfigDir();
        Path configPath = configDirectory.resolve(FILE_NAME);

        try {
            Files.createDirectories(configDirectory);
            if (Files.exists(configPath)) {
                try (Reader reader = Files.newBufferedReader(configPath)) {
                    config = PhysicsConfig.fromJson(GSON.fromJson(reader, JsonObject.class));
                }
            } else {
                config = new PhysicsConfig();
                save(configPath);
            }
        } catch (IOException | JsonParseException | IllegalStateException | IllegalArgumentException exception) {
            config = new PhysicsConfig();
        }
    }

    public static void reload() {
        load();
    }

    public static void save() {
        try {
            Path configDirectory = FabricLoader.getInstance().getConfigDir();
            Files.createDirectories(configDirectory);
            save(configDirectory.resolve(FILE_NAME));
        } catch (IOException exception) {
            // Keep the active in-memory configuration if saving fails.
        }
    }

    private static void save(Path configPath) throws IOException {
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            GSON.toJson(config.toJson(), writer);
        }
    }

    public static void resetPhysicsToDefaults() {
        PhysicsConfig defaults = new PhysicsConfig();
        config.gravity = defaults.gravity;
        config.drag = defaults.drag;
        config.speedMultiplier = defaults.speedMultiplier;
        config.randomness = defaults.randomness;
        config.terminalVelocity = defaults.terminalVelocity;
        config.maxLifetimeTicks = defaults.maxLifetimeTicks;
        config.ricochetVelocityLossPercent = defaults.ricochetVelocityLossPercent;
        save();
    }

    public static double getGravity() {
        return config.gravity;
    }

    public static void setGravity(double value) {
        config.gravity = value;
        save();
    }

    public static double getDrag() {
        return config.drag;
    }

    public static void setDrag(double value) {
        config.drag = value;
        config.validate();
        save();
    }

    public static double getSpeedMultiplier() {
        return config.speedMultiplier;
    }

    public static void setSpeedMultiplier(double value) {
        config.speedMultiplier = value;
        save();
    }

    public static double getRandomness() {
        return config.randomness;
    }

    public static void setRandomness(double value) {
        config.randomness = value;
        save();
    }

    public static double getTerminalVelocity() {
        return config.terminalVelocity;
    }

    public static void setTerminalVelocity(double value) {
        config.terminalVelocity = value;
        save();
    }

    public static int getMaxLifetimeTicks() {
        return config.maxLifetimeTicks;
    }

    public static void setMaxLifetimeTicks(int value) {
        config.maxLifetimeTicks = value;
        save();
    }

    public static double getRicochetVelocityLossPercent() {
        return config.ricochetVelocityLossPercent;
    }

    public static boolean isEnderArrowEnabled() {
        return config.enderArrowEnabled;
    }

    public static boolean isShockwaveArrowEnabled() {
        return config.shockwaveArrowEnabled;
    }

    public static boolean isImpulseArrowEnabled() {
        return config.impulseArrowEnabled;
    }

    public static boolean isExplosiveArrowEnabled() {
        return config.explosiveArrowEnabled;
    }

    public static boolean isExplosiveArrowAntiGriefEnabled() {
        return config.explosiveArrowAntiGrief;
    }

    public static double getShockwaveRadius() {
        return config.shockwaveRadius;
    }

    public static double getShockwaveStrength() {
        return config.shockwaveStrength;
    }

    public static double getImpulseRadius() {
        return config.impulseRadius;
    }

    public static double getImpulseStrength() {
        return config.impulseStrength;
    }

    public static double getExplosiveArrowSize() {
        return config.explosiveArrowSize;
    }

    public static double getOverdrawDamageIncreasePerTickPercent() {
        return config.overdrawDamageIncreasePerTickPercent;
    }

    public static double getOverdrawMaxDamageBonusPercent() {
        return config.overdrawMaxDamageBonusPercent;
    }

    public static int getOverdrawAutoFireDelayTicks() {
        return config.overdrawAutoFireDelayTicks;
    }

    public static int getOverdrawMinimumFailureDelayTicks() {
        return config.overdrawMinimumFailureDelayTicks;
    }

    public static int getOverdrawMaximumFailureDelayTicks() {
        return config.overdrawAutoFireDelayTicks;
    }

    public static int getOverdrawBowDisableTicks() {
        return config.overdrawBowDisableTicks;
    }

    public static double getOverdrawMisfireAngleDegrees() {
        return config.overdrawMisfireAngleDegrees;
    }

    public static double getOverdrawDurabilityLossPercent() {
        return config.overdrawDurabilityLossPercent;
    }

    public static double getOverdrawSelfDamageHearts() {
        return config.overdrawSelfDamageHearts;
    }

    public static double getLongshot16Threshold() {
        return config.longshot16Threshold;
    }

    public static double getLongshot32Threshold() {
        return config.longshot32Threshold;
    }

    public static double getLongshot48Threshold() {
        return config.longshot48Threshold;
    }

    public static double getLongshot64Threshold() {
        return config.longshot64Threshold;
    }

    public static double getLongshot16Multiplier() {
        return config.longshot16Multiplier;
    }

    public static double getLongshot32Multiplier() {
        return config.longshot32Multiplier;
    }

    public static double getLongshot48Multiplier() {
        return config.longshot48Multiplier;
    }

    public static double getLongshot64Multiplier() {
        return config.longshot64Multiplier;
    }

    public static int getFractureSplitDelayTicks() {
        return config.fractureSplitDelayTicks;
    }

    public static boolean isFractureEnabled() {
        return config.fractureEnabled;
    }

    public static double getFractureReferenceReleaseSpeed() {
        return config.fractureReferenceReleaseSpeed;
    }

    public static int getFractureMinSplitDelayTicks() {
        return config.fractureMinSplitDelayTicks;
    }

    public static int getFractureMaxSplitDelayTicks() {
        return config.fractureMaxSplitDelayTicks;
    }

    public static double getFractureSplitAngleDegrees() {
        return config.fractureSplitAngleDegrees;
    }

    public static int getBurstStaggerDelayTicks() {
        return config.burstStaggerDelayTicks;
    }

    public static int getBurstArrowsPerLevel() {
        return config.burstArrowsPerLevel;
    }

    public static int getFletchingRecipeOutputCount() {
        return config.fletchingRecipeOutputCount;
    }

    public static boolean isModEnabled() {
        return config.modEnabled;
    }

    public static boolean isHeadshotEnabled() {
        return config.headshotEnabled;
    }

    public static double getHeadshotDamageBonusI() {
        return config.headshotDamageBonusI;
    }

    public static double getHeadshotDamageBonusII() {
        return config.headshotDamageBonusII;
    }

    public static double getHeadshotDamageBonusIII() {
        return config.headshotDamageBonusIII;
    }

    public static double getHeadshotBoxRadius() {
        return config.headshotBoxRadius;
    }

    public static boolean isHeadshotFeedbackEnabled() {
        return config.headshotFeedbackEnabled;
    }

    public static double getHeadshotPvpDamageBonusI() {
        return config.headshotPvpDamageBonusI;
    }

    public static double getHeadshotPvpDamageBonusII() {
        return config.headshotPvpDamageBonusII;
    }

    public static double getHeadshotPvpDamageBonusIII() {
        return config.headshotPvpDamageBonusIII;
    }

    public static boolean isTrajectoryColourVisualisationEnabled() {
        return config.trajectoryColourVisualisation;
    }

    public static void setTrajectoryColourVisualisationEnabled(boolean enabled) {
        config.trajectoryColourVisualisation = enabled;
        saveAfterChange();
    }

    public static boolean isInfiniteLevels() {
        return config.infiniteLevels;
    }

    public static void setInfiniteLevels(boolean enabled) {
        config.infiniteLevels = enabled;
        saveAfterChange();
    }

    public static void setRicochetVelocityLossPercent(double value) {
        config.ricochetVelocityLossPercent = value;
        config.validate();
        saveAfterChange();
    }

    public static void setEnderArrowEnabled(boolean value) {
        config.enderArrowEnabled = value;
        saveAfterChange();
    }

    public static void setShockwaveArrowEnabled(boolean value) {
        config.shockwaveArrowEnabled = value;
        saveAfterChange();
    }

    public static void setImpulseArrowEnabled(boolean value) {
        config.impulseArrowEnabled = value;
        saveAfterChange();
    }

    public static void setExplosiveArrowEnabled(boolean value) {
        config.explosiveArrowEnabled = value;
        saveAfterChange();
    }

    public static void setExplosiveArrowAntiGriefEnabled(boolean value) {
        config.explosiveArrowAntiGrief = value;
        saveAfterChange();
    }

    public static void setShockwaveRadius(double value) {
        config.shockwaveRadius = value;
        saveAfterChange();
    }

    public static void setShockwaveStrength(double value) {
        config.shockwaveStrength = value;
        saveAfterChange();
    }

    public static void setImpulseRadius(double value) {
        config.impulseRadius = value;
        saveAfterChange();
    }

    public static void setImpulseStrength(double value) {
        config.impulseStrength = value;
        saveAfterChange();
    }

    public static void setExplosiveArrowSize(double value) {
        config.explosiveArrowSize = value;
        saveAfterChange();
    }

    public static void setOverdrawDamageIncreasePerTickPercent(double value) {
        config.overdrawDamageIncreasePerTickPercent = value;
        saveAfterChange();
    }

    public static void setOverdrawMaxDamageBonusPercent(double value) {
        config.overdrawMaxDamageBonusPercent = value;
        saveAfterChange();
    }

    public static void setOverdrawAutoFireDelayTicks(int value) {
        config.overdrawAutoFireDelayTicks = value;
        saveAfterChange();
    }

    public static void setOverdrawMinimumFailureDelayTicks(int value) {
        config.overdrawMinimumFailureDelayTicks = value;
        saveAfterChange();
    }

    public static void setOverdrawBowDisableTicks(int value) {
        config.overdrawBowDisableTicks = value;
        saveAfterChange();
    }

    public static void setOverdrawMisfireAngleDegrees(double value) {
        config.overdrawMisfireAngleDegrees = value;
        saveAfterChange();
    }

    public static void setOverdrawDurabilityLossPercent(double value) {
        config.overdrawDurabilityLossPercent = value;
        saveAfterChange();
    }

    public static void setOverdrawSelfDamageHearts(double value) {
        config.overdrawSelfDamageHearts = value;
        saveAfterChange();
    }

    public static void setLongshot16Threshold(double value) {
        config.longshot16Threshold = value;
        saveAfterChange();
    }

    public static void setLongshot32Threshold(double value) {
        config.longshot32Threshold = value;
        saveAfterChange();
    }

    public static void setLongshot48Threshold(double value) {
        config.longshot48Threshold = value;
        saveAfterChange();
    }

    public static void setLongshot64Threshold(double value) {
        config.longshot64Threshold = value;
        saveAfterChange();
    }

    public static void setLongshot16Multiplier(double value) {
        config.longshot16Multiplier = value;
        saveAfterChange();
    }

    public static void setLongshot32Multiplier(double value) {
        config.longshot32Multiplier = value;
        saveAfterChange();
    }

    public static void setLongshot48Multiplier(double value) {
        config.longshot48Multiplier = value;
        saveAfterChange();
    }

    public static void setLongshot64Multiplier(double value) {
        config.longshot64Multiplier = value;
        saveAfterChange();
    }

    public static void setFractureEnabled(boolean value) {
        config.fractureEnabled = value;
        saveAfterChange();
    }

    public static void setFractureSplitDelayTicks(int value) {
        config.fractureSplitDelayTicks = value;
        saveAfterChange();
    }

    public static void setFractureReferenceReleaseSpeed(double value) {
        config.fractureReferenceReleaseSpeed = value;
        saveAfterChange();
    }

    public static void setFractureMinSplitDelayTicks(int value) {
        config.fractureMinSplitDelayTicks = value;
        saveAfterChange();
    }

    public static void setFractureMaxSplitDelayTicks(int value) {
        config.fractureMaxSplitDelayTicks = value;
        saveAfterChange();
    }

    public static void setFractureSplitAngleDegrees(double value) {
        config.fractureSplitAngleDegrees = value;
        saveAfterChange();
    }

    public static void setBurstStaggerDelayTicks(int value) {
        config.burstStaggerDelayTicks = value;
        saveAfterChange();
    }

    public static void setBurstArrowsPerLevel(int value) {
        config.burstArrowsPerLevel = value;
        saveAfterChange();
    }

    public static void setFletchingRecipeOutputCount(int value) {
        config.fletchingRecipeOutputCount = value;
        saveAfterChange();
    }

    public static void setModEnabled(boolean value) {
        config.modEnabled = value;
        saveAfterChange();
    }

    public static void setHeadshotEnabled(boolean value) {
        config.headshotEnabled = value;
        saveAfterChange();
    }

    public static void setHeadshotDamageBonusI(double value) {
        config.headshotDamageBonusI = value;
        saveAfterChange();
    }

    public static void setHeadshotDamageBonusII(double value) {
        config.headshotDamageBonusII = value;
        saveAfterChange();
    }

    public static void setHeadshotDamageBonusIII(double value) {
        config.headshotDamageBonusIII = value;
        saveAfterChange();
    }

    public static void setHeadshotBoxRadius(double value) {
        config.headshotBoxRadius = value;
        saveAfterChange();
    }

    public static void setHeadshotFeedbackEnabled(boolean value) {
        config.headshotFeedbackEnabled = value;
        saveAfterChange();
    }

    public static void setHeadshotPvpDamageBonusI(double value) {
        config.headshotPvpDamageBonusI = value;
        saveAfterChange();
    }

    public static void setHeadshotPvpDamageBonusII(double value) {
        config.headshotPvpDamageBonusII = value;
        saveAfterChange();
    }

    public static void setHeadshotPvpDamageBonusIII(double value) {
        config.headshotPvpDamageBonusIII = value;
        saveAfterChange();
    }

    /** Limits custom enchantments to a large but safe finite maximum. */
    public static int limitEnchantmentLevel(int level, int normalMaximum) {
        if (level <= 0) {
            return 0;
        }
        return Math.min(level, config.infiniteLevels ? 255 : normalMaximum);
    }

    private static void saveAfterChange() {
        config.validate();
        save();
    }

    private static final class PhysicsConfig {
        private double gravity = 0.05D;
        private double drag = 0.99D;
        private double speedMultiplier = 1.0D;
        private double randomness = 0.0D;
        private double terminalVelocity = 999.0D;
        private int maxLifetimeTicks = 1200;
        private double ricochetVelocityLossPercent = 10.0D;
        private boolean enderArrowEnabled = true;
        private boolean shockwaveArrowEnabled = true;
        private boolean impulseArrowEnabled = true;
        private boolean explosiveArrowEnabled = true;
        private boolean explosiveArrowAntiGrief = false;
        private double shockwaveRadius = 4.0D;
        private double shockwaveStrength = 2.0D;
        private double impulseRadius = 4.0D;
        private double impulseStrength = 2.0D;
        private double explosiveArrowSize = 2.5D;
        private double overdrawDamageIncreasePerTickPercent = 1.0D;
        private double overdrawMaxDamageBonusPercent = 100.0D;
        private int overdrawAutoFireDelayTicks = 100;
        private int overdrawMinimumFailureDelayTicks = 40;
        private int overdrawBowDisableTicks = 60;
        private double overdrawMisfireAngleDegrees = 45.0D;
        private double overdrawDurabilityLossPercent = 25.0D;
        private double overdrawSelfDamageHearts = 2.0D;
        private double longshot16Threshold = 16.0D;
        private double longshot32Threshold = 32.0D;
        private double longshot48Threshold = 48.0D;
        private double longshot64Threshold = 64.0D;
        private double longshot16Multiplier = 1.5D;
        private double longshot32Multiplier = 2.0D;
        private double longshot48Multiplier = 2.5D;
        private double longshot64Multiplier = 3.0D;
        private boolean fractureEnabled = false;
        private int fractureSplitDelayTicks = 2;
        private double fractureReferenceReleaseSpeed = 3.0D;
        private int fractureMinSplitDelayTicks = 1;
        private int fractureMaxSplitDelayTicks = 40;
        private double fractureSplitAngleDegrees = 15.0D;
        private int burstStaggerDelayTicks = 3;
        private int burstArrowsPerLevel = 1;
        private int fletchingRecipeOutputCount = 4;
        private boolean modEnabled = true;
        private boolean trajectoryColourVisualisation = false;
        private boolean infiniteLevels = false;
        private boolean headshotEnabled = false;
        private double headshotDamageBonusI = 15.0D;
        private double headshotDamageBonusII = 30.0D;
        private double headshotDamageBonusIII = 45.0D;
        private double headshotBoxRadius = 0.35D;
        private boolean headshotFeedbackEnabled = true;
        private double headshotPvpDamageBonusI = 15.0D;
        private double headshotPvpDamageBonusII = 30.0D;
        private double headshotPvpDamageBonusIII = 45.0D;

        private static PhysicsConfig fromJson(JsonObject json) {
            PhysicsConfig result = new PhysicsConfig();
            if (json == null) {
                return result;
            }

            result.gravity = readDouble(json, "physics", "gravity", "gravity", result.gravity);
            result.drag = readDouble(json, "physics", "drag", "drag", result.drag);
            result.speedMultiplier = readDouble(json, "physics", "speed_multiplier", "speedMultiplier", result.speedMultiplier);
            result.randomness = readDouble(json, "physics", "randomness", "randomness", result.randomness);
            result.terminalVelocity = readDouble(json, "physics", "terminal_velocity", "terminalVelocity", result.terminalVelocity);
            result.maxLifetimeTicks = readInt(json, "physics", "max_lifetime_ticks", "maxLifetimeTicks", result.maxLifetimeTicks);
            result.ricochetVelocityLossPercent = readDouble(json, "physics",
                    "ricochet_velocity_loss_percent", "ricochetVelocityLossPercent", result.ricochetVelocityLossPercent);
            result.ricochetVelocityLossPercent = readDouble(json, "ricochet", "velocity_loss_percent",
                    null, result.ricochetVelocityLossPercent);

            result.enderArrowEnabled = readBoolean(json, "arrow_types", "ender.enabled",
                    "enderArrowEnabled", result.enderArrowEnabled);
            JsonElement shockwaveElement = readNested(json, "arrow_types", "shockwave");
            boolean hasShockwaveSettings = shockwaveElement != null && shockwaveElement.isJsonObject();
            if (hasShockwaveSettings) {
                result.shockwaveArrowEnabled = readBoolean(json, "arrow_types", "shockwave.enabled",
                        null, result.shockwaveArrowEnabled);
                result.shockwaveRadius = readDouble(json, "arrow_types", "shockwave.radius",
                        null, result.shockwaveRadius);
                result.shockwaveStrength = readDouble(json, "arrow_types", "shockwave.strength",
                        null, result.shockwaveStrength);
            } else {
                // Migrate the old impulse arrow settings to Shockwave. The
                // new inward Impulse arrow keeps its independent defaults.
                result.shockwaveArrowEnabled = readBoolean(json, "arrow_types", "impulse.enabled",
                        null, result.shockwaveArrowEnabled);
                result.shockwaveRadius = readDouble(json, "arrow_types", "impulse.blast_radius",
                        null, result.shockwaveRadius);
                result.shockwaveStrength = readDouble(json, "arrow_types", "impulse.knockback_strength",
                        null, result.shockwaveStrength);
            }
            if (hasShockwaveSettings) {
                result.impulseArrowEnabled = readBoolean(json, "arrow_types", "impulse.enabled",
                        null, result.impulseArrowEnabled);
                result.impulseRadius = readDouble(json, "arrow_types", "impulse.radius",
                        null, result.impulseRadius);
                result.impulseStrength = readDouble(json, "arrow_types", "impulse.strength",
                        null, result.impulseStrength);
            }
            result.explosiveArrowEnabled = readBoolean(json, "arrow_types", "explosive.enabled",
                    null, result.explosiveArrowEnabled);
            result.explosiveArrowSize = readDouble(json, "arrow_types", "explosive.explosion_size",
                    "explosiveArrowSize", result.explosiveArrowSize);
            result.explosiveArrowAntiGrief = readBoolean(json, "arrow_types", "explosive.anti_grief",
                    "explosiveArrowAntiGrief", result.explosiveArrowAntiGrief);

            result.overdrawDamageIncreasePerTickPercent = readDouble(json, "overdraw",
                    "damage_increase_per_tick_percent", "overdrawDamageIncreasePerTickPercent", result.overdrawDamageIncreasePerTickPercent);
            result.overdrawMaxDamageBonusPercent = readDouble(json, "overdraw",
                    "max_damage_bonus_percent", "overdrawMaxDamageBonusPercent", result.overdrawMaxDamageBonusPercent);
            result.overdrawSelfDamageHearts = readDouble(json, "overdraw", "self_damage_hearts",
                    "overdrawSelfDamageHearts", result.overdrawSelfDamageHearts);
            result.overdrawDurabilityLossPercent = readDouble(json, "overdraw", "durability_loss_percent",
                    "overdrawDurabilityLossPercent", result.overdrawDurabilityLossPercent);
            result.overdrawAutoFireDelayTicks = readInt(json, "overdraw", "auto_fire_delay_ticks",
                    "overdrawAutoFireDelayTicks", result.overdrawAutoFireDelayTicks);
            result.overdrawMinimumFailureDelayTicks = readInt(json, "overdraw", "minimum_failure_delay_ticks",
                    "overdrawMinimumFailureDelayTicks", result.overdrawMinimumFailureDelayTicks);
            result.overdrawBowDisableTicks = readInt(json, "overdraw", "bow_disable_ticks",
                    "overdrawBowDisableTicks", result.overdrawBowDisableTicks);
            result.overdrawMisfireAngleDegrees = readDouble(json, "overdraw", "misfire_angle_degrees",
                    "overdrawMisfireAngleDegrees", result.overdrawMisfireAngleDegrees);

            result.longshot32Threshold = readDouble(json, "longshot", "threshold_32_blocks",
                    "longshot32Threshold", result.longshot32Threshold);
            result.longshot64Threshold = readDouble(json, "longshot", "threshold_64_blocks",
                    "longshot64Threshold", result.longshot64Threshold);
            result.longshot32Multiplier = readDouble(json, "longshot", "damage_multiplier_32",
                    "longshot32Multiplier", result.longshot32Multiplier);
            result.longshot64Multiplier = readDouble(json, "longshot", "damage_multiplier_64",
                    "longshot64Multiplier", result.longshot64Multiplier);
            result.longshot16Threshold = readDouble(json, "longshot", "threshold_16_blocks",
                    "longshot16Threshold", result.longshot16Threshold);
            result.longshot48Threshold = readDouble(json, "longshot", "threshold_48_blocks",
                    "longshot48Threshold", result.longshot48Threshold);
            result.longshot16Multiplier = readDouble(json, "longshot", "damage_multiplier_16",
                    "longshot16Multiplier", result.longshot16Multiplier);
            result.longshot48Multiplier = readDouble(json, "longshot", "damage_multiplier_48",
                    "longshot48Multiplier", result.longshot48Multiplier);

            result.burstStaggerDelayTicks = readInt(json, "burst", "stagger_delay_ticks",
                    "burstStaggerDelayTicks", result.burstStaggerDelayTicks);
            result.burstArrowsPerLevel = readInt(json, "burst", "arrows_per_level",
                    "burstArrowsPerLevel", result.burstArrowsPerLevel);

            result.fractureSplitDelayTicks = readInt(json, "fracture", "split_delay_ticks",
                    "fractureSplitDelayTicks", result.fractureSplitDelayTicks);
            result.fractureSplitAngleDegrees = readDouble(json, "fracture", "split_angle_degrees",
                    "fractureSplitAngleDegrees", result.fractureSplitAngleDegrees);
            result.fractureEnabled = readBoolean(json, "fracture", "enabled",
                    "fractureEnabled", result.fractureEnabled);
            result.fractureReferenceReleaseSpeed = readDouble(json, "fracture", "reference_release_speed",
                    "fractureReferenceReleaseSpeed", result.fractureReferenceReleaseSpeed);
            result.fractureMinSplitDelayTicks = readInt(json, "fracture", "min_split_delay_ticks",
                    "fractureMinSplitDelayTicks", result.fractureMinSplitDelayTicks);
            result.fractureMaxSplitDelayTicks = readInt(json, "fracture", "max_split_delay_ticks",
                    "fractureMaxSplitDelayTicks", result.fractureMaxSplitDelayTicks);

            result.headshotEnabled = readBoolean(json, "headshot", "enableHeadshot",
                    "enableHeadshot", result.headshotEnabled);
            result.headshotDamageBonusI = readDouble(json, "headshot", "headshotDamageBonusI",
                    "headshotDamageBonusI", result.headshotDamageBonusI);
            result.headshotDamageBonusII = readDouble(json, "headshot", "headshotDamageBonusII",
                    "headshotDamageBonusII", result.headshotDamageBonusII);
            result.headshotDamageBonusIII = readDouble(json, "headshot", "headshotDamageBonusIII",
                    "headshotDamageBonusIII", result.headshotDamageBonusIII);
            result.headshotBoxRadius = readDouble(json, "headshot", "headshotBoxRadius",
                    "headshotBoxRadius", result.headshotBoxRadius);
            result.headshotFeedbackEnabled = readBoolean(json, "headshot", "headshotFeedbackEnabled",
                    "headshotFeedbackEnabled", result.headshotFeedbackEnabled);
            result.headshotPvpDamageBonusI = readDouble(json, "headshot", "headshotPvpDamageBonusI",
                    "headshotPvpDamageBonusI", result.headshotPvpDamageBonusI);
            result.headshotPvpDamageBonusII = readDouble(json, "headshot", "headshotPvpDamageBonusII",
                    "headshotPvpDamageBonusII", result.headshotPvpDamageBonusII);
            result.headshotPvpDamageBonusIII = readDouble(json, "headshot", "headshotPvpDamageBonusIII",
                    "headshotPvpDamageBonusIII", result.headshotPvpDamageBonusIII);

            result.fletchingRecipeOutputCount = readInt(json, "fletching", "recipe_output_count",
                    "fletchingRecipeOutputCount", result.fletchingRecipeOutputCount);
            result.trajectoryColourVisualisation = readBoolean(json, "trajectory", "colour_visualisation",
                    "trajectoryColourVisualisation", result.trajectoryColourVisualisation);
            result.modEnabled = readBoolean(json, "general", "mod_enabled", "modEnabled", result.modEnabled);
            result.infiniteLevels = readBoolean(json, "regular", "infinite_levels",
                    "infiniteLevels", result.infiniteLevels);
            result.validate();
            return result;
        }

        private JsonObject toJson() {
            JsonObject root = new JsonObject();

            JsonObject physics = new JsonObject();
            physics.addProperty("gravity", gravity);
            physics.addProperty("drag", drag);
            physics.addProperty("speed_multiplier", speedMultiplier);
            physics.addProperty("randomness", randomness);
            physics.addProperty("terminal_velocity", terminalVelocity);
            physics.addProperty("max_lifetime_ticks", maxLifetimeTicks);
            physics.addProperty("ricochet_velocity_loss_percent", ricochetVelocityLossPercent);
            root.add("physics", physics);

            JsonObject ricochet = new JsonObject();
            ricochet.addProperty("velocity_loss_percent", ricochetVelocityLossPercent);
            root.add("ricochet", ricochet);

            JsonObject overdraw = new JsonObject();
            overdraw.addProperty("damage_increase_per_tick_percent", overdrawDamageIncreasePerTickPercent);
            overdraw.addProperty("max_damage_bonus_percent", overdrawMaxDamageBonusPercent);
            overdraw.addProperty("self_damage_hearts", overdrawSelfDamageHearts);
            overdraw.addProperty("durability_loss_percent", overdrawDurabilityLossPercent);
            overdraw.addProperty("auto_fire_delay_ticks", overdrawAutoFireDelayTicks);
            overdraw.addProperty("minimum_failure_delay_ticks", overdrawMinimumFailureDelayTicks);
            overdraw.addProperty("bow_disable_ticks", overdrawBowDisableTicks);
            overdraw.addProperty("misfire_angle_degrees", overdrawMisfireAngleDegrees);
            root.add("overdraw", overdraw);

            JsonObject longshot = new JsonObject();
            longshot.addProperty("threshold_16_blocks", longshot16Threshold);
            longshot.addProperty("threshold_32_blocks", longshot32Threshold);
            longshot.addProperty("threshold_48_blocks", longshot48Threshold);
            longshot.addProperty("threshold_64_blocks", longshot64Threshold);
            longshot.addProperty("damage_multiplier_16", longshot16Multiplier);
            longshot.addProperty("damage_multiplier_32", longshot32Multiplier);
            longshot.addProperty("damage_multiplier_48", longshot48Multiplier);
            longshot.addProperty("damage_multiplier_64", longshot64Multiplier);
            root.add("longshot", longshot);

            JsonObject burst = new JsonObject();
            burst.addProperty("stagger_delay_ticks", burstStaggerDelayTicks);
            burst.addProperty("arrows_per_level", burstArrowsPerLevel);
            root.add("burst", burst);

            JsonObject fracture = new JsonObject();
            fracture.addProperty("enabled", fractureEnabled);
            fracture.addProperty("split_delay_ticks", fractureSplitDelayTicks);
            fracture.addProperty("split_angle_degrees", fractureSplitAngleDegrees);
            fracture.addProperty("reference_release_speed", fractureReferenceReleaseSpeed);
            fracture.addProperty("min_split_delay_ticks", fractureMinSplitDelayTicks);
            fracture.addProperty("max_split_delay_ticks", fractureMaxSplitDelayTicks);
            root.add("fracture", fracture);

            JsonObject arrowTypes = new JsonObject();
            JsonObject ender = new JsonObject();
            ender.addProperty("enabled", enderArrowEnabled);
            arrowTypes.add("ender", ender);
            JsonObject shockwave = new JsonObject();
            shockwave.addProperty("enabled", shockwaveArrowEnabled);
            shockwave.addProperty("radius", shockwaveRadius);
            shockwave.addProperty("strength", shockwaveStrength);
            arrowTypes.add("shockwave", shockwave);
            JsonObject impulse = new JsonObject();
            impulse.addProperty("enabled", impulseArrowEnabled);
            impulse.addProperty("radius", impulseRadius);
            impulse.addProperty("strength", impulseStrength);
            arrowTypes.add("impulse", impulse);
            JsonObject explosive = new JsonObject();
            explosive.addProperty("enabled", explosiveArrowEnabled);
            explosive.addProperty("explosion_size", explosiveArrowSize);
            explosive.addProperty("anti_grief", explosiveArrowAntiGrief);
            arrowTypes.add("explosive", explosive);
            root.add("arrow_types", arrowTypes);

            JsonObject fletching = new JsonObject();
            fletching.addProperty("recipe_output_count", fletchingRecipeOutputCount);
            root.add("fletching", fletching);

            JsonObject headshot = new JsonObject();
            headshot.addProperty("enableHeadshot", headshotEnabled);
            headshot.addProperty("headshotDamageBonusI", headshotDamageBonusI);
            headshot.addProperty("headshotDamageBonusII", headshotDamageBonusII);
            headshot.addProperty("headshotDamageBonusIII", headshotDamageBonusIII);
            headshot.addProperty("headshotBoxRadius", headshotBoxRadius);
            headshot.addProperty("headshotFeedbackEnabled", headshotFeedbackEnabled);
            headshot.addProperty("headshotPvpDamageBonusI", headshotPvpDamageBonusI);
            headshot.addProperty("headshotPvpDamageBonusII", headshotPvpDamageBonusII);
            headshot.addProperty("headshotPvpDamageBonusIII", headshotPvpDamageBonusIII);
            root.add("headshot", headshot);

            JsonObject trajectory = new JsonObject();
            trajectory.addProperty("colour_visualisation", trajectoryColourVisualisation);
            root.add("trajectory", trajectory);

            JsonObject general = new JsonObject();
            general.addProperty("mod_enabled", modEnabled);
            root.add("general", general);

            JsonObject regular = new JsonObject();
            regular.addProperty("infinite_levels", infiniteLevels);
            root.add("regular", regular);
            return root;
        }

        private void validate() {
            gravity = nonNegative(gravity, 0.05D);
            drag = clamp(drag, 0.0D, 1.0D, 0.99D);
            speedMultiplier = nonNegative(speedMultiplier, 1.0D);
            randomness = nonNegative(randomness, 0.0D);
            terminalVelocity = nonNegative(terminalVelocity, 999.0D);
            maxLifetimeTicks = Math.max(1, maxLifetimeTicks);
            ricochetVelocityLossPercent = clamp(ricochetVelocityLossPercent, 0.0D, 100.0D, 10.0D);
            shockwaveRadius = nonNegative(shockwaveRadius, 4.0D);
            shockwaveStrength = nonNegative(shockwaveStrength, 2.0D);
            impulseRadius = nonNegative(impulseRadius, 4.0D);
            impulseStrength = nonNegative(impulseStrength, 2.0D);
            explosiveArrowSize = nonNegative(explosiveArrowSize, 2.5D);
            overdrawDamageIncreasePerTickPercent = nonNegative(overdrawDamageIncreasePerTickPercent, 1.0D);
            overdrawMaxDamageBonusPercent = nonNegative(overdrawMaxDamageBonusPercent, 100.0D);
            overdrawAutoFireDelayTicks = Math.max(1, overdrawAutoFireDelayTicks);
            overdrawMinimumFailureDelayTicks = Math.max(1,
                    Math.min(overdrawMinimumFailureDelayTicks, overdrawAutoFireDelayTicks));
            overdrawBowDisableTicks = Math.max(1, overdrawBowDisableTicks);
            overdrawMisfireAngleDegrees = clamp(overdrawMisfireAngleDegrees, 0.0D, 180.0D, 45.0D);
            overdrawDurabilityLossPercent = clamp(overdrawDurabilityLossPercent, 0.0D, 100.0D, 25.0D);
            overdrawSelfDamageHearts = nonNegative(overdrawSelfDamageHearts, 2.0D);
            burstStaggerDelayTicks = Math.max(1, burstStaggerDelayTicks);
            burstArrowsPerLevel = Math.max(1, burstArrowsPerLevel);
            fractureSplitDelayTicks = Math.max(1, fractureSplitDelayTicks);
            fractureMinSplitDelayTicks = Math.max(1, fractureMinSplitDelayTicks);
            fractureMaxSplitDelayTicks = Math.max(fractureMinSplitDelayTicks, fractureMaxSplitDelayTicks);
            fletchingRecipeOutputCount = Math.max(1, fletchingRecipeOutputCount);
            headshotDamageBonusI = nonNegative(headshotDamageBonusI, 15.0D);
            headshotDamageBonusII = nonNegative(headshotDamageBonusII, 30.0D);
            headshotDamageBonusIII = nonNegative(headshotDamageBonusIII, 45.0D);
            headshotBoxRadius = nonNegative(headshotBoxRadius, 0.35D);
            headshotPvpDamageBonusI = nonNegative(headshotPvpDamageBonusI, 15.0D);
            headshotPvpDamageBonusII = nonNegative(headshotPvpDamageBonusII, 30.0D);
            headshotPvpDamageBonusIII = nonNegative(headshotPvpDamageBonusIII, 45.0D);
        }

        private static double readDouble(JsonObject root, String section, String key,
                                         String legacyKey, double fallback) {
            JsonElement value = readNested(root, section, key);
            if (value == null && legacyKey != null) {
                value = root.get(legacyKey);
            }
            return value != null && value.isJsonPrimitive() ? value.getAsDouble() : fallback;
        }

        private static int readInt(JsonObject root, String section, String key,
                                   String legacyKey, int fallback) {
            JsonElement value = readNested(root, section, key);
            if (value == null && legacyKey != null) {
                value = root.get(legacyKey);
            }
            return value != null && value.isJsonPrimitive() ? value.getAsInt() : fallback;
        }

        private static boolean readBoolean(JsonObject root, String section, String key,
                                           String legacyKey, boolean fallback) {
            JsonElement value = readNested(root, section, key);
            if (value == null && legacyKey != null) {
                value = root.get(legacyKey);
            }
            return value != null && value.isJsonPrimitive() ? value.getAsBoolean() : fallback;
        }

        private static JsonElement readNested(JsonObject root, String section, String key) {
            JsonObject current = root.getAsJsonObject(section);
            if (current == null) {
                return null;
            }
            for (String part : key.split("\\.")) {
                JsonElement element = current.get(part);
                if (element == null) {
                    return null;
                }
                if (part.equals(key.substring(key.lastIndexOf('.') + 1))) {
                    return element;
                }
                if (!element.isJsonObject()) {
                    return null;
                }
                current = element.getAsJsonObject();
            }
            return null;
        }

        private static double nonNegative(double value, double fallback) {
            return Double.isFinite(value) && value >= 0.0D ? value : fallback;
        }

        private static double clamp(double value, double min, double max, double fallback) {
            return Double.isFinite(value) ? Math.max(min, Math.min(max, value)) : fallback;
        }
    }
}
