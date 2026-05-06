package net.tangotek.tektopia.village;

import java.util.concurrent.atomic.AtomicLong;

public final class TekVillagePerfStats {
    private static final AtomicLong VILLAGE_TICK_COUNT = new AtomicLong();
    private static final AtomicLong VILLAGE_TICK_NANOS = new AtomicLong();
    private static final AtomicLong VILLAGE_TICK_MAX_NANOS = new AtomicLong();
    private static final AtomicLong WORKER_SCANS = new AtomicLong();
    private static final AtomicLong STRUCTURE_SCANS = new AtomicLong();
    private static final AtomicLong STORAGE_SCANS = new AtomicLong();
    private static final AtomicLong PACKET_SNAPSHOTS = new AtomicLong();
    private static final AtomicLong HOSTILES_SEEN = new AtomicLong();
    private static final AtomicLong WORLDGEN_TESTS = new AtomicLong();

    private TekVillagePerfStats() {
    }

    public static void recordVillageTick(long nanos, int villagesTicked) {
        VILLAGE_TICK_COUNT.incrementAndGet();
        VILLAGE_TICK_NANOS.addAndGet(Math.max(0L, nanos));
        updateMax(VILLAGE_TICK_MAX_NANOS, Math.max(0L, nanos));
    }

    public static void recordWorkerScan() {
        WORKER_SCANS.incrementAndGet();
    }

    public static void recordStructureScan(int discovered) {
        STRUCTURE_SCANS.incrementAndGet();
        if (discovered > 0) {
            STRUCTURE_SCANS.addAndGet(discovered);
        }
    }

    public static void recordStorageScan() {
        STORAGE_SCANS.incrementAndGet();
    }

    public static void recordPacketSnapshot() {
        PACKET_SNAPSHOTS.incrementAndGet();
    }

    public static void recordHostilesSeen(int count) {
        if (count > 0) {
            HOSTILES_SEEN.addAndGet(count);
        }
    }

    public static void recordWorldgenTest() {
        WORLDGEN_TESTS.incrementAndGet();
    }

    public static String formatSummary() {
        long tickCount = VILLAGE_TICK_COUNT.get();
        double averageMs = tickCount == 0L ? 0.0D : nanosToMillis(VILLAGE_TICK_NANOS.get() / tickCount);
        double maxMs = nanosToMillis(VILLAGE_TICK_MAX_NANOS.get());
        return "villageTicks=" + tickCount
                + " avgMs=" + formatMillis(averageMs)
                + " maxMs=" + formatMillis(maxMs)
                + " workerScans=" + WORKER_SCANS.get()
                + " structureScans=" + STRUCTURE_SCANS.get()
                + " storageScans=" + STORAGE_SCANS.get()
                + " packetSnapshots=" + PACKET_SNAPSHOTS.get()
                + " hostilesSeen=" + HOSTILES_SEEN.get()
                + " worldgenTests=" + WORLDGEN_TESTS.get();
    }

    private static void updateMax(AtomicLong value, long candidate) {
        long current;
        do {
            current = value.get();
            if (candidate <= current) {
                return;
            }
        } while (!value.compareAndSet(current, candidate));
    }

    private static double nanosToMillis(long nanos) {
        return (double) nanos / 1_000_000.0D;
    }

    private static String formatMillis(double millis) {
        return String.format("%.3f", millis);
    }
}
