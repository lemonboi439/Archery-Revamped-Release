package me.lemonboi439.archeryRevamped.arrow;

import java.util.EnumMap;
import java.util.Map;

public final class ArrowBehaviorRegistry {
    private static final Map<ArrowType, ArrowBehavior> BEHAVIORS = new EnumMap<>(ArrowType.class);
    private static final ArrowBehavior NOOP = new NoOpBehavior();

    static {
        register(ArrowType.NORMAL, NOOP);
    }

    private ArrowBehaviorRegistry() {
    }

    public static void register(ArrowType type, ArrowBehavior behavior) {
        BEHAVIORS.put(type, behavior);
    }

    public static ArrowBehavior getBehavior(ArrowType type) {
        return BEHAVIORS.getOrDefault(type, NOOP);
    }
}
