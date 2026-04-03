/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.entities;

import com.google.common.base.Optional;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIEarthReform;
import net.tangotek.tektopia.entities.ai.EntityAIGrowth;

public class EntityDruid
extends EntityVillagerTek {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityDruid.class);
    private static final DataParameter<Optional<BlockPos>> SPELL_EARTH_REFORM = EntityDataManager.func_187226_a(EntityDruid.class, (DataSerializer)DataSerializers.field_187201_k);
    protected static final DataParameter<Optional<BlockPos>> SPELL_BLOCK = EntityDataManager.func_187226_a(EntityDruid.class, (DataSerializer)DataSerializers.field_187201_k);
    private static final DataParameter<Boolean> CAST_EARTH_REFORM = EntityDataManager.func_187226_a(EntityDruid.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> CAST_GROWTH_CROPS = EntityDataManager.func_187226_a(EntityDruid.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final DataParameter<Boolean> CAST_GROWTH_TREES = EntityDataManager.func_187226_a(EntityDruid.class, (DataSerializer)DataSerializers.field_187198_h);
    private static final int[] blockStateIds;
    private int earthReformTicks = 0;
    private int growthTicks = 0;
    private BlockPos clientSpellBlock;
    private boolean growthFirst = true;

    public EntityDruid(World worldIn) {
        super(worldIn, ProfessionType.DRUID, VillagerRole.VILLAGER.value);
        Runnable runner = () -> this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, ModSoundEvents.earthBlast, SoundCategory.NEUTRAL, 1.0f, this.field_70146_Z.nextFloat() * 0.2f + 0.9f, false);
        if (this.field_70170_p.field_72995_K) {
            this.addAnimationTrigger("tektopia:villager_cast_forward", 64, runner);
        }
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.growthFirst = this.func_70681_au().nextBoolean();
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIEarthReform(this));
        this.field_70714_bg.func_75776_a(50, (EntityAIBase)new EntityAIGrowth(this, 6.0));
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_187214_a(SPELL_EARTH_REFORM, (Object)Optional.absent());
        this.field_70180_af.func_187214_a(SPELL_BLOCK, (Object)Optional.absent());
        this.registerAIFilter("cast_earth_reform", CAST_EARTH_REFORM);
        this.registerAIFilter("cast_growth_crops", CAST_GROWTH_CROPS);
        this.registerAIFilter("cast_growth_trees", CAST_GROWTH_TREES);
    }

    @Override
    protected void randomizeGoals() {
        super.randomizeGoals();
        this.growthFirst = this.func_70681_au().nextBoolean();
    }

    @Override
    public void addVillagerPosition() {
    }

    public BlockPos getSpellBlock() {
        return (BlockPos)((Optional)this.field_70180_af.func_187225_a(SPELL_BLOCK)).orNull();
    }

    public void setSpellBlock(BlockPos castingPos) {
        if (castingPos == null) {
            this.field_70180_af.func_187227_b(SPELL_BLOCK, (Object)Optional.absent());
        } else {
            this.field_70180_af.func_187227_b(SPELL_BLOCK, (Object)Optional.of((Object)castingPos));
        }
    }

    public void setCastingEarthReform(BlockPos pos) {
        this.func_184212_Q().func_187227_b(SPELL_EARTH_REFORM, (Object)Optional.fromNullable((Object)pos));
    }

    @Nullable
    public BlockPos getCastingEarthReform() {
        return (BlockPos)((Optional)this.func_184212_Q().func_187225_a(SPELL_EARTH_REFORM)).orNull();
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
        if (this.field_70170_p.field_72995_K && !this.isSleeping()) {
            BlockPos earthPos;
            int i;
            this.clientSpellBlock = this.getSpellBlock();
            if (this.clientSpellBlock != null) {
                ++this.growthTicks;
                int range = this.getGrowthRange();
                this.spawnRadialParticle(this.func_174791_d(), range, this.growthTicks * 3, 4, EnumParticleTypes.TOTEM);
                int particles = this.growthTicks > 40 ? 7 : 1;
                for (i = 0; i < particles; ++i) {
                    this.field_70170_p.func_175688_a(EnumParticleTypes.TOTEM, (double)this.clientSpellBlock.func_177958_n() + 0.5 + this.func_70681_au().nextGaussian() * (double)range, (double)this.clientSpellBlock.func_177956_o() + 0.5, (double)this.clientSpellBlock.func_177952_p() + 0.5 + this.func_70681_au().nextGaussian() * (double)range, this.func_70681_au().nextGaussian() * 0.1, (double)this.field_70146_Z.nextFloat(), this.func_70681_au().nextGaussian() * 0.1, new int[0]);
                }
            } else {
                this.growthTicks = 0;
            }
            if ((earthPos = this.getCastingEarthReform()) != null) {
                ++this.earthReformTicks;
                if (this.earthReformTicks < 44) {
                    int count = this.earthReformTicks / 3 + 1;
                    for (i = 0; i < count; ++i) {
                        double dx = this.func_70681_au().nextGaussian();
                        double dz = this.func_70681_au().nextGaussian();
                        double dy = this.func_70681_au().nextGaussian();
                        if (i > 0) {
                            this.field_70170_p.func_175688_a(EnumParticleTypes.BLOCK_DUST, this.field_70165_t + dx, this.field_70163_u + dy, this.field_70161_v + dz, 0.0, (double)this.func_70681_au().nextFloat() * 0.5, 0.0, new int[]{blockStateIds[this.func_70681_au().nextInt(blockStateIds.length)]});
                            continue;
                        }
                        this.field_70170_p.func_175688_a(EnumParticleTypes.TOTEM, this.field_70165_t + dx, this.field_70163_u + dy, this.field_70161_v + dz, 0.0, (double)this.func_70681_au().nextFloat() * 0.5, 0.0, new int[]{blockStateIds[this.func_70681_au().nextInt(blockStateIds.length)]});
                    }
                } else if (this.earthReformTicks < 55) {
                    Vec3d vec = new Vec3d((double)earthPos.func_177958_n(), (double)earthPos.func_177956_o(), (double)earthPos.func_177952_p());
                    vec = vec.func_178788_d(this.func_174791_d()).func_72432_b().func_186678_a(0.8);
                    for (i = 0; i < 3; ++i) {
                        double dx = this.func_70681_au().nextGaussian() * 0.1;
                        double dy = this.func_70681_au().nextGaussian() * 0.1;
                        double dz = this.func_70681_au().nextGaussian() * 0.1;
                        this.field_70170_p.func_175688_a(EnumParticleTypes.TOTEM, this.field_70165_t + dx + vec.field_72450_a, this.field_70163_u + 1.3 + dy, this.field_70161_v + dz + vec.field_72449_c, vec.field_72450_a * 4.0, 0.0, vec.field_72449_c * 4.0, new int[0]);
                    }
                }
            } else {
                this.earthReformTicks = 0;
                if (this.func_70681_au().nextInt(10) == 0) {
                    double dx = this.func_70681_au().nextGaussian() * 0.3;
                    double dz = this.func_70681_au().nextGaussian() * 0.3;
                    double dy = (double)this.func_70681_au().nextFloat() * 0.5;
                    this.field_70170_p.func_175688_a(EnumParticleTypes.TOTEM, this.field_70165_t + dx, this.field_70163_u + dy + 0.2, this.field_70161_v + dz, 0.0, (double)this.func_70681_au().nextFloat() * 0.25, 0.0, new int[]{blockStateIds[this.func_70681_au().nextInt(blockStateIds.length)]});
                }
            }
        }
    }

    private void spawnRadialParticle(Vec3d pos, double radius, float angle, int count, EnumParticleTypes type) {
        for (int i = 0; i < count; ++i) {
            this.field_70170_p.func_175688_a(type, pos.field_72450_a + (double)MathHelper.func_76134_b((float)(angle + (float)i)) * radius, pos.field_72448_b + 0.5, pos.field_72449_c + (double)MathHelper.func_76126_a((float)(angle + (float)i)) * radius, 0.0, (double)this.field_70146_Z.nextFloat() * 0.2, 0.0, new int[0]);
        }
    }

    public int getGrowthRange() {
        int range = MathHelper.func_76125_a((int)(this.func_70681_au().nextInt(this.getSkill(ProfessionType.DRUID)) / 30), (int)0, (int)2);
        return range;
    }

    public boolean isGrowTime() {
        if (!this.isAIFilterEnabled("cast_earth_reform")) {
            return Village.isTimeOfDay(this.field_70170_p, 0L, 13000L);
        }
        if (this.growthFirst) {
            return Village.isTimeOfDay(this.field_70170_p, 0L, 6000L);
        }
        return Village.isTimeOfDay(this.field_70170_p, 6000L, 13000L);
    }

    public boolean isEarthReformTime() {
        if (!this.isAIFilterEnabled("cast_growth_trees") && !this.isAIFilterEnabled("cast_growth_crops")) {
            return Village.isTimeOfDay(this.field_70170_p, 0L, 13000L);
        }
        if (this.growthFirst) {
            return Village.isTimeOfDay(this.field_70170_p, 6000L, 13000L);
        }
        return Village.isTimeOfDay(this.field_70170_p, 0L, 6000L);
    }

    static {
        int[] nArray = new int[4];
        nArray[0] = Block.func_176210_f((IBlockState)Blocks.field_150346_d.func_176223_P());
        nArray[1] = Block.func_176210_f((IBlockState)Blocks.field_150346_d.func_176223_P());
        nArray[2] = Block.func_176210_f((IBlockState)Blocks.field_150348_b.func_176223_P());
        nArray[3] = Block.func_176210_f((IBlockState)Blocks.field_150347_e.func_176223_P());
        blockStateIds = nArray;
        animHandler.addAnim("tektopia", "villager_cast_forward", "druid_m", false);
        animHandler.addAnim("tektopia", "villager_cast_grow", "druid_m", false);
        EntityVillagerTek.setupAnimations(animHandler, "druid_m");
    }
}

