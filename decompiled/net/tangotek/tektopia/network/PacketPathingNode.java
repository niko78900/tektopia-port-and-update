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
import net.tangotek.tektopia.pathing.PathingNodeClient;

public class PacketPathingNode
implements IMessage {
    private PathingNodeClient node;

    public PacketPathingNode() {
    }

    public PacketPathingNode(PathingNodeClient n) {
        this.node = n;
    }

    public void toBytes(ByteBuf buf) {
        if (this.node != null) {
            this.node.toBytes(buf);
        }
    }

    public void fromBytes(ByteBuf buf) {
        this.node = buf.isReadable() ? new PathingNodeClient(buf) : null;
    }

    public PathingNodeClient getNode() {
        return this.node;
    }

    public static class PacketPathingNodeHandler
    implements IMessageHandler<PacketPathingNode, IMessage> {
        public IMessage onMessage(PacketPathingNode message, MessageContext ctx) {
            Minecraft.func_71410_x().func_152344_a(() -> TekVillager.proxy.handleNodeUpdate(message.getNode()));
            return null;
        }
    }
}

