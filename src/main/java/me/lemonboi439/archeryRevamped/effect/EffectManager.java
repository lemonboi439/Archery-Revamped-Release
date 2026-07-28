package me.lemonboi439.archeryRevamped.effect;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** Centralized server-side particle and sound helpers. */
public final class EffectManager {
    private EffectManager() {
    }

    public static void spawnParticles(World world, Vec3d position,
                                      ParticleEffect particle, int count) {
        if (!(world instanceof ServerWorld serverWorld) || count <= 0) {
            return;
        }

        serverWorld.spawnParticles(
                particle,
                position.x, position.y, position.z,
                count,
                0.25D, 0.25D, 0.25D,
                0.05D
        );
    }

    public static void playSound(World world, Vec3d position, SoundEvent sound,
                                 float volume, float pitch) {
        if (world.isClient()) {
            return;
        }

        world.playSound(
                null,
                position.x, position.y, position.z,
                sound,
                SoundCategory.PLAYERS,
                volume,
                pitch
        );
    }

    public static void playSound(World world, Vec3d position,
                                 RegistryEntry<SoundEvent> sound,
                                 float volume, float pitch) {
        if (world.isClient()) {
            return;
        }

        world.playSound(
                null,
                position.x, position.y, position.z,
                sound,
                SoundCategory.PLAYERS,
                volume,
                pitch
        );
    }
}
