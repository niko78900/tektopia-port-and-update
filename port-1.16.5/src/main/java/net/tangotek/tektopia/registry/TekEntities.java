package net.tangotek.tektopia.registry;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.entities.TekArchitectEntity;
import net.tangotek.tektopia.entities.TekBardEntity;
import net.tangotek.tektopia.entities.TekBlacksmithEntity;
import net.tangotek.tektopia.entities.TekButcherEntity;
import net.tangotek.tektopia.entities.TekCaptainAuraEntity;
import net.tangotek.tektopia.entities.TekChefEntity;
import net.tangotek.tektopia.entities.TekChildEntity;
import net.tangotek.tektopia.entities.TekClericEntity;
import net.tangotek.tektopia.entities.TekDeathCloudEntity;
import net.tangotek.tektopia.entities.TekDruidEntity;
import net.tangotek.tektopia.entities.TekEnchanterEntity;
import net.tangotek.tektopia.entities.TekFarmerEntity;
import net.tangotek.tektopia.entities.TekGuardEntity;
import net.tangotek.tektopia.entities.TekLumberjackEntity;
import net.tangotek.tektopia.entities.TekMerchantEntity;
import net.tangotek.tektopia.entities.TekMinerEntity;
import net.tangotek.tektopia.entities.TekNecromancerEntity;
import net.tangotek.tektopia.entities.TekNitwitEntity;
import net.tangotek.tektopia.entities.TekNomadEntity;
import net.tangotek.tektopia.entities.TekRancherEntity;
import net.tangotek.tektopia.entities.TekSpiritSkullEntity;
import net.tangotek.tektopia.entities.TekTeacherEntity;
import net.tangotek.tektopia.entities.TekTradesmanEntity;

public final class TekEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, TekTopiaPort.MODID);
    public static final RegistryObject<EntityType<TekGuardEntity>> TEK_GUARD =
            ENTITY_TYPES.register("tek_guard",
                    () -> EntityType.Builder.of(TekGuardEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_guard"));
    public static final RegistryObject<EntityType<TekFarmerEntity>> TEK_FARMER =
            ENTITY_TYPES.register("tek_farmer",
                    () -> EntityType.Builder.of(TekFarmerEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_farmer"));
    public static final RegistryObject<EntityType<TekBlacksmithEntity>> TEK_BLACKSMITH =
            ENTITY_TYPES.register("tek_blacksmith",
                    () -> EntityType.Builder.of(TekBlacksmithEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_blacksmith"));
    public static final RegistryObject<EntityType<TekMinerEntity>> TEK_MINER =
            ENTITY_TYPES.register("tek_miner",
                    () -> EntityType.Builder.of(TekMinerEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_miner"));
    public static final RegistryObject<EntityType<TekLumberjackEntity>> TEK_LUMBERJACK =
            ENTITY_TYPES.register("tek_lumberjack",
                    () -> EntityType.Builder.of(TekLumberjackEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_lumberjack"));
    public static final RegistryObject<EntityType<TekChefEntity>> TEK_CHEF =
            ENTITY_TYPES.register("tek_chef",
                    () -> EntityType.Builder.of(TekChefEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_chef"));
    public static final RegistryObject<EntityType<TekRancherEntity>> TEK_RANCHER =
            ENTITY_TYPES.register("tek_rancher",
                    () -> EntityType.Builder.of(TekRancherEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_rancher"));
    public static final RegistryObject<EntityType<TekButcherEntity>> TEK_BUTCHER =
            ENTITY_TYPES.register("tek_butcher",
                    () -> EntityType.Builder.of(TekButcherEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_butcher"));
    public static final RegistryObject<EntityType<TekMerchantEntity>> TEK_MERCHANT =
            ENTITY_TYPES.register("tek_merchant",
                    () -> EntityType.Builder.of(TekMerchantEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_merchant"));
    public static final RegistryObject<EntityType<TekNomadEntity>> TEK_NOMAD =
            ENTITY_TYPES.register("tek_nomad",
                    () -> EntityType.Builder.of(TekNomadEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_nomad"));
    public static final RegistryObject<EntityType<TekNecromancerEntity>> TEK_NECROMANCER =
            ENTITY_TYPES.register("tek_necromancer",
                    () -> EntityType.Builder.of(TekNecromancerEntity::new, EntityClassification.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_necromancer"));
    public static final RegistryObject<EntityType<TekArchitectEntity>> TEK_ARCHITECT =
            ENTITY_TYPES.register("tek_architect",
                    () -> EntityType.Builder.of(TekArchitectEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_architect"));
    public static final RegistryObject<EntityType<TekTradesmanEntity>> TEK_TRADESMAN =
            ENTITY_TYPES.register("tek_tradesman",
                    () -> EntityType.Builder.of(TekTradesmanEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_tradesman"));
    public static final RegistryObject<EntityType<TekChildEntity>> TEK_CHILD =
            ENTITY_TYPES.register("tek_child",
                    () -> EntityType.Builder.of(TekChildEntity::new, EntityClassification.CREATURE)
                            .sized(0.45F, 1.15F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_child"));
    public static final RegistryObject<EntityType<TekTeacherEntity>> TEK_TEACHER =
            ENTITY_TYPES.register("tek_teacher",
                    () -> EntityType.Builder.of(TekTeacherEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_teacher"));
    public static final RegistryObject<EntityType<TekBardEntity>> TEK_BARD =
            ENTITY_TYPES.register("tek_bard",
                    () -> EntityType.Builder.of(TekBardEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_bard"));
    public static final RegistryObject<EntityType<TekClericEntity>> TEK_CLERIC =
            ENTITY_TYPES.register("tek_cleric",
                    () -> EntityType.Builder.of(TekClericEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_cleric"));
    public static final RegistryObject<EntityType<TekDruidEntity>> TEK_DRUID =
            ENTITY_TYPES.register("tek_druid",
                    () -> EntityType.Builder.of(TekDruidEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_druid"));
    public static final RegistryObject<EntityType<TekEnchanterEntity>> TEK_ENCHANTER =
            ENTITY_TYPES.register("tek_enchanter",
                    () -> EntityType.Builder.of(TekEnchanterEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_enchanter"));
    public static final RegistryObject<EntityType<TekNitwitEntity>> TEK_NITWIT =
            ENTITY_TYPES.register("tek_nitwit",
                    () -> EntityType.Builder.of(TekNitwitEntity::new, EntityClassification.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build(TekTopiaPort.MODID + ":tek_nitwit"));
    public static final RegistryObject<EntityType<TekSpiritSkullEntity>> TEK_SPIRIT_SKULL =
            ENTITY_TYPES.register("tek_spirit_skull",
                    () -> EntityType.Builder.of(TekSpiritSkullEntity::new, EntityClassification.MONSTER)
                            .sized(0.45F, 0.45F)
                            .clientTrackingRange(8)
                            .updateInterval(2)
                            .build(TekTopiaPort.MODID + ":tek_spirit_skull"));
    public static final RegistryObject<EntityType<TekDeathCloudEntity>> TEK_DEATH_CLOUD =
            ENTITY_TYPES.register("tek_death_cloud",
                    () -> EntityType.Builder.of(TekDeathCloudEntity::new, EntityClassification.MONSTER)
                            .sized(3.0F, 0.75F)
                            .clientTrackingRange(8)
                            .updateInterval(10)
                            .build(TekTopiaPort.MODID + ":tek_death_cloud"));
    public static final RegistryObject<EntityType<TekCaptainAuraEntity>> TEK_CAPTAIN_AURA =
            ENTITY_TYPES.register("tek_captain_aura",
                    () -> EntityType.Builder.of(TekCaptainAuraEntity::new, EntityClassification.MONSTER)
                            .sized(1.0F, 0.25F)
                            .clientTrackingRange(8)
                            .updateInterval(10)
                            .build(TekTopiaPort.MODID + ":tek_captain_aura"));

    private TekEntities() {
    }

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
    }
}
