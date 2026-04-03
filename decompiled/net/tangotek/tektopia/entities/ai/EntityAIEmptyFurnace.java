/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIEmptyFurnace
extends EntityAIMoveToBlock {
    private TileEntityFurnace furnace = null;
    private int pickupTime = -1;
    private final VillageStructureType[] structureTypes;
    private final Predicate<ItemStack> itemPred;
    private final EntityVillagerTek villager;
    protected final Predicate<EntityVillagerTek> shouldPred;

    public EntityAIEmptyFurnace(EntityVillagerTek v, VillageStructureType structure, Predicate<ItemStack> itemPred) {
        this(v, new VillageStructureType[]{structure}, itemPred, p -> true);
    }

    public EntityAIEmptyFurnace(EntityVillagerTek v, VillageStructureType[] structures, Predicate<ItemStack> itemPred, Predicate<EntityVillagerTek> shouldPred) {
        super(v);
        this.structureTypes = structures;
        this.villager = v;
        this.itemPred = itemPred;
        this.shouldPred = shouldPred;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.hasVillage() && this.villager.isWorkTime() && this.villager.getInventory().hasSlotFree() && this.shouldPred.test(this.villager)) {
            List<VillageStructure> blacksmiths = this.villager.getVillage().getStructures(this.structureTypes);
            for (VillageStructure smith : blacksmiths) {
                List<BlockPos> furnaces = smith.getSpecialBlocks(Blocks.field_150460_al);
                for (BlockPos pos : furnaces) {
                    TileEntityFurnace furnace;
                    ItemStack inFurnaceStack;
                    TileEntity te = this.villager.field_70170_p.func_175625_s(pos);
                    if (!(te instanceof TileEntityFurnace) || !this.itemPred.test(inFurnaceStack = (furnace = (TileEntityFurnace)te).func_70301_a(2))) continue;
                    this.furnace = furnace;
                    return super.func_75250_a();
                }
            }
        }
        return false;
    }

    @Override
    public void func_75246_d() {
        --this.pickupTime;
        if (this.pickupTime == 30) {
            this.extractItem();
        }
        super.func_75246_d();
    }

    @Override
    protected boolean isNearWalkPos() {
        return this.navigator.func_180425_c().func_177951_i((Vec3i)this.destinationPos) < 2.25;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        return this.furnace.func_174877_v();
    }

    @Override
    public boolean func_75253_b() {
        if (this.pickupTime > 0) {
            return true;
        }
        return super.func_75253_b();
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    protected void onArrival() {
        this.pickupTime = 40;
        super.onArrival();
    }

    private void extractItem() {
        ItemStack itemStack;
        if (!this.furnace.func_145837_r() && (itemStack = this.furnace.func_70304_b(2)) != ItemStack.field_190927_a) {
            ItemStack fuelItem = this.furnace.func_70301_a(1);
            int villagerSmelt = this.furnace.getTileData().func_74762_e("villager_smelt");
            int villagerFuel = this.furnace.getTileData().func_74762_e("villager_fuel");
            int fuelRemaining = fuelItem.func_190916_E() + (this.furnace.func_145950_i() ? 1 : 0);
            if ((villagerSmelt -= itemStack.func_190916_E()) >= 0 && villagerFuel > 0) {
                ModItems.makeTaggedItem(itemStack, ItemTagType.VILLAGER);
            }
            if (fuelRemaining > villagerFuel) {
                fuelRemaining = 0;
            }
            if (villagerSmelt > 0) {
                this.furnace.getTileData().func_74768_a("villager_smelt", villagerSmelt);
            } else {
                this.furnace.getTileData().func_82580_o("villager_smelt");
            }
            if (fuelRemaining > 0) {
                this.furnace.getTileData().func_74768_a("villager_fuel", fuelRemaining);
            } else {
                this.furnace.getTileData().func_82580_o("villager_fuel");
            }
            this.villager.getInventory().func_174894_a(itemStack);
        }
    }
}

