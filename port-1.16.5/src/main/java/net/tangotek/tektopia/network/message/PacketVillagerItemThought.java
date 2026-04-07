package net.tangotek.tektopia.network.message;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketVillagerItemThought {
    private static final int MAX_ITEM_ID_LEN = 128;
    private final int entityId;
    private final String itemId;

    public PacketVillagerItemThought(int entityId, String itemId) {
        this.entityId = entityId;
        this.itemId = itemId;
    }

    public static void encode(PacketVillagerItemThought msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
        buf.writeUtf(msg.itemId, MAX_ITEM_ID_LEN);
    }

    public static PacketVillagerItemThought decode(PacketBuffer buf) {
        return new PacketVillagerItemThought(buf.readInt(), buf.readUtf(MAX_ITEM_ID_LEN));
    }

    public static void handle(PacketVillagerItemThought msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Phase 3 scaffold: client particle/item-thought rendering sync is pending.
        });
        ctx.get().setPacketHandled(true);
    }
}
