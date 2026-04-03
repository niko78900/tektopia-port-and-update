/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockHorizontal
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyDirection
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.Mirror
 *  net.minecraft.util.Rotation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.blocks;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.TekVillager;

public class BlockChair
extends BlockHorizontal {
    public static final PropertyDirection FACING = BlockHorizontal.field_185512_D;
    protected String name;
    protected static final AxisAlignedBB CHAIR_AABB = new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.45, 0.75);

    public BlockChair(String name) {
        super(Material.field_151594_q);
        this.name = name;
        this.func_149663_c(name);
        this.setRegistryName(name);
        this.func_149647_a(CreativeTabs.field_78031_c);
    }

    public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CHAIR_AABB;
    }

    public void registerItemModel(Item itemBlock) {
        TekVillager.proxy.registerItemRenderer(itemBlock, 0, this.name);
    }

    public Item createItemBlock() {
        return (Item)new ItemBlock((Block)this).setRegistryName(this.getRegistryName());
    }

    public boolean func_149662_c(IBlockState state) {
        return false;
    }

    @Nullable
    public AxisAlignedBB func_180646_a(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return field_185506_k;
    }

    public boolean func_176205_b(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    public boolean func_149686_d(IBlockState state) {
        return false;
    }

    public boolean func_176196_c(World worldIn, BlockPos pos) {
        IBlockState downState = worldIn.func_180495_p(pos.func_177977_b());
        return super.func_176196_c(worldIn, pos) && (downState.func_185896_q() || downState.func_193401_d((IBlockAccess)worldIn, pos.func_177977_b(), EnumFacing.UP) == BlockFaceShape.SOLID);
    }

    public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.func_176223_P().func_177226_a((IProperty)FACING, (Comparable)placer.func_174811_aO().func_176734_d());
    }

    public IBlockState func_176203_a(int meta) {
        EnumFacing enumfacing = EnumFacing.func_82600_a((int)meta);
        if (enumfacing.func_176740_k() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        return this.func_176223_P().func_177226_a((IProperty)FACING, (Comparable)enumfacing);
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer func_180664_k() {
        return BlockRenderLayer.CUTOUT;
    }

    public int func_176201_c(IBlockState state) {
        return ((EnumFacing)state.func_177229_b((IProperty)FACING)).func_176745_a();
    }

    public IBlockState func_185499_a(IBlockState state, Rotation rot) {
        return state.func_177226_a((IProperty)FACING, (Comparable)rot.func_185831_a((EnumFacing)state.func_177229_b((IProperty)FACING)));
    }

    public IBlockState func_185471_a(IBlockState state, Mirror mirrorIn) {
        return state.func_185907_a(mirrorIn.func_185800_a((EnumFacing)state.func_177229_b((IProperty)FACING)));
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{FACING});
    }

    public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}

