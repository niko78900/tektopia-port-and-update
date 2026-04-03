/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  net.minecraft.block.Block
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.entities;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIAttachLeadToLostAnimal;
import net.tangotek.tektopia.entities.ai.EntityAIFeedAnimal;
import net.tangotek.tektopia.entities.ai.EntityAIHarvestAnimal;
import net.tangotek.tektopia.entities.ai.EntityAIReturnLostAnimal;
import net.tangotek.tektopia.storage.ItemDesire;
import net.tangotek.tektopia.structures.VillageStructureChickenCoop;
import net.tangotek.tektopia.structures.VillageStructureCowPen;
import net.tangotek.tektopia.structures.VillageStructurePigPen;
import net.tangotek.tektopia.structures.VillageStructureSheepPen;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityRancher
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityRancher.class);
    private static final DataParameter<Boolean> RETURN_LOST_ANIMALS = EntityDataManager.func_187226_a(EntityRancher.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> FEED_COW = EntityDataManager.func_187226_a(EntityRancher.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> FEED_SHEEP = EntityDataManager.func_187226_a(EntityRancher.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> FEED_PIG = EntityDataManager.func_187226_a(EntityRancher.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> FEED_CHICKEN = EntityDataManager.func_187226_a(EntityRancher.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> HARVEST_COW = EntityDataManager.func_187226_a(EntityRancher.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> HARVEST_SHEEP = EntityDataManager.func_187226_a(EntityRancher.class, (DataSerializer)DataSerializers.field_187198_h);
    private VillageStructureType priorityPen;

    public EntityRancher(World worldIn) {
        super(worldIn, ProfessionType.RANCHER, VillagerRole.VILLAGER.value);
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.registerAIFilter("return_lost_animals", RETURN_LOST_ANIMALS);
        this.registerAIFilter("feed_cow", FEED_COW);
        this.registerAIFilter("feed_sheep", FEED_SHEEP);
        this.registerAIFilter("feed_chicken", FEED_CHICKEN);
        this.registerAIFilter("feed_pig", FEED_PIG);
        this.registerAIFilter("harvest_cow", HARVEST_COW);
        this.registerAIFilter("harvest_sheep", HARVEST_SHEEP);
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.getDesireSet().addItemDesire(new ItemDesire("CowFood", VillageStructureCowPen.isFood(), 1, 6, 16, v -> this.isAIFilterEnabled("feed_cow")));
        this.getDesireSet().addItemDesire(new ItemDesire("ChickenFood", VillageStructureChickenCoop.isFood(), 1, 6, 16, v -> this.isAIFilterEnabled("feed_chicken")));
        this.getDesireSet().addItemDesire(new ItemDesire("PigFood", VillageStructurePigPen.isFood(), 1, 6, 16, v -> this.isAIFilterEnabled("feed_pig")));
        this.getDesireSet().addItemDesire(new ItemDesire("SheepFood", VillageStructureSheepPen.isFood(), 1, 6, 16, v -> this.isAIFilterEnabled("feed_sheep")));
        this.getDesireSet().addItemDesire(new ItemDesire((Item)Items.field_151097_aZ, 1, 1, 1, v -> this.isAIFilterEnabled("harvest_sheep")));
        this.getDesireSet().addItemDesire(new ItemDesire(Items.field_151133_ar, 1, 2, 2, v -> this.isAIFilterEnabled("harvest_cow")));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIAttachLeadToLostAnimal(this, p -> p.isWorkTime()));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIReturnLostAnimal(this));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIFeedAnimal(this, VillageStructureType.COW_PEN, 0, "feed_cow"));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIFeedAnimal(this, VillageStructureType.CHICKEN_COOP, 1, "feed_chicken"));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIFeedAnimal(this, VillageStructureType.SHEEP_PEN, 2, "feed_sheep"));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIFeedAnimal(this, VillageStructureType.PIG_PEN, 3, "feed_pig"));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIHarvestAnimal(this, VillageStructureType.COW_PEN, new ItemStack(Items.field_151133_ar), p -> p.isWorkTime() && p.isAIFilterEnabled("harvest_cow") && p.getVillage().getStorageCount(i -> i.func_77973_b() == Items.field_151117_aB) <= 3));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIHarvestAnimal(this, VillageStructureType.SHEEP_PEN, new ItemStack((Item)Items.field_151097_aZ), p -> p.isWorkTime() && p.isAIFilterEnabled("harvest_sheep")));
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
    }

    @Override
    public Predicate<ItemStack> isHarvestItem() {
        return p -> p.func_77973_b() == Items.field_151117_aB || p.func_77973_b() == Items.field_151110_aK || p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150325_L) || super.isHarvestItem().test((ItemStack)p);
    }

    public void setPriorityPen(VillageStructureType t) {
        this.priorityPen = t;
    }

    public VillageStructureType getPriorityPen() {
        return this.priorityPen;
    }

    @Override
    protected boolean canVillagerPickupItem(ItemStack itemIn) {
        return itemIn.func_77973_b() == Items.field_151110_aK || itemIn.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150325_L) || super.canVillagerPickupItem(itemIn);
    }

    static {
        animHandler.addAnim("tektopia", "villager_take", "rancher_m", true);
        EntityVillagerTek.setupAnimations(animHandler, "rancher_m");
    }
}

