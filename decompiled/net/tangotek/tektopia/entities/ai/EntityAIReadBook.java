/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIReadBook
extends EntityAIMoveToBlock {
    private boolean active = false;
    private int readTime = 0;
    VillageStructure library = null;
    protected final EntityVillagerTek villager;
    private static final int READ_TIME = 200;

    public EntityAIReadBook(EntityVillagerTek entityIn) {
        super(entityIn);
        this.villager = entityIn;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        this.library = this.villager.getVillage().getNearestStructure(VillageStructureType.LIBRARY, this.villager.func_180425_c());
        if (this.library != null) {
            BlockPos bookPos = this.library.getUnoccupiedSpecialBlock(Blocks.field_150342_X);
            if (bookPos != null) {
                return bookPos;
            }
            this.villager.setThought(EntityVillagerTek.VillagerThought.BOOKSHELF);
        }
        return null;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.hasVillage() && this.villager.isLearningTime() && this.villager.getIntelligence() < this.villager.getMaxIntelligence()) {
            return super.func_75250_a();
        }
        return false;
    }

    @Override
    public void func_75249_e() {
        this.library.occupySpecialBlock(this.destinationPos);
        this.active = true;
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
        super.func_75246_d();
        if (this.readTime > 0) {
            if (this.readTime == 190) {
                this.villager.equipActionItem(new ItemStack(Items.field_151122_aG));
            }
            if (this.villager.func_70681_au().nextInt(400) == 0) {
                this.villager.modifyHappy(2);
            }
            int extra = (int)MathHelper.func_151238_b((double)0.0, (double)3000.0, (double)((double)this.villager.getIntelligence() / 100.0));
            if (this.villager.func_70681_au().nextInt(500 + extra) == 0) {
                this.villager.addIntelligence(1);
            }
            --this.readTime;
            if (this.readTime == 10) {
                this.stopReading();
            } else if (this.readTime <= 0) {
                this.active = false;
            }
        }
    }

    @Override
    protected void onStuck() {
        this.active = false;
        super.onStuck();
    }

    @Override
    protected void onArrival() {
        this.startReading();
        super.onArrival();
    }

    private void startReading() {
        this.readTime = 200;
        this.villager.func_70661_as().func_75499_g();
        this.villager.playServerAnimation("villager_read");
    }

    private void stopReading() {
        this.villager.stopServerAnimation("villager_read");
        this.villager.unequipActionItem();
    }

    @Override
    public void func_75251_c() {
        this.library.vacateSpecialBlock(this.destinationPos);
        this.active = false;
        this.readTime = 0;
        this.stopReading();
        super.func_75251_c();
    }
}

