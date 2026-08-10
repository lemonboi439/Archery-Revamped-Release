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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;

/** Admin/debug commands for Archery Revamped. */
public final class ArcheryCommand {
    private ArcheryCommand() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(createRoot("archeryrevamped"));
        });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createRoot(String name) {
        return Commands.literal(name)
                .requires(source -> source.permissions()
                        .hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(2))))
                .then(Commands.literal("reload")
                        .executes(ArcheryCommand::reload))
                .then(Commands.literal("config")
                        .executes(ArcheryCommand::config))
                .then(Commands.literal("help")
                        .executes(ArcheryCommand::help))
                .then(Commands.literal("explosive_antigrief")
                        .executes(ArcheryCommand::showExplosiveAntiGrief)
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ArcheryCommand::setExplosiveAntiGrief)))
                .then(createPhysicsCommand())
                .then(Commands.literal("trajectory")
                        .executes(ArcheryCommand::showTrajectory)
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ArcheryCommand::setTrajectory))
                        .then(Commands.literal("colour")
                                .executes(ArcheryCommand::showTrajectoryColour)
                                .then(Commands.argument("enabled", BoolArgumentType.bool())
                                        .executes(ArcheryCommand::setTrajectoryColour))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createPhysicsCommand() {
        return Commands.literal("physics")
                .executes(ArcheryCommand::showPhysics)
                .then(Commands.literal("get")
                        .executes(ArcheryCommand::showPhysics))
                .then(Commands.literal("reset")
                        .executes(ArcheryCommand::resetPhysics))
                .then(Commands.literal("gravity")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0D))
                                .executes(ArcheryCommand::setGravity)))
                .then(Commands.literal("drag")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                .executes(ArcheryCommand::setDrag)))
                .then(Commands.literal("speed")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0D))
                                .executes(ArcheryCommand::setSpeed)))
                .then(Commands.literal("randomness")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0D))
                                .executes(ArcheryCommand::setRandomness)))
                .then(Commands.literal("terminal_velocity")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0D))
                                .executes(ArcheryCommand::setTerminalVelocity)))
                .then(Commands.literal("lifetime")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1))
                                .executes(ArcheryCommand::setLifetime)));
    }

    private static int help(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
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
        helpLine(source, "/archeryrevamped trajectory colour <true|false>", "toggle the experimental smooth speed-colour gradient");

        return 1;
    }

    private static void helpHeader(CommandSourceStack source, String text) {
        source.sendSuccess(() -> Component.literal("\n" + text).withStyle(ChatFormatting.GOLD), false);
    }

    private static void helpLine(CommandSourceStack source, String command, String description) {
        source.sendSuccess(() -> Component.literal(command).withStyle(ChatFormatting.AQUA)
                .append(Component.literal(" - ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(description).withStyle(ChatFormatting.GRAY)), false);
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        ConfigManager.reload();
        context.getSource().getServer().getPlayerList().getPlayers()
                .forEach(player -> TrajectoryNetworking.send(player, TrajectoryVisualizer.isEnabled()));
        context.getSource().sendSuccess(
                () -> Component.literal("Archery Revamped config reloaded."), true);
        return 1;
    }

    private static int config(CommandContext<CommandSourceStack> context) {
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
        context.getSource().sendSuccess(() -> Component.literal(message), false);
        return 1;
    }

    private static int showPhysics(CommandContext<CommandSourceStack> context) {
        String values = "Physics: gravity=" + ConfigManager.getGravity()
                + ", drag=" + ConfigManager.getDrag() + " (higher = more air resistance)"
                + ", velocity_multiplier=" + ConfigManager.getSpeedMultiplier()
                + ", spread=" + ConfigManager.getRandomness()
                + ", terminal_velocity=" + ConfigManager.getTerminalVelocity()
                + ", lifetime=" + ConfigManager.getMaxLifetimeTicks()
                + ", ricochet_velocity_loss_percent=" + ConfigManager.getRicochetVelocityLossPercent();
        context.getSource().sendSuccess(() -> Component.literal(values), false);
        return 1;
    }

    private static int showExplosiveAntiGrief(CommandContext<CommandSourceStack> context) {
        boolean enabled = ConfigManager.isExplosiveArrowAntiGriefEnabled();
        context.getSource().sendSuccess(() -> Component.literal(
                "Explosive-arrow anti-grief is " + (enabled ? "enabled" : "disabled")
                        + ". " + (enabled ? "Blocks are protected; entities can still be damaged." : "Explosions can destroy blocks.")), false);
        return 1;
    }

    private static int setExplosiveAntiGrief(CommandContext<CommandSourceStack> context) {
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        ConfigManager.setExplosiveArrowAntiGriefEnabled(enabled);
        context.getSource().sendSuccess(() -> Component.literal(
                "Explosive-arrow anti-grief " + (enabled ? "enabled" : "disabled") + "."), true);
        return 1;
    }

    private static int resetPhysics(CommandContext<CommandSourceStack> context) {
        ConfigManager.resetPhysicsToDefaults();
        context.getSource().sendSuccess(
                () -> Component.literal("Physics settings reset to defaults."), true);
        return 1;
    }

    private static int setGravity(CommandContext<CommandSourceStack> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        ConfigManager.setGravity(value);
        return report(context, "gravity", value);
    }

    private static int setDrag(CommandContext<CommandSourceStack> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        ConfigManager.setDrag(value);
        return report(context, "drag", value);
    }

    private static int setSpeed(CommandContext<CommandSourceStack> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        ConfigManager.setSpeedMultiplier(value);
        return report(context, "speed", value);
    }

    private static int setRandomness(CommandContext<CommandSourceStack> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        ConfigManager.setRandomness(value);
        return report(context, "randomness", value);
    }

    private static int setTerminalVelocity(CommandContext<CommandSourceStack> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        ConfigManager.setTerminalVelocity(value);
        return report(context, "terminal_velocity", value);
    }

    private static int setLifetime(CommandContext<CommandSourceStack> context) {
        int value = IntegerArgumentType.getInteger(context, "value");
        ConfigManager.setMaxLifetimeTicks(value);
        return report(context, "lifetime", value);
    }

    private static int report(CommandContext<CommandSourceStack> context, String setting, Object value) {
        context.getSource().sendSuccess(
                () -> Component.literal("Set physics " + setting + " to " + value + "."), true);
        return 1;
    }

    private static int showTrajectory(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Trajectory visualization is "
                        + (TrajectoryVisualizer.isEnabled() ? "enabled" : "disabled") + "."), false);
        return 1;
    }

    private static int setTrajectory(CommandContext<CommandSourceStack> context) {
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        TrajectoryVisualizer.setEnabled(enabled);
        context.getSource().getServer().getPlayerList().getPlayers()
                .forEach(player -> TrajectoryNetworking.send(player, enabled));
        context.getSource().sendSuccess(
                () -> Component.literal("Trajectory visualization " + (enabled ? "enabled" : "disabled") + "."), true);
        return 1;
    }

    private static int showTrajectoryColour(CommandContext<CommandSourceStack> context) {
        boolean enabled = TrajectoryVisualizer.isColourVisualisationEnabled();
        context.getSource().sendSuccess(
                () -> Component.literal("Trajectory speed-colour visualisation is "
                        + (enabled ? "enabled" : "disabled") + "."), false);
        return 1;
    }

    private static int setTrajectoryColour(CommandContext<CommandSourceStack> context) {
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        TrajectoryVisualizer.setColourVisualisationEnabled(enabled);
        context.getSource().getServer().getPlayerList().getPlayers()
                .forEach(player -> TrajectoryNetworking.send(player, TrajectoryVisualizer.isEnabled()));
        context.getSource().sendSuccess(
                () -> Component.literal("Trajectory speed-colour visualisation "
                        + (enabled ? "enabled" : "disabled") + "."), true);
        return 1;
    }

}
