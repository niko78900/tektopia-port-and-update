/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.projectile.ProjectileHelper
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.entities;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.client.ParticleDarkness;
import net.tangotek.tektopia.client.ParticleSkull;
import net.tangotek.tektopia.entities.EntityNecromancer;

public class EntitySpiritSkull
extends Entity {
    private double acceleration = 0.0;
    private int glowTimer = 0;
    private boolean initialSpawn = false;
    private static final DataParameter<Byte> SKULL_MODE = EntityDataManager.func_187226_a(EntitySpiritSkull.class, (DataSerializer)DataSerializers.field_187191_a);
    private static final DataParameter<Integer> NECRO = EntityDataManager.func_187226_a(EntitySpiritSkull.class, (DataSerializer)DataSerializers.field_187192_b);
    private static final DataParameter<Integer> SKULL_CREATURE = EntityDataManager.func_187226_a(EntitySpiritSkull.class, (DataSerializer)DataSerializers.field_187192_b);

    public EntitySpiritSkull(World worldIn, boolean init) {
        this(worldIn);
        this.initialSpawn = init;
    }

    public EntitySpiritSkull(World worldIn) {
        super(worldIn);
        this.func_70105_a(0.3125f, 0.3125f);
        this.func_82142_c(true);
    }

    @SideOnly(value=Side.CLIENT)
    public EntitySpiritSkull(World worldIn, double x, double y, double z) {
        super(worldIn);
        this.func_70012_b(x, y, z, this.field_70177_z, this.field_70125_A);
        this.func_70107_b(x, y, z);
        this.func_70105_a(0.3125f, 0.3125f);
        this.func_82142_c(true);
    }

    protected void func_70088_a() {
        this.field_70180_af.func_187214_a(SKULL_MODE, (Object)((byte)SkullMode.RETURNING.ordinal()));
        this.field_70180_af.func_187214_a(NECRO, (Object)0);
        this.field_70180_af.func_187214_a(SKULL_CREATURE, (Object)0);
    }

    public void func_70071_h_() {
        super.func_70071_h_();
        boolean valid = true;
        if (!this.field_70170_p.func_175667_e(new BlockPos((Entity)this))) {
            this.func_70106_y();
            valid = false;
        }
        SkullMode skullMode = this.getSkullMode();
        EntityNecromancer necro = this.getNecro();
        EntityCreature skullCreature = this.getSkullCreature();
        if (necro == null || !necro.func_70089_S()) {
            if (!this.field_70170_p.field_72995_K) {
                this.func_70106_y();
            }
            valid = false;
        } else if (skullCreature == null || !skullCreature.func_70089_S()) {
            if (!this.field_70170_p.field_72995_K) {
                necro.releaseSkull(this);
            }
            valid = false;
        } else if (this.field_70173_aa > 800) {
            necro.releaseSkull(this);
        }
        if (valid) {
            if (skullMode == SkullMode.RETURNING) {
                if (this.field_70173_aa > 23) {
                    Vec3d dest = necro.func_174791_d().func_72441_c(0.0, (double)necro.func_70047_e(), 0.0);
                    if (!this.field_70170_p.field_72995_K) {
                        if (this.func_82150_aj()) {
                            this.func_82142_c(false);
                            this.func_70012_b(skullCreature.field_70165_t, skullCreature.field_70163_u + (double)skullCreature.func_70047_e(), skullCreature.field_70161_v, 0.0f, 0.0f);
                            this.func_184185_a(ModSoundEvents.deathSkullRebound, this.field_70170_p.field_73012_v.nextFloat() * 0.4f + 1.2f, this.field_70170_p.field_73012_v.nextFloat() * 0.4f + 0.8f);
                        } else if (this.func_70092_e(dest.field_72450_a, dest.field_72448_b, dest.field_72449_c) < 1.0) {
                            this.setSkullMode(SkullMode.PROTECTING);
                            this.func_184185_a(ModSoundEvents.deathSkullArrive, this.field_70170_p.field_73012_v.nextFloat() * 0.4f + 1.2f, this.field_70170_p.field_73012_v.nextFloat() * 0.4f + 0.8f);
                        }
                    }
                    Vec3d dir = dest.func_178788_d(this.func_174791_d()).func_72432_b().func_186678_a(0.3);
                    this.field_70159_w = dir.field_72450_a;
                    this.field_70181_x = dir.field_72448_b;
                    this.field_70179_y = dir.field_72449_c;
                    this.field_70165_t += this.field_70159_w;
                    this.field_70163_u += this.field_70181_x;
                    this.field_70161_v += this.field_70179_y;
                    ProjectileHelper.func_188803_a((Entity)this, (float)0.2f);
                    this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
                }
            } else if (skullMode == SkullMode.PROTECTING) {
                Vec3d origin = necro.func_174791_d().func_72441_c(0.0, (double)necro.func_70047_e(), 0.0);
                this.func_70107_b(origin.field_72450_a, origin.field_72448_b, origin.field_72449_c);
            } else if (skullMode == SkullMode.ATTACKING) {
                this.field_70165_t += this.field_70159_w;
                this.field_70163_u += this.field_70181_x;
                this.field_70161_v += this.field_70179_y;
                ProjectileHelper.func_188803_a((Entity)this, (float)0.2f);
                this.field_70159_w *= this.acceleration;
                this.field_70181_x *= this.acceleration;
                this.field_70179_y *= this.acceleration;
                this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            }
            if (this.field_70170_p.field_72995_K) {
                if (skullMode != SkullMode.PROTECTING) {
                    this.createDarknessParticles(5, 0.1f);
                } else {
                    this.createDarknessParticles(2, 0.07f);
                }
                if (skullMode == SkullMode.PROTECTING || skullMode == SkullMode.RETURNING) {
                    necro.spawnCloud((Entity)skullCreature, 3);
                    if (necro.func_70681_au().nextInt(4) == 0) {
                        necro.skullParticles((Entity)skullCreature, 1);
                    }
                }
                if (this.field_70173_aa >= 1 && this.field_70173_aa <= 20 && necro != null && skullCreature != null) {
                    float percentFrom = (float)(this.field_70173_aa - 1) / 20.0f;
                    float percentTo = (float)this.field_70173_aa / 20.0f;
                    this.particleLine((Entity)necro, (Entity)skullCreature, percentFrom, percentTo, 25);
                }
                if (this.func_184202_aL()) {
                    this.field_70170_p.func_175688_a(EnumParticleTypes.SMOKE_LARGE, this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70170_p.field_73012_v.nextGaussian() * 0.1, 0.08, this.field_70170_p.field_73012_v.nextGaussian() * 0.1, new int[0]);
                }
                if (this.field_70173_aa > 23 && this.field_70173_aa < 27) {
                    this.createSkullParticles(4);
                }
            } else if (this.glowTimer > 0) {
                --this.glowTimer;
                if (this.glowTimer == 0) {
                    this.func_184195_f(false);
                }
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    private void particleLine(Entity source, Entity target, float from, float to, int count) {
        Vec3d start = source.func_174791_d().func_72441_c(0.0, (double)source.func_70047_e(), 0.0);
        Vec3d end = target.func_174791_d().func_72441_c(0.0, (double)target.func_70047_e(), 0.0);
        Vec3d line = end.func_178788_d(start);
        for (int i = 0; i < count; ++i) {
            Vec3d delta = line.func_186678_a((double)MathHelper.func_151240_a((Random)this.field_70170_p.field_73012_v, (float)from, (float)to));
            Vec3d pos = start.func_178787_e(delta);
            ParticleDarkness part = new ParticleDarkness(this.field_70170_p, Minecraft.func_71410_x().func_110434_K(), pos, this.field_70181_x);
            part.lifeTime = this.field_70170_p.field_73012_v.nextInt(10) + 10;
            part.func_70538_b(0.953f, 0.173f, 0.077f);
            part.func_189213_a();
            Minecraft.func_71410_x().field_71452_i.func_78873_a((Particle)part);
        }
    }

    @SideOnly(value=Side.CLIENT)
    private void createDarknessParticles(int count, float radius) {
        for (int i = 0; i < count; ++i) {
            double motionY = Math.random() * 0.03 + 0.01;
            Vec3d pos = new Vec3d(this.field_70165_t + this.field_70146_Z.nextGaussian() * 0.1, this.field_70163_u, this.field_70161_v + this.field_70146_Z.nextGaussian() * 0.1);
            ParticleDarkness part = new ParticleDarkness(this.field_70170_p, Minecraft.func_71410_x().func_110434_K(), pos, motionY);
            part.radius = this.field_70170_p.field_73012_v.nextGaussian() * (double)radius;
            part.radiusGrow = 0.005;
            part.torque = Math.random() * 0.04 - 0.02;
            part.lifeTime = this.field_70170_p.field_73012_v.nextInt(15) + 20;
            part.func_189213_a();
            Minecraft.func_71410_x().field_71452_i.func_78873_a((Particle)part);
        }
    }

    @SideOnly(value=Side.CLIENT)
    private void createSkullParticles(int count) {
        for (int i = 0; i < count; ++i) {
            double motionY = Math.random() * 0.03 + 0.01;
            Vec3d pos = new Vec3d(this.field_70165_t + this.field_70146_Z.nextGaussian() * 0.5, this.field_70163_u + this.field_70146_Z.nextGaussian(), this.field_70161_v + this.field_70146_Z.nextGaussian() * 0.5);
            ParticleSkull part = new ParticleSkull(this.field_70170_p, Minecraft.func_71410_x().func_110434_K(), pos, motionY);
            part.radius = this.field_70170_p.field_73012_v.nextGaussian() * 0.1;
            part.radiusGrow = 0.005;
            part.torque = Math.random() * 0.04 - 0.02;
            part.lifeTime = this.field_70170_p.field_73012_v.nextInt(15) + 20;
            part.func_189213_a();
            Minecraft.func_71410_x().field_71452_i.func_78873_a((Particle)part);
        }
    }

    @Nullable
    public EntityNecromancer getNecro() {
        Entity e;
        Integer entityId = (Integer)this.field_70180_af.func_187225_a(NECRO);
        if (entityId > 0 && (e = this.field_70170_p.func_73045_a(entityId.intValue())) instanceof EntityNecromancer) {
            return (EntityNecromancer)e;
        }
        return null;
    }

    public void setNecro(EntityNecromancer necro) {
        this.field_70180_af.func_187227_b(NECRO, (Object)(necro == null ? 0 : necro.func_145782_y()));
    }

    public void setSkullCreature(EntityCreature creature) {
        this.field_70180_af.func_187227_b(SKULL_CREATURE, (Object)(creature == null ? 0 : creature.func_145782_y()));
    }

    @Nullable
    public EntityCreature getSkullCreature() {
        Entity e;
        Integer entityId = (Integer)this.field_70180_af.func_187225_a(SKULL_CREATURE);
        if (entityId > 0 && (e = this.field_70170_p.func_73045_a(entityId.intValue())) instanceof EntityCreature && e.func_70089_S()) {
            return (EntityCreature)e;
        }
        return null;
    }

    public SkullMode getSkullMode() {
        byte index = (Byte)this.field_70180_af.func_187225_a(SKULL_MODE);
        return SkullMode.values()[index];
    }

    public void setSkullMode(SkullMode mode) {
        this.field_70159_w = 0.0;
        this.field_70181_x = 0.0;
        this.field_70179_y = 0.0;
        this.acceleration = 0.0;
        this.field_70180_af.func_187227_b(SKULL_MODE, (Object)((byte)mode.ordinal()));
        if (mode == SkullMode.ATTACKING) {
            this.acceleration = 1.02;
        }
    }

    public float getSpinRadius() {
        return 1.3f + (float)Math.abs(this.func_110124_au().getLeastSignificantBits() % 70L) / 100.0f;
    }

    public float getSpinSpeed() {
        float speed = 4.0f + (float)(Math.abs(this.func_110124_au().getLeastSignificantBits()) % 6L);
        if (this.func_110124_au().getLeastSignificantBits() % 2L == 0L) {
            speed = -speed;
        }
        return speed;
    }

    public float getSpinAxis() {
        return (float)Math.abs(this.func_110124_au().getLeastSignificantBits() % 6L) / 10.0f;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean func_70112_a(double distance) {
        double d0 = this.func_174813_aQ().func_72320_b() * 4.0;
        if (Double.isNaN(d0)) {
            d0 = 4.0;
        }
        return distance < (d0 *= 64.0) * d0;
    }

    protected void func_70037_a(NBTTagCompound compound) {
    }

    protected void func_70014_b(NBTTagCompound compound) {
    }

    public boolean func_70067_L() {
        return false;
    }

    public boolean func_70097_a(DamageSource source, float amount) {
        return false;
    }

    public static enum SkullMode {
        RETURNING,
        PROTECTING,
        ATTACKING;

    }
}

