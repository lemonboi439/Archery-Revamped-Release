package me.lemonboi439.archeryRevamped.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "archery_revamped-config.json";
    private static final PhysicsConfig DEFAULTS = new PhysicsConfig();
    private static PhysicsConfig config = DEFAULTS;

    private ConfigManager() {
    }

    public static void load() {
        Path configDirectory = FabricLoader.getInstance().getConfigDir();
        Path configPath = configDirectory.resolve(FILE_NAME);

        try {
            Files.createDirectories(configDirectory);
            if (Files.exists(configPath)) {
                try (Reader reader = Files.newBufferedReader(configPath)) {
                    JsonObject json = GSON.fromJson(reader, JsonObject.class);
                    config = PhysicsConfig.fromJson(json);
                }
            } else {
                config = DEFAULTS;
                save(configPath);
            }
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            config = DEFAULTS;
        }
    }

    public static void save() {
        try {
            Path configDirectory = FabricLoader.getInstance().getConfigDir();
            Files.createDirectories(configDirectory);
            save(configDirectory.resolve(FILE_NAME));
        } catch (IOException exception) {
            // Defaults remain active if the config cannot be written.
        }
    }

    private static void save(Path configPath) throws IOException {
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            GSON.toJson(config, writer);
        }
    }

    public static double getGravity() {
        return config.gravity;
    }

    public static double getDrag() {
        return config.drag;
    }

    public static double getSpeedMultiplier() {
        return config.speedMultiplier;
    }

    public static double getRandomness() {
        return config.randomness;
    }

    public static double getTerminalVelocity() {
        return config.terminalVelocity;
    }

    public static int getMaxLifetimeTicks() {
        return config.maxLifetimeTicks;
    }

    public static double getRicochetVelocityLossPercent() {
        return config.ricochetVelocityLossPercent;
    }

    public static boolean isEnderArrowEnabled() {
        return config.enderArrowEnabled;
    }

    public static double getImpulseBlastRadius() {
        return config.impulseBlastRadius;
    }

    public static double getImpulseKnockbackStrength() {
        return config.impulseKnockbackStrength;
    }

    public static double getExplosiveArrowSize() {
        return config.explosiveArrowSize;
    }

    public static double getStickyMovementReductionPercent() {
        return config.stickyMovementReductionPercent;
    }

    public static int getStickyDurationTicks() {
        return config.stickyDurationTicks;
    }

    public static int getStickySlownessLevel() {
        return config.stickySlownessLevel;
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

    public static double getSharpshooterReductionPercent(int level) {
        return switch (Math.max(1, Math.min(level, 3))) {
            case 1 -> config.sharpshooterLevel1ReductionPercent;
            case 2 -> config.sharpshooterLevel2ReductionPercent;
            default -> config.sharpshooterLevel3ReductionPercent;
        };
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
        private double impulseBlastRadius = 4.0D;
        private double impulseKnockbackStrength = 2.0D;
        private double explosiveArrowSize = 2.5D;
        private double stickyMovementReductionPercent = 50.0D;
        private int stickyDurationTicks = 100;
        private int stickySlownessLevel = 1;
        private double overdrawDamageIncreasePerTickPercent = 1.0D;
        private double overdrawMaxDamageBonusPercent = 100.0D;
        private int overdrawAutoFireDelayTicks = 100;
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
        private double sharpshooterLevel1ReductionPercent = 70.0D;
        private double sharpshooterLevel2ReductionPercent = 40.0D;
        private double sharpshooterLevel3ReductionPercent = 10.0D;

        private static PhysicsConfig fromJson(JsonObject json) {
            PhysicsConfig result = new PhysicsConfig();
            if (json == null) {
                return result;
            }
            result.gravity = getDouble(json, "gravity", result.gravity);
            result.drag = getDouble(json, "drag", result.drag);
            result.speedMultiplier = getDouble(json, "speedMultiplier", result.speedMultiplier);
            result.randomness = getDouble(json, "randomness", result.randomness);
            result.terminalVelocity = getDouble(json, "terminalVelocity", result.terminalVelocity);
            result.maxLifetimeTicks = getInt(json, "maxLifetimeTicks", result.maxLifetimeTicks);
            result.ricochetVelocityLossPercent = getDouble(json,
                    "ricochetVelocityLossPercent", result.ricochetVelocityLossPercent);
            result.enderArrowEnabled = getBoolean(json, "enderArrowEnabled", result.enderArrowEnabled);
            result.impulseBlastRadius = getDouble(json, "impulseBlastRadius", result.impulseBlastRadius);
            result.impulseKnockbackStrength = getDouble(json,
                    "impulseKnockbackStrength", result.impulseKnockbackStrength);
            result.explosiveArrowSize = getDouble(json, "explosiveArrowSize", result.explosiveArrowSize);
            result.stickyMovementReductionPercent = getDouble(json,
                    "stickyMovementReductionPercent", result.stickyMovementReductionPercent);
            result.stickyDurationTicks = getInt(json, "stickyDurationTicks", result.stickyDurationTicks);
            result.stickySlownessLevel = getInt(json, "stickySlownessLevel", result.stickySlownessLevel);
            result.overdrawDamageIncreasePerTickPercent = getDouble(json,
                    "overdrawDamageIncreasePerTickPercent", result.overdrawDamageIncreasePerTickPercent);
            result.overdrawMaxDamageBonusPercent = getDouble(json,
                    "overdrawMaxDamageBonusPercent", result.overdrawMaxDamageBonusPercent);
            result.overdrawAutoFireDelayTicks = getInt(json,
                    "overdrawAutoFireDelayTicks", result.overdrawAutoFireDelayTicks);
            result.overdrawDurabilityLossPercent = getDouble(json,
                    "overdrawDurabilityLossPercent", result.overdrawDurabilityLossPercent);
            result.overdrawSelfDamageHearts = getDouble(json,
                    "overdrawSelfDamageHearts", result.overdrawSelfDamageHearts);
            result.longshot16Threshold = getDouble(json, "longshot16Threshold", result.longshot16Threshold);
            result.longshot32Threshold = getDouble(json, "longshot32Threshold", result.longshot32Threshold);
            result.longshot48Threshold = getDouble(json, "longshot48Threshold", result.longshot48Threshold);
            result.longshot64Threshold = getDouble(json, "longshot64Threshold", result.longshot64Threshold);
            result.longshot16Multiplier = getDouble(json, "longshot16Multiplier", result.longshot16Multiplier);
            result.longshot32Multiplier = getDouble(json, "longshot32Multiplier", result.longshot32Multiplier);
            result.longshot48Multiplier = getDouble(json, "longshot48Multiplier", result.longshot48Multiplier);
            result.longshot64Multiplier = getDouble(json, "longshot64Multiplier", result.longshot64Multiplier);
            result.fractureEnabled = getBoolean(json, "fractureEnabled", result.fractureEnabled);
            result.fractureSplitDelayTicks = getInt(json, "fractureSplitDelayTicks", result.fractureSplitDelayTicks);
            result.fractureReferenceReleaseSpeed = getDouble(json,
                    "fractureReferenceReleaseSpeed", result.fractureReferenceReleaseSpeed);
            result.fractureMinSplitDelayTicks = getInt(json,
                    "fractureMinSplitDelayTicks", result.fractureMinSplitDelayTicks);
            result.fractureMaxSplitDelayTicks = getInt(json,
                    "fractureMaxSplitDelayTicks", result.fractureMaxSplitDelayTicks);
            result.fractureSplitAngleDegrees = getDouble(json,
                    "fractureSplitAngleDegrees", result.fractureSplitAngleDegrees);
            result.burstStaggerDelayTicks = getInt(json, "burstStaggerDelayTicks", result.burstStaggerDelayTicks);
            result.sharpshooterLevel1ReductionPercent = getDouble(json,
                    "sharpshooterLevel1ReductionPercent", result.sharpshooterLevel1ReductionPercent);
            result.sharpshooterLevel2ReductionPercent = getDouble(json,
                    "sharpshooterLevel2ReductionPercent", result.sharpshooterLevel2ReductionPercent);
            result.sharpshooterLevel3ReductionPercent = getDouble(json,
                    "sharpshooterLevel3ReductionPercent", result.sharpshooterLevel3ReductionPercent);
            return result;
        }

        private static double getDouble(JsonObject json, String key, double fallback) {
            return json.has(key) && json.get(key).isJsonPrimitive()
                    ? json.get(key).getAsDouble() : fallback;
        }

        private static int getInt(JsonObject json, String key, int fallback) {
            return json.has(key) && json.get(key).isJsonPrimitive()
                    ? json.get(key).getAsInt() : fallback;
        }

        private static boolean getBoolean(JsonObject json, String key, boolean fallback) {
            return json.has(key) && json.get(key).isJsonPrimitive()
                    ? json.get(key).getAsBoolean() : fallback;
        }
    }
}
