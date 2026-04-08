package net.tangotek.tektopia.village;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.structures.TekStructureStorage;

public final class TekVillageEconomy {
    public enum ArmorClass {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }

    private final List<ChestTileEntity> chests;

    public TekVillageEconomy(List<ChestTileEntity> chests) {
        this.chests = Collections.unmodifiableList(new ArrayList<>(chests));
    }

    public List<ChestTileEntity> getChests() {
        return this.chests;
    }

    public static TekVillageEconomy fromStorage(ServerWorld level, TekStructureStorage storage) {
        List<ChestTileEntity> chests = new ArrayList<>();
        for (BlockPos pos : storage.getChestPositions()) {
            TileEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ChestTileEntity) {
                chests.add((ChestTileEntity) blockEntity);
            }
        }
        return new TekVillageEconomy(chests);
    }

    public int countItem(Item item) {
        int total = 0;
        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack stack = chest.getItem(slot);
                if (!stack.isEmpty() && stack.getItem() == item) {
                    total += stack.getCount();
                }
            }
        }
        return total;
    }

    public Map<Item, Integer> countItemsByType() {
        Map<Item, Integer> counts = new HashMap<>();
        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack stack = chest.getItem(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }
        return counts;
    }

    public boolean canInsert(ItemStack stack) {
        return this.collectInsertPlan(stack) != null;
    }

    public boolean insert(ItemStack stack) {
        List<InsertStep> insertPlan = this.collectInsertPlan(stack);
        if (insertPlan == null) {
            return false;
        }
        this.applyInsertPlan(insertPlan, stack);
        return true;
    }

    public boolean craftWithInputs(Map<Item, Integer> inputs, ItemStack output) {
        if (inputs.isEmpty()) {
            return this.insert(output);
        }

        List<InsertStep> insertPlan = this.collectInsertPlan(output);
        if (insertPlan == null) {
            return false;
        }

        List<ConsumeStep> consumePlan = this.collectConsumePlan(inputs);
        if (consumePlan == null) {
            return false;
        }

        this.applyConsumePlan(consumePlan);
        this.applyInsertPlan(insertPlan, output);
        return true;
    }

    public Map<ArmorClass, Integer> countArmorByClass(Map<Item, ArmorClass> classifier) {
        Map<ArmorClass, Integer> counts = new EnumMap<>(ArmorClass.class);
        for (ArmorClass armorClass : ArmorClass.values()) {
            counts.put(armorClass, 0);
        }
        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack stack = chest.getItem(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                ArmorClass armorClass = classifier.get(stack.getItem());
                if (armorClass == null) {
                    continue;
                }
                counts.put(armorClass, counts.get(armorClass) + stack.getCount());
            }
        }
        return counts;
    }

    public List<ItemStack> snapshotStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack stack = chest.getItem(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                stacks.add(stack.copy());
            }
        }
        return stacks;
    }

    public boolean extractOne(ItemStack template) {
        if (template == null || template.isEmpty()) {
            return false;
        }
        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack stack = chest.getItem(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                if (!ItemStack.isSame(stack, template) || !ItemStack.tagMatches(stack, template)) {
                    continue;
                }
                stack.shrink(1);
                if (stack.isEmpty()) {
                    chest.setItem(slot, ItemStack.EMPTY);
                }
                chest.setChanged();
                return true;
            }
        }
        return false;
    }

    private List<ConsumeStep> collectConsumePlan(Map<Item, Integer> inputs) {
        List<ConsumeStep> plan = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : inputs.entrySet()) {
            int remaining = Math.max(0, entry.getValue());
            if (remaining == 0) {
                continue;
            }
            Item targetItem = entry.getKey();
            for (ChestTileEntity chest : this.chests) {
                for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                    ItemStack stack = chest.getItem(slot);
                    if (stack.isEmpty() || stack.getItem() != targetItem) {
                        continue;
                    }
                    int take = Math.min(stack.getCount(), remaining);
                    if (take <= 0) {
                        continue;
                    }
                    plan.add(new ConsumeStep(chest, slot, take));
                    remaining -= take;
                    if (remaining <= 0) {
                        break;
                    }
                }
                if (remaining <= 0) {
                    break;
                }
            }
            if (remaining > 0) {
                return null;
            }
        }
        return plan;
    }

    private List<InsertStep> collectInsertPlan(ItemStack stack) {
        int remaining = stack.getCount();
        List<InsertStep> plan = new ArrayList<>();
        if (remaining <= 0) {
            return plan;
        }

        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack existing = chest.getItem(slot);
                if (existing.isEmpty() || !ItemStack.isSame(existing, stack)) {
                    continue;
                }
                int max = Math.min(existing.getMaxStackSize(), chest.getMaxStackSize());
                int room = max - existing.getCount();
                int move = Math.min(room, remaining);
                if (move <= 0) {
                    continue;
                }
                plan.add(new InsertStep(chest, slot, move, false));
                remaining -= move;
                if (remaining <= 0) {
                    return plan;
                }
            }
        }

        for (ChestTileEntity chest : this.chests) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack existing = chest.getItem(slot);
                if (!existing.isEmpty()) {
                    continue;
                }
                int move = Math.min(chest.getMaxStackSize(), remaining);
                plan.add(new InsertStep(chest, slot, move, true));
                remaining -= move;
                if (remaining <= 0) {
                    return plan;
                }
            }
        }
        return null;
    }

    private void applyConsumePlan(List<ConsumeStep> plan) {
        for (ConsumeStep step : plan) {
            ItemStack stack = step.chest.getItem(step.slot);
            if (stack.isEmpty()) {
                continue;
            }
            stack.shrink(step.amount);
            if (stack.isEmpty()) {
                step.chest.setItem(step.slot, ItemStack.EMPTY);
            }
            step.chest.setChanged();
        }
    }

    private void applyInsertPlan(List<InsertStep> plan, ItemStack source) {
        for (InsertStep step : plan) {
            ItemStack current = step.chest.getItem(step.slot);
            if (step.emptySlot) {
                ItemStack inserted = source.copy();
                inserted.setCount(step.amount);
                step.chest.setItem(step.slot, inserted);
            } else {
                if (!current.isEmpty()) {
                    current.grow(step.amount);
                }
            }
            step.chest.setChanged();
        }
    }

    private static final class ConsumeStep {
        private final ChestTileEntity chest;
        private final int slot;
        private final int amount;

        private ConsumeStep(ChestTileEntity chest, int slot, int amount) {
            this.chest = chest;
            this.slot = slot;
            this.amount = amount;
        }
    }

    private static final class InsertStep {
        private final ChestTileEntity chest;
        private final int slot;
        private final int amount;
        private final boolean emptySlot;

        private InsertStep(ChestTileEntity chest, int slot, int amount, boolean emptySlot) {
            this.chest = chest;
            this.slot = slot;
            this.amount = amount;
            this.emptySlot = emptySlot;
        }
    }
}
