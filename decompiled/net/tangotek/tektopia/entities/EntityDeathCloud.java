/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityAreaEffectCloud
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.entities;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.client.ParticleSkull;

public class EntityDeathCloud
extends EntityAreaEffectCloud {
    private float angleRadians;

    public EntityDeathCloud(World worldIn) {
        super(worldIn);
    }

    public EntityDeathCloud(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.func_184491_a(EnumParticleTypes.TOWN_AURA);
    }

    public void func_70071_h_() {
        super.func_70071_h_();
        if (this.field_70170_p.field_72995_K) {
            for (int i = 0; i < 4; ++i) {
                this.angleRadians = (float)((double)this.angleRadians + 0.0035);
                if (this.angleRadians > 1.0f) {
                    this.angleRadians = 1.0f - this.angleRadians;
                }
                this.perimeterParticle(this.angleRadians, 0.0f);
                this.perimeterParticle(-this.angleRadians, -0.3f);
                this.interiorParticle();
            }
        } else {
            double radiusSq = this.func_184490_j() * this.func_184490_j();
            List entList = this.field_70170_p.func_72872_a(EntityPlayer.class, this.func_174813_aQ());
            for (EntityPlayer insideEnt : entList) {
                if (!(insideEnt.func_70068_e((Entity)this) < radiusSq)) continue;
                int duration = insideEnt instanceof EntityPlayer ? 160 : 80;
                insideEnt.func_70690_d(new PotionEffect(MobEffects.field_82731_v, duration));
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    private void perimeterParticle(float angleRadians, float radiusMod) {
        double motionY = Math.random() * 0.01 + 0.01;
        float f1 = angleRadians * ((float)Math.PI * 2);
        float xOffset = MathHelper.func_76134_b((float)f1) * (this.func_184490_j() + radiusMod);
        float zOffset = MathHelper.func_76126_a((float)f1) * (this.func_184490_j() + radiusMod);
        Vec3d pos = new Vec3d(this.field_70165_t + (double)xOffset, this.field_70163_u, this.field_70161_v + (double)zOffset);
        this.field_70170_p.func_175688_a(EnumParticleTypes.DRAGON_BREATH, pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, 0.0, motionY, 0.0, new int[0]);
    }

    @SideOnly(value=Side.CLIENT)
    private void interiorParticle() {
        double motionY = Math.random() * 0.01 + 0.01;
        float f1 = this.field_70170_p.field_73012_v.nextFloat() * ((float)Math.PI * 2);
        float xOffset = MathHelper.func_76134_b((float)f1) * this.func_184490_j() * this.field_70170_p.field_73012_v.nextFloat();
        float zOffset = MathHelper.func_76126_a((float)f1) * this.func_184490_j() * this.field_70170_p.field_73012_v.nextFloat();
        Vec3d pos = new Vec3d(this.field_70165_t + (double)xOffset, this.field_70163_u, this.field_70161_v + (double)zOffset);
        ParticleSkull part = new ParticleSkull(this.field_70170_p, Minecraft.func_71410_x().func_110434_K(), pos, motionY);
        part.radius = this.field_70146_Z.nextGaussian() * 0.05;
        part.radiusGrow = 0.002;
        part.torque = Math.random() * 0.04 - 0.02;
        part.lifeTime = this.field_70146_Z.nextInt(20) + 10;
        part.func_189213_a();
        Minecraft.func_71410_x().field_71452_i.func_78873_a((Particle)part);
    }
}

