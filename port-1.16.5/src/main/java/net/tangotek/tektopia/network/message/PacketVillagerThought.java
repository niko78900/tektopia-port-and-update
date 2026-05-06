package net.tangotek.tektopia.network.message;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.client.TekClientSyncState;

public class PacketVillagerThought {
    private static final int MAX_THOUGHT_KEY_LEN = 128;
    private final int entityId;
    private final String thoughtKey;

    public PacketVillagerThought(int entityId, String thoughtKey) {
        this.entityId = entityId;
        this.thoughtKey = thoughtKey;
    }

    public static void encode(PacketVillagerThought msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
        buf.writeUtf(msg.thoughtKey, MAX_THOUGHT_KEY_LEN);
    }

    public static PacketVillagerThought decode(PacketBuffer buf) {
        return new PacketVillagerThought(buf.readInt(), buf.readUtf(MAX_THOUGHT_KEY_LEN));
    }

    public static void handle(PacketVillagerThought msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    TekClientSyncState.setVillagerThought(msg.entityId, msg.thoughtKey));
        });
        ctx.get().setPacketHandled(true);
    }
}
