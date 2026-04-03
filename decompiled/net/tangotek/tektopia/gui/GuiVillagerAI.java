/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  org.lwjgl.input.Mouse
 */
package net.tangotek.tektopia.gui;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.gui.GuiTekCheckBox;
import net.tangotek.tektopia.gui.GuiVillager;
import net.tangotek.tektopia.network.PacketAIFilter;
import org.lwjgl.input.Mouse;

public class GuiVillagerAI
extends GuiContainer {
    public static final ResourceLocation GUI_VILLAGER_AI_TEXTURE = new ResourceLocation("tektopia", "textures/gui/container/villager_ai.png");
    private final EntityVillagerTek villager;
    private float currentScroll = 0.0f;
    private int scrollIndex = 0;
    private List<String> aiFilters;
    private final int MAX_VISIBLE_ROWS = 10;
    private boolean isScrolling;
    private boolean wasClicking;

    public GuiVillagerAI(EntityVillagerTek villager) {
        super(new Container(){

            public boolean func_75145_c(EntityPlayer playerIn) {
                return false;
            }
        });
        this.field_146999_f = 178;
        this.field_147000_g = 157;
        this.villager = villager;
        this.aiFilters = villager.getAIFilters();
        Collections.sort(this.aiFilters);
    }

    public void func_73866_w_() {
        super.func_73866_w_();
        for (int buttonId = 0; buttonId < 10; ++buttonId) {
            this.func_189646_b(new GuiTekCheckBox(buttonId, this.field_147003_i + 8, this.field_147009_r + 11 * buttonId + 39, 10, 10, true, 190, 57, 180, 57, GUI_VILLAGER_AI_TEXTURE));
        }
    }

    protected void func_146284_a(GuiButton button) throws IOException {
        if (button.field_146124_l && button instanceof GuiTekCheckBox) {
            GuiTekCheckBox checkBox = (GuiTekCheckBox)button;
            int index = checkBox.field_146127_k + this.scrollIndex;
            String aiFilter = this.aiFilters.get(index);
            TekVillager.NETWORK.sendToServer((IMessage)new PacketAIFilter((Entity)this.villager, aiFilter, checkBox.isChecked()));
        }
    }

    private boolean needsScrollBar() {
        return this.aiFilters.size() > 10;
    }

    public void func_146274_d() throws IOException {
        super.func_146274_d();
        int i = Mouse.getEventDWheel();
        if (i != 0 && this.needsScrollBar()) {
            int j = this.aiFilters.size() - 10;
            if (i > 0) {
                i = 1;
            }
            if (i < 0) {
                i = -1;
            }
            this.currentScroll = (float)((double)this.currentScroll - (double)i / (double)j);
            this.currentScroll = MathHelper.func_76131_a((float)this.currentScroll, (float)0.0f, (float)1.0f);
            this.scrollTo(this.currentScroll);
        }
    }

    private void scrollTo(float scroll) {
        this.scrollIndex = (int)((float)(this.aiFilters.size() - 10) * scroll);
        if (this.scrollIndex < 0) {
            this.scrollIndex = 0;
        }
    }

    protected void func_73864_a(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.func_146978_c(14, 0, 28, 29, mouseX, mouseY)) {
            this.field_146297_k.func_147108_a((GuiScreen)new GuiVillager(this.villager));
        }
        super.func_73864_a(mouseX, mouseY, mouseButton);
    }

    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        this.func_146276_q_();
        boolean flag = Mouse.isButtonDown((int)0);
        int i = this.field_147003_i;
        int j = this.field_147009_r;
        int k = i + 160;
        int l = j + 39;
        int i1 = k + 14;
        int j1 = l + 112;
        if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1) {
            this.isScrolling = this.needsScrollBar();
        }
        if (!flag) {
            this.isScrolling = false;
        }
        this.wasClicking = flag;
        if (this.isScrolling) {
            this.currentScroll = ((float)(mouseY - l) - 7.5f) / ((float)(j1 - l) - 15.0f);
            this.currentScroll = MathHelper.func_76131_a((float)this.currentScroll, (float)0.0f, (float)1.0f);
            this.scrollTo(this.currentScroll);
        }
        this.updateChecks();
        super.func_73863_a(mouseX, mouseY, partialTicks);
    }

    private void updateChecks() {
        for (GuiButton button : this.field_146292_n) {
            if (!(button instanceof GuiTekCheckBox)) continue;
            GuiTekCheckBox checkBox = (GuiTekCheckBox)button;
            int filterIndex = checkBox.field_146127_k + this.scrollIndex;
            if (filterIndex < this.aiFilters.size()) {
                checkBox.field_146125_m = true;
                checkBox.setIsChecked(this.villager.isAIFilterEnabled(this.aiFilters.get(filterIndex)));
                continue;
            }
            checkBox.field_146125_m = false;
        }
    }

    protected void func_146979_b(int mouseX, int mouseY) {
        GlStateManager.func_179140_f();
        GlStateManager.func_179084_k();
        this.field_146296_j.func_184391_a((EntityLivingBase)this.field_146297_k.field_71439_g, new ItemStack((Item)ModItems.getProfessionToken(this.villager.getProfessionType())), 20, 10);
        int topY = 40;
        for (int i = 0; i < 10; ++i) {
            int filterIndex = this.scrollIndex + i;
            if (filterIndex >= this.aiFilters.size()) continue;
            String filterName = this.aiFilters.get(filterIndex);
            TextComponentTranslation filterText = new TextComponentTranslation("ai.filter." + filterName, new Object[0]);
            this.field_146289_q.func_78276_b(filterText.func_150260_c(), 20, 40 + i * 11, 0x404040);
        }
        GlStateManager.func_179145_e();
    }

    protected void func_146976_a(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.field_146297_k.func_110434_K().func_110577_a(GUI_VILLAGER_AI_TEXTURE);
        int i = (this.field_146294_l - this.field_146999_f) / 2;
        int j = (this.field_146295_m - this.field_147000_g) / 2;
        this.func_73729_b(i, j, 0, 0, this.field_146999_f, this.field_147000_g);
        int scrollLeft = this.field_147003_i + 160;
        int scrollTop = this.field_147009_r + 39;
        int scrollHeight = scrollTop + 112;
        this.func_73729_b(scrollLeft, scrollTop + (int)((float)(scrollHeight - scrollTop - 17) * this.currentScroll), 180 + (this.needsScrollBar() ? 0 : 12), 38, 12, 15);
    }
}

