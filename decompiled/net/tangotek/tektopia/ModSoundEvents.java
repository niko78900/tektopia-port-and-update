/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundEvent
 *  net.minecraftforge.fml.common.registry.GameRegistry$ObjectHolder
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.IForgeRegistryEntry
 */
package net.tangotek.tektopia;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@GameRegistry.ObjectHolder(value="tektopia")
public class ModSoundEvents {
    public static final SoundEvent villagerSleep = ModSoundEvents.createSoundEvent("villager_sleep");
    public static final SoundEvent villagerAfraid = ModSoundEvents.createSoundEvent("villager_afraid");
    public static final SoundEvent villagerGrunt = ModSoundEvents.createSoundEvent("villager_grunt");
    public static final SoundEvent villagerEnchant = ModSoundEvents.createSoundEvent("villager_enchant");
    public static final SoundEvent villagerEnchantApply = ModSoundEvents.createSoundEvent("villager_enchant_apply");
    public static final SoundEvent villagerAngry = ModSoundEvents.createSoundEvent("villager_angry");
    public static final SoundEvent villagerHappy = ModSoundEvents.createSoundEvent("villager_happy");
    public static final SoundEvent villagerSocialize = ModSoundEvents.createSoundEvent("villager_socialize");
    public static final SoundEvent villagerHeartMagic = ModSoundEvents.createSoundEvent("villager_heart_magic");
    public static final SoundEvent twinkle = ModSoundEvents.createSoundEvent("twinkle");
    public static final SoundEvent healingSource = ModSoundEvents.createSoundEvent("healing_source");
    public static final SoundEvent healingTarget = ModSoundEvents.createSoundEvent("healing_target");
    public static final SoundEvent earthRumble = ModSoundEvents.createSoundEvent("earth_rumble");
    public static final SoundEvent earthBlast = ModSoundEvents.createSoundEvent("earth_blast");
    public static final SoundEvent deathCircle = ModSoundEvents.createSoundEvent("death_circle");
    public static final SoundEvent deathSummon = ModSoundEvents.createSoundEvent("death_summon");
    public static final SoundEvent deathSummonTarget = ModSoundEvents.createSoundEvent("death_summon_target");
    public static final SoundEvent deathSummonEnd = ModSoundEvents.createSoundEvent("death_summon_end");
    public static final SoundEvent deathSkullLeave = ModSoundEvents.createSoundEvent("death_skull_leave");
    public static final SoundEvent deathSkullArrive = ModSoundEvents.createSoundEvent("death_skull_arrive");
    public static final SoundEvent deathSkullRebound = ModSoundEvents.createSoundEvent("death_skull_rebound");
    public static final SoundEvent deathShield = ModSoundEvents.createSoundEvent("death_shield");
    public static final SoundEvent deathFullSkulls = ModSoundEvents.createSoundEvent("death_full_skulls");
    public static final SoundEvent necroDead = ModSoundEvents.createSoundEvent("necro_dead");
    public static final SoundEvent slamGround = ModSoundEvents.createSoundEvent("slam_ground");
    public static final SoundEvent bigAttack = ModSoundEvents.createSoundEvent("big_attack");
    public static final SoundEvent courageAura = ModSoundEvents.createSoundEvent("courage_aura");
    public static final SoundEvent fluteShort1 = ModSoundEvents.createSoundEvent("flute_short_1");
    public static final SoundEvent fluteShort2 = ModSoundEvents.createSoundEvent("flute_short_2");
    public static final SoundEvent fluteShort3 = ModSoundEvents.createSoundEvent("flute_short_3");
    public static final SoundEvent fluteShort4 = ModSoundEvents.createSoundEvent("flute_short_4");
    public static final SoundEvent fluteShort5 = ModSoundEvents.createSoundEvent("flute_short_5");
    public static final SoundEvent fluteShort6 = ModSoundEvents.createSoundEvent("flute_short_6");
    public static final SoundEvent fluteShort7 = ModSoundEvents.createSoundEvent("flute_short_7");
    public static final SoundEvent fluteTavern1 = ModSoundEvents.createSoundEvent("flute_tavern_1");
    public static final SoundEvent fluteTavern2 = ModSoundEvents.createSoundEvent("flute_tavern_2");
    public static final SoundEvent fluteTavern3 = ModSoundEvents.createSoundEvent("flute_tavern_3");

    public static void registerSounds(IForgeRegistry<SoundEvent> registry) {
        registry.registerAll((IForgeRegistryEntry[])new SoundEvent[]{villagerSleep, villagerAfraid, villagerGrunt, villagerEnchant, villagerEnchantApply, villagerAngry, villagerHappy, villagerHeartMagic, twinkle, healingSource, healingTarget, earthRumble, earthBlast, deathCircle, deathSummon, deathSummonTarget, deathSkullArrive, deathSkullLeave, deathSkullRebound, deathShield, deathFullSkulls, deathSummonEnd, necroDead, slamGround, bigAttack, courageAura});
        Arrays.stream(Performance.values()).map(p -> p.sound).forEach(s -> registry.register((IForgeRegistryEntry)s));
    }

    private static SoundEvent createSoundEvent(String soundName) {
        ResourceLocation soundID = new ResourceLocation("tektopia", soundName);
        return (SoundEvent)new SoundEvent(soundID).setRegistryName(soundID);
    }

    public static enum Performance {
        FluteShort1(1, fluteShort1, false, 204, "villager_flute_1"),
        FluteShort2(2, fluteShort2, false, 190, "villager_flute_1"),
        FluteShort3(3, fluteShort3, false, 140, "villager_flute_1"),
        FluteShort4(4, fluteShort4, false, 92, "villager_flute_1"),
        FluteShort5(5, fluteShort5, false, 184, "villager_flute_1"),
        FluteShort6(6, fluteShort6, false, 313, "villager_flute_1"),
        FluteShort7(7, fluteShort7, false, 112, "villager_flute_1"),
        FluteTavern1(8, fluteTavern1, true, 666, "villager_flute_1"),
        FluteTavern2(9, fluteTavern2, true, 764, "villager_flute_1"),
        FluteTavern3(10, fluteTavern3, true, 880, "villager_flute_1");

        public final SoundEvent sound;
        public final int duration;
        public final byte id;
        public final String anim;
        public final boolean inTavern;

        private Performance(byte id, SoundEvent sound, boolean tavern, int duration, String anim) {
            this.duration = duration;
            this.id = id;
            this.anim = anim;
            this.inTavern = tavern;
            this.sound = sound;
        }

        public static Performance valueOf(byte id) {
            for (Performance mode : Performance.values()) {
                if (mode.id != id) continue;
                return mode;
            }
            return null;
        }

        public static Performance getRandom(boolean tav, Random rnd) {
            List perfs = Arrays.stream(Performance.values()).filter(p -> p.inTavern == tav).collect(Collectors.toList());
            if (!perfs.isEmpty()) {
                return (Performance)((Object)perfs.get(rnd.nextInt(perfs.size())));
            }
            return null;
        }
    }
}

