/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.client.animation.ClientAnimationHandler
 *  com.leviathanstudio.craftstudio.common.animation.IAnimated
 *  com.leviathanstudio.craftstudio.common.animation.InfoChannel
 *  com.leviathanstudio.craftstudio.common.network.ClientIAnimatedEventMessage
 *  com.leviathanstudio.craftstudio.common.network.EnumIAnimatedEvent
 *  com.leviathanstudio.craftstudio.common.network.IAnimatedEventMessage
 *  com.leviathanstudio.craftstudio.common.network.IAnimatedEventMessage$IAnimatedEventHandler
 *  com.leviathanstudio.craftstudio.common.network.ServerIAnimatedEventMessage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.Entity
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package net.tangotek.tektopia.proxy;

import com.leviathanstudio.craftstudio.client.animation.ClientAnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import com.leviathanstudio.craftstudio.common.animation.InfoChannel;
import com.leviathanstudio.craftstudio.common.network.ClientIAnimatedEventMessage;
import com.leviathanstudio.craftstudio.common.network.EnumIAnimatedEvent;
import com.leviathanstudio.craftstudio.common.network.IAnimatedEventMessage;
import com.leviathanstudio.craftstudio.common.network.ServerIAnimatedEventMessage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.tangotek.tektopia.proxy.TekNetworkHelper;

public class TekClientAnimationHandler<T extends IAnimated>
extends ClientAnimationHandler<T> {
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

    public static class ClientIAnimatedEventHandler
    extends IAnimatedEventMessage.IAnimatedEventHandler
    implements IMessageHandler<ClientIAnimatedEventMessage, ServerIAnimatedEventMessage> {
        Map<UUID, Short> curAnimMap = new HashMap<UUID, Short>();

        public ServerIAnimatedEventMessage onMessage(ClientIAnimatedEventMessage message, MessageContext ctx) {
            Minecraft.func_71410_x().func_152344_a(() -> {
                if (ClientIAnimatedEventHandler.super.onMessage((IAnimatedEventMessage)message, ctx)) {
                    UUID uuid = new UUID(message.most, message.least);
                    boolean success = message.animated.getAnimationHandler().onClientIAnimatedEvent((IAnimatedEventMessage)message);
                    if (!success || !(message.animated.getAnimationHandler() instanceof ClientAnimationHandler) || message.event != EnumIAnimatedEvent.START_ANIM.getId() && message.event != EnumIAnimatedEvent.STOP_START_ANIM.getId()) {
                        Short lastAnimId;
                        if (message.event == EnumIAnimatedEvent.STOP_ANIM.getId() && (lastAnimId = this.curAnimMap.get(uuid)) != null && lastAnimId == message.animId) {
                            this.curAnimMap.remove(uuid);
                        }
                        return;
                    }
                    ClientAnimationHandler hand = (ClientAnimationHandler)message.animated.getAnimationHandler();
                    String animName = hand.getAnimNameFromId(message.animId);
                    InfoChannel infoC = (InfoChannel)hand.getAnimChannels().get(animName);
                    TekNetworkHelper.sendIAnimatedEvent((IAnimatedEventMessage)new ServerIAnimatedEventMessage(EnumIAnimatedEvent.ANSWER_START_ANIM, message.animated, message.animId, (float)infoC.totalFrames));
                    Short lastAnimId = this.curAnimMap.put(uuid, message.animId);
                    if (lastAnimId != null && lastAnimId != message.animId) {
                        String curAnimName = hand.getAnimNameFromId(lastAnimId.shortValue());
                        hand.clientStopAnimation(curAnimName, message.animated);
                    }
                }
            });
            return null;
        }

        public Entity getEntityByUUID(MessageContext ctx, long most, long least) {
            Entity e;
            UUID uuid = new UUID(most, least);
            Iterator var7 = Minecraft.func_71410_x().field_71441_e.field_72996_f.iterator();
            do {
                if (var7.hasNext()) continue;
                return null;
            } while (!(e = (Entity)var7.next()).getPersistentID().equals(uuid));
            return e;
        }

        public TileEntity getTileEntityByPos(MessageContext ctx, int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            return Minecraft.func_71410_x().field_71441_e.func_175625_s(pos);
        }
    }
}

