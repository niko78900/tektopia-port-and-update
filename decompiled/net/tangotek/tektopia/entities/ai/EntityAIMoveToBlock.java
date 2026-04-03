/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.entities.EntityVillageNavigator;
import net.tangotek.tektopia.pathing.BasePathingNode;
import net.tangotek.tektopia.pathing.PathNavigateVillager2;
import net.tangotek.tektopia.structures.VillageStructure;

public abstract class EntityAIMoveToBlock
extends EntityAIBase {
    private static int STUCK_TIME = 40;
    protected EntityVillageNavigator navigator;
    private BlockPos walkPos;
    protected BlockPos destinationPos;
    private int pathUpdateTick = 20;
    private boolean arrived = false;
    private int stuckCheck = STUCK_TIME;
    private Vec3d stuckPos = Vec3d.field_186680_a;
    private boolean stuck = false;
    private int lastPathIndex = -1;
    private Vec3d lastNodePos;

    public EntityAIMoveToBlock(EntityVillageNavigator v) {
        this.navigator = v;
        this.func_75248_a(1);
    }

    protected void setMoveDebug(String reason, BlockPos target) {
        this.navigator.setAIDebug(this.getClass().getSimpleName(), reason, target);
    }

    protected abstract BlockPos getDestinationBlock();

    protected void onArrival() {
        this.setMoveDebug("arrived", this.destinationPos);
    }

    public boolean func_75250_a() {
        if (this.navigator.hasVillage() && this.navigator.func_70661_as() instanceof PathNavigateVillager2 && this.canNavigate()) {
            this.destinationPos = this.getDestinationBlock();
            if (this.destinationPos != null) {
                this.stuck = false;
                this.stuckPos = new Vec3d(0.0, -400.0, 0.0);
                this.arrived = false;
                this.pathUpdateTick = 40;
                this.setMoveDebug("start", this.destinationPos);
                this.doMove();
                if (this.stuck) {
                    this.setMoveDebug("stuck_on_start", this.destinationPos);
                }
                return !this.stuck;
            }
            this.setMoveDebug("no_destination", null);
        }
        if (!this.canNavigate()) {
            this.setMoveDebug("cannot_navigate", null);
        }
        return false;
    }

    protected boolean isNearWalkPos() {
        return this.walkPos != null && this.walkPos.func_177951_i((Vec3i)this.navigator.func_180425_c()) <= 1.0;
    }

    protected boolean isNearDestination(double range) {
        return this.destinationPos.func_177951_i((Vec3i)this.navigator.func_180425_c()) < range * range;
    }

    protected boolean canNavigate() {
        return this.navigator.field_70122_E;
    }

    public void func_75249_e() {
        this.updateMovementMode();
        this.setMoveDebug("active", this.destinationPos);
    }

    public boolean func_75253_b() {
        return !this.arrived && !this.stuck && this.navigator.canNavigate();
    }

    protected void updateFacing() {
        if (!this.arrived) {
            if (!this.navigator.func_70661_as().func_75500_f()) {
                Vec3d lookPos = this.navigator.func_70661_as().func_75505_d().func_186310_f();
                this.navigator.faceLocation(lookPos.field_72450_a, lookPos.field_72449_c, 4.0f);
            }
        } else if (this.destinationPos != null) {
            // empty if block
        }
    }

    abstract void updateMovementMode();

    public void func_75246_d() {
        --this.pathUpdateTick;
        if (this.pathUpdateTick <= 0 && !this.arrived) {
            this.pathUpdateTick = 40;
            this.navigator.updateMovement(this.arrived);
        }
        if (!this.arrived && this.isNearWalkPos()) {
            this.arrived = true;
            this.navigator.func_70661_as().func_75499_g();
            this.onArrival();
        }
        this.updateFacing();
        if (!this.arrived) {
            if (this.navigator.func_70661_as().func_75500_f()) {
                this.doMove();
            } else {
                int pathIndex = this.navigator.func_70661_as().func_75505_d().func_75873_e();
                if (this.lastPathIndex != pathIndex) {
                    this.lastNodePos = this.navigator.func_70661_as().func_75505_d().func_186310_f();
                    this.lastPathIndex = pathIndex;
                }
            }
            --this.stuckCheck;
            if (this.stuckCheck < 0) {
                this.stuckCheck = STUCK_TIME;
                if (!this.navigator.func_70661_as().func_75500_f()) {
                    this.stuck = this.navigator.func_174791_d().func_72436_e(this.stuckPos) < 1.0;
                    this.stuckPos = this.navigator.func_174791_d();
                } else {
                    this.navigator.debugOut("has no path?");
                    this.setMoveDebug("no_path_repath", this.walkPos == null ? this.destinationPos : this.walkPos);
                }
            }
            if (this.stuck) {
                if (this.attemptStuckFix() && this.lastPathIndex >= 0) {
                    this.navigator.func_70661_as().func_75499_g();
                    this.setMoveDebug("stuck_retry", this.walkPos == null ? this.destinationPos : this.walkPos);
                    this.doMove();
                } else {
                    this.onStuck();
                }
            }
        }
    }

    protected boolean attemptStuckFix() {
        return false;
    }

    protected void onStuck() {
        VillageManager.get(this.navigator.field_70170_p).submitStuck(this.navigator.func_180425_c());
        Path path = this.navigator.func_70661_as().func_75505_d();
        if (path != null && this.navigator.hasVillage() && this.navigator.func_70661_as() instanceof PathNavigateVillager2) {
            PathNavigateVillager2 pathNavigateVillager2 = (PathNavigateVillager2)this.navigator.func_70661_as();
        }
        this.setMoveDebug("stuck", this.walkPos == null ? this.destinationPos : this.walkPos);
        this.navigator.func_70661_as().func_75499_g();
    }

    protected void onPathFailed(BlockPos pos) {
        VillageManager.get(this.navigator.field_70170_p).submitStuck(this.navigator.func_180425_c());
        this.stuck = true;
        this.setMoveDebug("path_failed", pos);
    }

    public BlockPos getWalkPos() {
        return this.walkPos;
    }

    protected BlockPos findWalkPos() {
        BlockPos pos = this.destinationPos;
        BlockPos diff = this.navigator.func_180425_c().func_177973_b((Vec3i)pos);
        EnumFacing facing = EnumFacing.func_176737_a((float)diff.func_177958_n(), (float)0.0f, (float)diff.func_177952_p());
        BlockPos testPos = pos.func_177972_a(facing);
        if (this.isWalkable(testPos, this.navigator)) {
            return testPos;
        }
        testPos = pos.func_177972_a(facing).func_177972_a(facing.func_176746_e());
        if (this.isWalkable(testPos, this.navigator)) {
            return testPos;
        }
        testPos = pos.func_177972_a(facing).func_177972_a(facing.func_176735_f());
        if (this.isWalkable(testPos, this.navigator)) {
            return testPos;
        }
        testPos = pos.func_177972_a(facing.func_176746_e());
        if (this.isWalkable(testPos, this.navigator)) {
            return testPos;
        }
        testPos = pos.func_177972_a(facing.func_176735_f());
        if (this.isWalkable(testPos, this.navigator)) {
            return testPos;
        }
        testPos = pos.func_177972_a(facing.func_176734_d());
        if (this.isWalkable(testPos, this.navigator)) {
            return testPos;
        }
        testPos = pos.func_177972_a(facing.func_176734_d()).func_177972_a(facing.func_176746_e());
        if (this.isWalkable(testPos, this.navigator)) {
            return testPos;
        }
        testPos = pos.func_177972_a(facing.func_176734_d()).func_177972_a(facing.func_176735_f());
        if (this.isWalkable(testPos, this.navigator)) {
            return testPos;
        }
        if (this.isWalkable(pos, this.navigator)) {
            return pos;
        }
        return null;
    }

    protected boolean isWalkable(BlockPos pos, EntityVillageNavigator nav) {
        BasePathingNode baseNode;
        if (nav.getVillage() != null && (baseNode = nav.getVillage().getPathingGraph().getBaseNode(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p())) != null) {
            return !VillageStructure.isWoodDoor(nav.field_70170_p, pos) && !VillageStructure.isGate(nav.field_70170_p, pos);
        }
        return false;
    }

    protected void doMove() {
        this.arrived = false;
        this.stuckCheck = STUCK_TIME;
        this.walkPos = this.findWalkPos();
        if (this.walkPos == null) {
            this.stuck = true;
            this.setMoveDebug("no_walk_pos", this.destinationPos);
        } else if (!this.isNearWalkPos() && this.canNavigate()) {
            boolean pathFound = this.navigator.func_70661_as().func_75492_a((double)this.walkPos.func_177958_n(), (double)this.walkPos.func_177956_o(), (double)this.walkPos.func_177952_p(), (double)this.navigator.func_70689_ay());
            if (pathFound) {
                this.navigator.func_70671_ap().func_75650_a((double)this.walkPos.func_177958_n(), (double)this.walkPos.func_177956_o(), (double)this.walkPos.func_177952_p(), 50.0f, (float)this.navigator.func_70646_bf());
                this.setMoveDebug("moving", this.walkPos);
            } else {
                this.onPathFailed(this.walkPos);
            }
        }
    }

    protected boolean hasArrived() {
        return this.arrived;
    }

    protected void setArrived() {
        this.arrived = true;
    }

    public void func_75251_c() {
        super.func_75251_c();
        if (this.arrived) {
            this.setMoveDebug("complete", this.destinationPos);
        } else if (this.stuck) {
            this.setMoveDebug("aborted_stuck", this.destinationPos);
        } else {
            this.setMoveDebug("stopped", this.destinationPos);
        }
        this.arrived = false;
        this.stuckCheck = STUCK_TIME;
        this.navigator.resetMovement();
    }
}
