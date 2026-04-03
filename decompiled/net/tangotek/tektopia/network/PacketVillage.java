/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package net.tangotek.tektopia.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.VillageClient;

public class PacketVillage
implements IMessage {
    private VillageClient villageClient;

    public PacketVillage() {
    }

    public PacketVillage(VillageClient vc) {
        this.villageClient = vc;
    }

    public void toBytes(ByteBuf buf) {
        if (this.villageClient != null) {
            this.villageClient.toBytes(buf);
        }
    }

    public void fromBytes(ByteBuf buf) {
        this.villageClient = buf.isReadable() ? new VillageClient(buf) : null;
    }

    public VillageClient getVillageClient() {
        return this.villageClient;
    }

    public static class PacketVillageHandler
    implements IMessageHandler<PacketVillage, IMessage> {
        public IMessage onMessage(PacketVillage message, MessageContext ctx) {
            Minecraft.func_71410_x().func_152344_a(() -> TekVillager.proxy.handleVillage(message.getVillageClient()));
            return null;
        }
    }
}

