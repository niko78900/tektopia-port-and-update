package net.tangotek.tektopia.network.message;

import java.util.function.Supplier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.client.TekClientSyncState;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.structures.TekVillageStructure;
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageStructureManager;

public class PacketVillage {
    private final CompoundNBT snapshot;

    public PacketVillage(boolean hasVillageData) {
        this.snapshot = new CompoundNBT();
        this.snapshot.putBoolean("hasVillageData", hasVillageData);
    }

    public PacketVillage(CompoundNBT snapshot) {
        this.snapshot = snapshot == null ? new CompoundNBT() : snapshot.copy();
    }

    public static void encode(PacketVillage msg, PacketBuffer buf) {
        buf.writeNbt(msg.snapshot);
    }

    public static PacketVillage decode(PacketBuffer buf) {
        return new PacketVillage(buf.readNbt());
    }

    public static void handle(PacketVillage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    TekClientSyncState.setVillageSnapshot(msg.snapshot));
        });
        ctx.get().setPacketHandled(true);
    }

    public static PacketVillage createSnapshot(ServerWorld level, TekVillage village, TekVillageStructureManager structureManager) {
        CompoundNBT snapshot = new CompoundNBT();
        snapshot.putBoolean("hasVillageData", village != null);
        snapshot.putLong("gameTime", level.getGameTime());
        if (village == null) {
            return new PacketVillage(snapshot);
        }

        snapshot.putUUID("id", village.getId());
        putBlockPos(snapshot, "center", village.getCenter());
        snapshot.putInt("radius", village.getRadius());
        snapshot.putInt("hostiles", village.getLastKnownHostileCount());
        snapshot.putInt("residents", village.getResidents().size());
        snapshot.putLong("createdTime", village.getCreatedTime());
        snapshot.putLong("lastAlertTime", village.getLastAlertTime());
        if (village.getLastAlertPos() != null) {
            putBlockPos(snapshot, "lastAlertPos", village.getLastAlertPos());
        }

        ListNBT structures = new ListNBT();
        if (structureManager != null) {
            for (TekVillageStructure structure : structureManager.getStructures()) {
                CompoundNBT tag = new CompoundNBT();
                tag.putString("type", structure.getType().name());
                putBlockPos(tag, "doorInside", structure.getDoorInside());
                if (structure.getSafeSpot() != null) {
                    putBlockPos(tag, "safeSpot", structure.getSafeSpot());
                }
                tag.putInt("floorTiles", structure.getFloorTileCount());
                tag.putDouble("averageCeilingHeight", structure.getAverageCeilingHeight());
                structures.add(tag);
            }
        }
        snapshot.put("structures", structures);

        ListNBT workers = new ListNBT();
        for (TekVillagerEntity villager : level.getEntitiesOfClass(TekVillagerEntity.class, village.getBounds().inflate(12.0D, 4.0D, 12.0D), LivingEntity::isAlive)) {
            CompoundNBT worker = new CompoundNBT();
            worker.putUUID("id", villager.getUUID());
            worker.putInt("entityId", villager.getId());
            worker.putString("type", villager.getType().getRegistryName() == null ? villager.getType().toString() : villager.getType().getRegistryName().toString());
            worker.putString("profession", villager.getProfessionType().getSerializedName());
            worker.putString("status", villager.getWorkerStatus().getSerializedName());
            worker.putString("thought", villager.getThoughtKey());
            worker.putString("itemThought", villager.getItemThoughtId());
            putBlockPos(worker, "pos", villager.blockPosition());
            workers.add(worker);
        }
        snapshot.put("workers", workers);
        return new PacketVillage(snapshot);
    }

    private static void putBlockPos(CompoundNBT tag, String key, BlockPos pos) {
        tag.putLong(key, pos.asLong());
    }
}
