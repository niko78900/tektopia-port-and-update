package net.tangotek.tektopia.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tangotek.tektopia.TekTopiaPort;

public final class TekItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TekTopiaPort.MODID);
    public static final RegistryObject<Item> STRUCTURE_TOWNHALL_TOKEN =
            ITEMS.register("structure_townhall_token", TekItems::basicItem);
    public static final RegistryObject<Item> STRUCTURE_STORAGE_TOKEN =
            ITEMS.register("structure_storage_token", TekItems::basicItem);
    public static final RegistryObject<Item> STRUCTURE_HOME_TOKEN =
            ITEMS.register("structure_home_token", TekItems::basicItem);
    public static final RegistryObject<Item> STRUCTURE_FARM_TOKEN =
            ITEMS.register("structure_farm_token", TekItems::basicItem);
    public static final RegistryObject<Item> STRUCTURE_MINESHAFT_TOKEN =
            ITEMS.register("structure_mineshaft_token", TekItems::basicItem);
    public static final RegistryObject<Item> STRUCTURE_LUMBER_TOKEN =
            ITEMS.register("structure_lumber_token", TekItems::basicItem);
    public static final RegistryObject<Item> STRUCTURE_KITCHEN_TOKEN =
            ITEMS.register("structure_kitchen_token", TekItems::basicItem);
    public static final RegistryObject<Item> STRUCTURE_BUTCHER_TOKEN =
            ITEMS.register("structure_butcher_token", TekItems::basicItem);
    public static final RegistryObject<Item> STRUCTURE_RANCH_TOKEN =
            ITEMS.register("structure_ranch_token", TekItems::basicItem);
    public static final RegistryObject<Item> STRUCTURE_GUARD_POST_TOKEN =
            ITEMS.register("structure_guard_post_token", TekItems::basicItem);
    public static final RegistryObject<Item> STRUCTURE_BARRACKS_TOKEN =
            ITEMS.register("structure_barracks_token", TekItems::basicItem);
    public static final RegistryObject<Item> STRUCTURE_MERCHANT_STALL_TOKEN =
            ITEMS.register("structure_merchant_stall_token", TekItems::basicItem);
    public static final RegistryObject<Item> TEK_FARMER_SPAWN_EGG =
            ITEMS.register("tek_farmer_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_FARMER,
                            0x7D5A3C,
                            0x5FA051,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_BLACKSMITH_SPAWN_EGG =
            ITEMS.register("tek_blacksmith_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_BLACKSMITH,
                            0x4A4A4A,
                            0xC47A38,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_GUARD_SPAWN_EGG =
            ITEMS.register("tek_guard_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_GUARD,
                            0x2F4F4F,
                            0xE0B040,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_MINER_SPAWN_EGG =
            ITEMS.register("tek_miner_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_MINER,
                            0x4B3621,
                            0x9AA0A6,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_LUMBERJACK_SPAWN_EGG =
            ITEMS.register("tek_lumberjack_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_LUMBERJACK,
                            0x6B3F1D,
                            0x2F7D32,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_CHEF_SPAWN_EGG =
            ITEMS.register("tek_chef_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_CHEF,
                            0xE8E8E8,
                            0xB02E26,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_RANCHER_SPAWN_EGG =
            ITEMS.register("tek_rancher_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_RANCHER,
                            0x8D6E63,
                            0xFBC02D,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_BUTCHER_SPAWN_EGG =
            ITEMS.register("tek_butcher_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_BUTCHER,
                            0x7B1F1F,
                            0xF5F5F5,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_MERCHANT_SPAWN_EGG =
            ITEMS.register("tek_merchant_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_MERCHANT,
                            0x3E2723,
                            0xFFC107,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_NOMAD_SPAWN_EGG =
            ITEMS.register("tek_nomad_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_NOMAD,
                            0x5D4037,
                            0x90CAF9,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_CLERIC_SPAWN_EGG =
            ITEMS.register("tek_cleric_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_CLERIC,
                            0xF2F2F2,
                            0x7E57C2,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_TEACHER_SPAWN_EGG =
            ITEMS.register("tek_teacher_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_TEACHER,
                            0x6D4C41,
                            0xFFF176,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_ENCHANTER_SPAWN_EGG =
            ITEMS.register("tek_enchanter_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_ENCHANTER,
                            0x4A148C,
                            0x80DEEA,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_DRUID_SPAWN_EGG =
            ITEMS.register("tek_druid_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_DRUID,
                            0x2E7D32,
                            0xA5D6A7,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_BARD_SPAWN_EGG =
            ITEMS.register("tek_bard_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_BARD,
                            0x6A1B9A,
                            0xFFCC80,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_ARCHITECT_SPAWN_EGG =
            ITEMS.register("tek_architect_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_ARCHITECT,
                            0x455A64,
                            0xFFB300,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_CHILD_SPAWN_EGG =
            ITEMS.register("tek_child_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_CHILD,
                            0x90CAF9,
                            0xF8BBD0,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_NITWIT_SPAWN_EGG =
            ITEMS.register("tek_nitwit_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_NITWIT,
                            0x8D6E63,
                            0xCFD8DC,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TEK_NECROMANCER_SPAWN_EGG =
            ITEMS.register("tek_necromancer_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_NECROMANCER,
                            0x151515,
                            0x7B0000,
                            new Item.Properties().tab(ItemGroup.TAB_MISC)));

    private TekItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    private static Item basicItem() {
        return new Item(new Item.Properties().tab(ItemGroup.TAB_MISC));
    }
}
