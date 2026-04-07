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
    public static final RegistryObject<Item> TEK_GUARD_SPAWN_EGG =
            ITEMS.register("tek_guard_spawn_egg",
                    () -> new ForgeSpawnEggItem(
                            TekEntities.TEK_GUARD,
                            0x2F4F4F,
                            0xE0B040,
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
