package net.tangotek.tektopia.network.message;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketPathingNode {
    private final boolean clearOnly;

    public PacketPathingNode(boolean clearOnly) {
        this.clearOnly = clearOnly;
    }

    public static void encode(PacketPathingNode msg, PacketBuffer buf) {
        buf.writeBoolean(msg.clearOnly);
    }

    public static PacketPathingNode decode(PacketBuffer buf) {
        return new PacketPathingNode(buf.readBoolean());
    }

    public static void handle(PacketPathingNode msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Phase 3 scaffold: client pathing overlay sync port is pending.
        });
        ctx.get().setPacketHandled(true);
    }
}
