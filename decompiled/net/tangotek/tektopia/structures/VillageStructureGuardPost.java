/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class VillageStructureGuardPost
extends VillageStructure {
    protected long lastVisited = 0L;

    protected VillageStructureGuardPost(World world, Village v, EntityItemFrame itemFrame) {
        super(world, v, itemFrame, VillageStructureType.GUARD_POST, "Guard Post");
    }

    @Override
    protected void doFloorScan() {
        super.doFloorScan();
        this.safeSpot = this.door;
    }

    @Override
    public boolean vacateSpecialBlock(BlockPos bp) {
        this.lastVisited = this.world.func_82737_E();
        return super.vacateSpecialBlock(bp);
    }

    public long getTimeSinceVisit() {
        return this.world.func_82737_E() - this.lastVisited;
    }

    @Override
    protected void scanFloor(BlockPos pos) {
    }

    @Override
    protected BlockPos findDoor() {
        int max;
        BlockPos testPos = this.framePos;
        for (max = 10; !this.village.getPathingGraph().isInGraph(testPos) && max > 0; --max) {
            testPos = testPos.func_177977_b();
        }
        if (max > 0) {
            return testPos;
        }
        return null;
    }

    @Override
    public boolean validate() {
        this.isValid = true;
        if (this.door == null) {
            this.isValid = false;
        } else if (this.world.func_175667_e(this.door)) {
            Entity e = this.world.func_73045_a(this.signEntityId);
            if (this.isValid && (e == null || !(e instanceof EntityItemFrame))) {
                this.debugOut("Village struct frame is missing or wrong type | " + this.getFramePos());
                this.isValid = false;
            }
            EntityItemFrame itemFrame = (EntityItemFrame)e;
            if (this.isValid && itemFrame.func_174857_n() != this.framePos) {
                this.debugOut("Village struct center has moved" + this.getFramePos());
                this.isValid = false;
            }
            if (this.isValid && !this.type.isItemEqual(itemFrame.func_82335_i())) {
                this.debugOut("Village struct frame item has changed" + this.getFramePos());
                this.isValid = false;
            }
        }
        return this.isValid;
    }
}

