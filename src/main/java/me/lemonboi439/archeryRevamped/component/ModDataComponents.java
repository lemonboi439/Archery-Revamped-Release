package me.lemonboi439.archeryRevamped.component;

import com.mojang.serialization.Codec;
import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

/** Data stored directly on a quiver ItemStack and therefore synced automatically. */
public final class ModDataComponents {
    public static final DataComponentType<Integer> QUIVER_SELECTED_SLOT = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "quiver_selected_slot"),
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build()
    );
    public static final DataComponentType<Integer> QUIVER_NEXT_SLOT = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(ArcheryRevamped.MOD_ID, "quiver_next_slot"),
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build()
    );

    private ModDataComponents() {
    }

    public static void register() {
        // Static initialization registers the component.
    }
}
