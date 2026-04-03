/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.storage.VillagerInventory;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAISmelting
extends EntityAIMoveToBlock {
    private TileEntityFurnace furnace = null;
    private int smeltTime = -1;
    private final VillageStructureType[] structureTypes;
    private final Function<ItemStack, Integer> getSmeltable;
    private final Runnable onSmelt;
    protected final EntityVillagerTek villager;
    protected final Predicate<EntityVillagerTek> shouldPred;

    public EntityAISmelting(EntityVillagerTek v, VillageStructureType structure, Predicate<EntityVillagerTek> shouldPred, Function<ItemStack, Integer> getSmeltable, Runnable onSmelt) {
        this(v, new VillageStructureType[]{structure}, shouldPred, getSmeltable, onSmelt);
    }

    public EntityAISmelting(EntityVillagerTek v, VillageStructureType[] structures, Predicate<EntityVillagerTek> shouldPred, Function<ItemStack, Integer> getSmeltable, Runnable onSmelt) {
        super(v);
        this.villager = v;
        this.structureTypes = structures;
        this.getSmeltable = getSmeltable;
        this.onSmelt = onSmelt;
        this.shouldPred = shouldPred;
    }

    private ItemStack getFuel(TileEntityFurnace furnace) {
        return furnace.func_70301_a(1);
    }

    private boolean hasFuel(TileEntityFurnace furnace) {
        return !this.getFuel(furnace).func_190926_b();
    }

    private boolean canAddfuel(TileEntityFurnace furnace) {
        return !furnace.func_145837_r() && !this.hasFuel(furnace) && !furnace.func_145950_i();
    }

    private boolean canAddSmeltable(TileEntityFurnace furnace) {
        return !furnace.func_145837_r() && furnace.func_70301_a(0).func_190926_b() && this.hasFuel(furnace);
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.hasVillage() && this.villager.isWorkTime() && this.shouldPred.test(this.villager)) {
            List<VillageStructure> structures = this.villager.getVillage().getStructures(this.structureTypes);
            for (VillageStructure smith : structures) {
                List<BlockPos> furnaces = smith.getSpecialBlocks(Blocks.field_150460_al);
                for (BlockPos pos : furnaces) {
                    ItemStack smeltItem;
                    ItemStack fuelItem;
                    TileEntity te = this.villager.field_70170_p.func_175625_s(pos);
                    if (!(te instanceof TileEntityFurnace)) continue;
                    TileEntityFurnace furnace = (TileEntityFurnace)te;
                    if (this.canAddfuel(furnace) && !(fuelItem = EntityAISmelting.getFuel(this.villager)).func_190926_b()) {
                        this.furnace = furnace;
                        this.villager.equipActionItem(fuelItem);
                        return super.func_75250_a();
                    }
                    if (!this.canAddSmeltable(furnace) || (smeltItem = this.villager.getInventory().getItem(this.getSmeltable)).func_190926_b()) continue;
                    this.furnace = furnace;
                    this.villager.equipActionItem(smeltItem);
                    return super.func_75250_a();
                }
            }
        }
        return false;
    }

    @Override
    public boolean func_75253_b() {
        if (this.smeltTime > 0) {
            return true;
        }
        return super.func_75253_b();
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    protected BlockPos getDestinationBlock() {
        if (this.furnace != null) {
            return this.furnace.func_174877_v();
        }
        return null;
    }

    @Override
    public void func_75246_d() {
        boolean done;
        --this.smeltTime;
        if (this.smeltTime == 5 && !(done = this.depositFuel())) {
            boolean bl = this.depositSmeltable();
        }
        super.func_75246_d();
    }

    @Override
    protected void onArrival() {
        this.smeltTime = 40;
        super.onArrival();
    }

    private boolean depositFuel() {
        List<ItemStack> fuel;
        if (this.canAddfuel(this.furnace) && !(fuel = this.villager.getInventory().removeItems(EntityAISmelting.getBestFuel(), 1)).isEmpty() && fuel.get(0) != ItemStack.field_190927_a) {
            ItemStack fuelItem = fuel.get(0);
            this.furnace.func_70299_a(1, fuelItem);
            if (ModItems.isTaggedItem(fuelItem, ItemTagType.VILLAGER)) {
                this.furnace.getTileData().func_74768_a("villager_fuel", fuelItem.func_190916_E());
            }
            return true;
        }
        return false;
    }

    private boolean depositSmeltable() {
        if (this.canAddSmeltable(this.furnace)) {
            int cookTime = this.getCookTime(this.furnace);
            List<ItemStack> bestSmeltList = this.villager.getInventory().removeItems(this.getSmeltable, 1);
            if (!bestSmeltList.isEmpty() && bestSmeltList.get(0) != ItemStack.field_190927_a) {
                ItemStack bestSmelt = bestSmeltList.get(0);
                int goalCount = cookTime / this.furnace.func_174904_a(bestSmelt);
                if (goalCount > 1) {
                    bestSmeltList.addAll(this.villager.getInventory().removeItems(p -> ItemStack.func_179545_c((ItemStack)p, (ItemStack)bestSmelt), goalCount - 1));
                }
                int itemCount = VillagerInventory.countItems(bestSmeltList);
                bestSmelt.func_190920_e(itemCount);
                if (ModItems.isTaggedItem(bestSmelt, ItemTagType.VILLAGER)) {
                    this.furnace.getTileData().func_74768_a("villager_smelt", bestSmelt.func_190916_E());
                }
                this.furnace.func_70299_a(0, bestSmelt);
                this.onSmelt.run();
                this.villager.throttledSadness(-2);
                return true;
            }
        }
        return false;
    }

    private int getCookTime(TileEntityFurnace furnace) {
        return TileEntityFurnace.func_145952_a((ItemStack)this.getFuel(furnace)) + furnace.func_174887_a_(0);
    }

    public static ItemStack getFuel(EntityVillagerTek villager) {
        List<ItemStack> fuelItems = villager.getInventory().getItems(EntityAISmelting.getBestFuel(), 1);
        if (fuelItems.size() > 0) {
            return fuelItems.get(0);
        }
        return ItemStack.field_190927_a;
    }

    public static Function<ItemStack, Integer> getBestFuel() {
        return p -> {
            if (p.func_77973_b() == Items.field_151044_h || p.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150364_r)) {
                return TileEntityFurnace.func_145952_a((ItemStack)p);
            }
            return -1;
        };
    }
}

