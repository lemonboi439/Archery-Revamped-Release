package me.lemonboi439.archeryRevamped.mixin;

import me.lemonboi439.archeryRevamped.client.OverdrawClientHandler;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"))
    private void archeryRevamped$applyOverdrawShake(
            World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView,
            float tickProgress, CallbackInfo callbackInfo
    ) {
        float intensity = OverdrawClientHandler.getShakeIntensity();
        if (intensity <= 0.001F) return;

        float pitchOffset = (float) ((ThreadLocalRandom.current().nextDouble() - 0.5D) * intensity);
        float yawOffset = (float) ((ThreadLocalRandom.current().nextDouble() - 0.5D) * intensity);
        Camera camera = (Camera) (Object) this;
        this.setRotation(camera.getYaw() + yawOffset, camera.getPitch() + pitchOffset);
    }
}
