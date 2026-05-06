package net.tangotek.tektopia.network.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.network.TekClientSyncCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class PacketVillagerGuiSnapshot {
    private static final int MAX_LINES = 16;
    private static final int MAX_LINE_LEN = 512;

    private final int entityId;
    private final List<String> lines;
    private final long serverTime;

    public PacketVillagerGuiSnapshot(int entityId, List<String> lines, long serverTime) {
        this.entityId = entityId;
        this.lines = sanitize(lines);
        this.serverTime = serverTime;
    }

    public static void encode(PacketVillagerGuiSnapshot msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
        buf.writeLong(msg.serverTime);
        buf.writeInt(msg.lines.size());
        for (String line : msg.lines) {
            buf.writeUtf(line, MAX_LINE_LEN);
        }
    }

    public static PacketVillagerGuiSnapshot decode(PacketBuffer buf) {
        int entityId = buf.readInt();
        long serverTime = buf.readLong();
        int count = Math.min(MAX_LINES, Math.max(0, buf.readInt()));
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            lines.add(buf.readUtf(MAX_LINE_LEN));
        }
        return new PacketVillagerGuiSnapshot(entityId, lines, serverTime);
    }

    public static void handle(PacketVillagerGuiSnapshot msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            TekClientSyncCache.updateGuiSnapshot(msg.entityId, msg.lines, msg.serverTime);
        });
        ctx.get().setPacketHandled(true);
    }

    private static List<String> sanitize(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        int limit = Math.min(MAX_LINES, lines.size());
        for (int i = 0; i < limit; i++) {
            String line = lines.get(i);
            if (line == null) {
                line = "";
            }
            result.add(line.length() <= MAX_LINE_LEN ? line : line.substring(0, MAX_LINE_LEN));
        }
        return Collections.unmodifiableList(result);
    }
}
