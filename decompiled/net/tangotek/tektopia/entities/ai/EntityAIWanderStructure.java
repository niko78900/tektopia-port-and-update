/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockHorizontal
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.structures.VillageStructure;

public class EntityAIWanderStructure
extends EntityAIMoveToBlock {
    private final int happyChance;
    private final Function<EntityVillagerTek, VillageStructure> wanderFunc;
    private BlockPos chairPos;
    private int sitTime = 0;
    private boolean forceExecute = false;
    private VillageStructure structure;
    protected final EntityVillagerTek villager;
    private final Predicate<EntityVillagerTek> shouldPred;

    public EntityAIWanderStructure(EntityVillagerTek v, Function<EntityVillagerTek, VillageStructure> whereFunc, Predicate<EntityVillagerTek> shouldPred, int happyChance) {
        super(v);
        this.villager = v;
        this.shouldPred = shouldPred;
        this.happyChance = happyChance;
        this.wanderFunc = whereFunc;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.hasVillage() && this.shouldPred.test(this.villager) && this.villager.func_70681_au().nextInt(2) == 0) {
            return super.func_75250_a();
        }
        return false;
    }

    @Override
    public boolean func_75253_b() {
        if (this.sitTime > 0) {
            return true;
        }
        return super.func_75253_b();
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    public void func_75249_e() {
        this.forceExecute = false;
        super.func_75249_e();
    }

    @Override
    protected BlockPos findWalkPos() {
        return this.destinationPos;
    }

    @Override
    protected boolean isNearWalkPos() {
        if (this.chairPos != null) {
            return this.chairPos != null && this.chairPos.func_177951_i((Vec3i)this.villager.func_180425_c()) <= 1.0;
        }
        return super.isNearWalkPos();
    }

    @Override
    protected BlockPos getDestinationBlock() {
        this.structure = this.wanderFunc.apply(this.villager);
        if (this.structure != null) {
            this.chairPos = this.structure.tryVillagerSit(this.villager);
            if (this.chairPos != null) {
                return this.chairPos;
            }
            BlockPos pos = this.structure.getRandomFloorTile();
            if (pos != null) {
                return pos;
            }
        }
        return null;
    }

    @Override
    public void func_75246_d() {
        if (this.sitTime > 0) {
            --this.sitTime;
            if (this.sitTime % 10 == 0) {
                this.moveToSitPos();
                if (this.villager.func_70681_au().nextInt(120) == 0) {
                    this.villager.modifyHappy(1);
                }
            }
        }
        super.func_75246_d();
    }

    @Override
    protected void onArrival() {
        if (this.chairPos != null) {
            this.sitTime = this.structure.getSitTime(this.villager);
            this.startSit();
            this.testHappy(3);
        } else {
            this.testHappy(2);
        }
        super.onArrival();
    }

    private void testHappy(int happyVal) {
        if (this.happyChance > 0 && this.villager.func_70681_au().nextInt(this.happyChance) == 0) {
            this.villager.modifyHappy(happyVal);
        }
    }

    private EnumFacing getChairFacing() {
        if (this.chairPos != null && this.villager.field_70170_p.func_175667_e(this.chairPos)) {
            IBlockState state = this.villager.field_70170_p.func_180495_p(this.chairPos);
            EnumFacing enumfacing = state.func_177230_c() instanceof BlockHorizontal ? ((EnumFacing)state.func_177229_b((IProperty)BlockHorizontal.field_185512_D)).func_176734_d() : null;
            return enumfacing;
        }
        return null;
    }

    private int getChairAxis() {
        EnumFacing facing = this.getChairFacing();
        if (facing != null) {
            return facing.func_176736_b();
        }
        return -1;
    }

    private void startSit() {
        int chairAxis = this.getChairAxis();
        if (chairAxis >= 0) {
            this.moveToSitPos();
            this.villager.onStartSit(chairAxis);
        }
    }

    private Vec3d getSitPos() {
        return new Vec3d((double)this.destinationPos.func_177958_n() + 0.5, (double)this.destinationPos.func_177956_o() + this.villager.getSitOffset(), (double)this.destinationPos.func_177952_p() + 0.5);
    }

    private void moveTo(Vec3d pos) {
        this.villager.func_70012_b(pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, this.villager.field_70177_z, this.villager.field_70125_A);
        this.villager.field_70159_w = 0.0;
        this.villager.field_70181_x = 0.0;
        this.villager.field_70179_y = 0.0;
    }

    private void moveToSitPos() {
        Vec3d sitPos = this.getSitPos();
        if (this.villager.func_174791_d().func_186679_c(sitPos.field_72450_a, sitPos.field_72448_b, sitPos.field_72449_c) > 0.05) {
            this.moveTo(sitPos);
        }
    }

    @Override
    public void func_75251_c() {
        if (this.chairPos != null && this.structure != null) {
            this.structure.vacateSpecialBlock(this.chairPos);
        }
        this.villager.onStopSit();
        this.sitTime = 0;
        super.func_75251_c();
    }
}

