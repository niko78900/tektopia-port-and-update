package net.tangotek.tektopia.network.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.network.TekClientSyncCache;

public class PacketTradeGuiSnapshot {
    private static final int MAX_ID_LEN = 64;
    private static final int MAX_LINES = 32;
    private static final int MAX_LINE_LEN = 512;

    private final String villageId;
    private final List<String> lines;
    private final long serverTime;

    public PacketTradeGuiSnapshot(String villageId, List<String> lines, long serverTime) {
        this.villageId = villageId == null ? "" : villageId;
        this.lines = sanitize(lines);
        this.serverTime = serverTime;
    }

    public static void encode(PacketTradeGuiSnapshot msg, PacketBuffer buf) {
        buf.writeUtf(msg.villageId, MAX_ID_LEN);
        buf.writeLong(msg.serverTime);
        buf.writeInt(msg.lines.size());
        for (String line : msg.lines) {
            buf.writeUtf(line, MAX_LINE_LEN);
        }
    }

    public static PacketTradeGuiSnapshot decode(PacketBuffer buf) {
        String villageId = buf.readUtf(MAX_ID_LEN);
        long serverTime = buf.readLong();
        int count = Math.min(MAX_LINES, Math.max(0, buf.readInt()));
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            lines.add(buf.readUtf(MAX_LINE_LEN));
        }
        return new PacketTradeGuiSnapshot(villageId, lines, serverTime);
    }

    public static void handle(PacketTradeGuiSnapshot msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> TekClientSyncCache.updateTradeGuiSnapshot(msg.villageId, msg.lines, msg.serverTime));
        ctx.get().setPacketHandled(true);
    }

    private static List<String> sanitize(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            if (line == null) {
                line = "";
            }
            result.add(line.length() <= MAX_LINE_LEN ? line : line.substring(0, MAX_LINE_LEN));
            if (result.size() >= MAX_LINES) {
                break;
            }
        }
        return Collections.unmodifiableList(result);
    }
}
