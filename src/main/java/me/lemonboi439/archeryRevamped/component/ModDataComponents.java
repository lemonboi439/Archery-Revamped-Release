package me.lemonboi439.archeryRevamped.component;

import com.mojang.serialization.Codec;
import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** Data stored directly on a quiver ItemStack and therefore synced automatically. */
public final class ModDataComponents {
    public static final ComponentType<Integer> QUIVER_SELECTED_SLOT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(ArcheryRevamped.MOD_ID, "quiver_selected_slot"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .packetCodec(PacketCodecs.INTEGER)
                    .build()
    );
    public static final ComponentType<Integer> QUIVER_NEXT_SLOT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(ArcheryRevamped.MOD_ID, "quiver_next_slot"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .packetCodec(PacketCodecs.INTEGER)
                    .build()
    );

    private ModDataComponents() {
    }

    public static void register() {
        // Static initialization registers the component.
    }
}
