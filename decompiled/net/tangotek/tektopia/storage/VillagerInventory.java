/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.InventoryBasic
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.Tuple
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.storage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class VillagerInventory
extends InventoryBasic {
    private final EntityVillagerTek owner;

    public VillagerInventory(EntityVillagerTek villager, String title, boolean customName, int slotCount) {
        super(title, customName, slotCount);
        this.owner = villager;
    }

    @SideOnly(value=Side.CLIENT)
    public VillagerInventory(ITextComponent title, int slotCount) {
        this(null, title.func_150260_c(), true, slotCount);
    }

    public List<ItemStack> removeItems(Function<ItemStack, Integer> func, int itemCount) {
        return this.findItems(func, itemCount, true);
    }

    public List<ItemStack> removeItems(Predicate<ItemStack> pred, int itemCount) {
        return this.findItems(p -> pred.test((ItemStack)p) ? 1 : 0, itemCount, true);
    }

    public List<ItemStack> getItems(Predicate<ItemStack> pred, int itemCount) {
        return this.findItems(p -> pred.test((ItemStack)p) ? 1 : 0, itemCount, false);
    }

    public ItemStack getItem(Function<ItemStack, Integer> func) {
        List<ItemStack> items = this.findItems(func, 1, false);
        if (!items.isEmpty()) {
            return items.get(0);
        }
        return ItemStack.field_190927_a;
    }

    public List<ItemStack> getItems(Function<ItemStack, Integer> func, int itemCount) {
        return this.findItems(func, itemCount, false);
    }

    public List<ItemStack> getItemsByStack(Function<ItemStack, Integer> func, int itemCount) {
        return this.findItems(func, itemCount, false);
    }

    public int getItemCount(Predicate<ItemStack> pred) {
        return this.getItemCount((ItemStack item) -> pred.test((ItemStack)item) ? 1 : -1);
    }

    public int getItemCount(Function<ItemStack, Integer> func) {
        List<ItemStack> itemList = this.getItems(func, 0);
        int count = 0;
        for (ItemStack itemStack : itemList) {
            count += itemStack.func_190916_E();
        }
        return count;
    }

    public void cloneFrom(VillagerInventory other) {
        for (int i = 0; i < other.func_70302_i_(); ++i) {
            this.func_174894_a(other.func_70301_a(i));
        }
    }

    public static int countItems(List<ItemStack> items) {
        return items.stream().mapToInt(ItemStack::func_190916_E).sum();
    }

    private List<ItemStack> findItems(Function<ItemStack, Integer> func, int itemCount, boolean remove) {
        int i;
        ArrayList<ItemStack> outList = new ArrayList<ItemStack>();
        int needed = itemCount > 0 ? itemCount : Integer.MAX_VALUE;
        ArrayList<Tuple> matchedItems = new ArrayList<Tuple>();
        for (i = 0; i < this.func_70302_i_(); ++i) {
            ItemStack itemStack = this.func_70301_a(i);
            if (!ModItems.canVillagerSee(itemStack) || func.apply(itemStack) <= 0) continue;
            matchedItems.add(new Tuple((Object)itemStack, (Object)i));
        }
        matchedItems.sort(Comparator.comparing(pair -> (Integer)func.apply((ItemStack)pair.func_76341_a())));
        for (i = matchedItems.size() - 1; i >= 0 && needed > 0; --i) {
            Tuple pair2 = (Tuple)matchedItems.get(i);
            ItemStack itemStack = (ItemStack)pair2.func_76341_a();
            int slot = (Integer)pair2.func_76340_b();
            if (remove) {
                if (itemStack.func_190916_E() <= needed) {
                    outList.add(itemStack);
                    this.func_70299_a(slot, ItemStack.field_190927_a);
                    needed -= itemStack.func_190916_E();
                    continue;
                }
                itemStack.func_190918_g(needed);
                ItemStack newItem = itemStack.func_77946_l();
                newItem.func_190920_e(needed);
                outList.add(newItem);
                needed = 0;
                this.onInventoryUpdated(newItem);
                continue;
            }
            outList.add(itemStack);
            needed -= itemStack.func_190916_E();
        }
        return outList;
    }

    private void onInventoryUpdated(ItemStack itemStack) {
        if (this.owner != null && !this.owner.field_70170_p.field_72995_K) {
            this.owner.onInventoryUpdated(itemStack);
        }
    }

    public void deleteOverstock(Item itemOuter, int max) {
        this.deleteOverstock((ItemStack p) -> p.func_77973_b() == itemOuter, max);
    }

    public void deleteOverstock(Predicate<ItemStack> predicate, int max) {
        int count = 0;
        for (int i = 0; i < this.func_70302_i_(); ++i) {
            ItemStack itemStack = this.func_70301_a(i);
            if (!predicate.test(itemStack)) continue;
            if (count >= max) {
                this.func_70299_a(i, ItemStack.field_190927_a);
                continue;
            }
            if (count + itemStack.func_190916_E() > max) {
                itemStack.func_190920_e(max - count);
                this.onInventoryUpdated(itemStack);
                count = max;
                continue;
            }
            count += itemStack.func_190916_E();
        }
    }

    public boolean hasSlotFree() {
        for (int i = 0; i < this.func_70302_i_(); ++i) {
            ItemStack itemStack = this.func_70301_a(i);
            if (!itemStack.func_190926_b()) continue;
            return true;
        }
        return false;
    }

    public boolean canStore(ItemStack stack) {
        if (stack == null || stack.func_190926_b()) {
            return true;
        }
        int remaining = stack.func_190916_E();
        for (int i = 0; i < this.func_70302_i_(); ++i) {
            ItemStack oldItem = this.func_70301_a(i);
            if (oldItem.func_190926_b()) {
                return true;
            }
            if (!VillagerInventory.areItemsStackable(oldItem, stack)) continue;
            int maxSize = Math.min(this.func_70297_j_(), oldItem.func_77976_d());
            int free = maxSize - oldItem.func_190916_E();
            if (free <= 0) continue;
            remaining -= free;
            if (remaining <= 0) {
                return true;
            }
        }
        return false;
    }

    public void mergeItems(VillagerInventory other) {
        for (int i = 0; i < other.func_70302_i_(); ++i) {
            ItemStack itemStack = other.func_70301_a(i);
            if (!itemStack.func_190926_b()) continue;
            this.func_174894_a(itemStack);
        }
    }

    public ItemStack func_174894_a(ItemStack stack) {
        ItemStack newItem = stack.func_77946_l();
        int emptySlot = -1;
        for (int i = 0; i < this.func_70302_i_(); ++i) {
            ItemStack oldItem = this.func_70301_a(i);
            if (oldItem.func_190926_b() && emptySlot < 0) {
                emptySlot = i;
                continue;
            }
            if (!VillagerInventory.areItemsStackable(oldItem, newItem)) continue;
            int j = Math.min(this.func_70297_j_(), oldItem.func_77976_d());
            int k = Math.min(newItem.func_190916_E(), j - oldItem.func_190916_E());
            if (k <= 0) continue;
            oldItem.func_190917_f(k);
            newItem.func_190918_g(k);
            if (!newItem.func_190926_b()) continue;
            this.onInventoryUpdated(oldItem);
            this.func_70296_d();
            return ItemStack.field_190927_a;
        }
        if (emptySlot >= 0 && !newItem.func_190926_b()) {
            this.func_70299_a(emptySlot, newItem);
            return ItemStack.field_190927_a;
        }
        if (newItem.func_190916_E() != stack.func_190916_E()) {
            this.func_70296_d();
        }
        return newItem;
    }

    public ItemStack func_70304_b(int index) {
        ItemStack itemStack = super.func_70304_b(index);
        if (!itemStack.func_190926_b()) {
            this.onInventoryUpdated(itemStack);
        }
        return itemStack;
    }

    public void func_70299_a(int index, ItemStack stack) {
        ItemStack oldItem = this.func_70301_a(index);
        super.func_70299_a(index, stack);
        if (!oldItem.func_190926_b()) {
            this.onInventoryUpdated(oldItem);
        }
        this.onInventoryUpdated(stack);
    }

    public static boolean areItemsStackable(ItemStack itemStack1, ItemStack itemStack2) {
        return ItemStack.func_179545_c((ItemStack)itemStack1, (ItemStack)itemStack2) && ItemStack.func_77970_a((ItemStack)itemStack1, (ItemStack)itemStack2);
    }

    public void debugSpam() {
        for (int i = 0; i < this.func_70302_i_(); ++i) {
            ItemStack itemStack = this.func_70301_a(i);
            if (itemStack.func_190926_b()) continue;
            System.out.println("    Item: " + itemStack.toString());
        }
    }

    public void writeNBT(NBTTagCompound compound) {
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.func_70302_i_(); ++i) {
            ItemStack itemstack = this.func_70301_a(i);
            if (itemstack.func_190926_b()) continue;
            nbttaglist.func_74742_a((NBTBase)itemstack.func_77955_b(new NBTTagCompound()));
        }
        compound.func_74782_a("Inventory", (NBTBase)nbttaglist);
    }

    public void readNBT(NBTTagCompound compound) {
        this.func_174888_l();
        NBTTagList nbttaglist = compound.func_150295_c("Inventory", 10);
        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            ItemStack itemstack = new ItemStack(nbttaglist.func_150305_b(i));
            if (itemstack.func_190926_b()) continue;
            this.func_174894_a(itemstack);
        }
    }
}
