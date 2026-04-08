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
    }
}
