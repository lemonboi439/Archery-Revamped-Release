package me.lemonboi439.archeryRevamped.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.debug.TrajectoryVisualizer;
import me.lemonboi439.archeryRevamped.debug.TrajectoryNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Admin/debug commands for Archery Revamped. */
public final class ArcheryCommand {
    private ArcheryCommand() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(createRoot("archeryrevamped"));
        });
    }

    private static LiteralArgumentBuilder<ServerCommandSource> createRoot(String name) {
        return CommandManager.literal(name)
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("reload")
                        .executes(ArcheryCommand::reload))
                .then(CommandManager.literal("config")
                        .executes(ArcheryCommand::config))
                .then(CommandManager.literal("help")
                        .executes(ArcheryCommand::help))
                .then(CommandManager.literal("explosive_antigrief")
                        .executes(ArcheryCommand::showExplosiveAntiGrief)
                        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ArcheryCommand::setExplosiveAntiGrief)))
                .then(createPhysicsCommand())
                .then(CommandManager.literal("trajectory")
                        .executes(ArcheryCommand::showTrajectory)
                        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ArcheryCommand::setTrajectory)));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> createPhysicsCommand() {
        return CommandManager.literal("physics")
                .executes(ArcheryCommand::showPhysics)
                .then(CommandManager.literal("get")
                        .executes(ArcheryCommand::showPhysics))
                .then(CommandManager.literal("reset")
                        .executes(ArcheryCommand::resetPhysics))
                .then(CommandManager.literal("gravity")
                        .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0D))
                                .executes(ArcheryCommand::setGravity)))
                .then(CommandManager.literal("drag")
                        .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                .executes(ArcheryCommand::setDrag)))
                .then(CommandManager.literal("speed")
                        .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0D))
                                .executes(ArcheryCommand::setSpeed)))
                .then(CommandManager.literal("randomness")
                        .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0D))
                                .executes(ArcheryCommand::setRandomness)))
                .then(CommandManager.literal("terminal_velocity")
                        .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0D))
                                .executes(ArcheryCommand::setTerminalVelocity)))
                .then(CommandManager.literal("lifetime")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                .executes(ArcheryCommand::setLifetime)));
    }

    private static int help(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        helpHeader(source, "Archery Revamped commands");
        helpHeader(source, "General");
        helpLine(source, "/archeryrevamped help", "show this colour-coded command guide");
        helpLine(source, "/archeryrevamped reload", "reload the JSON configuration");
        helpLine(source, "/archeryrevamped config", "explain how to open the optional Cloth Config screen");
        helpLine(source, "/archeryrevamped explosive_antigrief", "show whether explosive arrows can destroy blocks");
        helpLine(source, "/archeryrevamped explosive_antigrief <true|false>", "enable or disable explosive-arrow block protection");

        helpHeader(source, "Physics");
        helpLine(source, "/archeryrevamped physics get", "show the current physics values");
        helpLine(source, "/archeryrevamped physics reset", "restore physics defaults");
        helpLine(source, "/archeryrevamped physics gravity <value>", "set downward acceleration");
        helpLine(source, "/archeryrevamped physics drag <value>", "set air resistance; higher means more resistance");
        helpLine(source, "/archeryrevamped physics speed <value>", "set the velocity multiplier for arrows");
        helpLine(source, "/archeryrevamped physics randomness <value>", "set random velocity spread");
        helpLine(source, "/archeryrevamped physics terminal_velocity <value>", "set the maximum velocity magnitude");
        helpLine(source, "/archeryrevamped physics lifetime <ticks>", "set the maximum arrow lifetime");
        helpLine(source, "config: physics.ricochet_velocity_loss_percent", "set ricochet velocity loss; this is intentionally config-only");

        helpHeader(source, "Trajectory");
        helpLine(source, "/archeryrevamped trajectory <true|false>", "show or hide fired-arrow trajectory trails and forward predictions");

        return 1;
    }

    private static void helpHeader(ServerCommandSource source, String text) {
        source.sendFeedback(() -> Text.literal("\n" + text).formatted(Formatting.GOLD), false);
    }

    private static void helpLine(ServerCommandSource source, String command, String description) {
        source.sendFeedback(() -> Text.literal(command).formatted(Formatting.AQUA)
                .append(Text.literal(" - ").formatted(Formatting.DARK_GRAY))
                .append(Text.literal(description).formatted(Formatting.GRAY)), false);
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        ConfigManager.reload();
        context.getSource().getServer().getPlayerManager().getPlayerList()
                .forEach(player -> TrajectoryNetworking.send(player, TrajectoryVisualizer.isEnabled()));
        context.getSource().sendFeedback(
                () -> Text.literal("Archery Revamped config reloaded."), true);
        return 1;
    }

    private static int config(CommandContext<ServerCommandSource> context) {
        boolean clothConfigPresent;
        try {
            Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
            clothConfigPresent = true;
        } catch (ClassNotFoundException exception) {
            clothConfigPresent = false;
        }

        String message = clothConfigPresent
                ? "Open Archery Revamped from Mod Menu to edit the Cloth Config screen."
                : "Cloth Config is not installed. Use /archeryrevamped physics <setting> <value> instead.";
        context.getSource().sendFeedback(() -> Text.literal(message), false);
        return 1;
    }

    private static int showPhysics(CommandContext<ServerCommandSource> context) {
        String values = "Physics: gravity=" + ConfigManager.getGravity()
                + ", drag=" + ConfigManager.getDrag() + " (higher = more air resistance)"
                + ", velocity_multiplier=" + ConfigManager.getSpeedMultiplier()
                + ", spread=" + ConfigManager.getRandomness()
                + ", terminal_velocity=" + ConfigManager.getTerminalVelocity()
                + ", lifetime=" + ConfigManager.getMaxLifetimeTicks()
                + ", ricochet_velocity_loss_percent=" + ConfigManager.getRicochetVelocityLossPercent();
        context.getSource().sendFeedback(() -> Text.literal(values), false);
        return 1;
    }

    private static int showExplosiveAntiGrief(CommandContext<ServerCommandSource> context) {
        boolean enabled = ConfigManager.isExplosiveArrowAntiGriefEnabled();
        context.getSource().sendFeedback(() -> Text.literal(
                "Explosive-arrow anti-grief is " + (enabled ? "enabled" : "disabled")
                        + ". " + (enabled ? "Blocks are protected; entities can still be damaged." : "Explosions can destroy blocks.")), false);
        return 1;
    }

    private static int setExplosiveAntiGrief(CommandContext<ServerCommandSource> context) {
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        ConfigManager.setExplosiveArrowAntiGriefEnabled(enabled);
        context.getSource().sendFeedback(() -> Text.literal(
                "Explosive-arrow anti-grief " + (enabled ? "enabled" : "disabled") + "."), true);
        return 1;
    }

    private static int resetPhysics(CommandContext<ServerCommandSource> context) {
        ConfigManager.resetPhysicsToDefaults();
        context.getSource().sendFeedback(
                () -> Text.literal("Physics settings reset to defaults."), true);
        return 1;
    }

    private static int setGravity(CommandContext<ServerCommandSource> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        ConfigManager.setGravity(value);
        return report(context, "gravity", value);
    }

    private static int setDrag(CommandContext<ServerCommandSource> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        ConfigManager.setDrag(value);
        return report(context, "drag", value);
    }

    private static int setSpeed(CommandContext<ServerCommandSource> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        ConfigManager.setSpeedMultiplier(value);
        return report(context, "speed", value);
    }

    private static int setRandomness(CommandContext<ServerCommandSource> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        ConfigManager.setRandomness(value);
        return report(context, "randomness", value);
    }

    private static int setTerminalVelocity(CommandContext<ServerCommandSource> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        ConfigManager.setTerminalVelocity(value);
        return report(context, "terminal_velocity", value);
    }

    private static int setLifetime(CommandContext<ServerCommandSource> context) {
        int value = IntegerArgumentType.getInteger(context, "value");
        ConfigManager.setMaxLifetimeTicks(value);
        return report(context, "lifetime", value);
    }

    private static int report(CommandContext<ServerCommandSource> context, String setting, Object value) {
        context.getSource().sendFeedback(
                () -> Text.literal("Set physics " + setting + " to " + value + "."), true);
        return 1;
    }

    private static int showTrajectory(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(
                () -> Text.literal("Trajectory visualization is "
                        + (TrajectoryVisualizer.isEnabled() ? "enabled" : "disabled") + "."), false);
        return 1;
    }

    private static int setTrajectory(CommandContext<ServerCommandSource> context) {
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        TrajectoryVisualizer.setEnabled(enabled);
        context.getSource().getServer().getPlayerManager().getPlayerList()
                .forEach(player -> TrajectoryNetworking.send(player, enabled));
        context.getSource().sendFeedback(
                () -> Text.literal("Trajectory visualization " + (enabled ? "enabled" : "disabled") + "."), true);
        return 1;
    }

}
