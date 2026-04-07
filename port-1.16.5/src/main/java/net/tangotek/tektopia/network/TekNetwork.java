package net.tangotek.tektopia.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.tangotek.tektopia.TekTopiaPort;

public final class TekNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TekTopiaPort.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static boolean initialized = false;

    private TekNetwork() {
    }

    public static void register() {
        if (initialized) {
            return;
        }
        initialized = true;
        TekTopiaPort.LOGGER.info("TekTopia network channel initialized (Phase 2 scaffold)");
    }
}
