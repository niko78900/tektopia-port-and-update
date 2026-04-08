package net.tangotek.tektopia.village;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TekVillage {
    private final UUID id;
    private BlockPos center;
    private int radius;
    private final long createdTime;
    private final Set<UUID> residents = new HashSet<>();
    private int lastKnownHostileCount;

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
        return this.center.distSqr(pos) <= (double) this.radius * (double) this.radius;
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

    public CompoundNBT save() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUUID("id", this.id);
        nbt.putInt("centerX", this.center.getX());
        nbt.putInt("centerY", this.center.getY());
        nbt.putInt("centerZ", this.center.getZ());
        nbt.putInt("radius", this.radius);
        nbt.putLong("createdTime", this.createdTime);
        nbt.putInt("lastKnownHostileCount", this.lastKnownHostileCount);

        ListNBT residentsTag = new ListNBT();
        for (UUID residentId : this.residents) {
            CompoundNBT resident = new CompoundNBT();
            resident.putUUID("id", residentId);
            residentsTag.add(resident);
        }
        nbt.put("residents", residentsTag);
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
        village.setLastKnownHostileCount(nbt.getInt("lastKnownHostileCount"));

        ListNBT residentsTag = nbt.getList("residents", 10);
        for (int i = 0; i < residentsTag.size(); i++) {
            CompoundNBT resident = residentsTag.getCompound(i);
            if (!resident.hasUUID("id")) {
                continue;
            }
            village.addResident(resident.getUUID("id"));
        }
        return village;
    }
}
