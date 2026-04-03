/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  com.leviathanstudio.craftstudio.common.animation.IAnimated
 *  com.leviathanstudio.craftstudio.common.network.ClientIAnimatedEventMessage
 *  com.leviathanstudio.craftstudio.common.network.EnumIAnimatedEvent
 *  com.leviathanstudio.craftstudio.common.network.IAnimatedEventMessage
 *  com.leviathanstudio.craftstudio.common.network.IAnimatedEventMessage$IAnimatedEventHandler
 *  com.leviathanstudio.craftstudio.common.network.ServerIAnimatedEventMessage
 *  com.leviathanstudio.craftstudio.server.animation.ServerAnimationHandler
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package net.tangotek.tektopia.proxy;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import com.leviathanstudio.craftstudio.common.network.ClientIAnimatedEventMessage;
import com.leviathanstudio.craftstudio.common.network.EnumIAnimatedEvent;
import com.leviathanstudio.craftstudio.common.network.IAnimatedEventMessage;
import com.leviathanstudio.craftstudio.common.network.ServerIAnimatedEventMessage;
import com.leviathanstudio.craftstudio.server.animation.ServerAnimationHandler;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.tangotek.tektopia.proxy.TekNetworkHelper;

public class TekServerAnimationHandler<T extends IAnimated>
extends ServerAnimationHandler<T> {
    public void networkStartAnimation(String res, float startingFrame, T animatedElement, boolean clientSend) {
        if (animatedElement.isWorldRemote() == clientSend) {
            this.serverInitAnimation(res, startingFrame, (IAnimated)animatedElement);
            TekNetworkHelper.sendIAnimatedEvent(new IAnimatedEventMessage(EnumIAnimatedEvent.START_ANIM, animatedElement, this.getAnimIdFromName(res), startingFrame));
        }
    }

    public void networkStopAnimation(String res, T animatedElement, boolean clientSend) {
        if (animatedElement.isWorldRemote() == clientSend) {
            this.serverStopAnimation(res, (IAnimated)animatedElement);
            TekNetworkHelper.sendIAnimatedEvent(new IAnimatedEventMessage(EnumIAnimatedEvent.STOP_ANIM, animatedElement, this.getAnimIdFromName(res)));
        }
    }

    public void networkStopStartAnimation(String animToStop, String animToStart, float startingFrame, T animatedElement, boolean clientSend) {
        if (animatedElement.isWorldRemote() == clientSend) {
            this.serverStopStartAnimation(animToStop, animToStart, startingFrame, (IAnimated)animatedElement);
            TekNetworkHelper.sendIAnimatedEvent(new IAnimatedEventMessage(EnumIAnimatedEvent.STOP_START_ANIM, animatedElement, this.getAnimIdFromName(animToStart), startingFrame, this.getAnimIdFromName(animToStop)));
        }
    }

    public static class ServerIAnimatedEventHandler
    extends IAnimatedEventMessage.IAnimatedEventHandler
    implements IMessageHandler<ServerIAnimatedEventMessage, ClientIAnimatedEventMessage> {
        public ClientIAnimatedEventMessage onMessage(ServerIAnimatedEventMessage message, MessageContext ctx) {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().field_147369_b;
            serverPlayer.func_71121_q().func_152344_a(() -> {
                if (super.onMessage((IAnimatedEventMessage)message, ctx)) {
                    message.animated.getAnimationHandler();
                    boolean success = AnimationHandler.onServerIAnimatedEvent((IAnimatedEventMessage)message);
                    if (success && message.event != EnumIAnimatedEvent.ANSWER_START_ANIM.getId()) {
                        TekNetworkHelper.sendIAnimatedEvent((IAnimatedEventMessage)new ClientIAnimatedEventMessage((IAnimatedEventMessage)message));
                    }
                }
            });
            return null;
        }

        public Entity getEntityByUUID(MessageContext ctx, long most, long least) {
            Entity e;
            UUID uuid = new UUID(most, least);
            Iterator var7 = ctx.getServerHandler().field_147369_b.field_70170_p.field_72996_f.iterator();
            do {
                if (var7.hasNext()) continue;
                return null;
            } while (!(e = (Entity)var7.next()).getPersistentID().equals(uuid));
            return e;
        }

        public TileEntity getTileEntityByPos(MessageContext ctx, int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            return ctx.getServerHandler().field_147369_b.field_70170_p.func_175625_s(pos);
        }
    }
}

