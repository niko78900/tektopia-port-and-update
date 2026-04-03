/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.network.PacketBuffer
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 *  net.minecraftforge.fml.relauncher.Side
 */
package net.tangotek.tektopia.network;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.tangotek.tektopia.LicenseTracker;
import net.tangotek.tektopia.caps.IPlayerLicense;
import net.tangotek.tektopia.caps.PlayerLicenseProvider;

public class PacketLicense
implements IMessage {
    private String data;
    private UUID playerUUID;

    public PacketLicense() {
    }

    public PacketLicense(UUID uuid, String licFormat) {
        this.playerUUID = uuid;
        this.data = licFormat;
    }

    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.func_179252_a(this.playerUUID);
        pb.func_180714_a(this.data);
    }

    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.playerUUID = pb.func_179253_g();
        this.data = pb.func_150789_c(1024);
    }

    public String getLicenseData() {
        return this.data;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public static class PacketLicenseHandler
    implements IMessageHandler<PacketLicense, IMessage> {
        public IMessage onMessage(PacketLicense message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                EntityPlayerMP serverPlayer = ctx.getServerHandler().field_147369_b;
                serverPlayer.func_71121_q().func_152344_a(() -> LicenseTracker.INSTANCE.submitLicense(serverPlayer, message.getLicenseData()));
                return null;
            }
            Minecraft.func_71410_x().func_152344_a(() -> {
                EntityPlayer targetPlayer = Minecraft.func_71410_x().field_71441_e.func_152378_a(message.getPlayerUUID());
                ((IPlayerLicense)targetPlayer.getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY, null)).setLicenseData(message.getLicenseData());
            });
            return null;
        }
    }
}

