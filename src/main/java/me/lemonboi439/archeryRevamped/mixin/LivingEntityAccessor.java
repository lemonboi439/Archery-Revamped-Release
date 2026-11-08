package me.lemonboi439.archeryRevamped.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("lastHurt")
    void archeryRevamped$setLastDamageTaken(float lastDamageTaken);
}
