package me.lemonboi439.archeryRevamped.debug;

import me.lemonboi439.archeryRevamped.config.ConfigManager;

/**
 * Retained as the command state holder for backwards-compatible commands.
 * Actual trajectory rendering is client-side in ClientTrajectoryPreview.
 */
public final class TrajectoryVisualizer {
    private static boolean enabled;

    private TrajectoryVisualizer() {
    }

    public static void register() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        TrajectoryVisualizer.enabled = enabled;
    }

    public static boolean isColourVisualisationEnabled() {
        return ConfigManager.isTrajectoryColourVisualisationEnabled();
    }

    public static void setColourVisualisationEnabled(boolean enabled) {
        ConfigManager.setTrajectoryColourVisualisationEnabled(enabled);
    }
}
