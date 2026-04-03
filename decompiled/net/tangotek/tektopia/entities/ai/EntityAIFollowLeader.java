/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package net.tangotek.tektopia.entities.ai;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tangotek.tektopia.entities.EntityVillageNavigator;
import net.tangotek.tektopia.pathing.BasePathingNode;

public class EntityAIFollowLeader
extends EntityAIBase {
    private final EntityCreature creature;
    private final EntityVillageNavigator leader;
    private final double movementSpeed;
    private final int clusterSize;
    private int nextTick = 0;
    private BlockPos followPos;
    private boolean farAway = false;

    public EntityAIFollowLeader(EntityCreature creatureIn, EntityVillageNavigator leader, int clusterSize, double speedIn) {
        this.creature = creatureIn;
        this.movementSpeed = speedIn;
        this.clusterSize = clusterSize;
        this.leader = leader;
        this.nextTick = this.getTickRate(creatureIn);
        this.func_75248_a(1);
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @SubscribeEvent
    public void onLivingSetAttackTargetEvent(LivingSetAttackTargetEvent event) {
        if (event.getEntity() == this.creature && this.farAway && event.getTarget() != null) {
            this.creature.func_70624_b(null);
        }
    }

    public boolean func_75250_a() {
        if (this.leader.field_70128_L) {
            this.creature.func_70106_y();
        }
        if (this.creature.func_70089_S() && this.leader.hasVillage() && (this.farAway || this.creature.field_70173_aa > this.nextTick)) {
            this.nextTick += 20;
            boolean bl = this.farAway = this.creature.func_70068_e((Entity)this.leader) > 900.0;
            if (this.farAway) {
                this.creature.func_70624_b(null);
            }
            if (this.creature.func_70638_az() == null) {
                this.followPos = this.getFollowPos(this.leader, this.leader.func_70661_as().func_75500_f() ? 4.0f : 10.0f, this.clusterSize);
                if (this.followPos != null && this.creature.func_174818_b(this.followPos) > (double)(this.clusterSize * this.clusterSize)) {
                    return true;
                }
            }
        }
        return false;
    }

    private BlockPos getFollowPos(EntityVillageNavigator leader, float forwardDist, int clusterSize) {
        float distance = forwardDist;
        float f1 = MathHelper.func_76126_a((float)(leader.field_70177_z * ((float)Math.PI / 180)));
        float f2 = MathHelper.func_76134_b((float)(leader.field_70177_z * ((float)Math.PI / 180)));
        double behindX = -distance * f1;
        double behindZ = distance * f2;
        BlockPos testPos = new BlockPos(leader.field_70165_t + behindX, leader.field_70163_u, leader.field_70161_v + behindZ);
        for (int i = 0; i < 20; ++i) {
            BlockPos testBlock = this.randomNearbyBlock(testPos, clusterSize);
            BasePathingNode baseNode = leader.getVillage().getPathingGraph().getNodeYRange(testBlock.func_177958_n(), testBlock.func_177956_o() - 5, testBlock.func_177956_o() + 5, testBlock.func_177952_p());
            if (baseNode == null || leader.getVillage().isInStructure(testBlock)) continue;
            return baseNode.getBlockPos();
        }
        return null;
    }

    private BlockPos randomNearbyBlock(BlockPos pos, int xz) {
        Random rnd = this.leader.func_70681_au();
        int x = MathHelper.func_76136_a((Random)rnd, (int)(-xz), (int)xz);
        int z = MathHelper.func_76136_a((Random)rnd, (int)(-xz), (int)xz);
        return pos.func_177982_a(x, 0, z);
    }

    public boolean func_75253_b() {
        return !this.creature.func_70661_as().func_75500_f();
    }

    public void func_75249_e() {
        this.nextTick = this.creature.field_70173_aa + this.getTickRate(this.creature);
        this.creature.func_70661_as().func_75492_a((double)this.followPos.func_177958_n(), (double)this.followPos.func_177956_o(), (double)this.followPos.func_177952_p(), this.movementSpeed);
    }

    private int getTickRate(EntityCreature creature) {
        return creature.field_70170_p.field_73012_v.nextInt(30) + 60;
    }
}

