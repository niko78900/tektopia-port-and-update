package net.tangotek.tektopia.network.message;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.network.TekClientSyncCache;

public class PacketPathingNode {
    private static final int MAX_REASON_LEN = 128;
    private final int entityId;
    private final BlockPos target;
    private final String reason;
    private final boolean clearOnly;

    public PacketPathingNode(boolean clearOnly) {
        this(-1, BlockPos.ZERO, "", clearOnly);
    }

    public PacketPathingNode(int entityId, BlockPos target, String reason, boolean clearOnly) {
        this.entityId = entityId;
        this.target = target == null ? BlockPos.ZERO : target.immutable();
        this.reason = reason == null ? "" : reason;
        this.clearOnly = clearOnly;
    }

    public static void encode(PacketPathingNode msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
        buf.writeBlockPos(msg.target);
        buf.writeUtf(msg.reason, MAX_REASON_LEN);
        buf.writeBoolean(msg.clearOnly);
    }

    public static PacketPathingNode decode(PacketBuffer buf) {
        return new PacketPathingNode(
                buf.readInt(),
                buf.readBlockPos(),
                buf.readUtf(MAX_REASON_LEN),
                buf.readBoolean()
        );
    }

    public static void handle(PacketPathingNode msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (msg.clearOnly && msg.entityId < 0) {
                TekClientSyncCache.clearPathNodes();
            } else {
                TekClientSyncCache.updatePathNode(new TekClientSyncCache.PathNodeState(
                        msg.entityId,
                        msg.target,
                        msg.reason,
                        msg.clearOnly
                ));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
