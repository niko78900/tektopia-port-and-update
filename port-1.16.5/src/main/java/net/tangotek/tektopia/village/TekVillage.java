package net.tangotek.tektopia.village;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TekVillage {
    private static final int SAVE_VERSION = 3;
    private final UUID id;
    private BlockPos center;
    private int radius;
    private final long createdTime;
    private final Set<UUID> residents = new HashSet<>();
    private final Map<String, Integer> professionCounts = new LinkedHashMap<>();
    private String source = "manual";
    private UUID ownerId;
    private int tokenPurchaseCount;
    private int villagerDeathCount;
    private int visitorSpawnCount;
    private int raidLevel;
    private boolean raidActive;
    private long lastDailyTick = -1L;
    private long nextMerchantTick = -1L;
    private long nextNomadTick = -1L;
    private long nextRaidTick = -1L;
    private int lastKnownHostileCount;
    private BlockPos lastAlertPos;
    private long lastAlertTime = -1L;

    public TekVillage(BlockPos center, int radius, long createdTime) {
        this(UUID.randomUUID(), center, radius, createdTime);
    }

    public TekVillage(UUID id, BlockPos center, int radius, long createdTime) {
        this.id = id;
        this.center = center.immutable();
        this.radius = Math.max(8, radius);
        this.createdTime = createdTime;
    }

    public UUID getId() {
        return this.id;
    }

    public BlockPos getCenter() {
        return this.center;
    }

    public int getRadius() {
        return this.radius;
    }

    public long getCreatedTime() {
        return this.createdTime;
    }

    public int getLastKnownHostileCount() {
        return this.lastKnownHostileCount;
    }

    public void setLastKnownHostileCount(int count) {
        this.lastKnownHostileCount = Math.max(0, count);
    }

    public void updateGeometry(BlockPos center, int radius) {
        this.center = center.immutable();
        this.radius = Math.max(8, radius);
    }

    public AxisAlignedBB getBounds() {
        return new AxisAlignedBB(
                this.center.getX() - this.radius, this.center.getY() - 16, this.center.getZ() - this.radius,
                this.center.getX() + this.radius, this.center.getY() + 32, this.center.getZ() + this.radius
        );
    }

    public boolean contains(BlockPos pos) {
        return Math.abs(pos.getX() - this.center.getX()) <= this.radius
                && Math.abs(pos.getZ() - this.center.getZ()) <= this.radius
                && pos.getY() >= this.center.getY() - 32
                && pos.getY() <= this.center.getY() + 48;
    }

    public void addResident(UUID residentId) {
        this.residents.add(residentId);
    }

    public void removeResident(UUID residentId) {
        this.residents.remove(residentId);
    }

    public Set<UUID> getResidents() {
        return Collections.unmodifiableSet(this.residents);
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source == null || source.trim().isEmpty() ? "manual" : source.trim();
    }

    public UUID getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public int getTokenPurchaseCount() {
        return this.tokenPurchaseCount;
    }

    public void incrementTokenPurchaseCount() {
        this.tokenPurchaseCount++;
    }

    public int getTokenPriceTier() {
        return this.tokenPurchaseCount / 5;
    }

    public int getVillagerDeathCount() {
        return this.villagerDeathCount;
    }

    public void recordVillagerDeath() {
        this.villagerDeathCount++;
    }

    public int getVisitorSpawnCount() {
        return this.visitorSpawnCount;
    }

    public void recordVisitorSpawn() {
        this.visitorSpawnCount++;
    }

    public int getRaidLevel() {
        return this.raidLevel;
    }

    public void setRaidLevel(int raidLevel) {
        this.raidLevel = Math.max(0, raidLevel);
    }

    public boolean isRaidActive() {
        return this.raidActive;
    }

    public void setRaidActive(boolean raidActive) {
        this.raidActive = raidActive;
    }

    public long getLastDailyTick() {
        return this.lastDailyTick;
    }

    public void setLastDailyTick(long lastDailyTick) {
        this.lastDailyTick = lastDailyTick;
    }

    public long getNextMerchantTick() {
        return this.nextMerchantTick;
    }

    public void setNextMerchantTick(long nextMerchantTick) {
        this.nextMerchantTick = nextMerchantTick;
    }

    public long getNextNomadTick() {
        return this.nextNomadTick;
    }

    public void setNextNomadTick(long nextNomadTick) {
        this.nextNomadTick = nextNomadTick;
    }

    public long getNextRaidTick() {
        return this.nextRaidTick;
    }

    public void setNextRaidTick(long nextRaidTick) {
        this.nextRaidTick = nextRaidTick;
    }

    public Map<String, Integer> getProfessionCounts() {
        return Collections.unmodifiableMap(this.professionCounts);
    }

    public void setProfessionCount(String profession, int count) {
        if (profession == null || profession.trim().isEmpty()) {
            return;
        }
        if (count <= 0) {
            this.professionCounts.remove(profession);
            return;
        }
        this.professionCounts.put(profession, count);
    }

    public BlockPos getVisitorArrivalPoint(int cornerIndex) {
        int index = Math.floorMod(cornerIndex, 4);
        int dx = index == 0 || index == 3 ? -this.radius : this.radius;
        int dz = index == 0 || index == 1 ? -this.radius : this.radius;
        return new BlockPos(this.center.getX() + dx, this.center.getY(), this.center.getZ() + dz);
    }

    public void setAlert(BlockPos alertPos, long alertTime) {
        if (alertPos != null) {
            this.lastAlertPos = alertPos.immutable();
        }
        this.lastAlertTime = alertTime;
    }

    public boolean hasActiveAlert(long currentTime, long durationTicks) {
        return this.lastAlertPos != null && this.lastAlertTime >= 0L && currentTime - this.lastAlertTime <= durationTicks;
    }

    public BlockPos getLastAlertPos() {
        return this.lastAlertPos;
    }

    public long getLastAlertTime() {
        return this.lastAlertTime;
    }

    public void clearAlert() {
        this.lastAlertPos = null;
        this.lastAlertTime = -1L;
    }

    public CompoundNBT save() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("saveVersion", SAVE_VERSION);
        nbt.putUUID("id", this.id);
        nbt.putInt("centerX", this.center.getX());
        nbt.putInt("centerY", this.center.getY());
        nbt.putInt("centerZ", this.center.getZ());
        nbt.putInt("radius", this.radius);
        nbt.putLong("createdTime", this.createdTime);
        nbt.putString("source", this.source);
        if (this.ownerId != null) {
            nbt.putUUID("ownerId", this.ownerId);
        }
        nbt.putInt("tokenPurchaseCount", this.tokenPurchaseCount);
        nbt.putInt("villagerDeathCount", this.villagerDeathCount);
        nbt.putInt("visitorSpawnCount", this.visitorSpawnCount);
        nbt.putInt("raidLevel", this.raidLevel);
        nbt.putBoolean("raidActive", this.raidActive);
        nbt.putLong("lastDailyTick", this.lastDailyTick);
        nbt.putLong("nextMerchantTick", this.nextMerchantTick);
        nbt.putLong("nextNomadTick", this.nextNomadTick);
        nbt.putLong("nextRaidTick", this.nextRaidTick);
        nbt.putInt("lastKnownHostileCount", this.lastKnownHostileCount);
        if (this.lastAlertPos != null) {
            nbt.putLong("lastAlertPos", this.lastAlertPos.asLong());
        }
        nbt.putLong("lastAlertTime", this.lastAlertTime);

        ListNBT residentsTag = new ListNBT();
        for (UUID residentId : this.residents) {
            CompoundNBT resident = new CompoundNBT();
            resident.putUUID("id", residentId);
            residentsTag.add(resident);
        }
        nbt.put("residents", residentsTag);
        CompoundNBT professionsTag = new CompoundNBT();
        for (Map.Entry<String, Integer> entry : this.professionCounts.entrySet()) {
            professionsTag.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("professionCounts", professionsTag);
        return nbt;
    }

    public static TekVillage load(CompoundNBT nbt) {
        UUID id = nbt.hasUUID("id") ? nbt.getUUID("id") : UUID.randomUUID();
        BlockPos center = new BlockPos(
                nbt.getInt("centerX"),
                nbt.getInt("centerY"),
                nbt.getInt("centerZ")
        );
        TekVillage village = new TekVillage(id, center, nbt.getInt("radius"), nbt.getLong("createdTime"));
        village.setSource(nbt.contains("source", 8) ? nbt.getString("source") : "manual");
        if (nbt.hasUUID("ownerId")) {
            village.ownerId = nbt.getUUID("ownerId");
        }
        village.tokenPurchaseCount = Math.max(0, nbt.getInt("tokenPurchaseCount"));
        village.villagerDeathCount = Math.max(0, nbt.getInt("villagerDeathCount"));
        village.visitorSpawnCount = Math.max(0, nbt.getInt("visitorSpawnCount"));
        village.raidLevel = Math.max(0, nbt.getInt("raidLevel"));
        village.raidActive = nbt.getBoolean("raidActive");
        village.lastDailyTick = nbt.contains("lastDailyTick", 4) ? nbt.getLong("lastDailyTick") : -1L;
        village.nextMerchantTick = nbt.contains("nextMerchantTick", 4) ? nbt.getLong("nextMerchantTick") : -1L;
        village.nextNomadTick = nbt.contains("nextNomadTick", 4) ? nbt.getLong("nextNomadTick") : -1L;
        village.nextRaidTick = nbt.contains("nextRaidTick", 4) ? nbt.getLong("nextRaidTick") : -1L;
        village.setLastKnownHostileCount(nbt.getInt("lastKnownHostileCount"));
        if (nbt.contains("lastAlertPos", 4)) {
            village.lastAlertPos = BlockPos.of(nbt.getLong("lastAlertPos"));
        }
        village.lastAlertTime = nbt.getLong("lastAlertTime");

        ListNBT residentsTag = nbt.getList("residents", 10);
        for (int i = 0; i < residentsTag.size(); i++) {
            CompoundNBT resident = residentsTag.getCompound(i);
            if (!resident.hasUUID("id")) {
                continue;
            }
            village.addResident(resident.getUUID("id"));
        }
        if (nbt.contains("professionCounts", 10)) {
            CompoundNBT professionsTag = nbt.getCompound("professionCounts");
            for (String key : professionsTag.getAllKeys()) {
                village.setProfessionCount(key, professionsTag.getInt(key));
            }
        }
        return village;
    }
}
