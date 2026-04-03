/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityChicken
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.EntityTagType;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructureRancherPen;
import net.tangotek.tektopia.structures.VillageStructureType;

public class VillageStructureChickenCoop
extends VillageStructureRancherPen {
    protected VillageStructureChickenCoop(World world, Village v, EntityItemFrame itemFrame) {
        super(world, v, itemFrame, VillageStructureType.CHICKEN_COOP, 1, "Chicken Coop");
    }

    @Override
    public EntityAnimal spawnAnimal(BlockPos pos) {
        EntityChicken animal = new EntityChicken(this.world);
        animal.func_70012_b((double)pos.func_177958_n() + 0.5, (double)pos.func_177956_o(), (double)pos.func_177952_p() + 0.5, 0.0f, 0.0f);
        animal.func_180482_a(this.world.func_175649_E(pos), (IEntityLivingData)null);
        this.world.func_72838_d((Entity)animal);
        ModEntities.makeTaggedEntity((Entity)animal, EntityTagType.VILLAGER);
        return animal;
    }

    @Override
    public Class getAnimalClass() {
        return EntityChicken.class;
    }

    public static Predicate<ItemStack> isFood() {
        return p -> p.func_77973_b() == Items.field_151014_N || p.func_77973_b() == Items.field_185163_cU;
    }

    @Override
    public EntityVillagerTek.VillagerThought getNoFoodThought() {
        return EntityVillagerTek.VillagerThought.CHICKEN_FOOD;
    }

    @Override
    public EntityVillagerTek.VillagerThought getNoHarvestThought() {
        return EntityVillagerTek.VillagerThought.BUCKET;
    }

    @Override
    protected void updateAnimal(EntityAnimal animal) {
        if (animal instanceof EntityChicken) {
            EntityChicken chicken = (EntityChicken)animal;
            chicken.field_70887_j = 9999999;
            if (!(this.world.field_72995_K || chicken.func_70631_g_() || chicken.func_152116_bZ() || chicken.func_70681_au().nextInt(500) != 0)) {
                chicken.func_184185_a(SoundEvents.field_187665_Y, 1.0f, (chicken.func_70681_au().nextFloat() - chicken.func_70681_au().nextFloat()) * 0.2f + 1.0f);
                chicken.func_70099_a(ModItems.makeTaggedItem(new ItemStack(Items.field_151110_aK), ItemTagType.VILLAGER), 0.0f);
            }
        }
        super.updateAnimal(animal);
    }
}

