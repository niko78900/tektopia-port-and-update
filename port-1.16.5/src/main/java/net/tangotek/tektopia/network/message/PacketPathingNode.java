package net.tangotek.tektopia.network.message;

import java.util.function.Supplier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.client.TekClientSyncState;

public class PacketPathingNode {
    private final boolean clearOnly;
    private final CompoundNBT payload;

    public PacketPathingNode(boolean clearOnly) {
        this(clearOnly, new CompoundNBT());
    }

    public PacketPathingNode(boolean clearOnly, CompoundNBT payload) {
        this.clearOnly = clearOnly;
        this.payload = payload == null ? new CompoundNBT() : payload.copy();
    }

    public static void encode(PacketPathingNode msg, PacketBuffer buf) {
        buf.writeBoolean(msg.clearOnly);
        buf.writeNbt(msg.payload);
    }

    public static PacketPathingNode decode(PacketBuffer buf) {
        boolean clearOnly = buf.readBoolean();
        CompoundNBT payload = buf.readNbt();
        return new PacketPathingNode(clearOnly, payload);
    }

    public static void handle(PacketPathingNode msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    TekClientSyncState.setPathingSnapshot(msg.clearOnly, msg.payload));
        });
        ctx.get().setPacketHandled(true);
    }
}
