/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.monster.EntityZombie
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 */
package net.tangotek.tektopia.entities.ai;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.entities.EntityNecromancer;

public class EntityAISummonUndead
extends EntityAIBase {
    private EntityNecromancer necro;
    private int summonTime = 0;
    private int cooldownTick = 0;
    protected final int checkInterval;
    private BlockPos summonPos;
    private final int MAX_SUMMONS = 50;
    private int summonCount = 0;

    public EntityAISummonUndead(EntityNecromancer necro, int checkInterval) {
        this.checkInterval = checkInterval;
        this.necro = necro;
        this.func_75248_a(1);
    }

    public boolean func_75250_a() {
        List zombs;
        if (this.necro.isAITick() && this.isSummonReady(this.necro) && this.necro.getMinions().size() < this.necro.getMaxSummons() && (zombs = this.necro.field_70170_p.func_72872_a(EntityZombie.class, this.necro.func_174813_aQ().func_72314_b(30.0, 10.0, 30.0))).size() < this.necro.getLevel() * 2) {
            this.summonPos = this.findSummonPos(this.necro.func_70661_as().func_75500_f() ? 2.0f : 10.0f);
            return this.summonPos != null;
        }
        return false;
    }

    private boolean isSummonReady(EntityNecromancer necro) {
        if (this.summonCount >= 50) {
            return false;
        }
        if (this.necro.getMinions().size() < 1) {
            return true;
        }
        return this.necro.field_70173_aa > this.cooldownTick && (this.necro.getMinions().size() < 1 || this.necro.func_70681_au().nextInt(this.checkInterval) == 0);
    }

    public void func_75249_e() {
        this.necro.func_70661_as().func_75499_g();
        this.startSummoning();
        super.func_75249_e();
    }

    public boolean func_75253_b() {
        return this.summonTime > 0;
    }

    public void func_75246_d() {
        if (this.summonTime > 0) {
            this.necro.faceLocation(this.summonPos.func_177958_n(), this.summonPos.func_177952_p(), 100.0f);
            --this.summonTime;
            if (this.summonTime == 12) {
                this.spawnMinion();
            }
        }
    }

    public boolean func_75252_g() {
        return false;
    }

    private BlockPos findSummonPos(float forwardDist) {
        float distance = forwardDist;
        float f1 = MathHelper.func_76126_a((float)(this.necro.field_70177_z * ((float)Math.PI / 180)));
        float f2 = MathHelper.func_76134_b((float)(this.necro.field_70177_z * ((float)Math.PI / 180)));
        double behindX = -distance * f1;
        double behindZ = distance * f2;
        BlockPos testPos = new BlockPos(this.necro.field_70165_t + behindX, this.necro.field_70163_u, this.necro.field_70161_v + behindZ);
        EntityZombie zombie = new EntityZombie(this.necro.field_70170_p);
        int searchBoxXZ = 8;
        for (int l = 0; l < 50; ++l) {
            int k1;
            int j1;
            int i1 = testPos.func_177958_n() + MathHelper.func_76136_a((Random)this.necro.field_70170_p.field_73012_v, (int)(-searchBoxXZ), (int)searchBoxXZ);
            BlockPos bp = new BlockPos(i1, j1 = testPos.func_177956_o() + MathHelper.func_76136_a((Random)this.necro.field_70170_p.field_73012_v, (int)-5, (int)5), k1 = testPos.func_177952_p() + MathHelper.func_76136_a((Random)this.necro.field_70170_p.field_73012_v, (int)(-searchBoxXZ), (int)searchBoxXZ));
            if (!this.necro.field_70170_p.isSideSolid(bp.func_177977_b(), EnumFacing.UP)) continue;
            zombie.func_70107_b((double)i1 + 0.5, (double)j1, (double)k1 + 0.5);
            if (!zombie.func_70058_J() || this.necro.hasVillage() && this.necro.getVillage().isInStructure(bp)) continue;
            return bp;
        }
        return null;
    }

    private void startSummoning() {
        this.necro.debugOut("Summoning undead minion [ " + this.summonPos + "] Starting");
        this.summonTime = 47;
        this.necro.playServerAnimation("necro_summon");
        this.necro.func_184185_a(ModSoundEvents.deathSummon, this.necro.func_70681_au().nextFloat() * 0.2f + 1.2f, this.necro.func_70681_au().nextFloat() * 0.2f + 0.9f);
    }

    private void stopSummoning() {
        this.necro.debugOut("Summoning complete [ " + this.summonPos + "] Starting");
        this.necro.stopServerAnimation("necro_summon");
    }

    private void spawnMinion() {
        Object minion = this.necro.getLevel() == 5 && this.necro.func_70681_au().nextBoolean() ? this.necro.createWitherSkeleton(this.summonPos) : (this.necro.getLevel() == 4 && this.necro.func_70681_au().nextInt(4) == 0 ? this.necro.createWitherSkeleton(this.summonPos) : this.necro.createZombie(this.summonPos));
        this.necro.field_70170_p.func_72838_d((Entity)minion);
        this.necro.addMinion((EntityMob)minion);
        minion.func_184185_a(ModSoundEvents.deathSummonTarget, this.necro.func_70681_au().nextFloat() * 0.4f + 0.8f, this.necro.func_70681_au().nextFloat() * 0.4f + 0.8f);
        ++this.summonCount;
        this.necro.playSound(ModSoundEvents.deathSummonEnd);
        this.cooldownTick = this.necro.field_70173_aa + this.necro.getLevelCooldown(MathHelper.func_76136_a((Random)this.necro.func_70681_au(), (int)30, (int)50) + this.necro.getMinions().size() * 20);
    }

    public void func_75251_c() {
        this.summonTime = 0;
        this.stopSummoning();
        super.func_75251_c();
    }
}

