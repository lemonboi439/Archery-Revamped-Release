package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.ArcheryRevamped;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public final class ModItems {
    private static final ResourceLocation ENDER_ARROW_ID = new ResourceLocation(ArcheryRevamped.MOD_ID, "ender_arrow");
    private static final ResourceLocation SHOCKWAVE_ARROW_ID = new ResourceLocation(ArcheryRevamped.MOD_ID, "shockwave_arrow");
    private static final ResourceLocation IMPULSE_ARROW_ID = new ResourceLocation(ArcheryRevamped.MOD_ID, "impulse_arrow");
    private static final ResourceLocation EXPLOSIVE_ARROW_ID = new ResourceLocation(ArcheryRevamped.MOD_ID, "explosive_arrow");
    private static final ResourceLocation TIDAL_ARROW_ID = new ResourceLocation(ArcheryRevamped.MOD_ID, "tidal_arrow");
    private static final ResourceLocation SHATTERING_ARROW_ID = new ResourceLocation(ArcheryRevamped.MOD_ID, "shattering_arrow");
    private static final ResourceLocation ECHO_ARROW_ID = new ResourceLocation(ArcheryRevamped.MOD_ID, "echo_arrow");
    private static final ResourceLocation QUIVER_ID = new ResourceLocation(ArcheryRevamped.MOD_ID, "quiver");

    public static Item QUIVER;
    public static Item ENDER_ARROW;
    public static Item SHOCKWAVE_ARROW;
    public static Item IMPULSE_ARROW;
    public static Item EXPLOSIVE_ARROW;
    public static Item TIDAL_ARROW;
    public static Item SHATTERING_ARROW;
    public static Item ECHO_ARROW;

    private ModItems() {
    }

    public static void register() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModItems::onRegister);
        MinecraftForge.EVENT_BUS.addListener(ModItems::onBuildCreativeTab);
    }

    private static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
            return;
        }

        QUIVER = new QuiverItem(new Item.Properties().stacksTo(1));
        ENDER_ARROW = new EnderArrowItem(new Item.Properties().stacksTo(64));
        SHOCKWAVE_ARROW = new ShockwaveArrowItem(new Item.Properties().stacksTo(64));
        IMPULSE_ARROW = new ImpulseArrowItem(new Item.Properties().stacksTo(64));
        EXPLOSIVE_ARROW = new ExplosiveArrowItem(new Item.Properties().stacksTo(64));
        TIDAL_ARROW = new TidalArrowItem(new Item.Properties().stacksTo(64));
        SHATTERING_ARROW = new ShatteringArrowItem(new Item.Properties().stacksTo(64));
        ECHO_ARROW = new EchoArrowItem(new Item.Properties().stacksTo(64));

        event.register(ForgeRegistries.Keys.ITEMS, helper -> {
            helper.register(QUIVER_ID, QUIVER);
            helper.register(ENDER_ARROW_ID, ENDER_ARROW);
            helper.register(SHOCKWAVE_ARROW_ID, SHOCKWAVE_ARROW);
            helper.register(IMPULSE_ARROW_ID, IMPULSE_ARROW);
            helper.register(EXPLOSIVE_ARROW_ID, EXPLOSIVE_ARROW);
            helper.register(TIDAL_ARROW_ID, TIDAL_ARROW);
            helper.register(SHATTERING_ARROW_ID, SHATTERING_ARROW);
            helper.register(ECHO_ARROW_ID, ECHO_ARROW);
        });

        SpecialArrowDispenserBehavior dispenserBehavior = new SpecialArrowDispenserBehavior();
        DispenserBlock.registerBehavior(SHOCKWAVE_ARROW, dispenserBehavior);
        DispenserBlock.registerBehavior(IMPULSE_ARROW, dispenserBehavior);
        DispenserBlock.registerBehavior(EXPLOSIVE_ARROW, dispenserBehavior);
        DispenserBlock.registerBehavior(TIDAL_ARROW, dispenserBehavior);
        DispenserBlock.registerBehavior(SHATTERING_ARROW, dispenserBehavior);
        DispenserBlock.registerBehavior(ECHO_ARROW, dispenserBehavior);
    }

    private static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != CreativeModeTabs.COMBAT) {
            return;
        }
        event.accept(new ItemStack(QUIVER));
        event.accept(new ItemStack(ENDER_ARROW));
        event.accept(new ItemStack(SHOCKWAVE_ARROW));
        event.accept(new ItemStack(IMPULSE_ARROW));
        event.accept(new ItemStack(EXPLOSIVE_ARROW));
        event.accept(new ItemStack(TIDAL_ARROW));
        event.accept(new ItemStack(SHATTERING_ARROW));
        event.accept(new ItemStack(ECHO_ARROW));
    }
}
