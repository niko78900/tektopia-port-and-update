/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.client.renderer.vertex.VertexFormat
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleSkull
extends Particle {
    private static final ResourceLocation SAND_TEXTURE = new ResourceLocation("tektopia", "textures/particle/skull.png");
    private static final VertexFormat VERTEX_FORMAT = new VertexFormat().func_181721_a(DefaultVertexFormats.field_181713_m).func_181721_a(DefaultVertexFormats.field_181715_o).func_181721_a(DefaultVertexFormats.field_181714_n).func_181721_a(DefaultVertexFormats.field_181716_p).func_181721_a(DefaultVertexFormats.field_181717_q).func_181721_a(DefaultVertexFormats.field_181718_r);
    private int life;
    public double radius;
    public double radiusGrow;
    public double angle;
    public double torque;
    public int lifeTime;
    private final TextureManager textureManager;
    public final float size;
    private final Vec3d origin;

    public ParticleSkull(World world, TextureManager textureManagerIn, Vec3d pos, double motionY) {
        super(world, pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, 0.0, 0.0, 0.0);
        float f;
        this.textureManager = textureManagerIn;
        this.field_70552_h = f = this.field_187136_p.nextFloat() * 0.4f + 0.6f;
        this.field_70553_i = f;
        this.field_70551_j = f;
        this.size = 0.1f + (float)(Math.random() * (double)0.15f);
        this.field_187130_j = motionY;
        this.origin = new Vec3d(this.field_187126_f, this.field_187127_g, this.field_187128_h);
        this.angle = Math.random() * 360.0;
    }

    public void func_180434_a(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        this.textureManager.func_110577_a(SAND_TEXTURE);
        float f = 0.0f;
        float f1 = 1.0f;
        float f2 = 0.0f;
        float f3 = 1.0f;
        float f4 = this.size;
        float f5 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)partialTicks - field_70556_an);
        float f6 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)partialTicks - field_70554_ao);
        float f7 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)partialTicks - field_70555_ap);
        GlStateManager.func_179147_l();
        GlStateManager.func_187401_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.func_179094_E();
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)((float)(this.lifeTime - this.life) / (float)this.lifeTime));
        GlStateManager.func_179140_f();
        RenderHelper.func_74518_a();
        buffer.func_181668_a(7, VERTEX_FORMAT);
        buffer.func_181662_b((double)(f5 - rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 - rotationYZ * f4 - rotationXZ * f4)).func_187315_a((double)f1, (double)f3).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(0, 240).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
        buffer.func_181662_b((double)(f5 - rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 - rotationYZ * f4 + rotationXZ * f4)).func_187315_a((double)f1, (double)f2).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(0, 240).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
        buffer.func_181662_b((double)(f5 + rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 + rotationYZ * f4 + rotationXZ * f4)).func_187315_a((double)f, (double)f2).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(0, 240).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
        buffer.func_181662_b((double)(f5 + rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 + rotationYZ * f4 - rotationXZ * f4)).func_187315_a((double)f, (double)f3).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(0, 240).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
        Tessellator.func_178181_a().func_78381_a();
        GlStateManager.func_179121_F();
        GlStateManager.func_179084_k();
        GlStateManager.func_179145_e();
    }

    public void func_189213_a() {
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        ++this.life;
        this.updatePosition();
        if (this.life == this.lifeTime) {
            this.func_187112_i();
        }
    }

    private void updatePosition() {
        double x = this.origin.field_72450_a + Math.cos(this.angle) * this.radius;
        double z = this.origin.field_72449_c + Math.sin(this.angle) * this.radius;
        this.func_187109_b(x, this.field_187127_g + this.field_187130_j, z);
        this.radius += this.radiusGrow;
        this.angle += this.torque;
    }

    public int func_70537_b() {
        return 3;
    }

    public boolean func_187111_c() {
        return true;
    }
}

