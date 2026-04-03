/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.Entity
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package net.tangotek.tektopia.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class PacketVillagerThought
implements IMessage {
    private EntityVillagerTek.VillagerThought thought;
    private float scale;
    private int entityId;

    public PacketVillagerThought() {
    }

    public PacketVillagerThought(EntityVillagerTek v, EntityVillagerTek.VillagerThought thought, float scale) {
        this.thought = thought;
        this.scale = scale;
        this.entityId = v.func_145782_y();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.thought.getVal());
        buf.writeInt(this.entityId);
        buf.writeFloat(this.scale);
    }

    public void fromBytes(ByteBuf buf) {
        this.thought = EntityVillagerTek.VillagerThought.valueOf(buf.readInt());
        this.entityId = buf.readInt();
        this.scale = buf.readFloat();
    }

    public float getScale() {
        return this.scale;
    }

    public EntityVillagerTek.VillagerThought getThought() {
        return this.thought;
    }

    public EntityVillagerTek getVillager() {
        Entity ent = Minecraft.func_71410_x().field_71441_e.func_73045_a(this.entityId);
        if (ent instanceof EntityVillagerTek) {
            EntityVillagerTek villager = (EntityVillagerTek)ent;
            return villager;
        }
        return null;
    }

    public static class PacketVillagerThoughtHandler
    implements IMessageHandler<PacketVillagerThought, IMessage> {
        public IMessage onMessage(PacketVillagerThought message, MessageContext ctx) {
            Minecraft.func_71410_x().func_152344_a(() -> {
                EntityVillagerTek villager = message.getVillager();
                if (villager != null) {
                    villager.handleThought(message);
                }
            });
            return null;
        }
    }
}

