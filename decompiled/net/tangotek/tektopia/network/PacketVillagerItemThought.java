/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.Item
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package net.tangotek.tektopia.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.tangotek.tektopia.ModEntities;

public class PacketVillagerItemThought
implements IMessage {
    private int itemId;
    private int entityId;

    public PacketVillagerItemThought() {
    }

    public PacketVillagerItemThought(Entity v, Item item) {
        this.itemId = Item.field_150901_e.func_148757_b((Object)item);
        this.entityId = v.func_145782_y();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.itemId);
        buf.writeInt(this.entityId);
    }

    public void fromBytes(ByteBuf buf) {
        this.itemId = buf.readInt();
        this.entityId = buf.readInt();
    }

    public Item getItem() {
        return (Item)Item.field_150901_e.func_148754_a(this.itemId);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Entity getEntity() {
        Entity ent = Minecraft.func_71410_x().field_71441_e.func_73045_a(this.entityId);
        return ent;
    }

    public static class PacketVillagerItemThoughtHandler
    implements IMessageHandler<PacketVillagerItemThought, IMessage> {
        public IMessage onMessage(PacketVillagerItemThought message, MessageContext ctx) {
            Minecraft.func_71410_x().func_152344_a(() -> {
                Entity ent = message.getEntity();
                if (ent != null) {
                    ModEntities.handleItemThought(ent.field_70170_p, ent, message.getItem());
                }
            });
            return null;
        }
    }
}

