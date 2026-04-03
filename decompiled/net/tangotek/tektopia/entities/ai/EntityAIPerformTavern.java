/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityBard;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureTavern;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIPerformTavern
extends EntityAIMoveToBlock {
    private boolean arrived = false;
    private int castTime = 0;
    private EntityBard bard;
    private int happyGiven = 0;
    private BlockPos centerPatrons;
    private VillageStructureTavern tavern;

    public EntityAIPerformTavern(EntityVillagerTek entityIn) {
        super(entityIn);
        this.bard = (EntityBard)entityIn;
    }

    @Override
    public boolean func_75250_a() {
        if (this.bard.isAITick("perform_tavern") && this.bard.hasVillage() && !this.bard.isWorkTime() && !this.bard.isSleepingTime() && this.bard.func_70681_au().nextInt(4) == 0) {
            this.tavern = this.findTavern(this.bard.func_180425_c());
            if (this.tavern != null) {
                List<EntityVillagerTek> patrons = this.tavern.getEntitiesInside(EntityVillagerTek.class);
                patrons.remove((Object)this.bard);
                if (patrons.size() < 2) {
                    return false;
                }
                return super.func_75250_a();
            }
            this.bard.setThought(EntityVillagerTek.VillagerThought.TAVERN);
        }
        return false;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        BlockPos noteBlockPos = this.tavern.getUnoccupiedSpecialBlock(Blocks.field_150323_B);
        if (noteBlockPos != null) {
            return noteBlockPos;
        }
        return this.tavern.getRandomFloorTile();
    }

    private VillageStructureTavern findTavern(BlockPos pos) {
        VillageStructureTavern bestTavern = null;
        int crowd = -1;
        List<VillageStructure> taverns = this.bard.getVillage().getStructures(VillageStructureType.TAVERN);
        for (VillageStructure t : taverns) {
            VillageStructureTavern tavern = (VillageStructureTavern)t;
            if (tavern.getPerformingBard() != null) continue;
            if (tavern.isBlockInside(pos)) {
                return tavern;
            }
            List<EntityVillagerTek> patrons = tavern.getEntitiesInside(EntityVillagerTek.class);
            if (patrons.size() <= crowd) continue;
            crowd = patrons.size();
            bestTavern = tavern;
        }
        return bestTavern;
    }

    private BlockPos getCenterPatrons() {
        BlockPos sumPos = BlockPos.field_177992_a;
        int count = 0;
        List<EntityVillagerTek> patrons = this.tavern.getEntitiesInside(EntityVillagerTek.class);
        for (EntityVillagerTek v : patrons) {
            if (v == this.bard) continue;
            ++count;
            sumPos = sumPos.func_177971_a((Vec3i)v.func_180425_c());
        }
        if (count > 0) {
            return new BlockPos(sumPos.func_177958_n() / count, sumPos.func_177956_o() / count, sumPos.func_177952_p() / count);
        }
        return null;
    }

    @Override
    protected void onArrival() {
        if (!this.arrived) {
            this.tryPerform();
        }
        this.arrived = true;
        super.onArrival();
    }

    @Override
    public void func_75249_e() {
        this.tavern.setPerformingBard(this.bard);
        this.bard.setPerformance(ModSoundEvents.Performance.getRandom(true, this.bard.func_70681_au()));
        super.func_75249_e();
    }

    public boolean func_75252_g() {
        return this.castTime <= 0;
    }

    protected void tryPerform() {
        if (this.bard.func_70089_S()) {
            ModSoundEvents.Performance performance = this.bard.getPerformance();
            this.castTime = performance.duration;
            this.centerPatrons = this.getCenterPatrons();
            this.bard.func_70661_as().func_75499_g();
            this.bard.playServerAnimation(performance.anim);
            this.bard.func_184185_a(performance.sound, 2.0f, 1.0f);
            this.bard.modifyHunger(-6);
        }
    }

    @Override
    public void func_75246_d() {
        --this.castTime;
        if (this.castTime > 0 && this.bard.func_70681_au().nextInt(40) == 0) {
            this.distributeHappyNearby(16, 1, 3);
            this.bard.tryAddSkill(ProfessionType.BARD, 35);
        }
        if (this.centerPatrons != null) {
            this.bard.faceLocation(this.centerPatrons.func_177958_n(), this.centerPatrons.func_177952_p(), 90.0f);
        }
        if (this.castTime == 0) {
            this.distributeHappyNearby(2, 3, 5);
            if (this.happyGiven > 8) {
                this.bard.modifyHappy(this.happyGiven / 2);
            } else {
                this.bard.modifyHappy(-5);
            }
        }
        super.func_75246_d();
    }

    private void distributeHappyNearby(int chance, int min, int max) {
        List<EntityVillagerTek> patrons = this.tavern.getEntitiesInside(EntityVillagerTek.class);
        for (EntityVillagerTek v : patrons) {
            if (v == this.bard || this.bard.func_70681_au().nextInt(chance) != 0) continue;
            v.cheerBeer(this.bard.getSkillLerp(ProfessionType.BARD, min, max));
            ++this.happyGiven;
        }
    }

    @Override
    public void func_75251_c() {
        this.tavern.setPerformingBard(null);
        this.bard.stopServerAnimation(this.bard.getPerformance().anim);
        this.bard.setPerformance(null);
        this.arrived = false;
        this.castTime = 0;
        this.happyGiven = 0;
        this.tavern = null;
        super.func_75251_c();
    }

    @Override
    public boolean func_75253_b() {
        if (this.castTime > 0) {
            return true;
        }
        return super.func_75253_b();
    }

    @Override
    void updateMovementMode() {
        this.bard.setMovementMode(this.bard.getDefaultMovement());
    }
}

