package net.tangotek.tektopia.network.message;

import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.common.TekTradeActions;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.network.TekNetwork;

public class PacketTradeAction {
    private static final int MAX_ACTION_LEN = 64;
    private final int entityId;
    private final String action;

    public PacketTradeAction(int entityId, String action) {
        this.entityId = entityId;
        this.action = action == null ? "" : action;
    }

    public static void encode(PacketTradeAction msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
        buf.writeUtf(msg.action, MAX_ACTION_LEN);
    }

    public static PacketTradeAction decode(PacketBuffer buf) {
        return new PacketTradeAction(buf.readInt(), buf.readUtf(MAX_ACTION_LEN));
    }

    public static void handle(PacketTradeAction msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            if (sender == null) {
                return;
            }
            Entity entity = sender.level.getEntity(msg.entityId);
            TekTradeActions.Result result = TekTradeActions.purchaseFromGui(sender, entity, msg.action);
            result.sendTo(sender);
            if (entity instanceof TekVillagerEntity) {
                TekNetwork.sendVillagerGuiSnapshot(sender, (TekVillagerEntity) entity);
                TekNetwork.sendVillagerState(sender.getLevel(), (TekVillagerEntity) entity);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
