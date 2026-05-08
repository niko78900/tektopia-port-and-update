package net.tangotek.tektopia.client.gui;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.registry.TekContainers;

public class TekVillagerContainer extends Container {
    private final int entityId;

    public TekVillagerContainer(int windowId, PlayerInventory playerInventory, int entityId) {
        super(TekContainers.TEK_VILLAGER.get(), windowId);
        this.entityId = entityId;
    }

    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        Entity entity = player.level.getEntity(this.entityId);
        return entity instanceof TekVillagerEntity && entity.isAlive() && entity.distanceToSqr(player) < 64.0D;
    }
}
