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

public class EntityAIDeliverToStorage2
extends EntityAIMoveToBlock {
    private TileEntityChest chest;
    private int dropOffTick = -1;
    private ItemStack itemCarryingCopy;
    private EntityVillagerTek villager;
    private int deliveryId = 0;

    public EntityAIDeliverToStorage2(EntityVillagerTek v) {
        super(v);
        this.villager = v;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        if (this.villager.hasVillage() && !this.itemCarryingCopy.func_190926_b()) {
            this.chest = this.villager.getVillage().getAvailableStorageChest(this.itemCarryingCopy, this.villager.func_180425_c());
            if (this.chest != null) {
                return this.chest.func_174877_v();
            }
        }
        return null;
    }

    @Override
    public boolean func_75250_a() {
        if ((this.villager.isAITick() || this.villager.isStoragePriority()) && this.villager.isDeliveryTime()) {
            this.deliveryId = this.villager.getDesireSet().getDeliveryId(this.villager, this.villager.isStoragePriority() ? 1 : 3);
            if (this.deliveryId > 0) {
                this.itemCarryingCopy = this.villager.getDesireSet().getDeliveryItemCopy(this.villager);
                return super.func_75250_a();
            }
        }
        return false;
    }

    @Override
    public boolean func_75253_b() {
        if (this.dropOffTick >= 0) {
            return true;
        }
        if (!this.villager.getDesireSet().isDeliveryMatch(this.deliveryId)) {
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
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    public void func_75249_e() {
        super.func_75249_e();
        if (this.itemCarryingCopy != null) {
            this.villager.equipActionItem(this.itemCarryingCopy);
        }
    }

    public boolean func_75252_g() {
        return this.dropOffTick <= 0;
    }

    @Override
    public void func_75246_d() {
        super.func_75246_d();
        --this.dropOffTick;
        if (this.dropOffTick == 0) {
            this.dropOffItems();
            this.villager.throttledSadness(-1);
            this.closeChest();
            this.dropOffTick = -1;
        }
    }

    @Override
    public void func_75251_c() {
        this.dropOffTick = -1;
        this.deliveryId = 0;
        super.func_75251_c();
        if (this.itemCarryingCopy != null) {
            this.villager.unequipActionItem(this.itemCarryingCopy);
            this.itemCarryingCopy = null;
            this.villager.setStoragePriority();
        }
    }

    @Override
    protected void onArrival() {
        super.onArrival();
        this.dropOffTick = 30;
        this.openChest();
    }

    protected void dropOffItems() {
        if (this.isNearDestination(5.0) && !this.chest.func_145837_r()) {
            this.villager.setStoragePriority();
            if (!this.villager.getDesireSet().deliverItems(this.villager, this.chest, this.deliveryId)) {
                this.villager.getDesireSet().forceUpdate();
            }
        }
    }

    private void openChest() {
        TileEntity te = this.villager.field_70170_p.func_175625_s(this.destinationPos);
        if (te instanceof TileEntityChest) {
            TileEntityChest tileEntityChest = (TileEntityChest)te;
            EntityPlayer p = this.villager.field_70170_p.func_190525_a((double)this.destinationPos.func_177958_n(), (double)this.destinationPos.func_177956_o(), (double)this.destinationPos.func_177952_p(), -1.0, EntitySelectors.field_180132_d);
            if (p != null) {
                tileEntityChest.func_174889_b(p);
            }
        }
    }

    private void closeChest() {
        TileEntity te = this.villager.field_70170_p.func_175625_s(this.destinationPos);
        if (te instanceof TileEntityChest) {
            TileEntityChest tileEntityChest = (TileEntityChest)te;
            EntityPlayer p = this.villager.field_70170_p.func_190525_a((double)this.destinationPos.func_177958_n(), (double)this.destinationPos.func_177956_o(), (double)this.destinationPos.func_177952_p(), -1.0, EntitySelectors.field_180132_d);
            if (p != null) {
                tileEntityChest.func_174886_c(p);
            }
        }
    }
}

