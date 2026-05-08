package net.tangotek.tektopia.network.message;

import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.common.TekVillagerContainer;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.network.TekClientSyncCache;
import net.tangotek.tektopia.network.TekNetwork;

public class PacketAIFilter {
    private static final int MAX_FILTER_NAME_LEN = 64;
    private final int entityId;
    private final String filterName;
    private final boolean enabled;

    public PacketAIFilter(int entityId, String filterName, boolean enabled) {
        this.entityId = entityId;
        this.filterName = filterName == null ? "" : filterName;
        this.enabled = enabled;
    }

    public static void encode(PacketAIFilter msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
        buf.writeUtf(msg.filterName, MAX_FILTER_NAME_LEN);
        buf.writeBoolean(msg.enabled);
    }

    public static PacketAIFilter decode(PacketBuffer buf) {
        return new PacketAIFilter(buf.readInt(), buf.readUtf(MAX_FILTER_NAME_LEN), buf.readBoolean());
    }

    public static void handle(PacketAIFilter msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            if (sender == null) {
                TekClientSyncCache.updateAIFilter(msg.entityId, msg.filterName, msg.enabled);
                return;
            }
            Entity entity = sender.level.getEntity(msg.entityId);
            if (!(entity instanceof TekVillagerEntity)
                    || !TekVillagerContainer.canInteract(sender, entity, msg.entityId)
                    || !TekVillagerContainer.isOpenFor(sender, msg.entityId)) {
                sendFailure(sender, "AI filter change rejected: villager GUI is not open or target is out of range.");
                return;
            }
            if (((TekVillagerEntity) entity).setAIFilter(msg.filterName, msg.enabled)) {
                TekNetwork.sendToTracking(new PacketAIFilter(msg.entityId, msg.filterName, msg.enabled), entity);
                TekNetwork.sendToPlayer(new PacketAIFilter(msg.entityId, msg.filterName, msg.enabled), sender);
            } else {
                sendFailure(sender, "AI filter change rejected: unknown filter '" + msg.filterName + "'.");
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static void sendFailure(ServerPlayerEntity player, String message) {
        player.sendMessage(new StringTextComponent(message), player.getUUID());
    }
}
