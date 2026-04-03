/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.network.ClientIAnimatedEventMessage
 *  com.leviathanstudio.craftstudio.common.network.IAnimatedEventMessage
 *  com.leviathanstudio.craftstudio.common.network.ServerIAnimatedEventMessage
 *  net.minecraftforge.fml.common.network.NetworkRegistry$TargetPoint
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package net.tangotek.tektopia.proxy;

import com.leviathanstudio.craftstudio.common.network.ClientIAnimatedEventMessage;
import com.leviathanstudio.craftstudio.common.network.IAnimatedEventMessage;
import com.leviathanstudio.craftstudio.common.network.ServerIAnimatedEventMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.tangotek.tektopia.TekVillager;

public class TekNetworkHelper {
    public static final double EVENT_RANGE = 128.0;

    public static void sendIAnimatedEvent(IAnimatedEventMessage message) {
        if (message.animated.isWorldRemote()) {
            TekVillager.NETWORK.sendToServer((IMessage)new ServerIAnimatedEventMessage(message));
        } else {
            TekVillager.NETWORK.sendToAllAround((IMessage)new ClientIAnimatedEventMessage(message), new NetworkRegistry.TargetPoint(message.animated.getDimension(), message.animated.getX(), message.animated.getY(), message.animated.getZ(), 128.0));
        }
    }
}

