/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityRancher;
import net.tangotek.tektopia.entities.ai.EntityAIFollow;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureRancherPen;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIFeedAnimal
extends EntityAIFollow {
    private VillageStructureType penType;
    private VillageStructureRancherPen rancherPen = null;
    private EntityAnimal feedingAnimal = null;
    private boolean active = false;
    private int feedTime = 0;
    private ItemStack foodItem;
    private final int modChance;
    private final String aiFilter;
    private final EntityRancher rancher;

    public EntityAIFeedAnimal(EntityRancher v, VillageStructureType penType, int modChance, String aiFilter) {
        super(v);
        this.rancher = v;
        this.penType = penType;
        this.modChance = modChance;
        this.aiFilter = aiFilter;
    }

    @Override
    protected EntityLivingBase getFollowTarget() {
        return this.feedingAnimal;
    }

    private boolean isPriorityPen(EntityRancher rancher) {
        return rancher.getPriorityPen() == this.penType;
    }

    private boolean useThisPen(EntityRancher rancher, int modChance) {
        return rancher.getPriorityPen() != null ? this.isPriorityPen(rancher) : rancher.field_70173_aa % 4 == modChance;
    }

    @Override
    public boolean func_75250_a() {
        if (this.rancher.isAITick(this.aiFilter) && this.rancher.getVillage() != null && this.rancher.isWorkTime() && this.useThisPen(this.rancher, this.modChance)) {
            List<VillageStructure> structures = this.rancher.getVillage().getStructures(this.penType);
            Collections.shuffle(structures);
            for (VillageStructure struct : structures) {
                List<ItemStack> foodItems;
                this.rancherPen = (VillageStructureRancherPen)struct;
                this.foodItem = null;
                List animalList = this.rancherPen.getEntitiesInside(this.rancherPen.getAnimalClass());
                Collections.shuffle(animalList);
                if (!animalList.isEmpty() && !(foodItems = this.rancher.getInventory().getItems(p -> ((EntityAnimal)animalList.get(0)).func_70877_b(p), 1)).isEmpty()) {
                    this.foodItem = foodItems.get(0);
                }
                this.feedingAnimal = null;
                int minHunger = 100;
                for (EntityAnimal animal : animalList) {
                    int curHunger;
                    if (!this.isAnimalFeedable(this.rancherPen, animal) || this.rancherPen.isAnimalScheduled(animal, VillageStructureRancherPen.AnimalScheduleType.FEED) || (curHunger = ModEntities.getAnimalHunger(animal)) >= minHunger) continue;
                    this.feedingAnimal = animal;
                    minHunger = curHunger;
                }
                if (this.feedingAnimal == null) continue;
                if (this.foodItem == null) {
                    this.rancher.setThought(this.rancherPen.getNoFoodThought());
                    continue;
                }
                return super.func_75250_a();
            }
        }
        return false;
    }

    @Override
    public void func_75249_e() {
        this.active = true;
        this.rancher.equipActionItem(this.foodItem);
        this.rancherPen.scheduleAnimal(this.feedingAnimal, VillageStructureRancherPen.AnimalScheduleType.FEED);
        super.func_75249_e();
    }

    @Override
    public boolean func_75253_b() {
        return this.active;
    }

    @Override
    void updateMovementMode() {
        this.rancher.setMovementMode(this.rancher.getDefaultMovement());
    }

    @Override
    public void func_75246_d() {
        if (this.feedTime > 0) {
            this.rancher.func_70671_ap().func_75650_a((double)this.destinationPos.func_177958_n(), (double)this.destinationPos.func_177956_o(), (double)this.destinationPos.func_177952_p(), 30.0f, 30.0f);
            --this.feedTime;
            if (this.feedTime == 17) {
                this.rancher.unequipActionItem(this.foodItem);
            }
            if (this.feedTime == 0) {
                this.feedAnimal();
                this.active = false;
            }
        } else {
            super.func_75246_d();
        }
    }

    public boolean func_75252_g() {
        return this.feedTime <= 0;
    }

    @Override
    protected void onArrival() {
        if (!this.isAnimalFeedable(this.rancherPen, this.feedingAnimal)) {
            this.active = false;
        }
        this.startFeeding();
        super.onArrival();
    }

    @Override
    protected void onStuck() {
        this.active = false;
        super.onStuck();
    }

    @Override
    protected void onPathFailed(BlockPos pos) {
        this.active = false;
        super.onPathFailed(pos);
    }

    private void startFeeding() {
        int skillFactor = Math.max(this.rancher.getSkillLerp(ProfessionType.RANCHER, 6, 1) - 1, 1);
        this.feedTime = 34 * skillFactor;
        this.rancher.func_70661_as().func_75499_g();
        this.rancher.playServerAnimation("villager_take");
    }

    private void stopFeeding() {
        this.rancher.stopServerAnimation("villager_take");
    }

    private boolean isAnimalFeedable(VillageStructureRancherPen rancherPen, EntityAnimal animal) {
        return animal.func_70089_S() && (ModEntities.isAnimalHungry(animal) || this.isPriorityPen(this.rancher)) && animal.func_70874_b() <= 0 && !animal.func_70880_s();
    }

    private void feedAnimal() {
        if (this.isAnimalFeedable(this.rancherPen, this.feedingAnimal)) {
            this.rancher.debugOut("feeding " + this.feedingAnimal.getClass().getSimpleName() + " at " + this.rancher.func_180425_c());
            ModEntities.modifyAnimalHunger(this.feedingAnimal, 100);
            this.rancher.tryAddSkill(ProfessionType.RANCHER, 5);
            this.feedingAnimal.func_70606_j(this.feedingAnimal.func_110138_aP());
            if (!this.rancherPen.isPenFull(1.0f) && this.feedingAnimal.func_70681_au().nextInt(this.rancher.getSkillLerp(ProfessionType.RANCHER, 5, 1)) <= 1) {
                this.feedingAnimal.func_146082_f(null);
            }
            this.rancher.pickupItems(5);
            this.rancher.getInventory().removeItems(p -> this.feedingAnimal.func_70877_b(p), 1);
            this.rancher.throttledSadness(-3);
            this.rancher.modifyHunger(-this.rancherPen.getFeedCost(this.rancher));
            if (this.rancher.getPriorityPen() == null) {
                this.rancher.setPriorityPen(this.rancherPen.type);
            } else if (this.rancher.getPriorityPen() == this.rancherPen.type) {
                this.rancher.setPriorityPen(null);
            }
        }
    }

    @Override
    public void func_75251_c() {
        this.rancherPen.clearAnimalSchedule(this.feedingAnimal, VillageStructureRancherPen.AnimalScheduleType.FEED);
        this.rancherPen = null;
        this.feedTime = 0;
        this.active = false;
        this.rancher.unequipActionItem(this.foodItem);
        this.foodItem = null;
        this.stopFeeding();
        super.func_75251_c();
    }
}

