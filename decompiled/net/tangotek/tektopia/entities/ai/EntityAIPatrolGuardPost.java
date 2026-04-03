/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIPatrolPoint;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureGuardPost;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIPatrolGuardPost
extends EntityAIPatrolPoint {
    protected final EntityVillagerTek villager;
    protected VillageStructureGuardPost guardPost;

    public EntityAIPatrolGuardPost(EntityVillagerTek v, Predicate<EntityVillagerTek> shouldPred, int distanceFromPoint, int waitTime) {
        super(v, shouldPred, distanceFromPoint, waitTime);
        this.villager = v;
    }

    @Override
    public boolean func_75250_a() {
        return this.villager.isAITick("patrol_guard_post") && super.func_75250_a();
    }

    @Override
    protected BlockPos getPatrolPoint() {
        List<VillageStructure> posts = this.villager.getVillage().getStructures(VillageStructureType.GUARD_POST);
        Collections.shuffle(posts);
        long minVisitTime = 400L;
        VillageStructureGuardPost resultPost = null;
        for (int i = 0; i < posts.size(); ++i) {
            VillageStructureGuardPost post = (VillageStructureGuardPost)posts.get(i);
            if (post.isSpecialBlockOccupied(post.getDoor())) continue;
            long timeSince = post.getTimeSinceVisit();
            if (timeSince > minVisitTime) {
                minVisitTime = timeSince;
                resultPost = post;
                continue;
            }
            if (post != this.guardPost) continue;
            resultPost = post;
        }
        this.guardPost = resultPost;
        if (this.guardPost != null) {
            return this.guardPost.getDoor();
        }
        return null;
    }

    @Override
    public void func_75249_e() {
        super.func_75249_e();
        this.villager.throttledSadness(-1);
        if (this.guardPost != null) {
            this.guardPost.occupySpecialBlock(this.guardPost.getDoor());
        }
    }

    @Override
    public void func_75251_c() {
        if (this.guardPost != null) {
            this.guardPost.vacateSpecialBlock(this.guardPost.getDoor());
        }
        super.func_75251_c();
    }
}

