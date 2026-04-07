package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class TekGuardEntity extends TekVillagerEntity {
    public static final String FILTER_EQUIP_LEATHER_ARMOR = "equip_leather_armor";
    public static final String FILTER_EQUIP_IRON_ARMOR = "equip_iron_armor";
    public static final String FILTER_EQUIP_GOLD_ARMOR = "equip_gold_armor";
    public static final String FILTER_EQUIP_DIAMOND_ARMOR = "equip_diamond_armor";
    public static final String FILTER_EQUIP_ENCHANTED_ARMOR = "equip_enchanted_armor";
    public static final String FILTER_EQUIP_IRON_SWORD = "equip_iron_sword";
    public static final String FILTER_EQUIP_DIAMOND_SWORD = "equip_diamond_sword";
    public static final String FILTER_EQUIP_ENCHANTED_SWORD = "equip_enchanted_sword";

    public TekGuardEntity(EntityType<? extends TekGuardEntity> type, World world) {
        super(type, world);
        this.registerDefaultGuardFilters();
        this.setCanPickUpLoot(true);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.15D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 12.0F));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MonsterEntity.class, 10, true, false, this::isGuardHostile));
    }

    public boolean isGoldArmorEnabled() {
        return this.isAIFilterEnabled(FILTER_EQUIP_GOLD_ARMOR);
    }

    public void setGoldArmorEnabled(boolean value) {
        this.setAIFilter(FILTER_EQUIP_GOLD_ARMOR, value);
    }

    public boolean setGuardFilter(String filterName, boolean enabled) {
        return this.setAIFilter(filterName, enabled);
    }

    private boolean isGuardHostile(LivingEntity target) {
        return this.hostileSelector().test(target);
    }

    @Override
    public boolean canTakeItem(ItemStack stack) {
        if (!super.canTakeItem(stack) || stack.isEmpty()) {
            return false;
        }
        EquipmentSlotType slot = MobEntity.getEquipmentSlotForItem(stack);
        if (slot.getType() == EquipmentSlotType.Group.ARMOR) {
            int candidateScore = scoreArmor(stack, slot);
            if (candidateScore < 0) {
                return false;
            }
            int equippedScore = scoreArmor(this.getItemBySlot(slot), slot);
            return candidateScore > equippedScore;
        }
        if (slot == EquipmentSlotType.MAINHAND) {
            int candidateScore = scoreWeapon(stack);
            if (candidateScore < 0) {
                return false;
            }
            int equippedScore = scoreWeapon(this.getMainHandItem());
            return candidateScore > equippedScore;
        }
        return false;
    }

    public int scoreWeapon(ItemStack stack) {
        if (stack.isEmpty()) {
            return -1;
        }
        Item item = stack.getItem();
        if (!(item instanceof SwordItem)) {
            return -1;
        }
        SwordItem sword = (SwordItem) item;
        IItemTier tier = sword.getTier();
        if (stack.isEnchanted() && !this.isAIFilterEnabled(FILTER_EQUIP_ENCHANTED_SWORD)) {
            return -1;
        }
        if (tier == ItemTier.IRON && !this.isAIFilterEnabled(FILTER_EQUIP_IRON_SWORD)) {
            return -1;
        }
        if (tier == ItemTier.DIAMOND && !this.isAIFilterEnabled(FILTER_EQUIP_DIAMOND_SWORD)) {
            return -1;
        }
        int score = tier.getLevel() * 100;
        score += (int) (tier.getAttackDamageBonus() * 10.0F);
        if (stack.isEnchanted()) {
            score += 25;
        }
        return score;
    }

    public int scoreArmor(ItemStack stack, EquipmentSlotType slot) {
        if (stack.isEmpty()) {
            return -1;
        }
        Item item = stack.getItem();
        if (!(item instanceof ArmorItem)) {
            return -1;
        }
        ArmorItem armor = (ArmorItem) item;
        if (armor.getSlot() != slot) {
            return -1;
        }
        if (stack.isEnchanted() && !this.isAIFilterEnabled(FILTER_EQUIP_ENCHANTED_ARMOR)) {
            return -1;
        }
        IArmorMaterial material = armor.getMaterial();
        if (material == ArmorMaterial.DIAMOND && !this.isAIFilterEnabled(FILTER_EQUIP_DIAMOND_ARMOR)) {
            return -1;
        }
        if (material == ArmorMaterial.IRON && !this.isAIFilterEnabled(FILTER_EQUIP_IRON_ARMOR)) {
            return -1;
        }
        if (material == ArmorMaterial.GOLD && !this.isAIFilterEnabled(FILTER_EQUIP_GOLD_ARMOR)) {
            return -1;
        }
        if (material == ArmorMaterial.LEATHER && !this.isAIFilterEnabled(FILTER_EQUIP_LEATHER_ARMOR)) {
            return -1;
        }
        int score = material.getDefenseForSlot(slot) * 100;
        score += material.getDurabilityForSlot(slot) / 10;
        if (stack.isEnchanted()) {
            score += 25;
        }
        return score;
    }

    private void registerDefaultGuardFilters() {
        this.registerAIFilter(FILTER_EQUIP_LEATHER_ARMOR, true);
        this.registerAIFilter(FILTER_EQUIP_IRON_ARMOR, true);
        this.registerAIFilter(FILTER_EQUIP_GOLD_ARMOR, true);
        this.registerAIFilter(FILTER_EQUIP_DIAMOND_ARMOR, true);
        this.registerAIFilter(FILTER_EQUIP_ENCHANTED_ARMOR, true);
        this.registerAIFilter(FILTER_EQUIP_IRON_SWORD, true);
        this.registerAIFilter(FILTER_EQUIP_DIAMOND_SWORD, true);
        this.registerAIFilter(FILTER_EQUIP_ENCHANTED_SWORD, true);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("equipGoldArmorLegacy", this.isGoldArmorEnabled());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("equipGoldArmorLegacy")) {
            this.setGoldArmorEnabled(nbt.getBoolean("equipGoldArmorLegacy"));
        }
    }
}
