package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import me.lemonboi439.archeryRevamped.enchantment.BurstEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.FractureEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.HeadshotEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.LongshotEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.OverdrawEnchantment;
import me.lemonboi439.archeryRevamped.enchantment.RicochetEnchantment;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import java.util.ArrayList;
import java.util.List;

public final class ModItemGroups {
    private static final ResourceLocation ARCHERY_GROUP_ID = new ResourceLocation(
            ArcheryRevamped.MOD_ID, "archery_revamped"
    );

    public static CreativeModeTab ARCHERY_REVAMPED;

    private ModItemGroups() {
    }

    public static void register() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModItemGroups::onRegister);
    }

    private static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
            return;
        }

        ARCHERY_REVAMPED = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 7)
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
                    addEnchantmentBooks(entries);
                })
                .build();

        event.register(Registries.CREATIVE_MODE_TAB, helper ->
                helper.register(ARCHERY_GROUP_ID, ARCHERY_REVAMPED));
    }

    /** Adds only authored, normal enchantment levels to this mod's own tab. */
    private static void addEnchantmentBooks(CreativeModeTab.Output entries) {
        List<ItemStack> books = new ArrayList<>();
        addEnchantmentBooks(books, RicochetEnchantment.ENCHANTMENT, RicochetEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, OverdrawEnchantment.ENCHANTMENT, OverdrawEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, LongshotEnchantment.ENCHANTMENT, LongshotEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, FractureEnchantment.ENCHANTMENT, FractureEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, BurstEnchantment.ENCHANTMENT, BurstEnchantment.MAX_LEVEL);
        addEnchantmentBooks(books, HeadshotEnchantment.ENCHANTMENT, HeadshotEnchantment.MAX_LEVEL);
        books.forEach(entries::accept);
    }

    /** Creative tabs reject component-identical stacks, so only emit each book once. */
    private static void addEnchantmentBooks(List<ItemStack> books,
                                            Enchantment enchantment, int normalMaximum) {
        for (int level = 1; level <= normalMaximum; level++) {
            ItemStack book = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, level));
            if (books.stream().noneMatch(existing -> ItemStack.isSameItemSameTags(existing, book))) {
                books.add(book);
            }
        }
    }
}
