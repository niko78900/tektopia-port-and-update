package net.tangotek.tektopia.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.registry.TekContainers;

public class TekVillagerContainer extends Container {
    public static final double MAX_INTERACTION_DISTANCE_SQR = 64.0D;

    private final int entityId;

    public TekVillagerContainer(int windowId, PlayerInventory playerInventory, int entityId) {
        super(TekContainers.TEK_VILLAGER.get(), windowId);
        this.entityId = entityId;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public static boolean isOpenFor(PlayerEntity player, int entityId) {
        return player != null
                && player.containerMenu instanceof TekVillagerContainer
                && ((TekVillagerContainer) player.containerMenu).getEntityId() == entityId;
    }

    public static boolean canInteract(PlayerEntity player, Entity entity, int entityId) {
        return player != null
                && entity instanceof TekVillagerEntity
                && entity.getId() == entityId
                && entity.isAlive()
                && entity.level == player.level
                && entity.distanceToSqr(player) < MAX_INTERACTION_DISTANCE_SQR;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        Entity entity = player.level.getEntity(this.entityId);
        return canInteract(player, entity, this.entityId);
    }
}
