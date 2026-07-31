package me.lemonboi439.archeryRevamped.client.config;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Cloth Config screen for every value in archery_revamped-config.json.
 * Values are written through ConfigManager so commands and the GUI share the
 * exact same live configuration.
 */
public final class ArcheryRevampedConfigScreen {
    private ArcheryRevampedConfigScreen() {
    }

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Archery Revamped Configuration"));
        ConfigEntryBuilder entries = builder.entryBuilder();

        addPhysics(builder, entries);
        addArrowTypes(builder, entries);
        addOverdraw(builder, entries);
        addLongshot(builder, entries);
        addBurst(builder, entries);
        addFracture(builder, entries);
        addHeadshot(builder, entries);
        addFletching(builder, entries);
        addTrajectory(builder, entries);
        addRegular(builder, entries);

        builder.setSavingRunnable(ConfigManager::save);
        return builder.build();
    }

    private static void addPhysics(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Physics"));
        category.addEntry(entries.startDoubleField(Text.literal("Gravity"), ConfigManager.getGravity())
                .setDefaultValue(0.05D).setMin(0.0D).setSaveConsumer(ConfigManager::setGravity).build());
        category.addEntry(entries.startDoubleField(Text.literal("Drag"), ConfigManager.getDrag())
                .setDefaultValue(0.99D).setMin(0.0D).setMax(1.0D)
                .setSaveConsumer(ConfigManager::setDrag).build());
        category.addEntry(entries.startDoubleField(Text.literal("Speed multiplier"), ConfigManager.getSpeedMultiplier())
                .setDefaultValue(1.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setSpeedMultiplier).build());
        category.addEntry(entries.startDoubleField(Text.literal("Randomness"), ConfigManager.getRandomness())
                .setDefaultValue(0.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setRandomness).build());
        category.addEntry(entries.startDoubleField(Text.literal("Terminal velocity"), ConfigManager.getTerminalVelocity())
                .setDefaultValue(999.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setTerminalVelocity).build());
        category.addEntry(entries.startIntField(Text.literal("Maximum lifetime (ticks)"), ConfigManager.getMaxLifetimeTicks())
                .setDefaultValue(1200).setMin(1)
                .setSaveConsumer(ConfigManager::setMaxLifetimeTicks).build());
        category.addEntry(entries.startDoubleField(Text.literal("Ricochet velocity loss (%)"),
                        ConfigManager.getRicochetVelocityLossPercent())
                .setDefaultValue(10.0D).setMin(0.0D).setMax(100.0D)
                .setSaveConsumer(ConfigManager::setRicochetVelocityLossPercent).build());
    }

    private static void addArrowTypes(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Arrow types"));
        category.addEntry(entries.startBooleanToggle(Text.literal("Ender arrows enabled"),
                        ConfigManager.isEnderArrowEnabled())
                .setDefaultValue(true).setSaveConsumer(ConfigManager::setEnderArrowEnabled).build());
        category.addEntry(entries.startBooleanToggle(Text.literal("Shockwave arrows enabled"),
                        ConfigManager.isShockwaveArrowEnabled())
                .setDefaultValue(true).setSaveConsumer(ConfigManager::setShockwaveArrowEnabled).build());
        category.addEntry(entries.startDoubleField(Text.literal("Shockwave radius"),
                        ConfigManager.getShockwaveRadius())
                .setDefaultValue(4.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setShockwaveRadius).build());
        category.addEntry(entries.startDoubleField(Text.literal("Shockwave strength"),
                        ConfigManager.getShockwaveStrength())
                .setDefaultValue(2.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setShockwaveStrength).build());
        category.addEntry(entries.startBooleanToggle(Text.literal("Impulse arrows enabled"),
                        ConfigManager.isImpulseArrowEnabled())
                .setDefaultValue(true).setSaveConsumer(ConfigManager::setImpulseArrowEnabled).build());
        category.addEntry(entries.startDoubleField(Text.literal("Impulse radius"),
                        ConfigManager.getImpulseRadius())
                .setDefaultValue(4.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setImpulseRadius).build());
        category.addEntry(entries.startDoubleField(Text.literal("Impulse pull strength"),
                        ConfigManager.getImpulseStrength())
                .setDefaultValue(2.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setImpulseStrength).build());
        category.addEntry(entries.startBooleanToggle(Text.literal("Explosive arrows enabled"),
                        ConfigManager.isExplosiveArrowEnabled())
                .setDefaultValue(true).setSaveConsumer(ConfigManager::setExplosiveArrowEnabled).build());
        category.addEntry(entries.startDoubleField(Text.literal("Explosion size"),
                        ConfigManager.getExplosiveArrowSize())
                .setDefaultValue(2.5D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setExplosiveArrowSize).build());
    }

    private static void addOverdraw(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Overdraw"));
        category.addEntry(entries.startDoubleField(Text.literal("Damage increase per tick (%)"),
                        ConfigManager.getOverdrawDamageIncreasePerTickPercent())
                .setDefaultValue(1.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setOverdrawDamageIncreasePerTickPercent).build());
        category.addEntry(entries.startDoubleField(Text.literal("Maximum damage bonus (%)"),
                        ConfigManager.getOverdrawMaxDamageBonusPercent())
                .setDefaultValue(100.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setOverdrawMaxDamageBonusPercent).build());
        category.addEntry(entries.startIntField(Text.literal("Auto-fire delay (ticks)"),
                        ConfigManager.getOverdrawAutoFireDelayTicks())
                .setDefaultValue(100).setMin(1)
                .setSaveConsumer(ConfigManager::setOverdrawAutoFireDelayTicks).build());
        category.addEntry(entries.startDoubleField(Text.literal("Durability loss (%)"),
                        ConfigManager.getOverdrawDurabilityLossPercent())
                .setDefaultValue(25.0D).setMin(0.0D).setMax(100.0D)
                .setSaveConsumer(ConfigManager::setOverdrawDurabilityLossPercent).build());
        category.addEntry(entries.startDoubleField(Text.literal("Self damage (hearts)"),
                        ConfigManager.getOverdrawSelfDamageHearts())
                .setDefaultValue(2.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setOverdrawSelfDamageHearts).build());
    }

    private static void addLongshot(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Longshot"));
        category.addEntry(entries.startDoubleField(Text.literal("16-block threshold"), ConfigManager.getLongshot16Threshold())
                .setDefaultValue(16.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot16Threshold).build());
        category.addEntry(entries.startDoubleField(Text.literal("32-block threshold"), ConfigManager.getLongshot32Threshold())
                .setDefaultValue(32.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot32Threshold).build());
        category.addEntry(entries.startDoubleField(Text.literal("48-block threshold"), ConfigManager.getLongshot48Threshold())
                .setDefaultValue(48.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot48Threshold).build());
        category.addEntry(entries.startDoubleField(Text.literal("64-block threshold"), ConfigManager.getLongshot64Threshold())
                .setDefaultValue(64.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot64Threshold).build());
        category.addEntry(entries.startDoubleField(Text.literal("16-block damage multiplier"), ConfigManager.getLongshot16Multiplier())
                .setDefaultValue(1.5D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot16Multiplier).build());
        category.addEntry(entries.startDoubleField(Text.literal("32-block damage multiplier"), ConfigManager.getLongshot32Multiplier())
                .setDefaultValue(2.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot32Multiplier).build());
        category.addEntry(entries.startDoubleField(Text.literal("48-block damage multiplier"), ConfigManager.getLongshot48Multiplier())
                .setDefaultValue(2.5D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot48Multiplier).build());
        category.addEntry(entries.startDoubleField(Text.literal("64-block damage multiplier"), ConfigManager.getLongshot64Multiplier())
                .setDefaultValue(3.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot64Multiplier).build());
    }

    private static void addBurst(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Burst"));
        category.addEntry(entries.startIntField(Text.literal("Stagger delay (ticks)"), ConfigManager.getBurstStaggerDelayTicks())
                .setDefaultValue(3).setMin(1)
                .setSaveConsumer(ConfigManager::setBurstStaggerDelayTicks).build());
        category.addEntry(entries.startIntField(Text.literal("Arrows per level"), ConfigManager.getBurstArrowsPerLevel())
                .setDefaultValue(1).setMin(1)
                .setSaveConsumer(ConfigManager::setBurstArrowsPerLevel).build());
    }

    private static void addFracture(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Fracture"));
        category.addEntry(entries.startBooleanToggle(Text.literal("Enabled"), ConfigManager.isFractureEnabled())
                .setDefaultValue(false).setSaveConsumer(ConfigManager::setFractureEnabled).build());
        category.addEntry(entries.startIntField(Text.literal("Split delay (ticks)"), ConfigManager.getFractureSplitDelayTicks())
                .setDefaultValue(2).setMin(1)
                .setSaveConsumer(ConfigManager::setFractureSplitDelayTicks).build());
        category.addEntry(entries.startDoubleField(Text.literal("Split angle (degrees)"), ConfigManager.getFractureSplitAngleDegrees())
                .setDefaultValue(15.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setFractureSplitAngleDegrees).build());
        category.addEntry(entries.startDoubleField(Text.literal("Reference release speed"), ConfigManager.getFractureReferenceReleaseSpeed())
                .setDefaultValue(3.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setFractureReferenceReleaseSpeed).build());
        category.addEntry(entries.startIntField(Text.literal("Minimum speed-scaled delay"), ConfigManager.getFractureMinSplitDelayTicks())
                .setDefaultValue(1).setMin(1)
                .setSaveConsumer(ConfigManager::setFractureMinSplitDelayTicks).build());
        category.addEntry(entries.startIntField(Text.literal("Maximum speed-scaled delay"), ConfigManager.getFractureMaxSplitDelayTicks())
                .setDefaultValue(40).setMin(1)
                .setSaveConsumer(ConfigManager::setFractureMaxSplitDelayTicks).build());
    }

    private static void addFletching(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Fletching table"));
        category.addEntry(entries.startIntField(Text.literal("Recipe output count"), ConfigManager.getFletchingRecipeOutputCount())
                .setDefaultValue(4).setMin(1)
                .setSaveConsumer(ConfigManager::setFletchingRecipeOutputCount).build());
    }

    private static void addHeadshot(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Headshot"));
        category.addEntry(entries.startBooleanToggle(Text.literal("Enable Headshot"),
                        ConfigManager.isHeadshotEnabled())
                .setDefaultValue(false).setSaveConsumer(ConfigManager::setHeadshotEnabled).build());
        category.addEntry(entries.startDoubleField(Text.literal("PvE bonus I (%)"),
                        ConfigManager.getHeadshotDamageBonusI())
                .setDefaultValue(15.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotDamageBonusI).build());
        category.addEntry(entries.startDoubleField(Text.literal("PvE bonus II (%)"),
                        ConfigManager.getHeadshotDamageBonusII())
                .setDefaultValue(30.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotDamageBonusII).build());
        category.addEntry(entries.startDoubleField(Text.literal("PvE bonus III (%)"),
                        ConfigManager.getHeadshotDamageBonusIII())
                .setDefaultValue(45.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotDamageBonusIII).build());
        category.addEntry(entries.startDoubleField(Text.literal("Head box radius"),
                        ConfigManager.getHeadshotBoxRadius())
                .setDefaultValue(0.35D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotBoxRadius).build());
        category.addEntry(entries.startBooleanToggle(Text.literal("Headshot feedback"),
                        ConfigManager.isHeadshotFeedbackEnabled())
                .setDefaultValue(true).setSaveConsumer(ConfigManager::setHeadshotFeedbackEnabled).build());
        category.addEntry(entries.startDoubleField(Text.literal("PvP bonus I (%)"),
                        ConfigManager.getHeadshotPvpDamageBonusI())
                .setDefaultValue(15.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotPvpDamageBonusI).build());
        category.addEntry(entries.startDoubleField(Text.literal("PvP bonus II (%)"),
                        ConfigManager.getHeadshotPvpDamageBonusII())
                .setDefaultValue(30.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotPvpDamageBonusII).build());
        category.addEntry(entries.startDoubleField(Text.literal("PvP bonus III (%)"),
                        ConfigManager.getHeadshotPvpDamageBonusIII())
                .setDefaultValue(45.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotPvpDamageBonusIII).build());
    }

    private static void addRegular(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Regular"));
        category.addEntry(entries.startBooleanToggle(Text.literal("Infinite Archery Revamped enchantment levels"),
                        ConfigManager.isInfiniteLevels())
                .setDefaultValue(false).setSaveConsumer(ConfigManager::setInfiniteLevels).build());
        category.addEntry(entries.startBooleanToggle(Text.literal("Mod enabled"), ConfigManager.isModEnabled())
                .setDefaultValue(true).setSaveConsumer(ConfigManager::setModEnabled).build());
    }

    private static void addTrajectory(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Trajectory"));
        category.addEntry(entries.startBooleanToggle(Text.literal("Experimental colour visualisation"),
                        ConfigManager.isTrajectoryColourVisualisationEnabled())
                .setDefaultValue(false)
                .setSaveConsumer(ConfigManager::setTrajectoryColourVisualisationEnabled)
                .build());
    }
}
