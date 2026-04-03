/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.nbt.NBTTagString
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.village.MerchantRecipe
 *  net.minecraft.village.MerchantRecipeList
 */
package net.tangotek.tektopia.economy;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.economy.ItemValue;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class ItemEconomy {
    private Random rnd = new Random();
    protected Map<String, ItemValue> items = new HashMap<String, ItemValue>();
    protected MerchantRecipeList merchantList = new MerchantRecipeList();
    private boolean itemsDirty = true;
    private Queue<ItemValue> salesHistory = new LinkedList<ItemValue>();
    private float totalMarketValue = 0.0f;
    private float totalSalesAppearanceWeight;
    private int tradesAvailableForProfs;
    private Set<ProfessionType> profsInVillage = new HashSet<ProfessionType>();
    static final int MAX_ITEM_SALES = 5;
    static final int ITEMS_FOR_SALE = 8;

    public void addItem(ItemValue itemValue) {
        ItemValue existingValue = this.items.put(itemValue.getName(), itemValue);
        if (existingValue != null) {
            throw new InvalidParameterException("Duplicate marketId in economy: " + itemValue.getName());
        }
        this.totalMarketValue += (float)itemValue.getBaseValue();
        this.totalSalesAppearanceWeight += itemValue.getAppearanceWeight();
        this.itemsDirty = true;
    }

    public boolean hasItems() {
        return !this.items.isEmpty();
    }

    public void refreshValues(Village village) {
        this.totalSalesAppearanceWeight = 0.0f;
        this.tradesAvailableForProfs = 0;
        this.items.values().forEach(iv -> iv.reset());
        this.profsInVillage.clear();
        List<EntityVillagerTek> villagers = village.getWorld().func_72872_a(EntityVillagerTek.class, village.getAABB().func_186662_g(40.0));
        for (EntityVillagerTek v : villagers) {
            this.profsInVillage.add(v.getProfessionType());
        }
        if (this.profsInVillage.isEmpty()) {
            boolean bl = true;
        }
        int historyIndex = 1;
        for (ItemValue iv2 : this.salesHistory) {
            float reduction = iv2.markDown((float)historyIndex / (float)this.salesHistory.size());
            for (ItemValue iv22 : this.items.values()) {
                if (iv22 == iv2) continue;
                iv22.markUp((float)iv22.getBaseValue() * (reduction / (this.totalMarketValue - (float)iv2.getBaseValue())));
            }
            ++historyIndex;
        }
        for (ItemValue iv2 : this.items.values()) {
            this.totalSalesAppearanceWeight += iv2.getAppearanceWeight();
            if (!this.profsInVillage.contains((Object)iv2.getRequiredProfession())) continue;
            ++this.tradesAvailableForProfs;
        }
        this.itemsDirty = false;
        this.merchantList = null;
    }

    public MerchantRecipeList getMerchantList(Village v, int stallLevel) {
        if (this.itemsDirty) {
            this.refreshValues(v);
        }
        if (this.merchantList == null) {
            v.debugOut("Generating Merchant List");
            this.merchantList = this.generateMerchantList(stallLevel);
        }
        return this.merchantList;
    }

    private int getHistorySize(Village village) {
        return (int)MathHelper.func_151238_b((double)30.0, (double)50.0, (double)((double)village.getResidentCount() / 100.0));
    }

    public void sellItem(MerchantRecipe recipe, Village village) {
        ItemValue itemValue = this.items.get(ItemValue.getName(recipe.func_77394_a()));
        if (itemValue != null) {
            this.salesHistory.add(itemValue);
            if (this.salesHistory.size() > this.getHistorySize(village)) {
                this.salesHistory.poll();
            }
            this.itemsDirty = true;
        }
    }

    private MerchantRecipe createFoodTrade() {
        ArrayList<MerchantRecipe> tempList = new ArrayList<MerchantRecipe>();
        tempList.add(new MerchantRecipe(new ItemStack(Items.field_151166_bC, 2), ItemStack.field_190927_a, ModItems.createTaggedItem(Items.field_151168_bH, 15, ItemTagType.VILLAGER), 0, 1));
        tempList.add(new MerchantRecipe(new ItemStack(Items.field_151166_bC, 2), ItemStack.field_190927_a, ModItems.createTaggedItem(Items.field_151083_be, 8, ItemTagType.VILLAGER), 0, 1));
        tempList.add(new MerchantRecipe(new ItemStack(Items.field_151166_bC, 2), ItemStack.field_190927_a, ModItems.createTaggedItem(Items.field_151158_bO, 10, ItemTagType.VILLAGER), 0, 1));
        tempList.add(new MerchantRecipe(new ItemStack(Items.field_151166_bC, 2), ItemStack.field_190927_a, ModItems.createTaggedItem(Items.field_151106_aX, 15, ItemTagType.VILLAGER), 0, 1));
        tempList.add(new MerchantRecipe(new ItemStack(Items.field_151166_bC, 2), ItemStack.field_190927_a, ModItems.createTaggedItem(Items.field_151150_bK, 6, ItemTagType.VILLAGER), 0, 1));
        return (MerchantRecipe)tempList.get(this.rnd.nextInt(tempList.size()));
    }

    private MerchantRecipeList generateMerchantList(int stallLevel) {
        float totalWeight = this.totalSalesAppearanceWeight;
        MerchantRecipeList outList = new MerchantRecipeList();
        if (this.salesHistory.size() < this.items.size()) {
            outList.add(this.createFoodTrade());
        }
        LinkedList<ItemValue> workItems = new LinkedList<ItemValue>(this.items.values());
        Collections.shuffle(workItems);
        int itemsForSale = 0;
        int MAX_TRADES = 8 + stallLevel * 2;
        block0: while (itemsForSale < MAX_TRADES && !workItems.isEmpty()) {
            float appearanceRoll = this.rnd.nextFloat() * totalWeight;
            float count = 0.0f;
            ListIterator itr = workItems.listIterator();
            while (itr.hasNext()) {
                ItemValue iv = (ItemValue)itr.next();
                count += iv.getAppearanceWeight();
                if (!itr.hasNext()) {
                    System.out.println("Merchant economy count/weight mismatch");
                    count = appearanceRoll + 1.0f;
                }
                if (!(appearanceRoll < count)) continue;
                if (iv.isForSale() && this.profsInVillage.contains((Object)iv.getRequiredProfession())) {
                    outList.add(new MerchantRecipe(ModItems.makeTaggedItem(iv.getItemStack(), ItemTagType.VILLAGER), ItemStack.field_190927_a, new ItemStack(Items.field_151166_bC, iv.getCurrentValue()), 0, 5));
                    ++itemsForSale;
                }
                totalWeight -= iv.getAppearanceWeight();
                itr.remove();
                continue block0;
            }
        }
        return outList;
    }

    public void report(StringBuilder builder) {
        builder.append("========== Item Economy Report ========== [" + this.totalMarketValue + "]\n");
        builder.append("    --History--\n");
        this.salesHistory.forEach(iv -> builder.append("       " + iv.getItemStack() + "\n"));
        builder.append("    --Item Values--\n");
        int sumValues = 0;
        for (ItemValue iv2 : this.items.values()) {
            builder.append("       " + iv2 + "\n");
            sumValues += iv2.getCurrentValue();
        }
        builder.append("     Current Value Sum: " + sumValues + "\n");
    }

    public int getSalesHistorySize() {
        return this.salesHistory.size();
    }

    public void writeNBT(NBTTagCompound compound) {
        NBTTagList historyList = new NBTTagList();
        for (ItemValue iv : this.salesHistory) {
            historyList.func_74742_a((NBTBase)new NBTTagString(iv.getName()));
        }
        compound.func_74782_a("SalesHistory", (NBTBase)historyList);
    }

    public void readNBT(NBTTagCompound compound) {
        this.salesHistory.clear();
        NBTTagList nbttaglist = compound.func_150295_c("SalesHistory", 8);
        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            String name = nbttaglist.func_150307_f(i);
            ItemValue iv = this.items.get(name);
            if (iv == null) continue;
            this.salesHistory.add(iv);
        }
    }
}
