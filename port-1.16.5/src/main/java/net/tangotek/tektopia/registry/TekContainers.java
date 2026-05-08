package net.tangotek.tektopia.registry;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.client.gui.TekVillagerContainer;

public final class TekContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, TekTopiaPort.MODID);

    public static final RegistryObject<ContainerType<TekVillagerContainer>> TEK_VILLAGER =
            CONTAINERS.register(
                    "tek_villager",
                    () -> IForgeContainerType.create((windowId, inv, data) ->
                            new TekVillagerContainer(windowId, inv, data.readInt()))
            );

    private TekContainers() {
    }

    public static void register(IEventBus modBus) {
        CONTAINERS.register(modBus);
    }
}
