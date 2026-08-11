package me.lemonboi439.archeryRevamped.component;

/**
 * Minecraft 1.20.1 predates data components. The 1.20.1 port stores quiver
 * contents and selection state in the quiver ItemStack's NBT instead.
 */
public final class ModDataComponents {
    private ModDataComponents() {
    }

    public static void register() {
        // Kept as a stable startup hook shared by every supported version.
    }
}
