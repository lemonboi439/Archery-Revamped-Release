package me.lemonboi439.archeryRevamped.client.config;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
                .setTitle(Component.literal("Archery Revamped Configuration"));
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
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Physics"));
        category.addEntry(entries.startDoubleField(Component.literal("Gravity"), ConfigManager.getGravity())
                .setDefaultValue(0.05D).setMin(0.0D).setSaveConsumer(ConfigManager::setGravity).build());
        category.addEntry(entries.startDoubleField(Component.literal("Drag"), ConfigManager.getDrag())
                .setDefaultValue(0.99D).setMin(0.0D).setMax(1.0D)
                .setSaveConsumer(ConfigManager::setDrag).build());
        category.addEntry(entries.startDoubleField(Component.literal("Speed multiplier"), ConfigManager.getSpeedMultiplier())
                .setDefaultValue(1.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setSpeedMultiplier).build());
        category.addEntry(entries.startDoubleField(Component.literal("Randomness"), ConfigManager.getRandomness())
                .setDefaultValue(0.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setRandomness).build());
        category.addEntry(entries.startDoubleField(Component.literal("Terminal velocity"), ConfigManager.getTerminalVelocity())
                .setDefaultValue(999.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setTerminalVelocity).build());
        category.addEntry(entries.startIntField(Component.literal("Maximum lifetime (ticks)"), ConfigManager.getMaxLifetimeTicks())
                .setDefaultValue(1200).setMin(1)
                .setSaveConsumer(ConfigManager::setMaxLifetimeTicks).build());
        category.addEntry(entries.startDoubleField(Component.literal("Ricochet velocity loss (%)"),
                        ConfigManager.getRicochetVelocityLossPercent())
                .setDefaultValue(10.0D).setMin(0.0D).setMax(100.0D)
                .setSaveConsumer(ConfigManager::setRicochetVelocityLossPercent).build());
    }

    private static void addArrowTypes(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Arrow types"));
        category.addEntry(entries.startBooleanToggle(Component.literal("Ender arrows enabled"),
                        ConfigManager.isEnderArrowEnabled())
                .setDefaultValue(true).setSaveConsumer(ConfigManager::setEnderArrowEnabled).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Shockwave arrows enabled"),
                        ConfigManager.isShockwaveArrowEnabled())
                .setDefaultValue(true).setSaveConsumer(ConfigManager::setShockwaveArrowEnabled).build());
        category.addEntry(entries.startDoubleField(Component.literal("Shockwave radius"),
                        ConfigManager.getShockwaveRadius())
                .setDefaultValue(1.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setShockwaveRadius).build());
        category.addEntry(entries.startDoubleField(Component.literal("Shockwave strength"),
                        ConfigManager.getShockwaveStrength())
                .setDefaultValue(1.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setShockwaveStrength).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Impulse arrows enabled"),
                        ConfigManager.isImpulseArrowEnabled())
                .setDefaultValue(true).setSaveConsumer(ConfigManager::setImpulseArrowEnabled).build());
        category.addEntry(entries.startDoubleField(Component.literal("Impulse radius"),
                        ConfigManager.getImpulseRadius())
                .setDefaultValue(1.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setImpulseRadius).build());
        category.addEntry(entries.startDoubleField(Component.literal("Impulse pull strength"),
                        ConfigManager.getImpulseStrength())
                .setDefaultValue(1.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setImpulseStrength).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Explosive arrows enabled"),
                        ConfigManager.isExplosiveArrowEnabled())
                .setDefaultValue(true).setSaveConsumer(ConfigManager::setExplosiveArrowEnabled).build());
        category.addEntry(entries.startDoubleField(Component.literal("Explosion size"),
                        ConfigManager.getExplosiveArrowSize())
                .setDefaultValue(2.5D).setMin(0.0D).setMax(500.0D)
                .setSaveConsumer(ConfigManager::setExplosiveArrowSize).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Explosive anti-grief"),
                        ConfigManager.isExplosiveArrowAntiGriefEnabled())
                .setDefaultValue(false).setSaveConsumer(ConfigManager::setExplosiveArrowAntiGriefEnabled).build());
        category.addEntry(entries.startDoubleField(Component.literal("Echo sonic boom chance (%)"),
                        ConfigManager.getEchoSonicBoomChancePercent())
                .setDefaultValue(10.0D).setMin(0.0D).setMax(100.0D)
                .setSaveConsumer(ConfigManager::setEchoSonicBoomChancePercent).build());
        category.addEntry(entries.startDoubleField(Component.literal("Echo sonic boom damage"),
                        ConfigManager.getEchoSonicBoomDamage())
                .setDefaultValue(10.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setEchoSonicBoomDamage).build());
    }

    private static void addOverdraw(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Overdraw"));
        category.addEntry(entries.startDoubleField(Component.literal("Damage increase per tick (%)"),
                        ConfigManager.getOverdrawDamageIncreasePerTickPercent())
                .setDefaultValue(1.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setOverdrawDamageIncreasePerTickPercent).build());
        category.addEntry(entries.startDoubleField(Component.literal("Maximum damage bonus (%)"),
                        ConfigManager.getOverdrawMaxDamageBonusPercent())
                .setDefaultValue(100.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setOverdrawMaxDamageBonusPercent).build());
        category.addEntry(entries.startIntField(Component.literal("Minimum failure delay (ticks)"),
                        ConfigManager.getOverdrawMinimumFailureDelayTicks())
                .setDefaultValue(40).setMin(1)
                .setSaveConsumer(ConfigManager::setOverdrawMinimumFailureDelayTicks).build());
        category.addEntry(entries.startIntField(Component.literal("Maximum failure delay (ticks)"),
                        ConfigManager.getOverdrawAutoFireDelayTicks())
                .setDefaultValue(100).setMin(1)
                .setSaveConsumer(ConfigManager::setOverdrawAutoFireDelayTicks).build());
        category.addEntry(entries.startIntField(Component.literal("Bow disable time (ticks)"),
                        ConfigManager.getOverdrawBowDisableTicks())
                .setDefaultValue(60).setMin(1)
                .setSaveConsumer(ConfigManager::setOverdrawBowDisableTicks).build());
        category.addEntry(entries.startDoubleField(Component.literal("Misfire cone radius (degrees)"),
                        ConfigManager.getOverdrawMisfireAngleDegrees())
                .setDefaultValue(45.0D).setMin(0.0D).setMax(180.0D)
                .setSaveConsumer(ConfigManager::setOverdrawMisfireAngleDegrees).build());
        category.addEntry(entries.startDoubleField(Component.literal("Durability loss (%)"),
                        ConfigManager.getOverdrawDurabilityLossPercent())
                .setDefaultValue(25.0D).setMin(0.0D).setMax(100.0D)
                .setSaveConsumer(ConfigManager::setOverdrawDurabilityLossPercent).build());
        category.addEntry(entries.startDoubleField(Component.literal("Self damage (hearts)"),
                        ConfigManager.getOverdrawSelfDamageHearts())
                .setDefaultValue(2.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setOverdrawSelfDamageHearts).build());
    }

    private static void addLongshot(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Longshot"));
        category.addEntry(entries.startDoubleField(Component.literal("16-block threshold"), ConfigManager.getLongshot16Threshold())
                .setDefaultValue(16.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot16Threshold).build());
        category.addEntry(entries.startDoubleField(Component.literal("32-block threshold"), ConfigManager.getLongshot32Threshold())
                .setDefaultValue(32.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot32Threshold).build());
        category.addEntry(entries.startDoubleField(Component.literal("48-block threshold"), ConfigManager.getLongshot48Threshold())
                .setDefaultValue(48.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot48Threshold).build());
        category.addEntry(entries.startDoubleField(Component.literal("64-block threshold"), ConfigManager.getLongshot64Threshold())
                .setDefaultValue(64.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot64Threshold).build());
        category.addEntry(entries.startDoubleField(Component.literal("16-block damage multiplier"), ConfigManager.getLongshot16Multiplier())
                .setDefaultValue(1.5D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot16Multiplier).build());
        category.addEntry(entries.startDoubleField(Component.literal("32-block damage multiplier"), ConfigManager.getLongshot32Multiplier())
                .setDefaultValue(2.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot32Multiplier).build());
        category.addEntry(entries.startDoubleField(Component.literal("48-block damage multiplier"), ConfigManager.getLongshot48Multiplier())
                .setDefaultValue(2.5D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot48Multiplier).build());
        category.addEntry(entries.startDoubleField(Component.literal("64-block damage multiplier"), ConfigManager.getLongshot64Multiplier())
                .setDefaultValue(3.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setLongshot64Multiplier).build());
    }

    private static void addBurst(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Burst"));
        category.addEntry(entries.startIntField(Component.literal("Stagger delay (ticks)"), ConfigManager.getBurstStaggerDelayTicks())
                .setDefaultValue(3).setMin(1)
                .setSaveConsumer(ConfigManager::setBurstStaggerDelayTicks).build());
        category.addEntry(entries.startIntField(Component.literal("Arrows per level"), ConfigManager.getBurstArrowsPerLevel())
                .setDefaultValue(1).setMin(1)
                .setSaveConsumer(ConfigManager::setBurstArrowsPerLevel).build());
    }

    private static void addFracture(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Fracture"));
        category.addEntry(entries.startBooleanToggle(Component.literal("Enabled"), ConfigManager.isFractureEnabled())
                .setDefaultValue(false).setSaveConsumer(ConfigManager::setFractureEnabled).build());
        category.addEntry(entries.startIntField(Component.literal("Split delay (ticks)"), ConfigManager.getFractureSplitDelayTicks())
                .setDefaultValue(2).setMin(1)
                .setSaveConsumer(ConfigManager::setFractureSplitDelayTicks).build());
        category.addEntry(entries.startDoubleField(Component.literal("Split angle (degrees)"), ConfigManager.getFractureSplitAngleDegrees())
                .setDefaultValue(15.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setFractureSplitAngleDegrees).build());
        category.addEntry(entries.startDoubleField(Component.literal("Reference release speed"), ConfigManager.getFractureReferenceReleaseSpeed())
                .setDefaultValue(3.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setFractureReferenceReleaseSpeed).build());
        category.addEntry(entries.startIntField(Component.literal("Minimum speed-scaled delay"), ConfigManager.getFractureMinSplitDelayTicks())
                .setDefaultValue(1).setMin(1)
                .setSaveConsumer(ConfigManager::setFractureMinSplitDelayTicks).build());
        category.addEntry(entries.startIntField(Component.literal("Maximum speed-scaled delay"), ConfigManager.getFractureMaxSplitDelayTicks())
                .setDefaultValue(40).setMin(1)
                .setSaveConsumer(ConfigManager::setFractureMaxSplitDelayTicks).build());
    }

    private static void addFletching(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Fletching table"));
        category.addEntry(entries.startIntField(Component.literal("Recipe output count"), ConfigManager.getFletchingRecipeOutputCount())
                .setDefaultValue(4).setMin(1)
                .setSaveConsumer(ConfigManager::setFletchingRecipeOutputCount).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Allow other mods' arrows as Fletching inputs"),
                        ConfigManager.allowsModdedFletchingArrowInputs())
                .setDefaultValue(true)
                .setSaveConsumer(ConfigManager::setAllowModdedFletchingArrowInputs).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Worn Trinkets quiver overrides inventory quiver"),
                        ConfigManager.doesTrinketsQuiverOverrideInventory())
                .setDefaultValue(true)
                .setSaveConsumer(ConfigManager::setTrinketsQuiverOverridesInventory).build());
    }

    private static void addHeadshot(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Headshot"));
        category.addEntry(entries.startBooleanToggle(Component.literal("Enable Headshot"),
                        ConfigManager.isHeadshotEnabled())
                .setDefaultValue(false).setSaveConsumer(ConfigManager::setHeadshotEnabled).build());
        category.addEntry(entries.startDoubleField(Component.literal("PvE bonus I (%)"),
                        ConfigManager.getHeadshotDamageBonusI())
                .setDefaultValue(15.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotDamageBonusI).build());
        category.addEntry(entries.startDoubleField(Component.literal("PvE bonus II (%)"),
                        ConfigManager.getHeadshotDamageBonusII())
                .setDefaultValue(30.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotDamageBonusII).build());
        category.addEntry(entries.startDoubleField(Component.literal("PvE bonus III (%)"),
                        ConfigManager.getHeadshotDamageBonusIII())
                .setDefaultValue(45.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotDamageBonusIII).build());
        category.addEntry(entries.startDoubleField(Component.literal("Head box radius"),
                        ConfigManager.getHeadshotBoxRadius())
                .setDefaultValue(0.35D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotBoxRadius).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Headshot feedback"),
                        ConfigManager.isHeadshotFeedbackEnabled())
                .setDefaultValue(true).setSaveConsumer(ConfigManager::setHeadshotFeedbackEnabled).build());
        category.addEntry(entries.startDoubleField(Component.literal("PvP bonus I (%)"),
                        ConfigManager.getHeadshotPvpDamageBonusI())
                .setDefaultValue(15.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotPvpDamageBonusI).build());
        category.addEntry(entries.startDoubleField(Component.literal("PvP bonus II (%)"),
                        ConfigManager.getHeadshotPvpDamageBonusII())
                .setDefaultValue(30.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotPvpDamageBonusII).build());
        category.addEntry(entries.startDoubleField(Component.literal("PvP bonus III (%)"),
                        ConfigManager.getHeadshotPvpDamageBonusIII())
                .setDefaultValue(45.0D).setMin(0.0D)
                .setSaveConsumer(ConfigManager::setHeadshotPvpDamageBonusIII).build());
    }

    private static void addRegular(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Regular"));
        category.addEntry(entries.startBooleanToggle(Component.literal("Infinite Archery Revamped enchantment levels"),
                        ConfigManager.isInfiniteLevels())
                .setDefaultValue(false).setSaveConsumer(ConfigManager::setInfiniteLevels).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Limitless anvil (removes Too Expensive gate)"),
                        ConfigManager.isLimitlessAnvilEnabled())
                .setDefaultValue(false).setSaveConsumer(ConfigManager::setLimitlessAnvilEnabled).build());
        category.addEntry(entries.startBooleanToggle(Component.literal("Mod enabled"), ConfigManager.isModEnabled())
                .setDefaultValue(true).setSaveConsumer(ConfigManager::setModEnabled).build());
    }

    private static void addTrajectory(ConfigBuilder builder, ConfigEntryBuilder entries) {
        ConfigCategory category = builder.getOrCreateCategory(Component.literal("Trajectory"));
        category.addEntry(entries.startBooleanToggle(Component.literal("Experimental colour visualisation"),
                        ConfigManager.isTrajectoryColourVisualisationEnabled())
                .setDefaultValue(false)
                .setSaveConsumer(ConfigManager::setTrajectoryColourVisualisationEnabled)
                .build());
    }
}
