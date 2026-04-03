/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityPig
 *  net.minecraft.init.Items
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
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.EntityTagType;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructureRancherPen;
import net.tangotek.tektopia.structures.VillageStructureType;

public class VillageStructurePigPen
extends VillageStructureRancherPen {
    protected VillageStructurePigPen(World world, Village v, EntityItemFrame itemFrame) {
        super(world, v, itemFrame, VillageStructureType.PIG_PEN, 3, "Pig Pen");
    }

    @Override
    public EntityAnimal spawnAnimal(BlockPos pos) {
        EntityPig animal = new EntityPig(this.world);
        animal.func_70012_b((double)pos.func_177958_n() + 0.5, (double)pos.func_177956_o(), (double)pos.func_177952_p() + 0.5, 0.0f, 0.0f);
        animal.func_180482_a(this.world.func_175649_E(pos), (IEntityLivingData)null);
        this.world.func_72838_d((Entity)animal);
        ModEntities.makeTaggedEntity((Entity)animal, EntityTagType.VILLAGER);
        return animal;
    }

    @Override
    public EntityVillagerTek.VillagerThought getNoFoodThought() {
        return EntityVillagerTek.VillagerThought.PIG_FOOD;
    }

    @Override
    public Class getAnimalClass() {
        return EntityPig.class;
    }

    public static Predicate<ItemStack> isFood() {
        return p -> p.func_77973_b() == Items.field_151172_bF || p.func_77973_b() == Items.field_151174_bG || p.func_77973_b() == Items.field_185164_cV;
    }
}

