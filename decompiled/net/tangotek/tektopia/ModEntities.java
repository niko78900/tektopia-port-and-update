/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.JsonToNBT
 *  net.minecraft.nbt.NBTException
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.text.translation.I18n
 *  net.minecraft.world.World
 *  net.minecraftforge.event.entity.ProjectileImpactEvent
 *  net.minecraftforge.event.entity.living.BabyEntitySpawnEvent
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.AttackEntityEvent
 *  net.minecraftforge.fml.client.registry.IRenderFactory
 *  net.minecraftforge.fml.client.registry.RenderingRegistry
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$PlayerTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$WorldTickEvent
 *  net.minecraftforge.fml.common.network.NetworkRegistry$TargetPoint
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.registry.EntityRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.EntityTagType;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.client.ParticleItemThought;
import net.tangotek.tektopia.client.RenderArchitect;
import net.tangotek.tektopia.client.RenderBard;
import net.tangotek.tektopia.client.RenderBlacksmith;
import net.tangotek.tektopia.client.RenderButcher;
import net.tangotek.tektopia.client.RenderChef;
import net.tangotek.tektopia.client.RenderChild;
import net.tangotek.tektopia.client.RenderCleric;
import net.tangotek.tektopia.client.RenderDruid;
import net.tangotek.tektopia.client.RenderEnchanter;
import net.tangotek.tektopia.client.RenderFarmer;
import net.tangotek.tektopia.client.RenderGuard;
import net.tangotek.tektopia.client.RenderLumberjack;
import net.tangotek.tektopia.client.RenderMerchant;
import net.tangotek.tektopia.client.RenderMiner;
import net.tangotek.tektopia.client.RenderNecromancer;
import net.tangotek.tektopia.client.RenderNitwit;
import net.tangotek.tektopia.client.RenderNomad;
import net.tangotek.tektopia.client.RenderRancher;
import net.tangotek.tektopia.client.RenderSpiritSkull;
import net.tangotek.tektopia.client.RenderTeacher;
import net.tangotek.tektopia.client.RenderTradesman;
import net.tangotek.tektopia.entities.EntityArchitect;
import net.tangotek.tektopia.entities.EntityBard;
import net.tangotek.tektopia.entities.EntityBlacksmith;
import net.tangotek.tektopia.entities.EntityButcher;
import net.tangotek.tektopia.entities.EntityCaptainAura;
import net.tangotek.tektopia.entities.EntityChef;
import net.tangotek.tektopia.entities.EntityChild;
import net.tangotek.tektopia.entities.EntityCleric;
import net.tangotek.tektopia.entities.EntityDeathCloud;
import net.tangotek.tektopia.entities.EntityDruid;
import net.tangotek.tektopia.entities.EntityEnchanter;
import net.tangotek.tektopia.entities.EntityFarmer;
import net.tangotek.tektopia.entities.EntityGuard;
import net.tangotek.tektopia.entities.EntityLumberjack;
import net.tangotek.tektopia.entities.EntityMerchant;
import net.tangotek.tektopia.entities.EntityMiner;
import net.tangotek.tektopia.entities.EntityNecromancer;
import net.tangotek.tektopia.entities.EntityNitwit;
import net.tangotek.tektopia.entities.EntityNomad;
import net.tangotek.tektopia.entities.EntityRancher;
import net.tangotek.tektopia.entities.EntitySpiritSkull;
import net.tangotek.tektopia.entities.EntityTeacher;
import net.tangotek.tektopia.entities.EntityTradesman;
import net.tangotek.tektopia.network.PacketVillagerItemThought;

public class ModEntities {
    public static final int MAX_ANIMAL_HUNGER = 100;
    private static Map<Entity, Item> entityItemThoughts = new HashMap<Entity, Item>();

    public static void init() {
        int id = 1;
        System.out.println("Registering Entities");
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "lumberjack"), EntityLumberjack.class, (String)"Lumberjack", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)0x996600, (int)65280);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "farmer"), EntityFarmer.class, (String)"Farmer", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)0x446600, (int)65348);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "miner"), EntityMiner.class, (String)"Miner", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)0x441100, (int)0x77FF44);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "blacksmith"), EntityBlacksmith.class, (String)"Blacksmith", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)0x221122, (int)0xBBFFAA);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "guard"), EntityGuard.class, (String)"Guard", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)0x22BB22, (int)0xBB33CC);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "architect"), EntityArchitect.class, (String)"Architect", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)0x22BBBB, (int)0x113322);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "tradesman"), EntityTradesman.class, (String)"Tradesman", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)7813339, (int)8631074);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "merchant"), EntityMerchant.class, (String)"Merchant", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)0x22AA22, (int)0x7733FF);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "nomad"), EntityNomad.class, (String)"Nomad", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)420386, (int)492095);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "rancher"), EntityRancher.class, (String)"Rancher", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)0x2266BB, (int)1168210);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "butcher"), EntityButcher.class, (String)"Butcher", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)0xAA66BB, (int)1168210);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "enchanter"), EntityEnchanter.class, (String)"Enchanter", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)0x336677, (int)1157714);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "child"), EntityChild.class, (String)"Child", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)0x336611, (int)14527058);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "chef"), EntityChef.class, (String)"Chef", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)3370993, (int)4042514);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "teacher"), EntityTeacher.class, (String)"Teacher", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)2256881, (int)2273042);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "nitwit"), EntityNitwit.class, (String)"Nitwit", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)2261713, (int)2698914);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "cleric"), EntityCleric.class, (String)"Cleric", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)2261713, (int)2698914);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "druid"), EntityDruid.class, (String)"Druid", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)2261713, (int)2698914);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "bard"), EntityBard.class, (String)"Bard", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)0x22FFF1, (int)0x2CCCA2);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "necromancer"), EntityNecromancer.class, (String)"Necromancer", (int)id++, (Object)TekVillager.instance, (int)128, (int)1, (boolean)true, (int)0x222222, (int)0xBB0000);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "spirit_skull"), EntitySpiritSkull.class, (String)"Spirit Skull", (int)id++, (Object)TekVillager.instance, (int)64, (int)1, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "death_cloud"), EntityDeathCloud.class, (String)"Death Cloud", (int)id++, (Object)TekVillager.instance, (int)64, (int)1, (boolean)true);
        EntityRegistry.registerModEntity((ResourceLocation)new ResourceLocation("tektopia", "captain_aura"), EntityCaptainAura.class, (String)"Captain Aura", (int)id++, (Object)TekVillager.instance, (int)64, (int)1, (boolean)true);
    }

    @SideOnly(value=Side.CLIENT)
    public static void initModels() {
        RenderingRegistry.registerEntityRenderingHandler(EntityLumberjack.class, (IRenderFactory)RenderLumberjack.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityFarmer.class, (IRenderFactory)RenderFarmer.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityMiner.class, (IRenderFactory)RenderMiner.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityBlacksmith.class, (IRenderFactory)RenderBlacksmith.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityGuard.class, (IRenderFactory)RenderGuard.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityArchitect.class, (IRenderFactory)RenderArchitect.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityTradesman.class, (IRenderFactory)RenderTradesman.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityRancher.class, (IRenderFactory)RenderRancher.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityButcher.class, (IRenderFactory)RenderButcher.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityMerchant.class, (IRenderFactory)RenderMerchant.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityEnchanter.class, (IRenderFactory)RenderEnchanter.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityChild.class, (IRenderFactory)RenderChild.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityChef.class, (IRenderFactory)RenderChef.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityTeacher.class, (IRenderFactory)RenderTeacher.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityNitwit.class, (IRenderFactory)RenderNitwit.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityNomad.class, (IRenderFactory)RenderNomad.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityCleric.class, (IRenderFactory)RenderCleric.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityDruid.class, (IRenderFactory)RenderDruid.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityBard.class, (IRenderFactory)RenderBard.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityNecromancer.class, (IRenderFactory)RenderNecromancer.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntitySpiritSkull.class, (IRenderFactory)RenderSpiritSkull.FACTORY);
    }

    public static Entity makeTaggedEntity(Entity ent, EntityTagType tagType) {
        return ModEntities.makeTaggedEntity(ent, tagType, "");
    }

    private static Entity makeTaggedEntity(Entity ent, EntityTagType tagType, String displayName) {
        ent.getEntityData().func_74757_a(tagType.tag, true);
        if (!displayName.isEmpty()) {
            ent.func_96094_a(displayName);
        }
        return ent;
    }

    public static boolean isTaggedEntity(Entity ent, EntityTagType tagType) {
        return ent.getEntityData().func_74767_n(tagType.tag);
    }

    public static void sendItemThought(Entity e, Item i) {
        entityItemThoughts.put(e, i);
    }

    @SideOnly(value=Side.CLIENT)
    public static void handleItemThought(World world, Entity ent, Item item) {
        ParticleItemThought itemThought = new ParticleItemThought(world, ent, item);
        Minecraft.func_71410_x().field_71452_i.func_78873_a((Particle)itemThought);
    }

    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        ModEntities.processItemThoughts();
    }

    public static void processItemThoughts() {
        Iterator<Map.Entry<Entity, Item>> itr = entityItemThoughts.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<Entity, Item> eit = itr.next();
            Entity ent = eit.getKey();
            if (!ent.func_70089_S() || ent.field_70173_aa % 80 != 0) continue;
            PacketVillagerItemThought msg = new PacketVillagerItemThought(ent, eit.getValue());
            TekVillager.NETWORK.sendToAllAround((IMessage)msg, new NetworkRegistry.TargetPoint(ent.field_70170_p.field_73011_w.getDimension(), ent.field_70165_t, ent.field_70163_u + 0.5, ent.field_70161_v, 64.0));
            itr.remove();
        }
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        NBTTagCompound nbt = event.player.getEntityData();
        boolean loggedInBefore = nbt.func_74767_n("tektopia_loggedInBefore");
        if (!loggedInBefore) {
            ModEntities.givePlayerStarterBook(event.player);
            nbt.func_74757_a("tektopia_loggedInBefore", true);
        }
    }

    private static void givePlayerStarterBook(EntityPlayer player) {
        ItemStack bookItem = new ItemStack(Items.field_151164_bB);
        String json = I18n.func_74838_a((String)"starter_book.nbt");
        try {
            bookItem.func_77982_d(JsonToNBT.func_180713_a((String)json));
        }
        catch (NBTException e) {
            e.printStackTrace();
        }
        player.func_191521_c(bookItem);
    }

    public static void onBabySpawnEvent(BabyEntitySpawnEvent event) {
        if (event.getCausedByPlayer() == null && event.getChild() instanceof EntityAnimal) {
            ModEntities.makeTaggedEntity((Entity)event.getChild(), EntityTagType.VILLAGER);
        }
    }

    public static void onLivingHurtEvent(LivingHurtEvent event) {
        if (ModEntities.isTaggedEntity(event.getEntity(), EntityTagType.VILLAGER) && event.getSource() == DamageSource.field_76368_d) {
            event.setCanceled(true);
        }
    }

    public static void onLivingDropsEvent(LivingDropsEvent event) {
        if (event.getEntityLiving() instanceof EntityAnimal) {
            EntityButcher butcher;
            if (event.getSource() == DamageSource.field_76366_f) {
                event.setCanceled(true);
            } else if (event.getSource().func_76346_g() instanceof EntityButcher && (butcher = (EntityButcher)event.getSource().func_76346_g()) != null && ModEntities.isTaggedEntity((Entity)event.getEntityLiving(), EntityTagType.VILLAGER)) {
                event.getDrops().forEach(d -> ModItems.makeTaggedItem(d.func_92059_d(), ItemTagType.VILLAGER));
            }
        }
    }

    public static int getAnimalHunger(EntityAnimal animal) {
        int hunger = animal.getEntityData().func_74762_e("animal_hunger");
        if (hunger <= 0) {
            hunger = 100;
            ModEntities.setAnimalHunger(animal, hunger);
        }
        return hunger;
    }

    public static int modifyAnimalHunger(EntityAnimal animal, int delta) {
        int hunger = ModEntities.getAnimalHunger(animal);
        hunger = Math.max(Math.min(hunger + delta, 100), 1);
        ModEntities.setAnimalHunger(animal, hunger);
        return hunger;
    }

    public static boolean isAnimalStarving(EntityAnimal animal) {
        return ModEntities.getAnimalHunger(animal) == 1;
    }

    public static boolean isAnimalFull(EntityAnimal animal) {
        return ModEntities.getAnimalHunger(animal) == 100;
    }

    public static boolean isAnimalHungry(EntityAnimal animal) {
        return (double)ModEntities.getAnimalHunger(animal) < 75.0;
    }

    private static void setAnimalHunger(EntityAnimal animal, int value) {
        animal.getEntityData().func_74768_a("animal_hunger", value);
    }

    public static boolean isEntityUsable(Entity thing, long tickCooldown, long worldTimeNow) {
        return ModEntities.getEntityUseTime(thing) + tickCooldown < worldTimeNow;
    }

    public static void useEntity(Entity thing, long worldTimeNow) {
        thing.getEntityData().func_74772_a("entity_use_time", worldTimeNow);
    }

    private static long getEntityUseTime(Entity thing) {
        return thing.getEntityData().func_74763_f("entity_use_time");
    }

    public static void onProjectileImpact(ProjectileImpactEvent event) {
    }

    public static void onAttackEntityEvent(AttackEntityEvent event) {
    }

    public static void onPlayerClientTick(TickEvent.PlayerTickEvent event) {
    }
}

