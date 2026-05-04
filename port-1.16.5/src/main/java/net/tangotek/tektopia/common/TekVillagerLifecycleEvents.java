package net.tangotek.tektopia.common;

import java.util.List;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tangotek.tektopia.entities.TekChildEntity;
import net.tangotek.tektopia.entities.TekNecromancerEntity;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageRuntime;
import net.tangotek.tektopia.village.TekVillageManager;

public class TekVillagerLifecycleEvents {
    private static final long NECROMANCER_SUPPRESS_TICKS = 1200L;

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof TekVillagerEntity)
                || event.getEntityLiving().level.isClientSide()
                || !(event.getEntityLiving().level instanceof ServerWorld)) {
            return;
        }
        TekVillagerEntity dead = (TekVillagerEntity) event.getEntityLiving();
        ServerWorld level = (ServerWorld) dead.level;
        TekVillageRuntime runtime = TekVillageRuntime.get();
        TekVillageManager manager = runtime.villageManagerFor(level);
        TekVillage village = manager.findNearestVillage(dead.blockPosition()).orElse(null);
        if (village == null) {
            return;
        }

        village.recordVillagerDeath();
        village.removeResident(dead.getUUID());
        village.setAlert(dead.blockPosition(), level.getGameTime());
        this.applyWitnessSadness(level, dead, dead instanceof TekChildEntity);
        this.suppressNearbyNecromancers(level, village);
        runtime.saveRuntime(level);
    }

    private void applyWitnessSadness(ServerWorld level, TekVillagerEntity dead, boolean childDeath) {
        int penalty = childDeath ? 14 : 7;
        String thought = childDeath ? "saw_child_death" : "saw_villager_death";
        List<TekVillagerEntity> witnesses = level.getEntitiesOfClass(
                TekVillagerEntity.class,
                dead.getBoundingBox().inflate(36.0D, 12.0D, 36.0D),
                villager -> villager != null && villager.isAlive() && villager.getId() != dead.getId()
        );
        for (TekVillagerEntity witness : witnesses) {
            witness.setHappy(witness.getHappy() - penalty);
            witness.setThoughtKey(thought);
        }
    }

    private void suppressNearbyNecromancers(ServerWorld level, TekVillage village) {
        long suppressUntil = level.getGameTime() + NECROMANCER_SUPPRESS_TICKS;
        List<MonsterEntity> monsters = level.getEntitiesOfClass(
                MonsterEntity.class,
                village.getBounds().inflate(64.0D, 16.0D, 64.0D),
                monster -> monster instanceof TekNecromancerEntity
        );
        for (MonsterEntity monster : monsters) {
            monster.getPersistentData().putLong(TekNecromancerEntity.ABILITY_SUPPRESSED_UNTIL_TAG, suppressUntil);
        }
    }
}
