package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItemGroups {
    private static final Identifier ARCHERY_GROUP_ID = Identifier.of(
            ArcheryRevamped.MOD_ID, "archery_revamped"
    );

    public static final ItemGroup ARCHERY_REVAMPED = Registry.register(
            Registries.ITEM_GROUP,
            ARCHERY_GROUP_ID,
            ItemGroup.create(ItemGroup.Row.TOP, 7)
                    .displayName(Text.translatable("itemGroup.archery-revamped"))
                    .icon(() -> new ItemStack(ModItems.ENDER_ARROW))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.ENDER_ARROW);
                        entries.add(ModItems.IMPULSE_ARROW);
                        entries.add(ModItems.EXPLOSIVE_ARROW);
                        entries.add(ModItems.STICKY_ARROW);
                        entries.add(Items.ARROW);
                    })
                    .build()
    );

    private ModItemGroups() {
    }

    public static void register() {
        // Static initialization performs the registry insertion.
    }
}
