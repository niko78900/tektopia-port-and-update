/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.util.ResourceLocation
 */
package net.tangotek.tektopia.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiTekCheckBox
extends GuiButton {
    private boolean isChecked;
    ResourceLocation resourceLocation;
    private int checkedTexX;
    private int checkedTexY;
    private int uncheckedTexX;
    private int uncheckedTexY;

    public GuiTekCheckBox(int buttonId, int x, int y, int widthIn, int heightIn, boolean isChecked, int checkedTexX, int checkedTexY, int uncheckedTexX, int uncheckedTexY, ResourceLocation resourceLocation) {
        super(buttonId, x, y, widthIn, heightIn, "");
        this.resourceLocation = resourceLocation;
        this.isChecked = isChecked;
        this.field_146120_f = widthIn;
        this.field_146121_g = heightIn;
        this.checkedTexX = checkedTexX;
        this.checkedTexY = checkedTexY;
        this.uncheckedTexX = uncheckedTexX;
        this.uncheckedTexY = uncheckedTexY;
    }

    public void func_191745_a(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (this.field_146125_m) {
            this.field_146123_n = mouseX >= this.field_146128_h && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g;
            mc.func_110434_K().func_110577_a(this.resourceLocation);
            GlStateManager.func_179097_i();
            if (this.isChecked) {
                this.func_73729_b(this.field_146128_h, this.field_146129_i, this.checkedTexX, this.checkedTexY, this.field_146120_f, this.field_146121_g);
            } else {
                this.func_73729_b(this.field_146128_h, this.field_146129_i, this.uncheckedTexX, this.uncheckedTexY, this.field_146120_f, this.field_146121_g);
            }
            GlStateManager.func_179126_j();
        }
    }

    public boolean func_146116_c(Minecraft mc, int mouseX, int mouseY) {
        if (this.field_146124_l && this.field_146125_m && mouseX >= this.field_146128_h && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g) {
            this.isChecked = !this.isChecked;
            return true;
        }
        return false;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}

