/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.entities.crafting.Recipe;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAICraftItems
extends EntityAIMoveToBlock {
    protected final EntityVillagerTek villager;
    private final List<Recipe> recipes;
    protected Recipe activeRecipe = null;
    private final String craftAnimation;
    private final ItemStack heldItem;
    private final int craftTime;
    protected int timeRemaining;
    private int currentIteration;
    private final VillageStructureType structureType;
    private VillageStructure structure;
    private final Block machineBlock;
    private final Predicate<EntityVillagerTek> shouldPred;

    public EntityAICraftItems(EntityVillagerTek v, List<Recipe> recipes, String anim, ItemStack heldItem, int craftTime, VillageStructureType structureType, Block machineBlock, Predicate<EntityVillagerTek> shouldPred) {
        super(v);
        this.villager = v;
        this.shouldPred = shouldPred;
        this.recipes = recipes;
        this.craftAnimation = anim;
        this.heldItem = heldItem;
        this.craftTime = craftTime;
        this.timeRemaining = 0;
        this.currentIteration = 0;
        this.structureType = structureType;
        this.machineBlock = machineBlock;
        this.structure = null;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        this.structure = null;
        if (this.structureType == null) {
            return this.villager.func_180425_c();
        }
        if (this.villager.hasVillage()) {
            List<VillageStructure> structs = this.villager.getVillage().getStructures(this.structureType);
            Collections.shuffle(structs);
            for (VillageStructure str : structs) {
                if (this.machineBlock != null) {
                    BlockPos machinePos = str.getUnoccupiedSpecialBlock(this.machineBlock);
                    if (machinePos == null) continue;
                    this.structure = str;
                    return machinePos;
                }
                return str.getRandomFloorTile();
            }
        }
        return null;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.hasVillage() && this.shouldPred.test(this.villager)) {
            for (Recipe r : this.recipes) {
                if (!r.shouldCraft(this.villager) || !r.hasItems(this.villager) || !this.villager.getInventory().canStore(r.getProduct()) || !super.func_75250_a()) continue;
                this.activeRecipe = r;
                return true;
            }
        }
        return false;
    }

    @Override
    public void func_75249_e() {
        if (this.machineBlock != null && this.structure != null) {
            this.structure.occupySpecialBlock(this.destinationPos);
        }
        super.func_75249_e();
    }

    @Override
    public boolean func_75253_b() {
        if (this.timeRemaining >= 0) {
            return true;
        }
        return super.func_75253_b();
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    public void func_75246_d() {
        --this.timeRemaining;
        if (this.timeRemaining > 0) {
            this.villager.faceLocation(this.destinationPos.func_177958_n(), this.destinationPos.func_177952_p(), 30.0f);
        }
        if (this.timeRemaining == this.craftTime) {
            this.startCraft();
        }
        if (this.timeRemaining == 1) {
            ++this.currentIteration;
            this.stopCraft();
            if (this.currentIteration >= this.activeRecipe.getAnimationIterations(this.villager)) {
                ItemStack craftedItem = this.craftItem(this.villager);
                if (craftedItem != null) {
                    ItemStack remainder = this.villager.getInventory().func_174894_a(craftedItem);
                    if (!remainder.func_190926_b()) {
                        this.villager.func_70099_a(remainder, 0.2f);
                    }
                    this.villager.modifyHunger(-this.activeRecipe.getAnimationIterations(this.villager));
                    this.villager.throttledSadness(-3);
                }
            } else {
                this.timeRemaining = this.craftTime + 10;
            }
        }
        super.func_75246_d();
    }

    protected ItemStack craftItem(EntityVillagerTek villager) {
        return this.activeRecipe.craft(villager);
    }

    @Override
    protected void onArrival() {
        this.currentIteration = 0;
        this.timeRemaining = this.craftTime + 20;
        super.onArrival();
    }

    protected void startCraft() {
        this.villager.func_70661_as().func_75499_g();
        if (this.heldItem != null) {
            this.villager.equipActionItem(this.heldItem);
        } else {
            this.villager.equipActionItem(this.activeRecipe.getProduct());
        }
        if (this.craftAnimation != null) {
            this.villager.playServerAnimation(this.craftAnimation);
        }
    }

    protected void stopCraft() {
        if (this.heldItem != null) {
            this.villager.unequipActionItem(this.heldItem);
        } else {
            this.villager.unequipActionItem(this.activeRecipe.getProduct());
        }
        if (this.craftAnimation != null) {
            this.villager.stopServerAnimation(this.craftAnimation);
        }
    }

    @Override
    public void func_75251_c() {
        super.func_75251_c();
        if (this.machineBlock != null && this.structure != null) {
            this.structure.vacateSpecialBlock(this.destinationPos);
        }
        this.stopCraft();
        this.activeRecipe = null;
        this.timeRemaining = 0;
        this.currentIteration = 0;
    }
}
