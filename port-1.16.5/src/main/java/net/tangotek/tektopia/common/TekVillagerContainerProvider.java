package net.tangotek.tektopia.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.tangotek.tektopia.entities.TekVillagerEntity;

public class TekVillagerContainerProvider implements INamedContainerProvider {
    private final TekVillagerEntity villager;

    public TekVillagerContainerProvider(TekVillagerEntity villager) {
        this.villager = villager;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("TekTopia Villager");
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        return new TekVillagerContainer(windowId, playerInventory, this.villager.getId());
    }
}
