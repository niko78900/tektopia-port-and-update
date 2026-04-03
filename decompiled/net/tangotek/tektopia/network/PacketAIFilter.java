/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package net.tangotek.tektopia.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class PacketAIFilter
implements IMessage {
    private Integer entityId;
    private String aiFilter;
    private Boolean enabled;

    public PacketAIFilter() {
    }

    public PacketAIFilter(Entity ent, String filterName, boolean e) {
        this.entityId = ent.func_145782_y();
        this.aiFilter = filterName;
        this.enabled = e;
    }

    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeInt(this.entityId.intValue());
        pb.func_180714_a(this.aiFilter);
        pb.writeBoolean(this.enabled.booleanValue());
    }

    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.entityId = pb.readInt();
        this.aiFilter = pb.func_150789_c(30);
        this.enabled = buf.readBoolean();
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public String getAiFilter() {
        return this.aiFilter;
    }

    public EntityVillagerTek getVillager(World world) {
        Entity ent = world.func_73045_a(this.entityId.intValue());
        if (ent instanceof EntityVillagerTek) {
            EntityVillagerTek villager = (EntityVillagerTek)ent;
            return villager;
        }
        return null;
    }

    public static class PacketAIFilterHandler
    implements IMessageHandler<PacketAIFilter, IMessage> {
        public IMessage onMessage(PacketAIFilter message, MessageContext ctx) {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().field_147369_b;
            serverPlayer.func_71121_q().func_152344_a(() -> {
                EntityVillagerTek villager = message.getVillager((World)serverPlayer.func_71121_q());
                if (villager != null) {
                    villager.setAIFilter(message.getAiFilter(), message.getEnabled());
                    villager.equipBestGear();
                    villager.getDesireSet().forceUpdate();
                }
            });
            return null;
        }
    }
}

