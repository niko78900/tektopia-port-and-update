package net.tangotek.tektopia.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.tangotek.tektopia.network.message.PacketAIFilter;
import net.tangotek.tektopia.network.message.PacketLicense;
import net.tangotek.tektopia.network.message.PacketPathingNode;
import net.tangotek.tektopia.network.message.PacketVillage;
import net.tangotek.tektopia.network.message.PacketVillagerItemThought;
import net.tangotek.tektopia.network.message.PacketVillagerThought;
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
        int id = 0;
        CHANNEL.registerMessage(id++, PacketAIFilter.class, PacketAIFilter::encode, PacketAIFilter::decode, PacketAIFilter::handle);
        CHANNEL.registerMessage(id++, PacketLicense.class, PacketLicense::encode, PacketLicense::decode, PacketLicense::handle);
        CHANNEL.registerMessage(id++, PacketPathingNode.class, PacketPathingNode::encode, PacketPathingNode::decode, PacketPathingNode::handle);
        CHANNEL.registerMessage(id++, PacketVillage.class, PacketVillage::encode, PacketVillage::decode, PacketVillage::handle);
        CHANNEL.registerMessage(id++, PacketVillagerItemThought.class, PacketVillagerItemThought::encode, PacketVillagerItemThought::decode, PacketVillagerItemThought::handle);
        CHANNEL.registerMessage(id++, PacketVillagerThought.class, PacketVillagerThought::encode, PacketVillagerThought::decode, PacketVillagerThought::handle);
        TekTopiaPort.LOGGER.info("TekTopia network channel initialized (Phase 2 scaffold)");
    }
}
