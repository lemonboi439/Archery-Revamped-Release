package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import me.lemonboi439.archeryRevamped.enchantment.BurstEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.FractureEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.HeadshotEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.LongshotEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.OverdrawEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.RicochetEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

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
                        entries.add(ModItems.QUIVER);
                        entries.add(ModItems.ENDER_ARROW);
                        entries.add(ModItems.SHOCKWAVE_ARROW);
                        entries.add(ModItems.IMPULSE_ARROW);
                        entries.add(ModItems.EXPLOSIVE_ARROW);
                        entries.add(ModItems.TIDAL_ARROW);
                        entries.add(ModItems.SHATTERING_ARROW);
                        entries.add(ModItems.ECHO_ARROW);
                        entries.add(Items.ARROW);
                        addEnchantmentBooks(entries, displayContext);
                    })
                    .build()
    );

    private ModItemGroups() {
    }

    /** Adds only authored, normal enchantment levels to this mod's own tab. */
    private static void addEnchantmentBooks(ItemGroup.Entries entries,
                                            ItemGroup.DisplayContext displayContext) {
        var enchantments = displayContext.lookup().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        List<ItemStack> books = new ArrayList<>();
        addEnchantmentBooks(books, enchantments, RicochetEnchantment.KEY, RicochetEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, enchantments, OverdrawEnchantment.KEY, OverdrawEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, enchantments, LongshotEnchantment.KEY, LongshotEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, enchantments, FractureEnchantment.KEY, FractureEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, enchantments, BurstEnchantment.KEY, BurstEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, enchantments, HeadshotEnchantment.KEY, HeadshotEnchantment.MAX_LEVEL);
        books.forEach(entries::add);
    }

    /** Creative tabs reject component-identical stacks, so only emit each book once. */
    private static void addEnchantmentBooks(List<ItemStack> books,
                                            net.minecraft.registry.RegistryWrapper.Impl<Enchantment> enchantments,
                                            RegistryKey<Enchantment> key, int normalMaximum) {
        var enchantment = enchantments.getOrThrow(key);
        for (int level = 1; level <= normalMaximum; level++) {
            ItemStack book = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, level));
            if (books.stream().noneMatch(existing -> ItemStack.areItemsAndComponentsEqual(existing, book))) {
                books.add(book);
            }
        }
    }

    public static void register() {
        // Static initialization performs the registry insertion.
    }
}
