/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  com.leviathanstudio.craftstudio.common.animation.IAnimated
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIMoveTowardsRestriction
 *  net.minecraft.entity.ai.EntityAINearestAttackableTarget
 *  net.minecraft.entity.ai.EntityAISwimming
 *  net.minecraft.entity.ai.EntityAIWanderAvoidWater
 *  net.minecraft.entity.monster.AbstractSkeleton
 *  net.minecraft.entity.monster.EntityHusk
 *  net.minecraft.entity.monster.EntityIronGolem
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.monster.EntityWitherSkeleton
 *  net.minecraft.entity.monster.EntityZombie
 *  net.minecraft.entity.monster.IMob
 *  net.minecraft.entity.passive.EntityVillager
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.entities;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.TekDataSerializers;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.client.ParticleDarkness;
import net.tangotek.tektopia.client.ParticleSkull;
import net.tangotek.tektopia.entities.EntitySpiritSkull;
import net.tangotek.tektopia.entities.EntityVillageNavigator;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIDeathCloud;
import net.tangotek.tektopia.entities.ai.EntityAIFollowLeader;
import net.tangotek.tektopia.entities.ai.EntityAINecroMove;
import net.tangotek.tektopia.entities.ai.EntityAISoulLink;
import net.tangotek.tektopia.entities.ai.EntityAISummonUndead;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityNecromancer
extends EntityVillageNavigator
implements IMob {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityNecromancer.class);
    private static final DataParameter<Integer> SPELL_TARGET = EntityDataManager.func_187226_a(EntityNecromancer.class, (DataSerializer)DataSerializers.field_187192_b);
    private static final DataParameter<Integer> LEVEL = EntityDataManager.func_187226_a(EntityNecromancer.class, (DataSerializer)DataSerializers.field_187192_b);
    private static final DataParameter<List<Integer>> MINIONS = EntityDataManager.func_187226_a(EntityNecromancer.class, TekDataSerializers.INT_LIST);
    private final int SKULL_COOLDOWN_NORMAL = 100;
    private final int SKULL_COOLDOWN_ATTACKED = 40;
    private List<EntitySpiritSkull> skulls = new ArrayList<EntitySpiritSkull>();
    private int lastSkullGained = 0;
    private int skullCooldown = 100;
    private final int MAX_LEVEL = 5;
    private int maxSkulls = 1;
    private boolean isAngry = false;
    private BlockPos firstCheck;
    private boolean villagerDied = false;

    public EntityNecromancer(World worldIn) {
        super(worldIn, VillagerRole.ENEMY.value | VillagerRole.VISITOR.value);
        this.func_70105_a(0.6f, 1.95f);
        this.func_70101_b(0.0f, 0.0f);
        if (this.field_70170_p.field_72995_K) {
            this.addAnimationTriggerRange("tektopia:necro_summon", 102, 108, () -> this.skullParticles((Entity)this, 4));
            this.addAnimationTrigger("tektopia:necro_summon", 1, () -> this.skullParticles((Entity)this, 1));
            this.addAnimationTrigger("tektopia:necro_summon", 20, () -> this.skullParticles((Entity)this, 1));
            this.addAnimationTrigger("tektopia:necro_summon", 40, () -> this.skullParticles((Entity)this, 1));
            this.addAnimationTrigger("tektopia:necro_summon", 60, () -> this.skullParticles((Entity)this, 1));
            this.addAnimationTrigger("tektopia:necro_summon", 80, () -> this.skullParticles((Entity)this, 1));
        }
    }

    public void setLevel(int powerLevel) {
        int level = Math.max(1, Math.min(powerLevel, 5));
        this.field_70180_af.func_187227_b(LEVEL, (Object)level);
        this.maxSkulls = level;
    }

    public int getLevel() {
        return (Integer)this.field_70180_af.func_187225_a(LEVEL);
    }

    protected void func_184651_r() {
        this.field_70714_bg.func_75776_a(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.field_70714_bg.func_75776_a(9, (EntityAIBase)new EntityAISoulLink(this));
        this.field_70714_bg.func_75776_a(9, (EntityAIBase)new EntityAIDeathCloud(this));
        this.field_70714_bg.func_75776_a(9, (EntityAIBase)new EntityAISummonUndead(this, 1));
        this.field_70714_bg.func_75776_a(10, (EntityAIBase)new EntityAINecroMove(this));
        this.field_70715_bh.func_75776_a(2, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, EntityPlayer.class, true).func_190882_b(300));
        this.field_70715_bh.func_75776_a(3, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, EntityVillagerTek.class, false).func_190882_b(300));
        this.field_70715_bh.func_75776_a(5, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, EntityVillager.class, false).func_190882_b(300));
        this.field_70715_bh.func_75776_a(5, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, EntityIronGolem.class, false));
    }

    @Override
    protected void setupServerJobs() {
        super.setupServerJobs();
        this.addJob(new TickJob(40, 50, true, () -> this.convertNearbyZombies()));
        this.addJob(new TickJob(75, 50, true, () -> this.convertNearbySkeletons()));
        this.addJob(new TickJob(3, 3, true, () -> this.decaySurroundings()));
    }

    public boolean hasVillagerDied() {
        return this.villagerDied;
    }

    public void notifyVillagerDeath() {
        this.villagerDied = true;
    }

    private void prepStuck() {
        this.firstCheck = this.func_180425_c();
    }

    private void checkStuck() {
        IVillageData vd;
        if (this.hasVillage() && this.firstCheck.func_177951_i((Vec3i)this.func_180425_c()) < 2.0 && (vd = this.village.getTownData()) != null) {
            vd.setNomadsCheckedToday(true);
            vd.setMerchantCheckedToday(true);
        }
    }

    protected boolean shouldDecayBlock(BlockPos bp) {
        Block testBlock = this.field_70170_p.func_180495_p(bp).func_177230_c();
        return testBlock instanceof BlockBush;
    }

    protected void decaySurroundings() {
        for (int i = 0; i < 20; ++i) {
            BlockPos testPos = new BlockPos(this.field_70165_t + this.func_70681_au().nextGaussian() * 8.0, this.field_70163_u + this.func_70681_au().nextGaussian() * 0.6, this.field_70161_v + this.func_70681_au().nextGaussian() * 8.0);
            if (!this.shouldDecayBlock(testPos)) continue;
            this.field_70170_p.func_175698_g(testPos);
            return;
        }
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(20.0);
        this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(64.0);
        this.field_70180_af.func_187227_b(MINIONS, new ArrayList());
    }

    @Override
    protected void func_70088_a() {
        this.field_70180_af.func_187214_a(MINIONS, new ArrayList());
        this.field_70180_af.func_187214_a(SPELL_TARGET, (Object)0);
        this.field_70180_af.func_187214_a(LEVEL, (Object)1);
        super.func_70088_a();
    }

    public boolean isSpellcasting() {
        int entityId = (Integer)this.field_70180_af.func_187225_a(SPELL_TARGET);
        return entityId > 0;
    }

    public Entity getSpellTargetEntity() {
        int entityId = (Integer)this.field_70180_af.func_187225_a(SPELL_TARGET);
        if (entityId > 0) {
            return this.field_70170_p.func_73045_a(entityId);
        }
        return null;
    }

    public void setSpellTarget(Entity entity) {
        if (entity == null) {
            this.field_70180_af.func_187227_b(SPELL_TARGET, (Object)0);
        } else {
            this.field_70180_af.func_187227_b(SPELL_TARGET, (Object)entity.func_145782_y());
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void skullParticles(Entity entity, int count) {
        for (int i = 0; i < count; ++i) {
            Random rand = entity.field_70170_p.field_73012_v;
            double motionY = Math.random() * 0.03 + 0.01;
            Vec3d pos = new Vec3d(entity.field_70165_t + rand.nextGaussian() * 0.5, entity.field_70163_u + (double)rand.nextFloat(), entity.field_70161_v + rand.nextGaussian() * 0.5);
            ParticleSkull part = new ParticleSkull(entity.field_70170_p, Minecraft.func_71410_x().func_110434_K(), pos, motionY);
            part.radius = rand.nextGaussian() * 0.1;
            part.radiusGrow = 0.005;
            part.torque = Math.random() * 0.04 - 0.02;
            part.lifeTime = rand.nextInt(15) + 10;
            part.func_189213_a();
            Minecraft.func_71410_x().field_71452_i.func_78873_a((Particle)part);
        }
    }

    private void minionsDefendMe(EntityLivingBase enemy) {
        List<EntityMob> minions = this.getMinions();
        for (EntityMob minion : minions) {
            minion.func_70624_b(enemy);
        }
    }

    private void cleanMinions() {
        List idList;
        if (!this.field_70170_p.field_72995_K && (idList = (List)this.field_70180_af.func_187225_a(MINIONS)).removeIf(entityId -> !this.isValidMinion(this.field_70170_p.func_73045_a(entityId.intValue())))) {
            this.debugOut("Minion(s) removed. Now " + idList.size());
            this.field_70180_af.func_187227_b(MINIONS, (Object)idList);
        }
    }

    public void addMinion(EntityMob mob) {
        List idList = (List)this.field_70180_af.func_187225_a(MINIONS);
        idList.add(mob.func_145782_y());
        this.field_70180_af.func_187227_b(MINIONS, (Object)idList);
        this.debugOut("Minion " + mob.func_145782_y() + " added to list. Now " + idList.size());
    }

    private boolean isValidMinion(Entity e) {
        return e != null && e instanceof EntityMob && e.func_70089_S();
    }

    public List<EntityMob> getMinions() {
        List idList = (List)this.field_70180_af.func_187225_a(MINIONS);
        ArrayList<EntityMob> mobList = new ArrayList<EntityMob>();
        idList.forEach(id -> {
            EntityMob mob = (EntityMob)this.field_70170_p.func_73045_a(id.intValue());
            if (this.isValidMinion((Entity)mob)) {
                mobList.add(mob);
            }
        });
        return mobList;
    }

    public void func_70071_h_() {
        super.func_70071_h_();
        if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL) {
            this.func_70106_y();
        }
    }

    @Override
    public float func_70689_ay() {
        return 0.22f;
    }

    protected SoundEvent func_184639_G() {
        return null;
    }

    @Override
    public boolean isAITick() {
        return super.isAITick() || this.field_70737_aN > 0;
    }

    public int getMaxSummons() {
        if (this.villagerDied) {
            return 0;
        }
        return 4 + this.getLevel();
    }

    public boolean isReadyForSkull() {
        if (this.skulls.isEmpty()) {
            return true;
        }
        return this.skulls.size() < this.maxSkulls && !this.villagerDied && this.func_70681_au().nextInt(2) == 0 && this.field_70173_aa - this.skullCooldown > this.lastSkullGained;
    }

    private void convertNearbyZombies() {
        if (!this.villagerDied) {
            List zombies = this.field_70170_p.func_72872_a(EntityZombie.class, this.func_174813_aQ().func_186662_g((double)(15 + this.getLevel() * 4)));
            zombies.forEach(z -> this.convertZombie((EntityZombie)z));
        }
    }

    private void convertNearbySkeletons() {
        if (!this.villagerDied) {
            List skels = this.field_70170_p.func_72872_a(AbstractSkeleton.class, this.func_174813_aQ().func_186662_g((double)(15 + this.getLevel() * 4)));
            skels.forEach(s -> this.convertSkeleton((AbstractSkeleton)s));
        }
    }

    public static boolean isMinion(EntityLivingBase mob) {
        return mob.getEntityData().func_186855_b("master");
    }

    public static void makeMinion(EntityLivingBase minion, EntityNecromancer necro) {
        minion.getEntityData().func_186854_a("master", necro.func_110124_au());
    }

    public void convertZombie(EntityZombie z) {
        if (!EntityNecromancer.isMinion((EntityLivingBase)z)) {
            z.field_70714_bg.field_75782_a.removeIf(entry -> entry.field_75733_a.getClass() == EntityAIWanderAvoidWater.class);
            z.field_70714_bg.field_75782_a.removeIf(entry -> entry.field_75733_a.getClass() == EntityAIMoveTowardsRestriction.class);
            z.field_70714_bg.field_75782_a.removeIf(entry -> entry.field_75733_a.getClass() == EntityAIFollowLeader.class);
            z.field_70714_bg.func_75776_a(5, (EntityAIBase)new EntityAIFollowLeader((EntityCreature)z, this, 8, 1.0));
            z.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(64.0);
            EntityNecromancer.makeMinion((EntityLivingBase)z, this);
            this.debugOut("Converted nearby zombie | " + z.toString());
        }
    }

    public void convertSkeleton(AbstractSkeleton skel) {
        if (!EntityNecromancer.isMinion((EntityLivingBase)skel)) {
            skel.field_70714_bg.field_75782_a.removeIf(entry -> entry.field_75733_a.getClass() == EntityAIWanderAvoidWater.class);
            skel.field_70714_bg.field_75782_a.removeIf(entry -> entry.field_75733_a.getClass() == EntityAIFollowLeader.class);
            skel.field_70714_bg.func_75776_a(5, (EntityAIBase)new EntityAIFollowLeader((EntityCreature)skel, this, 8, 1.0));
            skel.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(32.0);
            EntityNecromancer.makeMinion((EntityLivingBase)skel, this);
        }
    }

    public EntityWitherSkeleton createWitherSkeleton(BlockPos summonPos) {
        EntityWitherSkeleton skel = new EntityWitherSkeleton(this.field_70170_p);
        this.convertSkeleton((AbstractSkeleton)skel);
        skel.func_70012_b((double)summonPos.func_177958_n() + 0.5, (double)summonPos.func_177956_o(), (double)summonPos.func_177952_p() + 0.5, this.field_70177_z, this.field_70125_A);
        skel.func_180482_a(this.field_70170_p.func_175649_E(summonPos), null);
        skel.func_70690_d(new PotionEffect(MobEffects.field_76441_p, 15));
        return skel;
    }

    public EntityZombie createZombie(BlockPos summonPos) {
        this.getVillage();
        Object zombie = Village.isTimeOfDay(this.field_70170_p, 13000L, 17000L) ? new EntityZombie(this.field_70170_p) : new EntityHusk(this.field_70170_p);
        this.convertZombie((EntityZombie)zombie);
        zombie.func_70012_b((double)summonPos.func_177958_n() + 0.5, (double)summonPos.func_177956_o(), (double)summonPos.func_177952_p() + 0.5, this.field_70177_z, this.field_70125_A);
        zombie.func_180482_a(this.field_70170_p.func_175649_E(summonPos), null);
        zombie.func_70690_d(new PotionEffect(MobEffects.field_76441_p, 15));
        return zombie;
    }

    public boolean isCreatureSkulled(EntityCreature creature) {
        this.skulls.removeIf(s -> !s.func_70089_S() || s.func_70068_e((Entity)this) > 625.0);
        return this.skulls.stream().anyMatch(s -> {
            EntityCreature c = s.getSkullCreature();
            return c != null && c.equals((Object)creature);
        });
    }

    public void addSkull(EntitySpiritSkull skull) {
        this.skulls.add(skull);
        this.lastSkullGained = this.field_70173_aa;
        this.skullCooldown = this.getLevelCooldown(100);
        skull.setNecro(this);
        this.func_184185_a(ModSoundEvents.deathFullSkulls, 1.3f, this.func_70681_au().nextFloat() * 0.4f + 0.8f);
    }

    public void releaseSkull(EntitySpiritSkull skull) {
        this.skulls.remove((Object)skull);
        skull.func_70106_y();
        skull.setNecro(null);
        this.playSound(ModSoundEvents.deathSkullLeave);
        this.isAngry = true;
    }

    public boolean isAngry() {
        return this.isAngry;
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
        if (this.isWorldRemote()) {
            this.updateIdle(this.isWalking());
            this.spawnCloud((Entity)this, this.isWalking() ? 30 : 20);
            if (this.isSpellcasting()) {
                this.skullParticles((Entity)this, this.getLevel() * 2);
            }
        } else {
            this.cleanMinions();
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnSmoke(Entity entity, float spread, int count) {
        for (int i = 0; i < count; ++i) {
            double xOffset = entity.field_70170_p.field_73012_v.nextGaussian() * (double)spread;
            double zOffset = entity.field_70170_p.field_73012_v.nextGaussian() * (double)spread;
            Vec3d pos = new Vec3d(entity.field_70165_t + xOffset, entity.field_70163_u + (double)Math.min(entity.field_70170_p.field_73012_v.nextFloat(), entity.field_70170_p.field_73012_v.nextFloat()) * 0.5 * (double)entity.field_70131_O, entity.field_70161_v + zOffset);
            entity.field_70170_p.func_175688_a(EnumParticleTypes.SMOKE_LARGE, pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, 0.0, 0.08, 0.0, new int[0]);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnCloud(Vec3d pos, int count) {
        for (int i = 0; i < count; ++i) {
            double motionY = Math.random() * 0.03 + 0.01;
            Vec3d newPos = new Vec3d(pos.field_72450_a + this.field_70170_p.field_73012_v.nextGaussian() * 0.5, pos.field_72448_b, pos.field_72449_c + this.field_70170_p.field_73012_v.nextGaussian() * 0.5);
            ParticleDarkness part = new ParticleDarkness(this.field_70170_p, Minecraft.func_71410_x().func_110434_K(), newPos, motionY);
            part.radius = this.field_70170_p.field_73012_v.nextGaussian() * 0.3;
            part.radiusGrow = 0.015;
            part.torque = Math.random() * 0.04 - 0.02;
            part.lifeTime = this.field_70170_p.field_73012_v.nextInt(15) + 10;
            part.func_189213_a();
            Minecraft.func_71410_x().field_71452_i.func_78873_a((Particle)part);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnCloud(Entity entity, int count) {
        for (int i = 0; i < count; ++i) {
            double motionY = Math.random() * 0.03 + 0.01;
            Vec3d pos = new Vec3d(entity.field_70165_t, entity.field_70163_u + (double)Math.min(this.field_70170_p.field_73012_v.nextFloat(), this.field_70170_p.field_73012_v.nextFloat()) * 0.5 * (double)entity.field_70131_O, entity.field_70161_v);
            ParticleDarkness part = new ParticleDarkness(this.field_70170_p, Minecraft.func_71410_x().func_110434_K(), pos, motionY);
            part.radius = this.field_70170_p.field_73012_v.nextGaussian() * 0.3;
            part.radiusGrow = 0.015;
            part.torque = Math.random() * 0.04 - 0.02;
            part.lifeTime = this.field_70170_p.field_73012_v.nextInt(15) + 15;
            part.func_189213_a();
            Minecraft.func_71410_x().field_71452_i.func_78873_a((Particle)part);
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    protected void startWalking() {
        this.playClientAnimation("necro_walk");
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    protected void stopWalking() {
        this.stopClientAnimation("necro_walk");
    }

    @SideOnly(value=Side.CLIENT)
    protected void updateIdle(boolean isWalking) {
        if (!this.isCasting() && !isWalking) {
            if (!this.getAnimationHandler().isAnimationActive("tektopia", "necro_idle", (IAnimated)this)) {
                this.getAnimationHandler().startAnimation("tektopia", "necro_idle", (IAnimated)this);
            }
        } else if (this.getAnimationHandler().isAnimationActive("tektopia", "necro_idle", (IAnimated)this)) {
            this.getAnimationHandler().stopAnimation("tektopia", "necro_idle", (IAnimated)this);
        }
    }

    public boolean isCasting() {
        return false;
    }

    protected boolean func_70692_ba() {
        return false;
    }

    public boolean func_70097_a(DamageSource source, float amount) {
        List nearbySkullls;
        boolean livingAttack = source.func_76346_g() instanceof EntityLivingBase;
        if (livingAttack) {
            this.minionsDefendMe((EntityLivingBase)source.func_76346_g());
        }
        if ((nearbySkullls = this.field_70170_p.func_72872_a(EntitySpiritSkull.class, this.func_174813_aQ().func_186662_g(5.0))).isEmpty() || source == DamageSource.field_76380_i) {
            this.skullCooldown -= 20;
            return super.func_70097_a(source, amount);
        }
        this.func_184185_a(ModSoundEvents.deathShield, 1.2f + this.func_70681_au().nextFloat() * 0.4f, this.func_70681_au().nextFloat() * 0.2f + 0.9f);
        this.func_70690_d(new PotionEffect(MobEffects.field_188423_x, 20));
        return false;
    }

    public int getLevelCooldown(int baseValue) {
        return baseValue - (int)((double)baseValue * MathHelper.func_151238_b((double)0.0, (double)0.6, (double)((double)this.getLevel() / 5.0)));
    }

    public void func_70645_a(DamageSource cause) {
        this.getMinions().stream().forEach(m -> m.func_70606_j(0.0f));
        this.playSound(ModSoundEvents.necroDead);
        super.func_70645_a(cause);
    }

    protected void func_70628_a(boolean wasRecentlyHit, int lootingModifier) {
        this.func_145779_a(Items.field_151166_bC, this.getLevel() * 4);
    }

    @SideOnly(value=Side.CLIENT)
    public void func_70103_a(byte id) {
        super.func_70103_a(id);
    }

    public void func_70604_c(@Nullable EntityLivingBase target) {
        super.func_70604_c(target);
    }

    public boolean func_184645_a(EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.func_184586_b(hand);
        if (itemStack.func_77973_b() == ModItems.beer && this.getLevel() < 5) {
            this.setLevel(this.getLevel() + 1);
            return true;
        }
        return super.func_184645_a(player, hand);
    }

    @Override
    public SoundCategory func_184176_by() {
        return SoundCategory.HOSTILE;
    }

    public void func_70014_b(NBTTagCompound compound) {
        super.func_70014_b(compound);
        compound.func_74768_a("level", this.getLevel());
    }

    public void func_70037_a(NBTTagCompound compound) {
        super.func_70037_a(compound);
        this.setLevel(compound.func_74762_e("level"));
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    static {
        animHandler.addAnim("tektopia", "necro_walk", "necromancer", true);
        animHandler.addAnim("tektopia", "necro_idle", "necromancer", true);
        animHandler.addAnim("tektopia", "necro_summon", "necromancer", false);
        animHandler.addAnim("tektopia", "necro_siphon", "necromancer", false);
        animHandler.addAnim("tektopia", "necro_cast_forward", "necromancer", false);
    }
}

