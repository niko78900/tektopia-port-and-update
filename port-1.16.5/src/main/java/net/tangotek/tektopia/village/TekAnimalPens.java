package net.tangotek.tektopia.village;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.common.TekItemMeta;
import net.tangotek.tektopia.structures.TekStructureType;
import net.tangotek.tektopia.structures.TekVillageStructure;

public final class TekAnimalPens {
    private static final String OWNED_TAG = "tektopia_villager_animal";
    private static final String PEN_TAG = "tektopia_pen_type";
    private static final String LAST_COLLECT_TICK_TAG = "tektopia_last_collect_tick";
    private static final long EGG_COOLDOWN = 6000L;
    private static final long MILK_COOLDOWN = 2400L;
    private static final TekStructureType[] PEN_TYPES = new TekStructureType[] {
            TekStructureType.SHEEP_PEN,
            TekStructureType.COW_PEN,
            TekStructureType.PIG_PEN,
            TekStructureType.CHICKEN_COOP,
            TekStructureType.RANCH_PEN
    };

    private TekAnimalPens() {
    }

    public static PenSnapshot findRancherPen(ServerWorld level, TekVillageStructureManager structureManager, TekVillageEconomy economy, long gameTime) {
        List<PenSnapshot> pens = scanPens(level, structureManager);
        for (PenSnapshot pen : pens) {
            if (hasCollectableOutput(pen, economy, gameTime) || canBreed(pen, economy, gameTime)) {
                return pen;
            }
        }
        return pens.isEmpty() ? null : pens.get(0);
    }

    public static String runRancherAction(PenSnapshot pen, TekVillageEconomy economy, long gameTime) {
        if (pen == null || !pen.valid) {
            return "missing_pen";
        }
        tagAnimals(pen);
        String collected = collectOutput(pen, economy, gameTime);
        if (!"-".equals(collected)) {
            return collected;
        }
        if (canBreed(pen, economy, gameTime)) {
            return breedAnimals(pen, economy, gameTime);
        }
        if (pen.animals.isEmpty()) {
            return "empty_" + pen.type.name().toLowerCase(Locale.ROOT);
        }
        if (pen.animals.size() >= pen.capacity) {
            return "full_" + pen.type.name().toLowerCase(Locale.ROOT);
        }
        return "missing_feed";
    }

    public static ButcherTarget findButcherTarget(ServerWorld level, TekVillageStructureManager structureManager, TekVillageEconomy economy, BlockPos origin) {
        List<PenSnapshot> pens = scanPens(level, structureManager);
        ButcherTarget best = null;
        double bestDistance = Double.MAX_VALUE;
        for (PenSnapshot pen : pens) {
            tagAnimals(pen);
            if (pen.animals.size() <= 2) {
                continue;
            }
            for (AnimalEntity animal : pen.animals) {
                if (animal.isBaby()) {
                    continue;
                }
                List<ItemStack> outputs = butcherOutputs(animal);
                if (outputs.isEmpty() || !canInsertAll(economy, outputs)) {
                    continue;
                }
                double distance = origin == null ? 0.0D : animal.blockPosition().distSqr(origin);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = new ButcherTarget(pen, animal, outputs);
                }
            }
        }
        return best;
    }

    public static String processButcherTarget(ButcherTarget target, TekVillageEconomy economy) {
        if (target == null || target.animal == null || !target.animal.isAlive()) {
            return "no_surplus_animal";
        }
        if (target.pen.animals.size() <= 2) {
            return "breeding_stock_reserved";
        }
        if (!canInsertAll(economy, target.outputs)) {
            return "storage_full";
        }
        for (ItemStack output : target.outputs) {
            economy.insert(TekItemMeta.markVillagerItem(output.copy()));
        }
        target.animal.remove();
        return "processed_" + animalKey(target.animal);
    }

    public static String describePens(ServerWorld level, TekVillageStructureManager structureManager) {
        List<PenSnapshot> pens = scanPens(level, structureManager);
        if (pens.isEmpty()) {
            return "no valid animal pens";
        }
        StringBuilder sb = new StringBuilder();
        for (PenSnapshot pen : pens) {
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(pen.type.name().toLowerCase(Locale.ROOT))
                    .append(" animals=")
                    .append(pen.animals.size())
                    .append("/")
                    .append(pen.capacity)
                    .append(" at ")
                    .append(formatPos(pen.workPos));
        }
        return sb.toString();
    }

    private static List<PenSnapshot> scanPens(ServerWorld level, TekVillageStructureManager structureManager) {
        List<PenSnapshot> pens = new ArrayList<>();
        if (level == null || structureManager == null) {
            return pens;
        }
        for (TekStructureType type : PEN_TYPES) {
            TekVillageStructure structure = structureManager.getStructure(type).orElse(null);
            if (structure == null || !structure.isValid() || structure.getBounds() == null) {
                continue;
            }
            List<AnimalEntity> animals = level.getEntitiesOfClass(
                    AnimalEntity.class,
                    structure.getBounds().inflate(2.0D),
                    animal -> animal != null && animal.isAlive() && matchesPen(type, animal)
            );
            animals.sort(Comparator.comparingDouble(animal -> animal.distanceToSqr(structure.getSafeSpot().getX(), structure.getSafeSpot().getY(), structure.getSafeSpot().getZ())));
            int capacity = Math.max(2, Math.min(24, structure.getFloorTileCount() / 2));
            pens.add(new PenSnapshot(type, structure.getSafeSpot() == null ? structure.getDoorInside() : structure.getSafeSpot(), capacity, animals));
        }
        return pens;
    }

    private static boolean matchesPen(TekStructureType type, AnimalEntity animal) {
        switch (type) {
            case SHEEP_PEN:
                return animal instanceof SheepEntity;
            case COW_PEN:
                return animal instanceof CowEntity;
            case PIG_PEN:
                return animal instanceof PigEntity;
            case CHICKEN_COOP:
                return animal instanceof ChickenEntity;
            case RANCH_PEN:
                return isSupportedAnimal(animal);
            default:
                return false;
        }
    }

    private static boolean isSupportedAnimal(AnimalEntity animal) {
        return animal instanceof SheepEntity
                || animal instanceof CowEntity
                || animal instanceof PigEntity
                || animal instanceof ChickenEntity;
    }

    private static void tagAnimals(PenSnapshot pen) {
        for (AnimalEntity animal : pen.animals) {
            CompoundNBT data = animal.getPersistentData();
            data.putBoolean(OWNED_TAG, true);
            data.putString(PEN_TAG, pen.type.name().toLowerCase(Locale.ROOT));
        }
    }

    private static boolean hasCollectableOutput(PenSnapshot pen, TekVillageEconomy economy, long gameTime) {
        for (AnimalEntity animal : pen.animals) {
            if (animal instanceof SheepEntity && !((SheepEntity) animal).isSheared() && economy.canInsert(new ItemStack(Items.WHITE_WOOL))) {
                return true;
            }
            if (animal instanceof ChickenEntity
                    && gameTime - animal.getPersistentData().getLong(LAST_COLLECT_TICK_TAG) >= EGG_COOLDOWN
                    && economy.canInsert(new ItemStack(Items.EGG))) {
                return true;
            }
            if (animal instanceof CowEntity
                    && gameTime - animal.getPersistentData().getLong(LAST_COLLECT_TICK_TAG) >= MILK_COOLDOWN
                    && economy.countAvailableItem(Items.BUCKET, gameTime) > 0
                    && economy.canInsert(new ItemStack(Items.MILK_BUCKET))) {
                return true;
            }
        }
        return false;
    }

    private static String collectOutput(PenSnapshot pen, TekVillageEconomy economy, long gameTime) {
        for (AnimalEntity animal : pen.animals) {
            if (animal instanceof SheepEntity && !((SheepEntity) animal).isSheared()) {
                ItemStack wool = TekItemMeta.markVillagerItem(new ItemStack(Items.WHITE_WOOL));
                if (economy.insert(wool)) {
                    ((SheepEntity) animal).setSheared(true);
                    return "collected_wool";
                }
                return "storage_full";
            }
            if (animal instanceof ChickenEntity) {
                if (gameTime - animal.getPersistentData().getLong(LAST_COLLECT_TICK_TAG) < EGG_COOLDOWN) {
                    continue;
                }
                if (economy.insert(TekItemMeta.markVillagerItem(new ItemStack(Items.EGG)))) {
                    animal.getPersistentData().putLong(LAST_COLLECT_TICK_TAG, gameTime);
                    return "collected_egg";
                }
                return "storage_full";
            }
            if (animal instanceof CowEntity) {
                if (gameTime - animal.getPersistentData().getLong(LAST_COLLECT_TICK_TAG) < MILK_COOLDOWN) {
                    continue;
                }
                java.util.Map<Item, Integer> inputs = new java.util.LinkedHashMap<>();
                inputs.put(Items.BUCKET, 1);
                if (economy.craftWithReservedInputs("rancher_milk", inputs, new ItemStack(Items.MILK_BUCKET), gameTime)) {
                    animal.getPersistentData().putLong(LAST_COLLECT_TICK_TAG, gameTime);
                    return "collected_milk";
                }
            }
        }
        return "-";
    }

    private static boolean canBreed(PenSnapshot pen, TekVillageEconomy economy, long gameTime) {
        if (pen.animals.size() >= pen.capacity) {
            return false;
        }
        Item feed = feedForPen(pen);
        if (feed == null || economy.countAvailableItem(feed, gameTime) < 2) {
            return false;
        }
        int adults = 0;
        for (AnimalEntity animal : pen.animals) {
            if (!animal.isBaby()) {
                adults++;
            }
        }
        return adults >= 2;
    }

    private static String breedAnimals(PenSnapshot pen, TekVillageEconomy economy, long gameTime) {
        Item feed = feedForPen(pen);
        if (feed == null) {
            return "missing_feed";
        }
        java.util.Map<Item, Integer> inputs = new java.util.LinkedHashMap<>();
        inputs.put(feed, 2);
        if (!economy.craftWithReservedInputs("rancher_feed", inputs, ItemStack.EMPTY, gameTime)) {
            return "missing_feed";
        }
        int fed = 0;
        for (AnimalEntity animal : pen.animals) {
            if (animal.isBaby()) {
                continue;
            }
            animal.setInLove(null);
            fed++;
            if (fed >= 2) {
                break;
            }
        }
        return "bred_" + pen.type.name().toLowerCase(Locale.ROOT);
    }

    private static Item feedForPen(PenSnapshot pen) {
        AnimalEntity sample = pen.animals.isEmpty() ? null : pen.animals.get(0);
        if (pen.type == TekStructureType.SHEEP_PEN || sample instanceof SheepEntity || sample instanceof CowEntity || pen.type == TekStructureType.COW_PEN) {
            return Items.WHEAT;
        }
        if (pen.type == TekStructureType.PIG_PEN || sample instanceof PigEntity) {
            return Items.CARROT;
        }
        if (pen.type == TekStructureType.CHICKEN_COOP || sample instanceof ChickenEntity) {
            return Items.WHEAT_SEEDS;
        }
        return null;
    }

    private static List<ItemStack> butcherOutputs(AnimalEntity animal) {
        List<ItemStack> outputs = new ArrayList<>();
        if (animal instanceof CowEntity) {
            outputs.add(new ItemStack(Items.BEEF, 2));
            outputs.add(new ItemStack(Items.LEATHER));
        } else if (animal instanceof PigEntity) {
            outputs.add(new ItemStack(Items.PORKCHOP, 2));
        } else if (animal instanceof SheepEntity) {
            outputs.add(new ItemStack(Items.MUTTON, 2));
            outputs.add(new ItemStack(Items.WHITE_WOOL));
        } else if (animal instanceof ChickenEntity) {
            outputs.add(new ItemStack(Items.CHICKEN));
            outputs.add(new ItemStack(Items.FEATHER));
        }
        return outputs;
    }

    private static boolean canInsertAll(TekVillageEconomy economy, List<ItemStack> outputs) {
        for (ItemStack output : outputs) {
            if (!output.isEmpty() && !economy.canInsert(output)) {
                return false;
            }
        }
        return true;
    }

    private static String animalKey(AnimalEntity animal) {
        if (animal instanceof CowEntity) {
            return "cow";
        }
        if (animal instanceof PigEntity) {
            return "pig";
        }
        if (animal instanceof SheepEntity) {
            return "sheep";
        }
        if (animal instanceof ChickenEntity) {
            return "chicken";
        }
        return "animal";
    }

    private static String formatPos(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public static final class PenSnapshot {
        public final TekStructureType type;
        public final BlockPos workPos;
        public final int capacity;
        public final List<AnimalEntity> animals;
        public final boolean valid;

        private PenSnapshot(TekStructureType type, BlockPos workPos, int capacity, List<AnimalEntity> animals) {
            this.type = type;
            this.workPos = workPos == null ? BlockPos.ZERO : workPos.immutable();
            this.capacity = capacity;
            this.animals = java.util.Collections.unmodifiableList(new ArrayList<>(animals));
            this.valid = true;
        }
    }

    public static final class ButcherTarget {
        public final PenSnapshot pen;
        public final AnimalEntity animal;
        public final List<ItemStack> outputs;

        private ButcherTarget(PenSnapshot pen, AnimalEntity animal, List<ItemStack> outputs) {
            this.pen = pen;
            this.animal = animal;
            this.outputs = java.util.Collections.unmodifiableList(new ArrayList<>(outputs));
        }

        public BlockPos getWorkPos() {
            return this.animal.blockPosition();
        }
    }
}
