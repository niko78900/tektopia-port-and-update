/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.util.EntitySelectors
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.storage.ItemDesire;

public class EntityAIRetrieveFromStorage2
extends EntityAIMoveToBlock {
    private TileEntityChest chest = null;
    protected ItemDesire retrieveDesire = null;
    protected final EntityVillagerTek villager;
    private int pickUpTick = 0;
    protected boolean autoCheck = false;
    protected ItemStack itemTaken = null;

    public EntityAIRetrieveFromStorage2(EntityVillagerTek v) {
        super(v);
        this.villager = v;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        if (this.chest != null) {
            return this.chest.func_174877_v();
        }
        return null;
    }

    @Override
    protected void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    public boolean func_75250_a() {
        if ((this.villager.isAITick() || this.villager.isStoragePriority()) && this.villager.hasVillage()) {
            this.autoCheck = false;
            this.retrieveDesire = this.villager.getDesireSet().getNeededDesire(this.villager);
            if (this.retrieveDesire != null && this.villager.getInventory().hasSlotFree()) {
                this.chest = this.retrieveDesire.getPickUpChest(this.villager);
                if (this.chest != null) {
                    return super.func_75250_a();
                }
            }
        }
        return false;
    }

    public boolean func_75252_g() {
        return this.pickUpTick <= 0;
    }

    @Override
    public void func_75249_e() {
        super.func_75249_e();
    }

    @Override
    public boolean func_75253_b() {
        if (this.pickUpTick > 0) {
            return true;
        }
        if (this.chest != this.retrieveDesire.getPickUpChest(this.villager)) {
            return false;
        }
        return super.func_75253_b();
    }

    @Override
    protected BlockPos findWalkPos() {
        BlockPos result = super.findWalkPos();
        if (result == null) {
            BlockPos pos = this.destinationPos;
            BlockPos testPos = pos.func_177985_f(2);
            if (this.isWalkable(testPos, this.navigator)) {
                return testPos;
            }
            testPos = pos.func_177965_g(2);
            if (this.isWalkable(testPos, this.navigator)) {
                return testPos;
            }
            testPos = pos.func_177964_d(2);
            if (this.isWalkable(testPos, this.navigator)) {
                return testPos;
            }
            testPos = pos.func_177970_e(2);
            if (this.isWalkable(testPos, this.navigator)) {
                return testPos;
            }
        }
        return result;
    }

    @Override
    public void func_75246_d() {
        super.func_75246_d();
        --this.pickUpTick;
        if (this.pickUpTick == 15) {
            this.pickUpItems();
        } else if (this.pickUpTick == 1) {
            this.closeChest();
        }
    }

    @Override
    public void func_75251_c() {
        this.pickUpTick = 0;
        if (this.itemTaken != null) {
            this.villager.unequipActionItem(this.itemTaken);
            this.itemTaken = null;
            this.villager.setStoragePriority();
        }
        super.func_75251_c();
    }

    @Override
    protected void onArrival() {
        super.onArrival();
        this.pickUpTick = 30;
        this.openChest();
    }

    protected void pickUpItems() {
        if (this.isNearDestination(5.0) && !this.chest.func_145837_r()) {
            this.itemTaken = this.retrieveDesire.pickUpItems(this.villager);
            if (this.itemTaken != ItemStack.field_190927_a) {
                this.itemAcquired(this.itemTaken);
            } else {
                this.villager.getDesireSet().forceUpdate();
            }
        }
    }

    protected boolean itemAcquired(ItemStack itemStack) {
        this.autoCheck = true;
        this.villager.setStoragePriority();
        boolean result = this.villager.getInventory().func_174894_a(itemStack) == ItemStack.field_190927_a;
        return result;
    }

    private void openChest() {
        TileEntity te = this.villager.field_70170_p.func_175625_s(this.destinationPos);
        if (te instanceof TileEntityChest) {
            TileEntityChest tileEntityChest = (TileEntityChest)te;
            EntityPlayer p = this.villager.field_70170_p.field_73010_i.stream().filter(EntitySelectors.field_180132_d).findFirst().orElse(null);
            if (p != null) {
                tileEntityChest.func_174889_b(p);
            }
        }
    }

    private void closeChest() {
        TileEntity te = this.villager.field_70170_p.func_175625_s(this.destinationPos);
        if (te instanceof TileEntityChest) {
            TileEntityChest tileEntityChest = (TileEntityChest)te;
            EntityPlayer p = this.villager.field_70170_p.field_73010_i.stream().filter(EntitySelectors.field_180132_d).findFirst().orElse(null);
            if (p != null) {
                tileEntityChest.func_174886_c(p);
            }
        }
    }
}

