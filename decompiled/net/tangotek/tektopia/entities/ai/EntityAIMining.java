/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Enchantments
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemTool
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModBlocks;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityMiner;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.structures.VillageStructureMineshaft;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityAIMining
extends EntityAIMoveToBlock {
    private boolean active = false;
    private BlockPos miningPos;
    private BlockPos orePos;
    private VillageStructureMineshaft mineshaft;
    private int mineTime = 0;
    private MiningPhase phase = null;
    private ItemStack toolUsed = null;
    protected final EntityVillagerTek villager;
    private final Predicate<EntityVillagerTek> shouldPred;

    public EntityAIMining(EntityVillagerTek entityIn, Predicate<EntityVillagerTek> shouldPred) {
        super(entityIn);
        this.villager = entityIn;
        this.shouldPred = shouldPred;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        if (this.villager.getVillage() != null) {
            this.mineshaft = this.villager.getVillage().requestMineshaft(this.villager, m -> m.canMine(), (C, B) -> C.getTunnelLength() < B.getTunnelLength());
            if (this.mineshaft != null) {
                return this.mineshaft.getWalkingPos();
            }
        }
        return null;
    }

    @Override
    protected BlockPos findWalkPos() {
        return this.destinationPos;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public boolean func_75250_a() {
        if (!this.villager.isAITick("mining")) return false;
        if (!this.villager.hasVillage()) return false;
        if (this.villager.isHungry()) return false;
        if (!this.shouldPred.test(this.villager)) return false;
        this.toolUsed = null;
        List<ItemStack> toolList = this.villager.getInventory().getItems(EntityMiner.getBestPick(this.villager), 1);
        if (toolList.isEmpty()) {
            this.villager.setThought(EntityVillagerTek.VillagerThought.PICK);
            return false;
        }
        if (this.villager.getInventory().getItemCount(EntityMiner.isTorch()) < 10) {
            this.villager.setThought(EntityVillagerTek.VillagerThought.TORCH);
            return false;
        }
        this.toolUsed = toolList.get(0);
        return super.func_75250_a();
    }

    @Override
    protected void updateFacing() {
        if (this.phase == MiningPhase.WALKING_TO_TUNNEL) {
            super.updateFacing();
        }
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    public void func_75249_e() {
        this.active = true;
        this.miningPos = this.mineshaft.getMiningPos();
        this.phase = MiningPhase.WALKING_TO_TUNNEL;
        super.func_75249_e();
    }

    @Override
    public boolean func_75253_b() {
        return this.active && this.shouldPred.test(this.villager) && !this.villager.isHungry() && (this.mineshaft.getTunnelMiner() == null || this.mineshaft.getTunnelMiner() == this.villager) && !this.toolUsed.func_190926_b();
    }

    @Override
    public void func_75246_d() {
        super.func_75246_d();
        if (this.phase == MiningPhase.BREAK_TUNNEL) {
            if (this.mineTime > 0) {
                --this.mineTime;
                if (this.mineTime == 20) {
                    this.stopMining();
                    if (!VillageStructureMineshaft.isAir(this.mineshaft, this.miningPos.func_177984_a())) {
                        this.mineBlock(this.miningPos.func_177984_a(), 15);
                    } else if (!VillageStructureMineshaft.isAir(this.mineshaft, this.miningPos)) {
                        this.mineBlock(this.miningPos, 15);
                    }
                } else if (!(this.mineTime != 1 || VillageStructureMineshaft.isAir(this.mineshaft, this.miningPos.func_177984_a()) && VillageStructureMineshaft.isAir(this.mineshaft, this.miningPos))) {
                    this.startMining();
                }
            } else {
                this.phase = MiningPhase.ORE_SCAN;
            }
        } else if (this.phase == MiningPhase.ORE_SCAN) {
            if (this.mineTime > 0) {
                --this.mineTime;
                if (this.mineTime == 20) {
                    this.stopMining();
                    this.mineBlock(this.orePos, 5);
                }
            } else {
                this.orePos = this.mineshaft.findOre(this.miningPos, 0, EnumFacing.DOWN);
                if (this.orePos == null) {
                    this.orePos = this.mineshaft.findOre(this.miningPos.func_177984_a(), 0, EnumFacing.UP);
                }
                if (this.orePos == null) {
                    this.phase = MiningPhase.REPAIR_TUNNEL;
                } else {
                    this.startMining();
                }
            }
        } else if (this.phase == MiningPhase.REPAIR_TUNNEL) {
            if (this.mineTime > 0) {
                --this.mineTime;
            } else if (!this.repairTunnel(this.miningPos)) {
                this.mineshaft.tryPlaceTorch(this.villager);
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
        if (this.phase == MiningPhase.WALKING_TO_TUNNEL) {
            this.phase = MiningPhase.BREAK_TUNNEL;
            if (!VillageStructureMineshaft.isAir(this.mineshaft, this.miningPos.func_177984_a()) || !VillageStructureMineshaft.isAir(this.mineshaft, this.miningPos)) {
                this.startMining();
            }
        }
        super.onArrival();
    }

    @Override
    protected void onPathFailed(BlockPos pos) {
        if (this.phase == MiningPhase.WALKING_TO_TUNNEL && this.villager.getVillage() != null) {
            System.err.println("Failed to find path to mining tunnel - " + this.miningPos);
        }
        super.onPathFailed(pos);
    }

    private boolean repairTunnel(BlockPos pos) {
        BlockPos flawPos = VillageStructureMineshaft.getTunnelFlaw(this.mineshaft, pos);
        if (flawPos != null) {
            this.villager.field_70170_p.func_180501_a(flawPos, Blocks.field_150347_e.func_176223_P(), 2);
            this.mineTime = 30;
            return true;
        }
        return false;
    }

    private static int getToolMaterialValue(ItemTool tool) {
        if (tool.func_77861_e().equals("STONE")) {
            return 15;
        }
        if (tool.func_77861_e().equals("IRON")) {
            return 9;
        }
        if (tool.func_77861_e().equals("DIAMOND")) {
            return 6;
        }
        return 20;
    }

    public static int getSwingCount(EntityVillagerTek villager, ItemStack toolUsed) {
        int skill = villager.getSkill(ProfessionType.MINER);
        int enchantLevel = EnchantmentHelper.func_77506_a((Enchantment)Enchantments.field_185305_q, (ItemStack)toolUsed);
        int materialValue = EntityAIMining.getToolMaterialValue((ItemTool)toolUsed.func_77973_b());
        float enchantMult = (10.0f - (float)enchantLevel) / 10.0f;
        float damagePerChop = (int)(((float)skill + 50.0f) / ((float)materialValue * enchantMult)) + 3;
        return Math.max(Math.round(80.0f / damagePerChop), 1);
    }

    private void startMining() {
        if (!this.toolUsed.func_190926_b()) {
            int swingCount = EntityAIMining.getSwingCount(this.villager, this.toolUsed);
            this.mineTime = swingCount * 20 + 20;
            this.villager.func_70661_as().func_75499_g();
            this.villager.equipActionItem(this.toolUsed);
            this.villager.playServerAnimation("villager_chop");
        }
    }

    private void stopMining() {
        this.villager.unequipActionItem();
        this.villager.stopServerAnimation("villager_chop");
    }

    private void mineBlock(BlockPos minePos, int skillChance) {
        boolean dropBlock = VillageStructureMineshaft.isOre(this.villager.field_70170_p, minePos);
        if (this.villager.field_70170_p.func_180495_p(minePos).func_177230_c() == Blocks.field_150348_b) {
            this.tryBonusOre(this.villager);
        }
        ModBlocks.villagerDestroyBlock(minePos, this.villager, dropBlock, this.villager.field_70170_p.func_180495_p(minePos).func_177230_c() != Blocks.field_150412_bA);
        this.villager.modifyHunger(-1);
        this.villager.throttledSadness(-this.villager.func_70681_au().nextInt(2) - 1);
        this.villager.tryAddSkill(ProfessionType.MINER, skillChance);
        if (this.villager.func_70681_au().nextInt(3) != 0) {
            this.villager.damageItem(this.toolUsed, 1);
        }
        this.villager.addJob(new TickJob(20, 0, false, () -> this.villager.pickupItems(4)));
    }

    private void tryBonusOre(EntityVillagerTek villager) {
        int skill = this.villager.getSkill(ProfessionType.MINER);
        float chance = 0.02f + (float)skill / 100.0f * 0.04f;
        Random rnd = this.villager.func_70681_au();
        if (rnd.nextFloat() <= chance) {
            ItemStack itemStack = this.oreToItem(VillageStructureMineshaft.getRegrowBlock(villager));
            villager.getInventory().func_174894_a(ModItems.makeTaggedItem(itemStack, ItemTagType.VILLAGER));
            this.villager.modifyHappy(3);
            this.villager.func_184185_a(SoundEvents.field_187802_ec, 1.2f, 1.0f);
        }
    }

    private ItemStack oreToItem(Block block) {
        if (block == Blocks.field_150482_ag) {
            return new ItemStack(Items.field_151045_i);
        }
        if (block == Blocks.field_150369_x) {
            return new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b());
        }
        if (block == Blocks.field_150352_o) {
            return new ItemStack(Item.func_150898_a((Block)Blocks.field_150352_o));
        }
        if (block == Blocks.field_150366_p) {
            return new ItemStack(Item.func_150898_a((Block)Blocks.field_150366_p));
        }
        if (block == Blocks.field_150450_ax) {
            return new ItemStack(Items.field_151137_ax);
        }
        return new ItemStack(Items.field_151044_h);
    }

    @Override
    public void func_75251_c() {
        super.func_75251_c();
        this.stopMining();
        this.mineshaft.checkExtendTunnel();
        this.mineshaft = null;
        this.miningPos = null;
        this.toolUsed = null;
        this.phase = MiningPhase.WALKING_TO_TUNNEL;
    }

    private static enum MiningPhase {
        WALKING_TO_TUNNEL,
        BREAK_TUNNEL,
        ORE_SCAN,
        REPAIR_TUNNEL;

    }
}

