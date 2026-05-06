package net.tangotek.tektopia.network.message;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.client.TekClientSyncState;
import net.tangotek.tektopia.common.TekCapabilityEvents;

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
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = context.getSender();
            if (sender == null) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        TekClientSyncState.setLicense(msg.playerId, msg.licenseData));
                return;
            }
            if (msg.playerId.equals(sender.getUUID())) {
                TekCapabilityEvents.submitLicense(sender, msg.licenseData);
            }
        });
        context.setPacketHandled(true);
    }
}
