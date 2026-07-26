package me.lemonboi439.archeryRevamped.fracture;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class FractureScheduler {
    private static final List<PendingSplit> PENDING_SPLITS = new ArrayList<>();

    private FractureScheduler() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(FractureScheduler::tick);
    }

    public static void schedule(ArcheryArrowEntity parent) {
        if (parent.isRemoved() || parent.isArrowInGround()
                || !(parent.getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        int childCount = parent.getFractureLevel() >= 2 ? 3 : 2;
        // Keep the split asynchronous, but make the child arrows appear
        // almost immediately after the speed-scaled split timer expires.
        int delay = 1 + serverWorld.getRandom().nextInt(2);
        PENDING_SPLITS.add(new PendingSplit(parent, delay, childCount,
                ConfigManager.getFractureSplitAngleDegrees()));
    }

    private static void tick(MinecraftServer server) {
        Iterator<PendingSplit> iterator = PENDING_SPLITS.iterator();
        while (iterator.hasNext()) {
            PendingSplit pending = iterator.next();
            pending.ticksRemaining--;
            if (pending.ticksRemaining > 0) {
                continue;
            }

            ArcheryArrowEntity parent = pending.parent;
            if (!parent.isRemoved() && !parent.isArrowInGround()
                    && parent.getEntityWorld() instanceof ServerWorld serverWorld) {
                if (pending.childCount == 2) {
                    spawnChild(serverWorld, parent, -pending.angleDegrees);
                    spawnChild(serverWorld, parent, pending.angleDegrees);
                } else {
                    spawnChild(serverWorld, parent, 0.0D);
                    spawnChild(serverWorld, parent, -pending.angleDegrees);
                    spawnChild(serverWorld, parent, pending.angleDegrees);
                }
            }
            iterator.remove();
        }
    }

    private static void spawnChild(ServerWorld world, ArcheryArrowEntity parent, double angleDegrees) {
        world.spawnEntity(parent.createFractureChild(angleDegrees));
    }

    private static final class PendingSplit {
        private final ArcheryArrowEntity parent;
        private int ticksRemaining;
        private final int childCount;
        private final double angleDegrees;

        private PendingSplit(ArcheryArrowEntity parent, int ticksRemaining,
                             int childCount, double angleDegrees) {
            this.parent = parent;
            this.ticksRemaining = ticksRemaining;
            this.childCount = childCount;
            this.angleDegrees = angleDegrees;
        }
    }
}
