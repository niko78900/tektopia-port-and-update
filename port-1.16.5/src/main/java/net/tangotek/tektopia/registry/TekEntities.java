package net.tangotek.tektopia.registry;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.entities.TekBlacksmithEntity;
import net.tangotek.tektopia.entities.TekButcherEntity;
import net.tangotek.tektopia.entities.TekChefEntity;
import net.tangotek.tektopia.entities.TekFarmerEntity;
import net.tangotek.tektopia.entities.TekGuardEntity;
import net.tangotek.tektopia.entities.TekLumberjackEntity;
import net.tangotek.tektopia.entities.TekMerchantEntity;
import net.tangotek.tektopia.entities.TekMinerEntity;
import net.tangotek.tektopia.entities.TekNecromancerEntity;
import net.tangotek.tektopia.entities.TekNomadEntity;
import net.tangotek.tektopia.entities.TekRancherEntity;

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

    private TekEntities() {
    }

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
    }
}
