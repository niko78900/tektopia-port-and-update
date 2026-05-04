package net.tangotek.tektopia.registry;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tangotek.tektopia.TekTopiaPort;

public final class TekSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TekTopiaPort.MODID);

    private TekSounds() {
    }

    public static void register(IEventBus modBus) {
        SOUNDS.register(modBus);
    }
}
