package net.tangotek.tektopia.registry;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tangotek.tektopia.TekTopiaPort;

public final class TekParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, TekTopiaPort.MODID);
    public static final RegistryObject<BasicParticleType> THOUGHT = register("thought");
    public static final RegistryObject<BasicParticleType> ITEM_THOUGHT = register("item_thought");
    public static final RegistryObject<BasicParticleType> DARKNESS = register("darkness");
    public static final RegistryObject<BasicParticleType> SKULL = register("skull");

    private TekParticles() {
    }

    public static void register(IEventBus modBus) {
        PARTICLES.register(modBus);
    }

    private static RegistryObject<BasicParticleType> register(String name) {
        return PARTICLES.register(name, () -> new BasicParticleType(false));
    }
}
