/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.IMerchant
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityChicken
 *  net.minecraft.entity.passive.EntityCow
 *  net.minecraft.entity.passive.EntityPig
 *  net.minecraft.entity.passive.EntitySheep
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.nbt.NBTTagString
 *  net.minecraft.pathfinding.PathNavigate
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.village.MerchantRecipe
 *  net.minecraft.village.MerchantRecipeList
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.entities;

import com.google.common.base.Predicate;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.EntityTagType;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIGenericMove;
import net.tangotek.tektopia.entities.ai.EntityAIReadBook;
import net.tangotek.tektopia.entities.ai.EntityAIReturnLostAnimal;
import net.tangotek.tektopia.entities.ai.EntityAIVisitMerchantStall;
import net.tangotek.tektopia.entities.ai.EntityAIWanderStructure;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityMerchant
extends EntityVillagerTek
implements IMerchant {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityMerchant.class);
    private BlockPos firstCheck;
    private int stallLevel = 0;
    @Nullable
    private EntityPlayer buyingPlayer;
    @Nullable
    private MerchantRecipeList merchantList;
    private List<EntityAnimal> animalDeliveries = new ArrayList<EntityAnimal>();

    public EntityMerchant(World worldIn) {
        super(worldIn, null, VillagerRole.VENDOR.value | VillagerRole.VISITOR.value);
        this.sleepOffset = 0;
    }

    @Override
    protected void setupServerJobs() {
        super.setupServerJobs();
        this.addJob(new TickJob(50, 50, true, () -> this.releashAnimals()));
        this.addJob(new TickJob(100, 0, false, () -> this.prepStuck()));
        this.addJob(new TickJob(400, 0, false, () -> this.checkStuck()));
        this.addJob(new TickJob(50, 0, true, () -> {
            if (this.isSleepingTime()) {
                this.func_70106_y();
            }
        }));
        this.addJob(new TickJob(300, 100, true, () -> {
            if (!this.hasVillage() || !this.getVillage().isValid()) {
                this.debugOut("Killing Self.  No village");
                this.func_70106_y();
            }
        }));
    }

    private void prepStuck() {
        this.firstCheck = this.func_190671_u_();
    }

    private void checkStuck() {
        if (this.firstCheck.func_177951_i((Vec3i)this.func_190671_u_()) < 20.0) {
            this.debugOut("Merchant failed to find a way to the village.");
            this.func_70106_y();
        }
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.addTask(50, new EntityAIGenericMove(this, p -> p.hasVillage() && Village.isNightTime(this.field_70170_p), v -> this.village.getEdgeNode(), EntityVillagerTek.MovementMode.WALK, null, () -> {
            this.debugOut("Killing Self.  Left the village");
            this.func_70106_y();
        }));
        this.addTask(50, new EntityAIVisitMerchantStall(this, p -> this.hasVillage(), 3, 60));
        this.addTask(50, new EntityAIGenericMove(this, p -> !Village.isNightTime(this.field_70170_p) && p.hasVillage() && !this.isTrading(), v -> this.animalDeliveries.isEmpty() ? this.village.getLastVillagerPos() : this.getDeliveryPos(), EntityVillagerTek.MovementMode.WALK, null, () -> this.animalDeliveries.clear()));
    }

    @Override
    protected void initEntityAIBase() {
    }

    @Override
    public boolean canNavigate() {
        if (this.isTrading()) {
            return false;
        }
        return super.canNavigate();
    }

    @Override
    protected PathNavigate func_175447_b(World worldIn) {
        PathNavigate nav = super.func_175447_b(worldIn);
        return nav;
    }

    @Override
    public float func_70689_ay() {
        return super.func_70689_ay() * 0.9f;
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
        if (!this.field_70170_p.field_72995_K) {
            ListIterator<EntityAnimal> itr = this.animalDeliveries.listIterator();
            while (itr.hasNext()) {
                Object animal = itr.next();
                if (!animal.func_70089_S()) {
                    itr.remove();
                    if ((animal = animal instanceof EntityCow ? new EntityCow(this.field_70170_p) : (animal instanceof EntityChicken ? new EntityChicken(this.field_70170_p) : (animal instanceof EntitySheep ? new EntitySheep(this.field_70170_p) : (animal instanceof EntityPig ? new EntityPig(this.field_70170_p) : null)))) == null) continue;
                    itr.add((EntityAnimal)animal);
                    this.spawnAnimal((EntityAnimal)animal);
                    continue;
                }
                if (animal.func_110166_bE() == null) {
                    this.teleportAnimal((EntityAnimal)animal);
                    animal.func_110162_b((Entity)this, true);
                    continue;
                }
                if (animal.func_110166_bE() != this) {
                    itr.remove();
                    continue;
                }
                if (!(animal.func_70068_e((Entity)this) > 40.0)) continue;
                this.teleportAnimal((EntityAnimal)animal);
            }
        }
    }

    @Override
    protected void addTask(int priority, EntityAIBase task) {
        if (task instanceof EntityAIWanderStructure && priority <= 100) {
            return;
        }
        if (task instanceof EntityAIReadBook) {
            return;
        }
        super.addTask(priority, task);
    }

    private void releashAnimals() {
        for (EntityAnimal animal : this.animalDeliveries) {
            if (!animal.func_70089_S()) continue;
            animal.func_110162_b(null, true);
            animal.func_110162_b((Entity)this, true);
        }
    }

    @Override
    public boolean isMale() {
        return true;
    }

    private void teleportAnimal(EntityAnimal animal) {
        animal.func_70634_a(this.getX(), this.getY(), this.getZ());
    }

    public void addAnimalDelivery(EntityAnimal animal) {
        this.animalDeliveries.add(animal);
        this.spawnAnimal(animal);
    }

    private BlockPos getDeliveryPos() {
        VillageStructure struct;
        if (this.animalDeliveries.size() > 0 && (struct = EntityAIReturnLostAnimal.getDestinationStructure(this.getVillage(), this.animalDeliveries.get(0), this.func_180425_c())) != null) {
            return struct.getDoorOutside();
        }
        return this.getVillage().getOrigin();
    }

    private void spawnAnimal(EntityAnimal animal) {
        animal.func_70012_b(this.getX() + 0.5, this.getY(), this.getZ() + 0.5, 0.0f, 0.0f);
        animal.func_180482_a(this.field_70170_p.func_175649_E(this.func_190671_u_()), (IEntityLivingData)null);
        this.field_70170_p.func_72838_d((Entity)animal);
        ModEntities.makeTaggedEntity((Entity)animal, EntityTagType.VILLAGER);
    }

    @Override
    public void addVillagerPosition() {
    }

    public void setStall(int level) {
        this.stallLevel = level;
    }

    public void func_70932_a_(@Nullable EntityPlayer player) {
        this.buyingPlayer = player;
        this.func_70661_as().func_75499_g();
    }

    @Nullable
    public EntityPlayer func_70931_l_() {
        return this.buyingPlayer;
    }

    public boolean isTrading() {
        return this.buyingPlayer != null;
    }

    @Override
    protected void bedCheck() {
    }

    @Nullable
    public MerchantRecipeList func_70934_b(EntityPlayer player) {
        if (this.merchantList == null) {
            this.populateBuyingList(this.stallLevel);
        }
        return this.merchantList;
    }

    @Override
    protected boolean getCanUseDoors() {
        return true;
    }

    @Override
    public boolean func_184645_a(EntityPlayer player, EnumHand hand) {
        if (!(!this.func_70089_S() || this.isTrading() || this.func_70631_g_() || player.func_70093_af() || this.field_70170_p.field_72995_K)) {
            if (this.merchantList == null) {
                this.populateBuyingList(this.stallLevel);
            }
            if (this.merchantList != null && !this.merchantList.isEmpty()) {
                this.func_70932_a_(player);
                player.func_180472_a((IMerchant)this);
                this.func_70661_as().func_75499_g();
            }
        }
        return true;
    }

    private void populateBuyingList(int stallLevel) {
        IVillageData vd;
        if (this.merchantList == null && this.hasVillage() && (vd = this.getVillage().getTownData()) != null) {
            vd.initEconomy();
            this.merchantList = vd.getEconomy().getMerchantList(this.getVillage(), stallLevel);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void func_70930_a(@Nullable MerchantRecipeList recipeList) {
    }

    public World func_190670_t_() {
        return this.field_70170_p;
    }

    public void func_70933_a(MerchantRecipe recipe) {
        recipe.func_77399_f();
        this.field_70757_a = -this.func_70627_aG();
        this.func_184185_a(SoundEvents.field_187915_go, this.func_70599_aP(), this.func_70647_i());
        int i = 3 + this.field_70146_Z.nextInt(4);
        if (recipe.func_180321_e() == 1 || this.field_70146_Z.nextInt(5) == 0) {
            i += 5;
        }
        if (recipe.func_180322_j()) {
            this.field_70170_p.func_72838_d((Entity)new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u + 0.5, this.field_70161_v, i));
        }
        if (this.hasVillage()) {
            this.getVillage().purchaseFromMerchant(recipe, this, this.func_70931_l_());
        }
    }

    public void func_110297_a_(ItemStack stack) {
        if (!this.field_70170_p.field_72995_K && this.field_70757_a > -this.func_70627_aG() + 20) {
            this.field_70757_a = -this.func_70627_aG();
            this.func_184185_a(stack.func_190926_b() ? SoundEvents.field_187913_gm : SoundEvents.field_187915_go, this.func_70599_aP(), this.func_70647_i());
        }
    }

    public ITextComponent func_145748_c_() {
        TextComponentTranslation itextcomponent = new TextComponentTranslation("entity.villager.merchant", new Object[0]);
        itextcomponent.func_150256_b().func_150209_a(this.func_174823_aP());
        itextcomponent.func_150256_b().func_179989_a(this.func_189512_bd());
        return itextcomponent;
    }

    @Override
    public Predicate<Entity> isHostile() {
        return e -> false;
    }

    @Override
    public boolean isFleeFrom(Entity e) {
        return false;
    }

    public BlockPos func_190671_u_() {
        return new BlockPos((Entity)this);
    }

    @Override
    public void func_70014_b(NBTTagCompound compound) {
        super.func_70014_b(compound);
        NBTTagList nbttaglist = new NBTTagList();
        for (EntityAnimal animal : this.animalDeliveries) {
            nbttaglist.func_74742_a((NBTBase)new NBTTagString(animal.func_110124_au().toString()));
        }
        compound.func_74782_a("animals", (NBTBase)nbttaglist);
        if (this.merchantList != null) {
            compound.func_74782_a("Offers", (NBTBase)this.merchantList.func_77202_a());
        }
    }

    @Override
    public void func_70037_a(NBTTagCompound compound) {
        super.func_70037_a(compound);
        NBTTagList nbttaglist = compound.func_150295_c("animals", 10);
        block0: for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            UUID uuid = UUID.fromString(nbttaglist.func_150307_f(i));
            for (EntityAnimal animal : this.field_70170_p.func_72872_a(EntityAnimal.class, this.func_174813_aQ().func_186662_g(12.0))) {
                if (!animal.func_110124_au().equals(uuid)) continue;
                this.animalDeliveries.add(animal);
                continue block0;
            }
        }
        if (compound.func_150297_b("Offers", 10)) {
            NBTTagCompound nbttagcompound = compound.func_74775_l("Offers");
            this.merchantList = new MerchantRecipeList(nbttagcompound);
        }
    }

    static {
        EntityVillagerTek.setupAnimations(animHandler, "merchant_m");
    }
}

