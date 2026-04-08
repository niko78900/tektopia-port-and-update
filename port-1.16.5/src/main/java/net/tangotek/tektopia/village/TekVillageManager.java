package net.tangotek.tektopia.village;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.structures.TekStructureType;
import net.tangotek.tektopia.structures.TekVillageStructure;

public class TekVillageManager {
    private static final double GUARD_RETREAT_HEALTH_RATIO = 0.35D;

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

    public void tick(ServerWorld level, TekVillageStructureManager structureManager) {
        for (TekVillage village : this.villages.values()) {
            AxisAlignedBB engagementBounds = village.getBounds().inflate(12.0D, 4.0D, 12.0D);
            List<TekVillagerEntity> villagers = level.getEntitiesOfClass(
                    TekVillagerEntity.class,
                    engagementBounds,
                    villager -> villager != null && villager.isAlive()
            );
            this.syncVillageResidents(village, villagers);

            List<MonsterEntity> hostiles = level.getEntitiesOfClass(
                    MonsterEntity.class,
                    engagementBounds,
                    this::isVillageHostile
            );
            village.setLastKnownHostileCount(hostiles.size());
            BlockPos retreatPos = this.resolveRetreatPos(structureManager).orElse(village.getCenter());

            List<TekGuardEntity> guards = level.getEntitiesOfClass(
                    TekGuardEntity.class,
                    engagementBounds,
                    guard -> guard != null && guard.isAlive()
            );

            if (hostiles.isEmpty()) {
                if (level.isDay()) {
                    for (TekGuardEntity guard : guards) {
                        if (guard.distanceToSqr(
                                retreatPos.getX() + 0.5D,
                                retreatPos.getY(),
                                retreatPos.getZ() + 0.5D
                        ) > 16.0D) {
                            guard.getNavigation().moveTo(
                                    retreatPos.getX() + 0.5D,
                                    retreatPos.getY(),
                                    retreatPos.getZ() + 0.5D,
                                    1.05D
                            );
                        }
                    }
                } else {
                    for (TekVillagerEntity villager : villagers) {
                        if (villager instanceof TekGuardEntity) {
                            continue;
                        }
                        if (villager.distanceToSqr(
                                retreatPos.getX() + 0.5D,
                                retreatPos.getY(),
                                retreatPos.getZ() + 0.5D
                        ) > 9.0D) {
                            villager.getNavigation().moveTo(
                                    retreatPos.getX() + 0.5D,
                                    retreatPos.getY(),
                                    retreatPos.getZ() + 0.5D,
                                    1.0D
                            );
                        }
                    }
                }
                continue;
            }

            for (TekGuardEntity guard : guards) {
                if (this.isLowHealth(guard)) {
                    guard.setTarget(null);
                    guard.getNavigation().moveTo(
                            retreatPos.getX() + 0.5D,
                            retreatPos.getY(),
                            retreatPos.getZ() + 0.5D,
                            1.20D
                    );
                    continue;
                }
                LivingEntity nearestHostile = hostiles.stream()
                        .min(Comparator.comparingDouble(h -> h.distanceToSqr(guard)))
                        .orElse(null);
                if (nearestHostile == null) {
                    continue;
                }
                guard.setTarget(nearestHostile);
                guard.getNavigation().moveTo(nearestHostile, 1.15D);
            }

            for (TekVillagerEntity villager : villagers) {
                if (villager instanceof TekGuardEntity) {
                    continue;
                }
                villager.setTarget(null);
                villager.getNavigation().moveTo(
                        retreatPos.getX() + 0.5D,
                        retreatPos.getY(),
                        retreatPos.getZ() + 0.5D,
                        1.08D
                );
            }
        }
    }

    private void syncVillageResidents(TekVillage village, List<TekVillagerEntity> villagers) {
        Set<UUID> seen = new HashSet<>();
        for (TekVillagerEntity villager : villagers) {
            if (!village.contains(villager.blockPosition())) {
                continue;
            }
            UUID id = villager.getUUID();
            seen.add(id);
            village.addResident(id);
        }
        Set<UUID> stale = new HashSet<>(village.getResidents());
        stale.removeAll(seen);
        for (UUID staleResident : stale) {
            village.removeResident(staleResident);
        }
    }

    private Optional<BlockPos> resolveRetreatPos(TekVillageStructureManager structureManager) {
        if (structureManager == null) {
            return Optional.empty();
        }
        TekVillageStructure townHall = structureManager.getStructure(TekStructureType.TOWNHALL).orElse(null);
        if (townHall == null || townHall.getSafeSpot() == null) {
            return Optional.empty();
        }
        return Optional.of(townHall.getSafeSpot());
    }

    private boolean isLowHealth(TekGuardEntity guard) {
        return guard.getHealth() <= guard.getMaxHealth() * GUARD_RETREAT_HEALTH_RATIO;
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
