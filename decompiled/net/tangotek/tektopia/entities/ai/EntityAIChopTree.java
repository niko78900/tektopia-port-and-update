/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockOldLog
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.BlockSapling
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Enchantments
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemTool
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.ModBlocks;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.blockfinder.TreeScanner;
import net.tangotek.tektopia.entities.EntityLumberjack;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;

public class EntityAIChopTree
extends EntityAIMoveToBlock {
    private final boolean replant;
    private final EntityVillagerTek villager;
    private BlockPlanks.EnumType treeType;
    private BlockPos treePos;
    private ItemStack bestAxe;
    private boolean active = false;
    private int chopTime = 0;

    public EntityAIChopTree(EntityVillagerTek entityIn, boolean replant) {
        super(entityIn);
        this.replant = replant;
        this.villager = entityIn;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        if (this.villager.getVillage() != null) {
            this.releaseTreeClaim();
            BlockPos treePos = this.villager.getVillage().requestBlock(Blocks.field_150364_r);
            if (treePos != null) {
                BlockPlanks.EnumType treeType = (BlockPlanks.EnumType)this.villager.field_70170_p.func_180495_p(treePos).func_177229_b((IProperty)BlockOldLog.field_176301_b);
                if (this.villager.isAIFilterEnabled("chop_tree_" + treeType.func_176610_l())) {
                    return treePos;
                }
            }
        }
        return null;
    }

    @Override
    protected BlockPos findWalkPos() {
        BlockPos diff = this.villager.func_180425_c().func_177973_b((Vec3i)this.destinationPos);
        EnumFacing facing = EnumFacing.func_176737_a((float)diff.func_177958_n(), (float)0.0f, (float)diff.func_177952_p());
        BlockPos focusPos = this.destinationPos.func_177972_a(facing);
        if (this.isWalkable(focusPos, this.villager)) {
            return focusPos;
        }
        BlockPos closest = null;
        double minDist = Double.MAX_VALUE;
        for (int x = -3; x <= 3; ++x) {
            for (int z = -3; z <= 3; ++z) {
                double thisDist;
                BlockPos testPos = focusPos.func_177982_a(x, 0, z);
                if (!this.isWalkable(testPos, this.villager) || !((thisDist = testPos.func_177951_i((Vec3i)focusPos)) < minDist)) continue;
                minDist = thisDist;
                closest = testPos;
            }
        }
        return closest;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.hasVillage() && this.villager.isWorkTime() && this.villager.getVillage().hasBlock(Blocks.field_150364_r)) {
            List<ItemStack> axeList = this.villager.getInventory().getItems(EntityLumberjack.getBestAxe(this.villager), 1);
            if (!axeList.isEmpty()) {
                this.bestAxe = axeList.get(0);
                return super.func_75250_a();
            }
            this.villager.setThought(EntityVillagerTek.VillagerThought.AXE);
            this.bestAxe = null;
        }
        return false;
    }

    @Override
    public void func_75249_e() {
        this.treePos = this.destinationPos;
        this.active = true;
        this.chopTime = 0;
        super.func_75249_e();
    }

    @Override
    public boolean func_75253_b() {
        return this.active && !this.bestAxe.func_190926_b();
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    public void func_75246_d() {
        super.func_75246_d();
        if (this.chopTime > 0) {
            --this.chopTime;
            if (this.chopTime == 30) {
                this.stopChopping();
                this.chopTree();
            } else if (this.chopTime == 0) {
                this.pickUpItems();
                this.active = false;
                if (this.replant && this.treeType != null && this.consumeSaplingItem(this.treeType)) {
                    this.villager.field_70170_p.func_180501_a(this.treePos, Blocks.field_150345_g.func_176223_P().func_177226_a((IProperty)BlockSapling.field_176480_a, (Comparable)this.treeType), 2);
                }
            }
            if (this.chopTime >= 30 && (this.chopTime - 30) % 20 == 0 && this.villager.func_70681_au().nextInt(this.villager.getSkillLerp(ProfessionType.LUMBERJACK, 2, 6)) == 0) {
                this.villager.damageItem(this.bestAxe, 1);
            }
            this.villager.faceLocation(this.treePos.func_177958_n(), this.treePos.func_177952_p(), 30.0f);
        }
    }

    public boolean func_75252_g() {
        return this.chopTime > 30 || this.chopTime <= 0;
    }

    @Override
    protected void onStuck() {
        this.active = false;
        super.onStuck();
    }

    @Override
    protected boolean attemptStuckFix() {
        int trimmed = this.trimLeaves();
        return trimmed > 0;
    }

    @Override
    protected void onArrival() {
        if (this.isNearDestination(2.5)) {
            if (TreeScanner.treeTest(this.villager.field_70170_p, this.treePos) != null) {
                this.startChopping();
                super.onArrival();
            } else {
                this.active = false;
            }
        } else if (this.isNearWalkPos()) {
            int trimmed = this.trimLeaves();
            if (trimmed > 0) {
                this.doMove();
            } else {
                this.active = false;
            }
        }
    }

    @Override
    protected void onPathFailed(BlockPos pos) {
        this.active = false;
        super.onPathFailed(pos);
    }

    private static int getToolMaterialValue(ItemTool tool) {
        if (tool.func_77861_e().equals("STONE")) {
            return 25;
        }
        if (tool.func_77861_e().equals("IRON")) {
            return 35;
        }
        if (tool.func_77861_e().equals("DIAMOND")) {
            return 45;
        }
        return 15;
    }

    public static int getChopCount(EntityVillagerTek villager, ItemStack bestAxe) {
        int skill = villager.getSkill(ProfessionType.LUMBERJACK);
        int enchantLevel = EnchantmentHelper.func_77506_a((Enchantment)Enchantments.field_185305_q, (ItemStack)bestAxe);
        int materialValue = EntityAIChopTree.getToolMaterialValue((ItemTool)bestAxe.func_77973_b());
        float damagePerChop = (float)skill + 70.0f + (float)materialValue + (float)(enchantLevel * 8);
        return Math.max(Math.round(1500.0f / damagePerChop), 1);
    }

    private void startChopping() {
        int chopCount = EntityAIChopTree.getChopCount(this.villager, this.bestAxe);
        this.chopTime = chopCount * 20 + 30;
        this.villager.func_70661_as().func_75499_g();
        this.villager.equipActionItem(this.bestAxe);
        this.villager.playServerAnimation("villager_chop");
    }

    private void stopChopping() {
        this.villager.unequipActionItem();
        this.villager.stopServerAnimation("villager_chop");
    }

    private void chopTree() {
        int logsChopped = 0;
        IBlockState blockState = this.villager.field_70170_p.func_180495_p(this.treePos);
        if (TreeScanner.isLog(blockState)) {
            this.treeType = (BlockPlanks.EnumType)blockState.func_177229_b((IProperty)BlockOldLog.field_176301_b);
            switch (this.treeType) {
                case OAK: {
                    logsChopped = this.chopTreeOak(this.treePos);
                    break;
                }
                case BIRCH: 
                case JUNGLE: 
                case SPRUCE: {
                    logsChopped = this.chopTreeStraight(this.treePos);
                }
            }
            if (logsChopped > 0) {
                this.villager.throttledSadness(-2);
                this.villager.tryAddSkill(ProfessionType.LUMBERJACK, 7);
                this.villager.modifyHunger(-logsChopped / 2);
                this.villager.debugOut("ChopTree [ " + this.treePos.func_177958_n() + ", " + this.treePos.func_177952_p() + "] Chopped: " + logsChopped);
            }
        }
    }

    private int chopTreeOak(BlockPos treePos) {
        int count = 0;
        BlockPos trunkPos = treePos;
        int skill = this.villager.getSkill(ProfessionType.LUMBERJACK);
        while (this.chopLog(trunkPos, count == 0 ? true : this.logDropCheck(skill), false)) {
            trunkPos = trunkPos.func_177984_a();
            ++count;
        }
        if (count >= 6) {
            trunkPos = treePos.func_177981_b(3);
            for (BlockPos branchPos : BlockPos.func_177980_a((BlockPos)trunkPos.func_177982_a(-5, 0, -5), (BlockPos)trunkPos.func_177982_a(5, 9, 5))) {
                if (!this.chopLog(branchPos, this.logDropCheck(skill), true)) continue;
                ++count;
            }
        }
        return count;
    }

    private int chopTreeStraight(BlockPos treePos) {
        int count = 0;
        int skill = this.villager.getSkill(ProfessionType.LUMBERJACK);
        while (this.chopLog(treePos, count == 0 ? true : this.logDropCheck(skill), false)) {
            treePos = treePos.func_177984_a();
            ++count;
        }
        return count;
    }

    private boolean logDropCheck(int skill) {
        return this.villager.func_70681_au().nextInt(5) == 0;
    }

    private boolean hasAdjacentLeaf(BlockPos pos) {
        return TreeScanner.isLeaf(this.villager.field_70170_p.func_180495_p(pos.func_177976_e())) || TreeScanner.isLeaf(this.villager.field_70170_p.func_180495_p(pos.func_177974_f())) || TreeScanner.isLeaf(this.villager.field_70170_p.func_180495_p(pos.func_177978_c())) || TreeScanner.isLeaf(this.villager.field_70170_p.func_180495_p(pos.func_177968_d())) || TreeScanner.isLeaf(this.villager.field_70170_p.func_180495_p(pos.func_177984_a()));
    }

    private boolean chopLog(BlockPos pos, boolean dropBlock, boolean adjacentLeafCheck) {
        if (TreeScanner.isLog(this.villager.field_70170_p.func_180495_p(pos)) && (!adjacentLeafCheck || this.hasAdjacentLeaf(pos))) {
            ModBlocks.villagerDestroyBlock(pos, this.villager, dropBlock, true);
            if (this.treeType == BlockPlanks.EnumType.JUNGLE && this.villager.func_70681_au().nextInt(8) == 0) {
                this.villager.func_70099_a(new ItemStack(Item.func_150898_a((Block)Blocks.field_150345_g), 1, 3), 0.0f);
            }
            return true;
        }
        return false;
    }

    private boolean consumeSaplingItem(BlockPlanks.EnumType blockType) {
        List<ItemStack> removedSaplings = this.villager.getInventory().removeItems(is -> EntityLumberjack.isSapling().test((ItemStack)is) && is.func_77960_j() == blockType.func_176839_a(), 1);
        return !removedSaplings.isEmpty();
    }

    private int trimLeaves() {
        BlockPos pos = this.villager.func_180425_c();
        int trimmed = 0;
        for (BlockPos bp : BlockPos.func_191532_a((int)(pos.func_177958_n() - 2), (int)pos.func_177956_o(), (int)(pos.func_177952_p() - 2), (int)(pos.func_177958_n() + 2), (int)(pos.func_177956_o() + 2), (int)(pos.func_177952_p() + 2))) {
            IBlockState blockState = this.villager.field_70170_p.func_180495_p(bp);
            if (!TreeScanner.isLeaf(blockState) || !((Boolean)blockState.func_177229_b((IProperty)BlockLeaves.field_176237_a)).booleanValue()) continue;
            blockState.func_177230_c().func_176226_b(this.villager.field_70170_p, bp, blockState, 0);
            this.villager.field_70170_p.func_175698_g(bp);
            ++trimmed;
        }
        return trimmed;
    }

    private void pickUpItems() {
        if (!this.villager.field_70128_L) {
            for (EntityItem entityitem : this.villager.field_70170_p.func_72872_a(EntityItem.class, this.villager.func_174813_aQ().func_72314_b(8.0, 12.0, 8.0))) {
                if (entityitem.field_70128_L || entityitem.func_92059_d().func_190926_b() || entityitem.func_174874_s()) continue;
                this.villager.func_175445_a(entityitem);
            }
        }
    }

    private void releaseTreeClaim() {
        if (this.treePos != null && this.villager.getVillage() != null) {
            this.villager.getVillage().releaseBlockClaim(Blocks.field_150364_r, this.treePos);
            this.treePos = null;
        }
    }

    @Override
    public void func_75251_c() {
        this.stopChopping();
        this.active = false;
        this.releaseTreeClaim();
        super.func_75251_c();
    }
}

