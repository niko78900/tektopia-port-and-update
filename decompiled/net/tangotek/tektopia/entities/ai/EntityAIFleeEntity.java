/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.EntitySelectors
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.pathing.BasePathingNode;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureHome;

public class EntityAIFleeEntity
extends EntityAIMoveToBlock {
    private final Predicate<Entity> entityPredicate;
    private final EntityVillagerTek villager;
    private final float avoidDistance;
    private Entity fleeEntity;
    private BlockPos destPos;

    public EntityAIFleeEntity(EntityVillagerTek v, Predicate<Entity> inPred, float avoidDistanceIn) {
        super(v);
        this.villager = v;
        this.avoidDistance = avoidDistanceIn;
        this.func_75248_a(1);
        this.entityPredicate = Predicates.and((Predicate[])new Predicate[]{EntitySelectors.field_188444_d, e -> e.func_70089_S() && this.villager.func_70635_at().func_75522_a(e), inPred});
    }

    @Override
    public boolean func_75250_a() {
        List fleeEnts;
        if (this.villager.isAITick() && this.villager.hasVillage() && !(fleeEnts = this.villager.field_70170_p.func_175674_a((Entity)this.villager, this.villager.func_174813_aQ().func_72314_b((double)this.avoidDistance, 8.0, (double)this.avoidDistance), this.entityPredicate)).isEmpty()) {
            this.fleeEntity = (Entity)fleeEnts.get(0);
            VillageStructureHome home = this.villager.getHome();
            if (home != null && home.isBlockInside(this.villager.func_180425_c()) && VillageStructure.isWoodDoor(this.villager.field_70170_p, home.getDoor()) && !home.isBlockInside(this.fleeEntity.func_180425_c())) {
                return false;
            }
            BlockPos fleeBlock = this.findRandomTargetAwayFrom(this.fleeEntity);
            if (fleeBlock != null) {
                this.destPos = fleeBlock;
                return super.func_75250_a();
            }
        }
        return false;
    }

    protected BlockPos findRandomTargetAwayFrom(Entity fleeEntity) {
        Vec3d fleeDelta;
        Vec3d villagerPos = this.villager.func_174791_d();
        Vec3d enemyPos = this.fleeEntity.func_174791_d();
        if (this.villager.getBedPos() != null) {
            if (this.villager.func_174818_b(this.villager.getBedPos()) < 256.0) {
                return this.villager.getBedPos();
            }
            fleeDelta = new Vec3d((Vec3i)this.villager.getBedPos()).func_178788_d(villagerPos);
        } else {
            fleeDelta = this.villager.hasVillage() ? (this.villager.func_180425_c().func_177951_i((Vec3i)this.villager.getVillage().getOrigin()) < 1600.0 ? villagerPos.func_178788_d(enemyPos) : this.villager.func_174791_d().func_178788_d(new Vec3d((Vec3i)this.villager.getVillage().getOrigin()))) : villagerPos.func_178788_d(enemyPos);
        }
        Vec3d fleeDeltaNorm = fleeDelta.func_72432_b();
        Vec3d fleeDir = new Vec3d(fleeDeltaNorm.field_72450_a, 0.0, fleeDeltaNorm.field_72448_b);
        if (this.villager.hasVillage() && this.villager.getVillage().getAABB().func_72318_a(villagerPos) && villagerPos.func_178787_e(fleeDir).func_72436_e(enemyPos) < villagerPos.func_72436_e(enemyPos) && villagerPos.func_178787_e(fleeDir = fleeDir.func_178785_b(60.0f)).func_72436_e(enemyPos) < villagerPos.func_72436_e(enemyPos)) {
            fleeDir = fleeDir.func_178785_b(-120.0f);
        }
        Vec3d fleePos = this.villager.func_174791_d().func_178787_e(fleeDir.func_186678_a(16.0));
        BlockPos fleeBlock = new BlockPos(fleePos.field_72450_a, fleePos.field_72448_b, fleePos.field_72449_c);
        for (int i = 0; i < 20; ++i) {
            BlockPos testBlock = this.randomNearbyBlock(fleeBlock, i + 3);
            BasePathingNode baseNode = this.villager.getVillage().getPathingGraph().getNodeYRange(testBlock.func_177958_n(), testBlock.func_177956_o() - 5, testBlock.func_177956_o() + 5, testBlock.func_177952_p());
            if (baseNode == null) continue;
            return baseNode.getBlockPos();
        }
        return null;
    }

    private BlockPos randomNearbyBlock(BlockPos pos, int xz) {
        return pos.func_177982_a(this.villager.func_70681_au().nextInt(xz * 2) - xz, 0, this.villager.func_70681_au().nextInt(xz * 2) - xz);
    }

    @Override
    public void func_75249_e() {
        if (!(this.fleeEntity instanceof EntityVillagerTek)) {
            if (!this.villager.isRole(VillagerRole.DEFENDER)) {
                this.villager.modifyHappy(-2);
            }
            if (this.villager.func_70681_au().nextInt(2) == 0) {
                this.villager.playSound(ModSoundEvents.villagerAfraid);
            }
        }
        super.func_75249_e();
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(EntityVillagerTek.MovementMode.RUN);
    }

    @Override
    protected BlockPos getDestinationBlock() {
        return this.destPos;
    }

    @Override
    public void func_75251_c() {
        this.fleeEntity = null;
        super.func_75251_c();
    }
}

