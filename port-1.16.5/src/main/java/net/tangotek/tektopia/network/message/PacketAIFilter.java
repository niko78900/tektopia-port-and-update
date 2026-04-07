package net.tangotek.tektopia.network.message;

import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.entities.TekVillagerEntity;

public class PacketAIFilter {
    private static final int MAX_FILTER_NAME_LEN = 64;
    private final int entityId;
    private final String filterName;
    private final boolean enabled;

    public PacketAIFilter(int entityId, String filterName, boolean enabled) {
        this.entityId = entityId;
        this.filterName = filterName;
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
                return;
            }
            Entity entity = sender.level.getEntity(msg.entityId);
            if (!(entity instanceof TekVillagerEntity)) {
                return;
            }
            ((TekVillagerEntity) entity).setAIFilter(msg.filterName, msg.enabled);
        });
        ctx.get().setPacketHandled(true);
    }
}
