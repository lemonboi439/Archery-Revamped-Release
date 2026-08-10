package me.lemonboi439.archeryRevamped.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Centralized server-side particle and sound helpers. */
public final class EffectManager {
    private EffectManager() {
    }

    public static void spawnParticles(Level world, Vec3 position,
                                      ParticleOptions particle, int count) {
        if (!(world instanceof ServerLevel serverWorld) || count <= 0) {
            return;
        }

        serverWorld.sendParticles(
                particle,
                position.x, position.y, position.z,
                count,
                0.25D, 0.25D, 0.25D,
                0.05D
        );
    }

    public static void playSound(Level world, Vec3 position, SoundEvent sound,
                                 float volume, float pitch) {
        if (world.isClientSide()) {
            return;
        }

        world.playSound(
                null,
                position.x, position.y, position.z,
                sound,
                SoundSource.PLAYERS,
                volume,
                pitch
        );
    }

    public static void playSound(Level world, Vec3 position,
                                 Holder<SoundEvent> sound,
                                 float volume, float pitch) {
        if (world.isClientSide()) {
            return;
        }

        world.playSound(
                null,
                position.x, position.y, position.z,
                sound,
                SoundSource.PLAYERS,
                volume,
                pitch
        );
    }
}
