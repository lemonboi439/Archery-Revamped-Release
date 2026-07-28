package me.lemonboi439.archeryRevamped.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.debug.TrajectoryVisualizer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

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
                .requires(source -> source.getPermissions()
                        .hasPermission(new Permission.Level(PermissionLevel.fromLevel(2))))
                .then(CommandManager.literal("reload")
                        .executes(ArcheryCommand::reload))
                .then(CommandManager.literal("config")
                        .executes(ArcheryCommand::config))
                .then(createPhysicsCommand())
                .then(CommandManager.literal("trajectory")
                        .executes(ArcheryCommand::showTrajectory)
                        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ArcheryCommand::setTrajectory)));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> createPhysicsCommand() {
        return CommandManager.literal("physics")
                .executes(ArcheryCommand::showPhysics)
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

    private static int reload(CommandContext<ServerCommandSource> context) {
        ConfigManager.reload();
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
                ? "Cloth Config is installed, but no Archery Revamped Cloth Config screen is registered."
                : "Cloth Config is not installed. Use /archeryrevamped physics <setting> <value> instead.";
        context.getSource().sendFeedback(() -> Text.literal(message), false);
        return 1;
    }

    private static int showPhysics(CommandContext<ServerCommandSource> context) {
        String values = "Physics: gravity=" + ConfigManager.getGravity()
                + ", drag=" + ConfigManager.getDrag()
                + ", speed=" + ConfigManager.getSpeedMultiplier()
                + ", randomness=" + ConfigManager.getRandomness()
                + ", terminal_velocity=" + ConfigManager.getTerminalVelocity()
                + ", lifetime=" + ConfigManager.getMaxLifetimeTicks();
        context.getSource().sendFeedback(() -> Text.literal(values), false);
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
        context.getSource().sendFeedback(
                () -> Text.literal("Trajectory visualization " + (enabled ? "enabled" : "disabled") + "."), true);
        return 1;
    }
}
