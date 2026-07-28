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

    public static boolean isImpulseArrowEnabled() {
        return config.impulseArrowEnabled;
    }

    public static boolean isExplosiveArrowEnabled() {
        return config.explosiveArrowEnabled;
    }

    public static boolean isStickyArrowEnabled() {
        return config.stickyArrowEnabled;
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

    public static int getBurstArrowsPerLevel() {
        return config.burstArrowsPerLevel;
    }

    public static double getSharpshooterReductionPercent(int level) {
        return switch (Math.max(1, Math.min(level, 3))) {
            case 1 -> config.sharpshooterLevel1ReductionPercent;
            case 2 -> config.sharpshooterLevel2ReductionPercent;
            default -> config.sharpshooterLevel3ReductionPercent;
        };
    }

    public static boolean isSharpshooterEnabled() {
        return config.sharpshooterEnabled;
    }

    public static int getFletchingCraftingTimeTicks() {
        return config.fletchingCraftingTimeTicks;
    }

    public static int getFletchingRecipeOutputCount() {
        return config.fletchingRecipeOutputCount;
    }

    public static boolean isModEnabled() {
        return config.modEnabled;
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
        private boolean impulseArrowEnabled = true;
        private boolean explosiveArrowEnabled = true;
        private boolean stickyArrowEnabled = true;
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
        private int burstArrowsPerLevel = 1;
        private boolean sharpshooterEnabled = false;
        private double sharpshooterLevel1ReductionPercent = 70.0D;
        private double sharpshooterLevel2ReductionPercent = 40.0D;
        private double sharpshooterLevel3ReductionPercent = 10.0D;
        private int fletchingCraftingTimeTicks = 20;
        private int fletchingRecipeOutputCount = 4;
        private boolean modEnabled = true;

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
            result.impulseArrowEnabled = readBoolean(json, "arrow_types", "impulse.enabled",
                    null, result.impulseArrowEnabled);
            result.explosiveArrowEnabled = readBoolean(json, "arrow_types", "explosive.enabled",
                    null, result.explosiveArrowEnabled);
            result.stickyArrowEnabled = readBoolean(json, "arrow_types", "sticky.enabled",
                    null, result.stickyArrowEnabled);
            result.impulseBlastRadius = readDouble(json, "arrow_types", "impulse.blast_radius",
                    "impulseBlastRadius", result.impulseBlastRadius);
            result.impulseKnockbackStrength = readDouble(json, "arrow_types", "impulse.knockback_strength",
                    "impulseKnockbackStrength", result.impulseKnockbackStrength);
            result.explosiveArrowSize = readDouble(json, "arrow_types", "explosive.explosion_size",
                    "explosiveArrowSize", result.explosiveArrowSize);
            result.stickyMovementReductionPercent = readDouble(json, "arrow_types",
                    "sticky.movement_reduction_percent", "stickyMovementReductionPercent",
                    result.stickyMovementReductionPercent);
            result.stickyDurationTicks = readInt(json, "arrow_types", "sticky.duration_ticks",
                    "stickyDurationTicks", result.stickyDurationTicks);
            result.stickySlownessLevel = readInt(json, "arrow_types", "sticky.slowness_level",
                    "stickySlownessLevel", result.stickySlownessLevel);

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

            result.sharpshooterEnabled = readBoolean(json, "sharpshooter", "enabled",
                    "sharpshooterEnabled", result.sharpshooterEnabled);
            result.sharpshooterLevel1ReductionPercent = readDouble(json, "sharpshooter", "level_1_reduction_percent",
                    "sharpshooterLevel1ReductionPercent", result.sharpshooterLevel1ReductionPercent);
            result.sharpshooterLevel2ReductionPercent = readDouble(json, "sharpshooter", "level_2_reduction_percent",
                    "sharpshooterLevel2ReductionPercent", result.sharpshooterLevel2ReductionPercent);
            result.sharpshooterLevel3ReductionPercent = readDouble(json, "sharpshooter", "level_3_reduction_percent",
                    "sharpshooterLevel3ReductionPercent", result.sharpshooterLevel3ReductionPercent);
            result.fletchingCraftingTimeTicks = readInt(json, "fletching", "crafting_time_ticks",
                    "fletchingCraftingTimeTicks", result.fletchingCraftingTimeTicks);
            result.fletchingRecipeOutputCount = readInt(json, "fletching", "recipe_output_count",
                    "fletchingRecipeOutputCount", result.fletchingRecipeOutputCount);
            result.modEnabled = readBoolean(json, "general", "mod_enabled", "modEnabled", result.modEnabled);
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
            JsonObject impulse = new JsonObject();
            impulse.addProperty("enabled", impulseArrowEnabled);
            impulse.addProperty("blast_radius", impulseBlastRadius);
            impulse.addProperty("knockback_strength", impulseKnockbackStrength);
            arrowTypes.add("impulse", impulse);
            JsonObject explosive = new JsonObject();
            explosive.addProperty("enabled", explosiveArrowEnabled);
            explosive.addProperty("explosion_size", explosiveArrowSize);
            arrowTypes.add("explosive", explosive);
            JsonObject sticky = new JsonObject();
            sticky.addProperty("enabled", stickyArrowEnabled);
            sticky.addProperty("movement_reduction_percent", stickyMovementReductionPercent);
            sticky.addProperty("duration_ticks", stickyDurationTicks);
            sticky.addProperty("slowness_level", stickySlownessLevel);
            arrowTypes.add("sticky", sticky);
            root.add("arrow_types", arrowTypes);

            JsonObject sharpshooter = new JsonObject();
            sharpshooter.addProperty("enabled", sharpshooterEnabled);
            sharpshooter.addProperty("level_1_reduction_percent", sharpshooterLevel1ReductionPercent);
            sharpshooter.addProperty("level_2_reduction_percent", sharpshooterLevel2ReductionPercent);
            sharpshooter.addProperty("level_3_reduction_percent", sharpshooterLevel3ReductionPercent);
            root.add("sharpshooter", sharpshooter);

            JsonObject fletching = new JsonObject();
            fletching.addProperty("crafting_time_ticks", fletchingCraftingTimeTicks);
            fletching.addProperty("recipe_output_count", fletchingRecipeOutputCount);
            root.add("fletching", fletching);

            JsonObject general = new JsonObject();
            general.addProperty("mod_enabled", modEnabled);
            root.add("general", general);
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
            impulseBlastRadius = nonNegative(impulseBlastRadius, 4.0D);
            impulseKnockbackStrength = nonNegative(impulseKnockbackStrength, 2.0D);
            explosiveArrowSize = nonNegative(explosiveArrowSize, 2.5D);
            stickyMovementReductionPercent = clamp(stickyMovementReductionPercent, 0.0D, 100.0D, 50.0D);
            stickyDurationTicks = Math.max(1, stickyDurationTicks);
            stickySlownessLevel = Math.max(1, stickySlownessLevel);
            overdrawDamageIncreasePerTickPercent = nonNegative(overdrawDamageIncreasePerTickPercent, 1.0D);
            overdrawMaxDamageBonusPercent = nonNegative(overdrawMaxDamageBonusPercent, 100.0D);
            overdrawAutoFireDelayTicks = Math.max(1, overdrawAutoFireDelayTicks);
            overdrawDurabilityLossPercent = clamp(overdrawDurabilityLossPercent, 0.0D, 100.0D, 25.0D);
            overdrawSelfDamageHearts = nonNegative(overdrawSelfDamageHearts, 2.0D);
            burstStaggerDelayTicks = Math.max(1, burstStaggerDelayTicks);
            burstArrowsPerLevel = Math.max(1, burstArrowsPerLevel);
            fractureSplitDelayTicks = Math.max(1, fractureSplitDelayTicks);
            fractureMinSplitDelayTicks = Math.max(1, fractureMinSplitDelayTicks);
            fractureMaxSplitDelayTicks = Math.max(fractureMinSplitDelayTicks, fractureMaxSplitDelayTicks);
            fletchingCraftingTimeTicks = Math.max(1, fletchingCraftingTimeTicks);
            fletchingRecipeOutputCount = Math.max(1, fletchingRecipeOutputCount);
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
