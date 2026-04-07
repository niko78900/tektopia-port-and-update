package net.tangotek.tektopia.network.message;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketVillage {
    private final boolean hasVillageData;

    public PacketVillage(boolean hasVillageData) {
        this.hasVillageData = hasVillageData;
    }

    public static void encode(PacketVillage msg, PacketBuffer buf) {
        buf.writeBoolean(msg.hasVillageData);
    }

    public static PacketVillage decode(PacketBuffer buf) {
        return new PacketVillage(buf.readBoolean());
    }

    public static void handle(PacketVillage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Phase 3 scaffold: client village HUD/data sync is pending.
        });
        ctx.get().setPacketHandled(true);
    }
}
