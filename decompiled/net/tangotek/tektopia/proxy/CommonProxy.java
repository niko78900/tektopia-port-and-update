/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  com.leviathanstudio.craftstudio.common.animation.IAnimated
 *  com.leviathanstudio.craftstudio.common.network.ClientIAnimatedEventMessage
 *  com.leviathanstudio.craftstudio.common.network.ServerIAnimatedEventMessage
 *  net.minecraft.item.Item
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraftforge.common.capabilities.Capability$IStorage
 *  net.minecraftforge.common.capabilities.CapabilityManager
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPostInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  net.minecraftforge.fml.relauncher.Side
 */
package net.tangotek.tektopia.proxy;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import com.leviathanstudio.craftstudio.common.network.ClientIAnimatedEventMessage;
import com.leviathanstudio.craftstudio.common.network.ServerIAnimatedEventMessage;
import java.util.concurrent.Callable;
import net.minecraft.item.Item;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.tangotek.tektopia.LicenseTracker;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.VillageClient;
import net.tangotek.tektopia.caps.IPlayerLicense;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.caps.PlayerLicense;
import net.tangotek.tektopia.caps.VillageData;
import net.tangotek.tektopia.generation.TekStructureVillagePieces;
import net.tangotek.tektopia.network.PacketAIFilter;
import net.tangotek.tektopia.network.PacketLicense;
import net.tangotek.tektopia.network.PacketPathingNode;
import net.tangotek.tektopia.network.PacketVillage;
import net.tangotek.tektopia.network.PacketVillagerItemThought;
import net.tangotek.tektopia.network.PacketVillagerThought;
import net.tangotek.tektopia.pathing.PathingNodeClient;
import net.tangotek.tektopia.proxy.TekClientAnimationHandler;
import net.tangotek.tektopia.proxy.TekServerAnimationHandler;

public abstract class CommonProxy {
    public static int NETWORK_DISCRIMINATOR = 0;

    public void preInit(FMLPreInitializationEvent e) {
        TekVillager.NETWORK.registerMessage(TekClientAnimationHandler.ClientIAnimatedEventHandler.class, ClientIAnimatedEventMessage.class, NETWORK_DISCRIMINATOR++, Side.CLIENT);
        TekVillager.NETWORK.registerMessage(TekServerAnimationHandler.ServerIAnimatedEventHandler.class, ServerIAnimatedEventMessage.class, NETWORK_DISCRIMINATOR++, Side.SERVER);
        TekVillager.NETWORK.registerMessage(PacketVillagerItemThought.PacketVillagerItemThoughtHandler.class, PacketVillagerItemThought.class, NETWORK_DISCRIMINATOR++, Side.CLIENT);
        TekVillager.NETWORK.registerMessage(PacketVillagerThought.PacketVillagerThoughtHandler.class, PacketVillagerThought.class, NETWORK_DISCRIMINATOR++, Side.CLIENT);
        TekVillager.NETWORK.registerMessage(PacketPathingNode.PacketPathingNodeHandler.class, PacketPathingNode.class, NETWORK_DISCRIMINATOR++, Side.CLIENT);
        TekVillager.NETWORK.registerMessage(PacketVillage.PacketVillageHandler.class, PacketVillage.class, NETWORK_DISCRIMINATOR++, Side.CLIENT);
        TekVillager.NETWORK.registerMessage(PacketAIFilter.PacketAIFilterHandler.class, PacketAIFilter.class, NETWORK_DISCRIMINATOR++, Side.SERVER);
        TekVillager.NETWORK.registerMessage(PacketLicense.PacketLicenseHandler.class, PacketLicense.class, NETWORK_DISCRIMINATOR++, Side.SERVER);
        TekVillager.NETWORK.registerMessage(PacketLicense.PacketLicenseHandler.class, PacketLicense.class, NETWORK_DISCRIMINATOR++, Side.CLIENT);
        ModEntities.init();
        CapabilityManager.INSTANCE.register(IVillageData.class, (Capability.IStorage)new VillageData(), (Callable)new VillageStorageFactory());
        CapabilityManager.INSTANCE.register(IPlayerLicense.class, (Capability.IStorage)new PlayerLicense(), (Callable)new PlayerLicenseFactory());
        LicenseTracker.INSTANCE.setup();
    }

    public void init(FMLInitializationEvent e) {
        TekStructureVillagePieces.registerVillagePieces();
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    public void handleNodeUpdate(PathingNodeClient node) {
    }

    public void handleVillage(VillageClient vc) {
    }

    public abstract <T extends IAnimated> AnimationHandler<T> getNewAnimationHandler(Class<T> var1);

    public void registerItemRenderer(Item item, int meta, String id) {
    }

    public void registerModels() {
    }

    public void registerAnims() {
    }

    public void onChunkLoaded(Chunk ch) {
    }

    private static class PlayerLicenseFactory
    implements Callable<IPlayerLicense> {
        private PlayerLicenseFactory() {
        }

        @Override
        public IPlayerLicense call() throws Exception {
            return new PlayerLicense();
        }
    }

    private static class VillageStorageFactory
    implements Callable<IVillageData> {
        private VillageStorageFactory() {
        }

        @Override
        public IVillageData call() throws Exception {
            return new VillageData();
        }
    }
}

