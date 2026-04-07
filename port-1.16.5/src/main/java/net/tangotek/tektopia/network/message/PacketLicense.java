package net.tangotek.tektopia.network.message;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketLicense {
    private static final int MAX_LICENSE_LEN = 2048;
    private final UUID playerId;
    private final String licenseData;

    public PacketLicense(UUID playerId, String licenseData) {
        this.playerId = playerId;
        this.licenseData = licenseData;
    }

    public static void encode(PacketLicense msg, PacketBuffer buf) {
        buf.writeUUID(msg.playerId);
        buf.writeUtf(msg.licenseData, MAX_LICENSE_LEN);
    }

    public static PacketLicense decode(PacketBuffer buf) {
        return new PacketLicense(buf.readUUID(), buf.readUtf(MAX_LICENSE_LEN));
    }

    public static void handle(PacketLicense msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Phase 3 scaffold: capability-based license sync port is pending.
        });
        ctx.get().setPacketHandled(true);
    }
}
