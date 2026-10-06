package me.lemonboi439.archeryRevamped.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("timeUntilRegen")
    void archeryRevamped$setTimeUntilRegen(int timeUntilRegen);
}
