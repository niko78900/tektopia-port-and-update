package net.tangotek.tektopia.registry;

import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tangotek.tektopia.TekTopiaPort;

public final class TekItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TekTopiaPort.MODID);

    private TekItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
