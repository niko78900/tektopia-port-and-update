/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.client.config.HoverChecker
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.gui;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.gui.ContainerVillager;
import net.tangotek.tektopia.gui.GuiVillagerAI;

@SideOnly(value=Side.CLIENT)
public class GuiVillager
extends GuiContainer {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("tektopia", "textures/gui/container/villager_main.png");
    private final EntityVillagerTek villager;
    private Map<HoverChecker, String> hoverChecks = new HashMap<HoverChecker, String>();

    public GuiVillager(EntityVillagerTek villager) {
        super((Container)new ContainerVillager(villager.getInventory()));
        this.villager = villager;
        this.field_146999_f = 178;
        this.field_147000_g = 157;
    }

    protected void func_73864_a(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.villager.isRole(VillagerRole.VILLAGER) && this.func_146978_c(43, 0, 28, 29, mouseX, mouseY)) {
            this.field_146297_k.func_147108_a((GuiScreen)new GuiVillagerAI(this.villager));
        }
        super.func_73864_a(mouseX, mouseY, mouseButton);
    }

    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        this.func_146276_q_();
        super.func_73863_a(mouseX, mouseY, partialTicks);
        this.func_191948_b(mouseX, mouseY);
        for (Map.Entry<HoverChecker, String> entry : this.hoverChecks.entrySet()) {
            if (!entry.getKey().checkHover(mouseX, mouseY)) continue;
            this.func_146279_a(entry.getValue(), mouseX, mouseY);
        }
    }

    protected void func_146979_b(int mouseX, int mouseY) {
        GlStateManager.func_179140_f();
        GlStateManager.func_179084_k();
        boolean firstDraw = this.hoverChecks.isEmpty();
        this.field_146296_j.func_184391_a((EntityLivingBase)this.field_146297_k.field_71439_g, new ItemStack((Item)ModItems.getProfessionToken(this.villager.getProfessionType())), 20, 10);
        this.field_146296_j.func_184391_a((EntityLivingBase)this.field_146297_k.field_71439_g, new ItemStack((Item)ModItems.getProfessionToken(this.villager.getProfessionType())), 5, 34);
        if (firstDraw) {
            HoverChecker hoverChecker = new HoverChecker(this.field_147009_r + 34, this.field_147009_r + 34 + 16, this.field_147003_i + 5, this.field_147003_i + 5 + 16, 350);
            this.hoverChecks.put(hoverChecker, I18n.func_135052_a((String)("item.prof_" + this.villager.getProfessionType().name + ".name"), (Object[])new Object[0]));
        }
        this.field_146289_q.func_78276_b(this.villager.func_145748_c_().func_150254_d(), 27, 39, 0x404040);
        this.drawStat("ui.villager.health", 185, 29, 2, 52, this.villager.func_110143_aJ(), this.villager.func_110138_aP(), firstDraw);
        this.drawStat("ui.villager.hunger", 196, 29, 6, 63, this.villager.getHunger(), this.villager.getMaxHunger(), firstDraw);
        this.drawStat("ui.villager.happy", 207, 29, 4, 74, this.villager.getHappy(), this.villager.getMaxHappy(), firstDraw);
        this.drawStat("ui.villager.intelligence", 218, 29, 0, 85, this.villager.getIntelligence(), this.villager.getMaxIntelligence(), firstDraw);
        this.drawProfessions(firstDraw);
        GlStateManager.func_179145_e();
    }

    protected void func_146976_a(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.field_146297_k.func_110434_K().func_110577_a(GUI_TEXTURE);
        int i = (this.field_146294_l - this.field_146999_f) / 2;
        int j = (this.field_146295_m - this.field_147000_g) / 2;
        this.func_73729_b(i, j, 0, 0, this.field_146999_f, this.field_147000_g);
    }

    private void drawProfessions(boolean firstDraw) {
        ArrayList<AbstractMap.SimpleEntry<ProfessionType, Integer>> skillList = new ArrayList<AbstractMap.SimpleEntry<ProfessionType, Integer>>();
        for (ProfessionType pt : ProfessionType.values()) {
            int skill = this.villager.getSkill(pt);
            if (skill <= 0 || !pt.canCopy) continue;
            skillList.add(new AbstractMap.SimpleEntry<ProfessionType, Integer>(pt, skill));
        }
        Comparator<AbstractMap.SimpleEntry> comp = Comparator.comparing(AbstractMap.SimpleEntry::getValue);
        skillList.sort(comp);
        Collections.reverse(skillList);
        this.drawProfession(skillList, 0, 110, 55, firstDraw);
        this.drawProfession(skillList, 1, 110, 73, firstDraw);
        this.drawProfession(skillList, 2, 145, 55, firstDraw);
        this.drawProfession(skillList, 3, 145, 73, firstDraw);
    }

    private void drawProfession(List<AbstractMap.SimpleEntry<ProfessionType, Integer>> skillList, int index, int x, int y, boolean firstDraw) {
        if (index < skillList.size()) {
            ProfessionType profType = skillList.get(index).getKey();
            int level = skillList.get(index).getValue();
            ItemStack itemStack = new ItemStack((Item)ModItems.getProfessionToken(profType), level);
            this.field_146296_j.func_184391_a((EntityLivingBase)this.field_146297_k.field_71439_g, itemStack, x, y);
            int color = 0xFFFFFF;
            if (this.villager.getBlessed() > 0 && profType == this.villager.getProfessionType()) {
                color = EnumDyeColor.LIME.func_193350_e();
            }
            int xPosition = x + 12;
            int yPosition = y - 5;
            String s = String.valueOf(level);
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            GlStateManager.func_179084_k();
            this.field_146289_q.func_175063_a(s, (float)(xPosition + 19 - 2 - this.field_146289_q.func_78256_a(s)), (float)(yPosition + 6 + 3), color);
            GlStateManager.func_179145_e();
            GlStateManager.func_179126_j();
            GlStateManager.func_179147_l();
            if (firstDraw) {
                HoverChecker hoverChecker = new HoverChecker(this.field_147009_r + y, this.field_147009_r + y + 16, this.field_147003_i + x, this.field_147003_i + x + 16, 350);
                this.hoverChecks.put(hoverChecker, I18n.func_135052_a((String)("item.prof_" + profType.name + ".name"), (Object[])new Object[0]));
            }
        }
    }

    private void drawStat(String name, int xTex, int yTex, int colorIndex, int y, float value, float max, boolean firstDraw) {
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.field_146297_k.func_110434_K().func_110577_a(GUI_TEXTURE);
        int BAR_LEFT = 24;
        int BAR_WIDTH = 67;
        int BAR_TEX_LEFT = 182;
        int BAR_TEX_TOP = 53;
        int ICON_LEFT = 8;
        this.func_73729_b(8, y, xTex, yTex, 11, 9);
        if (firstDraw) {
            HoverChecker hoverChecker = new HoverChecker(this.field_147009_r + y, this.field_147009_r + y + 9, this.field_147003_i + 8, this.field_147003_i + 8 + 11, 350);
            this.hoverChecks.put(hoverChecker, I18n.func_135052_a((String)name, (Object[])new Object[0]));
        }
        this.func_73729_b(24, y + 2, 182, 53 + colorIndex * 5 * 2, 67, 5);
        int i = (int)(value / max * 67.0f);
        if (i > 0) {
            this.func_73729_b(24, y + 2, 182, 53 + (colorIndex * 5 * 2 + 5), i, 5);
        }
        this.field_146289_q.func_78276_b(String.valueOf((int)value), 93, y + 1, 0x404040);
    }
}

