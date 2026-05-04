package net.tangotek.tektopia.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.caps.IPlayerLicense;
import net.tangotek.tektopia.caps.PlayerLicenseProvider;
import net.tangotek.tektopia.caps.VillageDataProvider;
import net.tangotek.tektopia.network.TekNetwork;
import net.tangotek.tektopia.network.message.PacketLicense;

public class TekCapabilityEvents {
    private static final ResourceLocation PLAYER_LICENSE_KEY =
            new ResourceLocation(TekTopiaPort.MODID, "player_license");
    private static final ResourceLocation VILLAGE_DATA_KEY =
            new ResourceLocation(TekTopiaPort.MODID, "village_data");

    @SubscribeEvent
    public void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof PlayerEntity)) {
            return;
        }
        PlayerLicenseProvider provider = new PlayerLicenseProvider();
        event.addCapability(PLAYER_LICENSE_KEY, provider);
        event.addListener(provider::invalidate);
    }

    @SubscribeEvent
    public void onAttachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack.isEmpty() || stack.getItem().getRegistryName() == null) {
            return;
        }
        ResourceLocation itemName = stack.getItem().getRegistryName();
        if (!TekTopiaPort.MODID.equals(itemName.getNamespace()) || !itemName.getPath().contains("townhall")) {
            return;
        }
        VillageDataProvider provider = new VillageDataProvider();
        event.addCapability(VILLAGE_DATA_KEY, provider);
        event.addListener(provider::invalidate);
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY).ifPresent(oldCap ->
                event.getPlayer().getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY).ifPresent(newCap ->
                        newCap.setLicenseData(oldCap.getLicenseData())));
    }

    @SubscribeEvent
    public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) {
            return;
        }
        if (!(event.getTarget() instanceof PlayerEntity)) {
            return;
        }
        ServerPlayerEntity tracker = (ServerPlayerEntity) event.getPlayer();
        PlayerEntity source = (PlayerEntity) event.getTarget();
        sendLicenseToPlayer(source, tracker);
    }

    public static void submitLicense(ServerPlayerEntity player, String licenseData) {
        player.getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY).ifPresent(cap -> {
            cap.setLicenseData(licenseData);
            if (cap.isValid(player.getScoreboardName())) {
                sendLicenseToPlayer(player, player);
                sendLicenseToTracking(player);
            }
        });
    }

    private static void sendLicenseToPlayer(Entity source, ServerPlayerEntity target) {
        source.getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY).ifPresent(cap -> {
            String licenseData = cap.getLicenseData();
            if (licenseData != null) {
                TekNetwork.sendToPlayer(new PacketLicense(source.getUUID(), licenseData), target);
            }
        });
    }

    private static void sendLicenseToTracking(Entity source) {
        source.getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY).ifPresent(cap -> {
            String licenseData = cap.getLicenseData();
            if (licenseData != null) {
                TekNetwork.sendToTracking(new PacketLicense(source.getUUID(), licenseData), source);
            }
        });
    }
}
