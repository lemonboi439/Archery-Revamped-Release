package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import me.lemonboi439.archeryRevamped.enchantment.BurstEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.FractureEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.HeadshotEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.LongshotEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.OverdrawEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.RicochetEnchantment;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;

public final class ModItemGroups {
    private static final Identifier ARCHERY_GROUP_ID = Identifier.fromNamespaceAndPath(
            ArcheryRevamped.MOD_ID, "archery_revamped"
    );

    public static final CreativeModeTab ARCHERY_REVAMPED = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            ARCHERY_GROUP_ID,
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 7)
                    .title(Component.translatable("itemGroup.archery-revamped"))
                    .icon(() -> new ItemStack(ModItems.ENDER_ARROW))
                    .displayItems((displayContext, entries) -> {
                        entries.accept(ModItems.QUIVER);
                        entries.accept(ModItems.ENDER_ARROW);
                        entries.accept(ModItems.SHOCKWAVE_ARROW);
                        entries.accept(ModItems.IMPULSE_ARROW);
                        entries.accept(ModItems.EXPLOSIVE_ARROW);
                        entries.accept(ModItems.TIDAL_ARROW);
                        entries.accept(ModItems.SHATTERING_ARROW);
                        entries.accept(ModItems.ECHO_ARROW);
                        entries.accept(Items.ARROW);
                        addEnchantmentBooks(entries, displayContext.holders());
                    })
                    .build()
    );

    private ModItemGroups() {
    }

    /**
     * Creative books always use the enchantment's authored maximum, never the
     * optional gameplay-only infinite-level cap. Keeping them in this tab also
     * avoids cluttering vanilla creative categories.
     */
    private static void addEnchantmentBooks(CreativeModeTab.Output entries,
                                            HolderLookup.Provider registries) {
        List<ItemStack> books = new ArrayList<>();
        addEnchantmentBooks(books, registries, RicochetEnchantment.KEY, RicochetEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, registries, OverdrawEnchantment.KEY, OverdrawEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, registries, LongshotEnchantment.KEY, LongshotEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, registries, FractureEnchantment.KEY, FractureEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, registries, BurstEnchantment.KEY, BurstEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, registries, HeadshotEnchantment.KEY, HeadshotEnchantment.MAX_LEVEL);
        books.forEach(entries::accept);
    }

    /** Creative tabs reject component-identical stacks, so only emit each book once. */
    private static void addEnchantmentBooks(List<ItemStack> books,
                                            HolderLookup.Provider registries,
                                            ResourceKey<Enchantment> key, int normalMaximum) {
        Holder<Enchantment> enchantment = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
        for (int level = 1; level <= normalMaximum; level++) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            int bookLevel = level;
            EnchantmentHelper.updateEnchantments(book,
                    enchantments -> enchantments.set(enchantment, bookLevel));
            if (books.stream().noneMatch(existing -> ItemStack.isSameItemSameComponents(existing, book))) {
                books.add(book);
            }
        }
    }

    public static void register() {
        // Static initialization performs the registry insertion.
    }
}
