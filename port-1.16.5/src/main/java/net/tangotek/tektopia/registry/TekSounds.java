package net.tangotek.tektopia.registry;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tangotek.tektopia.TekTopiaPort;

public final class TekSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TekTopiaPort.MODID);
    public static final RegistryObject<SoundEvent> VILLAGER_SLEEP = register("villager_sleep");
    public static final RegistryObject<SoundEvent> VILLAGER_AFRAID = register("villager_afraid");
    public static final RegistryObject<SoundEvent> VILLAGER_GRUNT = register("villager_grunt");
    public static final RegistryObject<SoundEvent> VILLAGER_ENCHANT = register("villager_enchant");
    public static final RegistryObject<SoundEvent> VILLAGER_ENCHANT_APPLY = register("villager_enchant_apply");
    public static final RegistryObject<SoundEvent> VILLAGER_ANGRY = register("villager_angry");
    public static final RegistryObject<SoundEvent> VILLAGER_HAPPY = register("villager_happy");
    public static final RegistryObject<SoundEvent> VILLAGER_SOCIALIZE = register("villager_socialize");
    public static final RegistryObject<SoundEvent> VILLAGER_HEART_MAGIC = register("villager_heart_magic");
    public static final RegistryObject<SoundEvent> TWINKLE = register("twinkle");
    public static final RegistryObject<SoundEvent> HEALING_SOURCE = register("healing_source");
    public static final RegistryObject<SoundEvent> HEALING_TARGET = register("healing_target");
    public static final RegistryObject<SoundEvent> EARTH_RUMBLE = register("earth_rumble");
    public static final RegistryObject<SoundEvent> EARTH_BLAST = register("earth_blast");
    public static final RegistryObject<SoundEvent> DEATH_CIRCLE = register("death_circle");
    public static final RegistryObject<SoundEvent> DEATH_SUMMON = register("death_summon");
    public static final RegistryObject<SoundEvent> DEATH_SUMMON_TARGET = register("death_summon_target");
    public static final RegistryObject<SoundEvent> DEATH_SUMMON_END = register("death_summon_end");
    public static final RegistryObject<SoundEvent> DEATH_SKULL_LEAVE = register("death_skull_leave");
    public static final RegistryObject<SoundEvent> DEATH_SKULL_ARRIVE = register("death_skull_arrive");
    public static final RegistryObject<SoundEvent> DEATH_SKULL_REBOUND = register("death_skull_rebound");
    public static final RegistryObject<SoundEvent> DEATH_SHIELD = register("death_shield");
    public static final RegistryObject<SoundEvent> DEATH_FULL_SKULLS = register("death_full_skulls");
    public static final RegistryObject<SoundEvent> NECRO_DEAD = register("necro_dead");
    public static final RegistryObject<SoundEvent> SLAM_GROUND = register("slam_ground");
    public static final RegistryObject<SoundEvent> BIG_ATTACK = register("big_attack");
    public static final RegistryObject<SoundEvent> COURAGE_AURA = register("courage_aura");
    public static final RegistryObject<SoundEvent> FLUTE_SHORT_1 = register("flute_short_1");
    public static final RegistryObject<SoundEvent> FLUTE_SHORT_2 = register("flute_short_2");
    public static final RegistryObject<SoundEvent> FLUTE_SHORT_3 = register("flute_short_3");
    public static final RegistryObject<SoundEvent> FLUTE_SHORT_4 = register("flute_short_4");
    public static final RegistryObject<SoundEvent> FLUTE_SHORT_5 = register("flute_short_5");
    public static final RegistryObject<SoundEvent> FLUTE_SHORT_6 = register("flute_short_6");
    public static final RegistryObject<SoundEvent> FLUTE_SHORT_7 = register("flute_short_7");
    public static final RegistryObject<SoundEvent> FLUTE_TAVERN_1 = register("flute_tavern_1");
    public static final RegistryObject<SoundEvent> FLUTE_TAVERN_2 = register("flute_tavern_2");
    public static final RegistryObject<SoundEvent> FLUTE_TAVERN_3 = register("flute_tavern_3");

    private TekSounds() {
    }

    public static void register(IEventBus modBus) {
        SOUNDS.register(modBus);
    }

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(TekTopiaPort.MODID, name)));
    }

    public enum Performance {
        FLUTE_SHORT_1((byte) 1, TekSounds.FLUTE_SHORT_1, false, 204, "villager_flute_1"),
        FLUTE_SHORT_2((byte) 2, TekSounds.FLUTE_SHORT_2, false, 190, "villager_flute_1"),
        FLUTE_SHORT_3((byte) 3, TekSounds.FLUTE_SHORT_3, false, 140, "villager_flute_1"),
        FLUTE_SHORT_4((byte) 4, TekSounds.FLUTE_SHORT_4, false, 92, "villager_flute_1"),
        FLUTE_SHORT_5((byte) 5, TekSounds.FLUTE_SHORT_5, false, 184, "villager_flute_1"),
        FLUTE_SHORT_6((byte) 6, TekSounds.FLUTE_SHORT_6, false, 313, "villager_flute_1"),
        FLUTE_SHORT_7((byte) 7, TekSounds.FLUTE_SHORT_7, false, 112, "villager_flute_1"),
        FLUTE_TAVERN_1((byte) 8, TekSounds.FLUTE_TAVERN_1, true, 666, "villager_flute_1"),
        FLUTE_TAVERN_2((byte) 9, TekSounds.FLUTE_TAVERN_2, true, 764, "villager_flute_1"),
        FLUTE_TAVERN_3((byte) 10, TekSounds.FLUTE_TAVERN_3, true, 880, "villager_flute_1");

        private final byte id;
        private final RegistryObject<SoundEvent> sound;
        private final boolean tavern;
        private final int duration;
        private final String animation;

        Performance(byte id, RegistryObject<SoundEvent> sound, boolean tavern, int duration, String animation) {
            this.id = id;
            this.sound = sound;
            this.tavern = tavern;
            this.duration = duration;
            this.animation = animation;
        }

        public byte id() {
            return this.id;
        }

        public SoundEvent sound() {
            return this.sound.get();
        }

        public boolean inTavern() {
            return this.tavern;
        }

        public int duration() {
            return this.duration;
        }

        public String animation() {
            return this.animation;
        }

        public static Performance fromId(byte id) {
            for (Performance performance : values()) {
                if (performance.id == id) {
                    return performance;
                }
            }
            return null;
        }
    }
}
