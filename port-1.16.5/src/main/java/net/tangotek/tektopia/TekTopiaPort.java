package net.tangotek.tektopia;

import net.tangotek.tektopia.caps.TekCapabilities;
import net.tangotek.tektopia.common.TekCapabilityEvents;
import net.tangotek.tektopia.common.TekCommandEvents;
import net.tangotek.tektopia.common.TekGameRules;
import net.tangotek.tektopia.common.TekInteractionEvents;
import net.tangotek.tektopia.common.TekStructureEvents;
import net.tangotek.tektopia.entities.TekEntityEvents;
import net.tangotek.tektopia.network.TekNetwork;
import net.tangotek.tektopia.registry.TekBlocks;
import net.tangotek.tektopia.registry.TekEntities;
import net.tangotek.tektopia.registry.TekItems;
import net.tangotek.tektopia.registry.TekPotions;
import net.tangotek.tektopia.registry.TekSounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TekTopiaPort.MODID)
public class TekTopiaPort {
    public static final String MODID = "tektopia";
    public static final Logger LOGGER = LogManager.getLogger();

    public TekTopiaPort() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        TekBlocks.register(modBus);
        TekItems.register(modBus);
        TekEntities.register(modBus);
        TekPotions.register(modBus);
        TekSounds.register(modBus);

        modBus.addListener(this::onCommonSetup);
        modBus.addListener(TekEntityEvents::onEntityAttributeCreation);

        // Force static gamerule registration at startup.
        TekGameRules.bootstrap();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new TekCommandEvents());
        MinecraftForge.EVENT_BUS.register(new TekCapabilityEvents());
        MinecraftForge.EVENT_BUS.register(new TekInteractionEvents());
        MinecraftForge.EVENT_BUS.register(new TekStructureEvents());
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing TekTopia 1.16.5 port bootstrap (Phase 6)");
        event.enqueueWork(() -> {
            TekCapabilities.register();
            TekNetwork.register();
        });
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("TekTopia port server startup hook active");
    }
}
