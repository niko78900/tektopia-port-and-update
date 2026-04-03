/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.client.renderer.vertex.VertexFormat
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class ParticleThought
extends Particle {
    private final ResourceLocation texture;
    private static final VertexFormat VERTEX_FORMAT = new VertexFormat().func_181721_a(DefaultVertexFormats.field_181713_m).func_181721_a(DefaultVertexFormats.field_181715_o).func_181721_a(DefaultVertexFormats.field_181714_n).func_181721_a(DefaultVertexFormats.field_181716_p).func_181721_a(DefaultVertexFormats.field_181717_q).func_181721_a(DefaultVertexFormats.field_181718_r);
    private final TextureManager textureManager;
    private int life;
    private final float scale;
    private final int lifeTime;
    private final Entity ent;

    public ParticleThought(World worldIn, TextureManager tm, Entity ent, float scale, String texture) {
        super(worldIn, ent.field_70165_t, ent.field_70163_u + 2.5, ent.field_70161_v, 0.0, 0.0, 0.0);
        this.texture = new ResourceLocation("tektopia:textures/particle/" + texture);
        this.textureManager = tm;
        this.ent = ent;
        this.lifeTime = 40;
        this.scale = scale;
        this.field_70552_h = 1.0f;
        this.field_70553_i = 1.0f;
        this.field_70551_j = 1.0f;
        this.field_187129_i = 0.0;
        this.field_187130_j = 0.005;
        this.field_187131_k = 0.0;
        this.field_82339_as = 1.0f;
        this.field_190017_n = false;
    }

    public void func_189213_a() {
        this.field_187127_g += this.field_187130_j;
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        this.func_187110_a(this.ent.field_70165_t - this.field_187123_c, this.field_187130_j, this.ent.field_70161_v - this.field_187125_e);
        ++this.life;
        if (this.life * 2 > this.lifeTime) {
            this.field_82339_as -= 2.0f / (float)this.lifeTime;
            if (this.life == this.lifeTime) {
                this.func_187112_i();
            }
        }
    }

    public int func_70537_b() {
        return 3;
    }

    public void func_180434_a(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        this.textureManager.func_110577_a(this.texture);
        float f = 0.0f;
        float f1 = 1.0f;
        float f2 = 0.0f;
        float f3 = 1.0f;
        float f4 = 0.3f * this.scale;
        float f5 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)partialTicks - field_70556_an);
        float f6 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)partialTicks - field_70554_ao);
        float f7 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)partialTicks - field_70555_ap);
        GlStateManager.func_179094_E();
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.func_179140_f();
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179147_l();
        GlStateManager.func_179126_j();
        GlStateManager.func_187401_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        buffer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
        buffer.func_181662_b((double)(f5 - rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 - rotationYZ * f4 - rotationXZ * f4)).func_187315_a((double)f1, (double)f3).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_181675_d();
        buffer.func_181662_b((double)(f5 - rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 - rotationYZ * f4 + rotationXZ * f4)).func_187315_a((double)f1, (double)f2).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_181675_d();
        buffer.func_181662_b((double)(f5 + rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 + rotationYZ * f4 + rotationXZ * f4)).func_187315_a((double)f, (double)f2).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_181675_d();
        buffer.func_181662_b((double)(f5 + rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 + rotationYZ * f4 - rotationXZ * f4)).func_187315_a((double)f, (double)f3).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_181675_d();
        Tessellator.func_178181_a().func_78381_a();
        GlStateManager.func_179084_k();
        GlStateManager.func_179145_e();
        GlStateManager.func_179121_F();
    }

    public int func_189214_a(float p_189214_1_) {
        return 61680;
    }
}

