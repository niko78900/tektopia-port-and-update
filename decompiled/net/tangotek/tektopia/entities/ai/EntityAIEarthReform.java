/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityDruid;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.structures.VillageStructureMineshaft;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityAIEarthReform
extends EntityAIMoveToBlock {
    private boolean arrived = false;
    private EntityDruid druid = null;
    private int castTime = 0;
    private final int CAST_TIME = 80;
    private final EntityVillagerTek villager;
    private VillageStructureMineshaft mineshaft;

    public EntityAIEarthReform(EntityVillagerTek entityIn) {
        super(entityIn);
        this.villager = entityIn;
        this.druid = (EntityDruid)entityIn;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick("cast_earth_reform") && this.villager.hasVillage() && this.villager.isWorkTime() && this.druid.isEarthReformTime()) {
            this.mineshaft = this.villager.getVillage().requestMineshaft(this.villager, m -> m.getTunnelLength() > 3 && m.getPlayersInside().isEmpty() && m.getDruid() == null, (C, B) -> C.getTunnelLength() > B.getTunnelLength() && C.getTunnelLength() > 10);
            if (this.mineshaft != null) {
                return super.func_75250_a();
            }
        }
        return false;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        BlockPos destBlock = this.mineshaft.getDoorOutside(2);
        if (!this.isWalkable(destBlock, this.villager)) {
            destBlock = this.mineshaft.getDoorOutside(1);
        }
        return destBlock;
    }

    @Override
    protected boolean isNearWalkPos() {
        if (this.getWalkPos() != null) {
            return this.getWalkPos().func_177957_d(this.villager.getX(), this.villager.getY(), this.villager.getZ()) < 0.5;
        }
        return super.isNearWalkPos();
    }

    @Override
    protected BlockPos findWalkPos() {
        return this.destinationPos;
    }

    @Override
    protected void onArrival() {
        if (!this.arrived) {
            this.tryCast();
        }
        this.arrived = true;
        super.onArrival();
    }

    @Override
    public void func_75249_e() {
        this.mineshaft.setDruid(this.druid);
        super.func_75249_e();
    }

    public boolean func_75252_g() {
        return this.castTime <= 0;
    }

    protected void tryCast() {
        if (this.villager.func_70089_S()) {
            this.mineshaft.updateOccupants();
            if (this.mineshaft.getTunnelMiner() == null) {
                this.castTime = 80;
                this.villager.playServerAnimation("villager_cast_forward");
                this.druid.setCastingEarthReform(this.mineshaft.getMiningPos());
                this.villager.modifyHunger(-8);
                this.villager.func_70661_as().func_75499_g();
                this.villager.addJob(new TickJob(4, 0, false, () -> this.villager.playSound(ModSoundEvents.earthRumble)));
            }
        }
    }

    @Override
    public void func_75246_d() {
        --this.castTime;
        if (this.castTime > 0) {
            this.villager.faceLocation(this.mineshaft.getMiningPos().func_177958_n(), this.mineshaft.getMiningPos().func_177952_p(), 30.0f);
        }
        if (this.castTime == 38) {
            this.mineshaft.updateOccupants();
            if (this.mineshaft.getTunnelMiner() == null) {
                this.villager.tryAddSkill(ProfessionType.DRUID, 9);
                this.mineshaft.regrow(this.villager);
                for (int i = 0; i < 3; ++i) {
                    if (this.villager.func_70681_au().nextInt(100) >= this.villager.getSkill(ProfessionType.DRUID)) continue;
                    this.mineshaft.regrow(this.villager);
                }
            }
        }
        super.func_75246_d();
    }

    @Override
    public void func_75251_c() {
        this.villager.stopServerAnimation("villager_cast_forward");
        this.arrived = false;
        this.castTime = 0;
        this.druid.setCastingEarthReform(null);
        this.mineshaft.setDruid(null);
        super.func_75251_c();
    }

    @Override
    public boolean func_75253_b() {
        if (this.mineshaft.getTunnelMiner() != null) {
            return false;
        }
        if (this.castTime > 0) {
            return true;
        }
        return super.func_75253_b();
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }
}

