/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.Item
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class ParticleItemThought
extends Particle {
    private final int lifeTime;
    private final Entity ent;

    public ParticleItemThought(World worldIn, Entity ent, Item item) {
        super(worldIn, ent.field_70165_t, ent.func_174813_aQ().field_72337_e + 0.8, ent.field_70161_v, 0.0, 0.0, 0.0);
        this.func_187117_a(Minecraft.func_71410_x().func_175599_af().func_175037_a().func_178082_a(item));
        this.ent = ent;
        this.lifeTime = 40;
        this.field_70552_h = 1.0f;
        this.field_70553_i = 1.0f;
        this.field_70551_j = 1.0f;
        this.field_187129_i = 0.0;
        this.field_187130_j = 0.013;
        this.field_187131_k = 0.0;
        this.field_70545_g = 0.0f;
        this.field_70547_e = 40;
        this.field_70544_f = 0.2f;
        this.func_70541_f(0.15f);
        this.field_190017_n = false;
    }

    public int func_70537_b() {
        return 1;
    }

    public void func_189213_a() {
        this.field_82339_as -= 0.02f;
        super.func_189213_a();
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        this.func_187110_a(this.ent.field_70165_t - this.field_187123_c, this.field_187130_j, this.ent.field_70161_v - this.field_187125_e);
    }

    public void func_180434_a(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        GlStateManager.func_179094_E();
        float f = this.field_187119_C.func_94209_e();
        float f1 = this.field_187119_C.func_94212_f();
        float f2 = this.field_187119_C.func_94206_g();
        float f3 = this.field_187119_C.func_94210_h();
        float f4 = 0.5f;
        float f5 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)partialTicks - field_70556_an);
        float f6 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)partialTicks - field_70554_ao);
        float f7 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)partialTicks - field_70555_ap);
        int i = this.func_189214_a(partialTicks);
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        float scale = 0.3f;
        buffer.func_181662_b((double)(f5 - rotationX * scale - rotationXY * scale), (double)(f6 - rotationZ * scale), (double)(f7 - rotationYZ * scale - rotationXZ * scale)).func_187315_a((double)f1, (double)f3).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(j, k).func_181675_d();
        buffer.func_181662_b((double)(f5 - rotationX * scale + rotationXY * scale), (double)(f6 + rotationZ * scale), (double)(f7 - rotationYZ * scale + rotationXZ * scale)).func_187315_a((double)f1, (double)f2).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(j, k).func_181675_d();
        buffer.func_181662_b((double)(f5 + rotationX * scale + rotationXY * scale), (double)(f6 + rotationZ * scale), (double)(f7 + rotationYZ * scale + rotationXZ * scale)).func_187315_a((double)f, (double)f2).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(j, k).func_181675_d();
        buffer.func_181662_b((double)(f5 + rotationX * scale - rotationXY * scale), (double)(f6 - rotationZ * scale), (double)(f7 + rotationYZ * scale - rotationXZ * scale)).func_187315_a((double)f, (double)f3).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(j, k).func_181675_d();
        GlStateManager.func_179121_F();
    }
}

