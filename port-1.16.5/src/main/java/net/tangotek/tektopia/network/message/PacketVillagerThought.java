package net.tangotek.tektopia.network.message;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.network.TekClientSyncCache;

public class PacketVillagerThought {
    private static final int MAX_THOUGHT_KEY_LEN = 128;
    private static final int MAX_STATUS_LEN = 64;
    private static final int MAX_PROFESSION_LEN = 64;
    private final int entityId;
    private final String thoughtKey;
    private final String workerStatus;
    private final String profession;

    public PacketVillagerThought(int entityId, String thoughtKey) {
        this(entityId, thoughtKey, "", "");
    }

    public PacketVillagerThought(int entityId, String thoughtKey, String workerStatus, String profession) {
        this.entityId = entityId;
        this.thoughtKey = thoughtKey == null ? "" : thoughtKey;
        this.workerStatus = workerStatus == null ? "" : workerStatus;
        this.profession = profession == null ? "" : profession;
    }

    public static void encode(PacketVillagerThought msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
        buf.writeUtf(msg.thoughtKey, MAX_THOUGHT_KEY_LEN);
        buf.writeUtf(msg.workerStatus, MAX_STATUS_LEN);
        buf.writeUtf(msg.profession, MAX_PROFESSION_LEN);
    }

    public static PacketVillagerThought decode(PacketBuffer buf) {
        return new PacketVillagerThought(
                buf.readInt(),
                buf.readUtf(MAX_THOUGHT_KEY_LEN),
                buf.readUtf(MAX_STATUS_LEN),
                buf.readUtf(MAX_PROFESSION_LEN)
        );
    }

    public static void handle(PacketVillagerThought msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            TekClientSyncCache.updateThought(msg.entityId, msg.thoughtKey, msg.workerStatus, msg.profession);
        });
        ctx.get().setPacketHandled(true);
    }
}
