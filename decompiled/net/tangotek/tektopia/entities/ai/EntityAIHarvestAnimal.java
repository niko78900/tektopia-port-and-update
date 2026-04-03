/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityCow
 *  net.minecraft.entity.passive.EntitySheep
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.EntityTagType;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIFollow;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureRancherPen;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIHarvestAnimal
extends EntityAIFollow {
    private VillageStructureType penType;
    private VillageStructureRancherPen rancherPen = null;
    private ItemStack toolRequired;
    private ItemStack toolUsed;
    private EntityAnimal targetAnimal = null;
    private boolean active = false;
    private int harvestTime = 0;
    protected final EntityVillagerTek villager;
    public static long COW_MILK_TIME = 6000L;
    protected final Predicate<EntityVillagerTek> shouldPred;

    public EntityAIHarvestAnimal(EntityVillagerTek v, VillageStructureType penType, ItemStack toolRequired, Predicate<EntityVillagerTek> shouldPred) {
        super(v);
        this.villager = v;
        this.penType = penType;
        this.toolRequired = toolRequired;
        this.toolUsed = null;
        this.shouldPred = shouldPred;
    }

    @Override
    protected EntityLivingBase getFollowTarget() {
        return this.targetAnimal;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.getVillage() != null && this.villager.isWorkTime() && this.shouldPred.test(this.villager)) {
            List<VillageStructure> structures = this.villager.getVillage().getStructures(this.penType);
            Collections.shuffle(structures);
            for (VillageStructure struct : structures) {
                List<ItemStack> tools;
                this.rancherPen = (VillageStructureRancherPen)struct;
                List animalList = this.rancherPen.getEntitiesInside(this.rancherPen.getAnimalClass());
                this.toolUsed = null;
                if (!animalList.isEmpty() && !(tools = this.villager.getInventory().getItems(p -> p.func_77973_b() == this.toolRequired.func_77973_b(), 1)).isEmpty()) {
                    this.toolUsed = tools.get(0);
                }
                this.targetAnimal = null;
                double minDistance = Double.MAX_VALUE;
                for (EntityAnimal animal : animalList) {
                    double curDist;
                    if (!this.isAnimalHarvestable(animal) || this.rancherPen.isAnimalScheduled(animal, VillageStructureRancherPen.AnimalScheduleType.HARVEST) || !((curDist = animal.func_70068_e((Entity)this.villager)) < minDistance)) continue;
                    this.targetAnimal = animal;
                    minDistance = curDist;
                }
                if (this.targetAnimal == null) continue;
                if (this.toolUsed != null) {
                    return super.func_75250_a();
                }
                this.villager.setThought(this.rancherPen.getNoHarvestThought());
            }
        }
        return false;
    }

    @Override
    public void func_75249_e() {
        this.active = true;
        this.villager.equipActionItem(this.toolUsed);
        this.rancherPen.scheduleAnimal(this.targetAnimal, VillageStructureRancherPen.AnimalScheduleType.HARVEST);
        super.func_75249_e();
    }

    @Override
    public boolean func_75253_b() {
        return this.active;
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    public void func_75246_d() {
        if (this.harvestTime > 0) {
            this.villager.func_70671_ap().func_75650_a((double)this.destinationPos.func_177958_n(), (double)this.destinationPos.func_177956_o(), (double)this.destinationPos.func_177952_p(), 30.0f, 30.0f);
            --this.harvestTime;
            if (this.harvestTime == 17) {
                this.villager.unequipActionItem(this.toolUsed);
            }
            if (this.harvestTime == 10) {
                this.harvestAnimal();
            } else if (this.harvestTime == 0) {
                this.active = false;
            }
        } else {
            super.func_75246_d();
        }
    }

    public boolean func_75252_g() {
        return this.harvestTime <= 0;
    }

    @Override
    protected void onArrival() {
        this.startHarvesting();
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

    private void startHarvesting() {
        int skillFactor = Math.max(this.villager.getSkillLerp(ProfessionType.RANCHER, 6, 1) - 1, 1);
        this.harvestTime = 34 * skillFactor;
        this.villager.func_70661_as().func_75499_g();
        this.villager.playServerAnimation("villager_take");
    }

    private void stopHarvesting() {
        this.villager.stopServerAnimation("villager_take");
    }

    private boolean isAnimalHarvestable(EntityAnimal animal) {
        if (animal.func_70089_S() && !animal.func_70631_g_()) {
            if (animal instanceof EntityCow) {
                return ModEntities.isEntityUsable((Entity)animal, COW_MILK_TIME, this.villager.field_70170_p.func_82737_E());
            }
            if (animal instanceof EntitySheep) {
                EntitySheep sheep = (EntitySheep)animal;
                return !sheep.func_70892_o();
            }
        }
        return false;
    }

    private void harvestAnimal() {
        if (this.isAnimalHarvestable(this.targetAnimal)) {
            if (this.targetAnimal instanceof EntitySheep) {
                EntitySheep sheep = (EntitySheep)this.targetAnimal;
                sheep.func_70893_e(true);
                Random rng = this.villager.func_70681_au();
                this.villager.damageItem(this.toolUsed, 1);
                this.villager.tryAddSkill(ProfessionType.RANCHER, 5);
                int i = 1;
                if (rng.nextInt(100) < this.villager.getSkill(ProfessionType.RANCHER) && rng.nextInt(100) < this.villager.getSkill(ProfessionType.RANCHER)) {
                    ++i;
                }
                for (int j = 0; j < i; ++j) {
                    EntityItem entityitem = sheep.func_70099_a(ModItems.makeTaggedItem(new ItemStack(Item.func_150898_a((Block)Blocks.field_150325_L), 1, sheep.func_175509_cj().func_176765_a()), ItemTagType.VILLAGER), 1.0f);
                    entityitem.field_70181_x += (double)(rng.nextFloat() * 0.05f);
                    entityitem.field_70159_w += (double)((rng.nextFloat() - rng.nextFloat()) * 0.1f);
                    entityitem.field_70179_y += (double)((rng.nextFloat() - rng.nextFloat()) * 0.1f);
                }
                this.villager.func_184185_a(SoundEvents.field_187763_eJ, 1.0f, 1.0f);
            } else if (this.targetAnimal instanceof EntityCow && ModEntities.isEntityUsable((Entity)this.targetAnimal, COW_MILK_TIME, this.villager.field_70170_p.func_82737_E())) {
                ItemStack addedItem;
                ModEntities.useEntity((Entity)this.targetAnimal, this.villager.field_70170_p.func_82737_E());
                this.villager.tryAddSkill(ProfessionType.RANCHER, 5);
                this.villager.func_184185_a(SoundEvents.field_187564_an, 1.0f, 1.0f);
                ItemStack milkItem = new ItemStack(Items.field_151117_aB);
                if (ModEntities.isTaggedEntity((Entity)this.targetAnimal, EntityTagType.VILLAGER) && ModItems.isTaggedItem(this.toolUsed, ItemTagType.VILLAGER)) {
                    ModItems.makeTaggedItem(milkItem, ItemTagType.VILLAGER);
                }
                if ((addedItem = this.villager.getInventory().func_174894_a(milkItem)) == ItemStack.field_190927_a) {
                    this.villager.getInventory().removeItems(p -> ItemStack.func_77989_b((ItemStack)this.toolUsed, (ItemStack)p), 1);
                }
            }
            this.villager.throttledSadness(-3);
            this.villager.modifyHunger(-3);
        }
    }

    @Override
    public void func_75251_c() {
        this.villager.pickupItems(5);
        this.rancherPen.clearAnimalSchedule(this.targetAnimal, VillageStructureRancherPen.AnimalScheduleType.HARVEST);
        this.rancherPen = null;
        this.harvestTime = 0;
        this.active = false;
        this.villager.unequipActionItem(this.toolUsed);
        this.toolUsed = null;
        this.stopHarvesting();
        super.func_75251_c();
    }
}

