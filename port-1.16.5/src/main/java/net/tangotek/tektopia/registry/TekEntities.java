package net.tangotek.tektopia.registry;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.entities.TekGuardEntity;

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

    private TekEntities() {
    }

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
    }
}
