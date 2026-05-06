package net.tangotek.tektopia.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.tangotek.tektopia.registry.TekContainers;

public class TekVillagerContainer extends Container {
    private final int entityId;

    public TekVillagerContainer(int windowId, int entityId) {
        super(TekContainers.TEK_VILLAGER.get(), windowId);
        this.entityId = entityId;
    }

    public TekVillagerContainer(int windowId, PlayerInventory inventory, PacketBuffer buffer) {
        this(windowId, buffer.readInt());
    }

    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return player != null && player.isAlive();
    }
}
