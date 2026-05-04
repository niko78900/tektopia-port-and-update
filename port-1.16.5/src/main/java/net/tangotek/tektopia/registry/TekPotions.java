package net.tangotek.tektopia.registry;

import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tangotek.tektopia.TekTopiaPort;

public final class TekPotions {
    public static final DeferredRegister<Effect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.POTIONS, TekTopiaPort.MODID);
    public static final DeferredRegister<Potion> POTION_TYPES =
            DeferredRegister.create(ForgeRegistries.POTION_TYPES, TekTopiaPort.MODID);

    private TekPotions() {
    }

    public static void register(IEventBus modBus) {
        EFFECTS.register(modBus);
        POTION_TYPES.register(modBus);
    }
}
