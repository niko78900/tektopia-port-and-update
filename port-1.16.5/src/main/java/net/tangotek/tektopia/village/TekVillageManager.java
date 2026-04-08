package net.tangotek.tektopia.village;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.entities.TekGuardEntity;

public class TekVillageManager {
    private final Map<UUID, TekVillage> villages = new LinkedHashMap<>();

    public TekVillage createVillage(BlockPos center, int radius, long gameTime) {
        TekVillage village = new TekVillage(center, radius, gameTime);
        this.villages.put(village.getId(), village);
        return village;
    }

    public TekVillage upsertNearestVillage(BlockPos center, int radius, long gameTime) {
        TekVillage village = this.findNearestVillage(center).orElse(null);
        if (village == null || village.getCenter().distSqr(center) > (double) radius * (double) radius * 4.0D) {
            return this.createVillage(center, radius, gameTime);
        }
        village.updateGeometry(center, radius);
        return village;
    }

    public boolean removeVillage(UUID villageId) {
        return this.villages.remove(villageId) != null;
    }

    public void clear() {
        this.villages.clear();
    }

    public int size() {
        return this.villages.size();
    }

    public Collection<TekVillage> getVillages() {
        return Collections.unmodifiableCollection(this.villages.values());
    }

    public Optional<TekVillage> findNearestVillage(BlockPos pos) {
        return this.villages.values().stream()
                .min(Comparator.comparingDouble(v -> v.getCenter().distSqr(pos)));
    }

    public void tick(ServerWorld level) {
        for (TekVillage village : this.villages.values()) {
            AxisAlignedBB engagementBounds = village.getBounds().inflate(12.0D, 4.0D, 12.0D);
            List<MonsterEntity> hostiles = level.getEntitiesOfClass(
                    MonsterEntity.class,
                    engagementBounds,
                    this::isVillageHostile
            );
            village.setLastKnownHostileCount(hostiles.size());
            if (hostiles.isEmpty()) {
                continue;
            }

            List<TekGuardEntity> guards = level.getEntitiesOfClass(
                    TekGuardEntity.class,
                    engagementBounds,
                    guard -> guard != null && guard.isAlive()
            );

            for (TekGuardEntity guard : guards) {
                LivingEntity nearestHostile = hostiles.stream()
                        .min(Comparator.comparingDouble(h -> h.distanceToSqr(guard)))
                        .orElse(null);
                if (nearestHostile == null) {
                    continue;
                }
                guard.setTarget(nearestHostile);
                guard.getNavigation().moveTo(nearestHostile, 1.15D);
            }
        }
    }

    private boolean isVillageHostile(MonsterEntity entity) {
        if (entity == null || !entity.isAlive()) {
            return false;
        }
        if (entity instanceof ZombieEntity && !(entity instanceof ZombifiedPiglinEntity)
                || entity instanceof WitherSkeletonEntity
                || entity instanceof EvokerEntity
                || entity instanceof VexEntity
                || entity instanceof VindicatorEntity) {
            return true;
        }
        if (entity.getType().getRegistryName() == null) {
            return false;
        }
        return "tektopia".equals(entity.getType().getRegistryName().getNamespace())
                && entity.getType().getRegistryName().getPath().contains("necromancer");
    }
}
