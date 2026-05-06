package net.tangotek.tektopia.entities;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.tangotek.tektopia.registry.TekEntities;

public final class TekEntityEvents {
    private TekEntityEvents() {
    }

    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(TekEntities.TEK_GUARD.get(), TekGuardEntity.createAttributes().build());
        event.put(TekEntities.TEK_FARMER.get(), TekFarmerEntity.createAttributes().build());
        event.put(TekEntities.TEK_BLACKSMITH.get(), TekBlacksmithEntity.createAttributes().build());
        event.put(TekEntities.TEK_MINER.get(), TekWorkerEntity.createWorkerAttributes(22.0D, 0.27D).build());
        event.put(TekEntities.TEK_LUMBERJACK.get(), TekWorkerEntity.createWorkerAttributes(22.0D, 0.28D).build());
        event.put(TekEntities.TEK_CHEF.get(), TekWorkerEntity.createWorkerAttributes(20.0D, 0.27D).build());
        event.put(TekEntities.TEK_RANCHER.get(), TekWorkerEntity.createWorkerAttributes(22.0D, 0.28D).build());
        event.put(TekEntities.TEK_BUTCHER.get(), TekWorkerEntity.createWorkerAttributes(22.0D, 0.27D).build());
        event.put(TekEntities.TEK_MERCHANT.get(), TekWorkerEntity.createWorkerAttributes(24.0D, 0.27D).build());
        event.put(TekEntities.TEK_NOMAD.get(), TekWorkerEntity.createWorkerAttributes(20.0D, 0.29D).build());
        event.put(TekEntities.TEK_CLERIC.get(), TekWorkerEntity.createWorkerAttributes(22.0D, 0.27D).build());
        event.put(TekEntities.TEK_TEACHER.get(), TekWorkerEntity.createWorkerAttributes(20.0D, 0.27D).build());
        event.put(TekEntities.TEK_ENCHANTER.get(), TekWorkerEntity.createWorkerAttributes(20.0D, 0.26D).build());
        event.put(TekEntities.TEK_DRUID.get(), TekWorkerEntity.createWorkerAttributes(22.0D, 0.28D).build());
        event.put(TekEntities.TEK_BARD.get(), TekWorkerEntity.createWorkerAttributes(20.0D, 0.29D).build());
        event.put(TekEntities.TEK_ARCHITECT.get(), TekWorkerEntity.createWorkerAttributes(22.0D, 0.27D).build());
        event.put(TekEntities.TEK_CHILD.get(), TekWorkerEntity.createWorkerAttributes(12.0D, 0.31D).build());
        event.put(TekEntities.TEK_NITWIT.get(), TekWorkerEntity.createWorkerAttributes(20.0D, 0.27D).build());
        event.put(TekEntities.TEK_NECROMANCER.get(), TekNecromancerEntity.createAttributes().build());
    }
}
