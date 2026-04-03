/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.capabilities.ICapabilityProvider
 *  net.minecraftforge.event.AttachCapabilitiesEvent
 *  net.minecraftforge.event.entity.EntityJoinWorldEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$Clone
 *  net.minecraftforge.event.entity.player.PlayerEvent$StartTracking
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package net.tangotek.tektopia;

import com.websina.license.LicenseManager;
import com.websina.license.LicenseManagerTek;
import java.security.GeneralSecurityException;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.caps.IPlayerLicense;
import net.tangotek.tektopia.caps.PlayerLicenseProvider;
import net.tangotek.tektopia.network.PacketLicense;

public class LicenseTracker {
    public static final LicenseTracker INSTANCE = new LicenseTracker();

    protected LicenseTracker() {
    }

    public void setup() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    public void submitLicense(EntityPlayerMP player, String licData) {
        IPlayerLicense pl = (IPlayerLicense)player.getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY, null);
        pl.setLicenseData(licData);
        if (pl.isValid(player.func_70005_c_())) {
            this.sendLicenseToTracking((Entity)player);
            this.sendLicenseToPlayer((Entity)player, player);
        }
    }

    private void sendLicenseToPlayer(Entity source, EntityPlayerMP target) {
        String licData = ((IPlayerLicense)source.getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY, null)).getLicenseData();
        if (licData != null) {
            TekVillager.NETWORK.sendTo((IMessage)new PacketLicense(source.func_110124_au(), licData), target);
        }
    }

    private void sendLicenseToTracking(Entity source) {
        String licData = ((IPlayerLicense)source.getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY, null)).getLicenseData();
        if (licData != null) {
            TekVillager.NETWORK.sendToAllTracking((IMessage)new PacketLicense(source.func_110124_au(), licData), source);
        }
    }

    @SubscribeEvent
    public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (!event.getEntityPlayer().func_130014_f_().field_72995_K && event.getTarget() instanceof EntityPlayer) {
            INSTANCE.sendLicenseToPlayer(event.getTarget(), (EntityPlayerMP)event.getEntityPlayer());
        }
    }

    @SubscribeEvent
    public void playerCapabilities(AttachCapabilitiesEvent<Entity> event) {
        EntityPlayer player;
        if (event.getObject() instanceof EntityPlayer && !(player = (EntityPlayer)event.getObject()).hasCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY, null)) {
            event.addCapability(new ResourceLocation("tektopia", "PlayerLicense"), (ICapabilityProvider)new PlayerLicenseProvider());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        String licData;
        IPlayerLicense pl = (IPlayerLicense)event.getOriginal().getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY, null);
        if (pl.isValid(event.getEntityPlayer().func_70005_c_()) && (licData = pl.getLicenseData()) != null) {
            ((IPlayerLicense)event.getEntityPlayer().getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY, null)).setLicenseData(licData);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().field_72995_K && event.getEntity().func_110124_au().equals(Minecraft.func_71410_x().field_71439_g.func_110124_au())) {
            try {
                LicenseManagerTek manager = new LicenseManagerTek();
                if (((LicenseManager)manager).isValid()) {
                    System.out.println("Valid TekTopia licence file found! Sending to server");
                    TekVillager.NETWORK.sendToServer((IMessage)new PacketLicense(Minecraft.func_71410_x().field_71439_g.func_110124_au(), ((LicenseManager)manager).getLicense()));
                } else {
                    System.err.println("Invalid TekTopia licence file");
                }
            }
            catch (GeneralSecurityException gse) {
                System.out.println("GeneralSecurityException process TekTopia license file - " + gse.getLocalizedMessage());
            }
            catch (RuntimeException ex) {
                System.out.println("Missing or invalid TekTopia licence file. (This is not a problem)");
            }
        }
    }

    public static enum Feature {
        HATS("hats");

        private final String name;

        private Feature(String n2) {
            this.name = n2;
        }

        public String getName() {
            return this.name;
        }
    }
}

