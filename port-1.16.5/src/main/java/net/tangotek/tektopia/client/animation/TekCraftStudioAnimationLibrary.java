package net.tangotek.tektopia.client.animation;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.common.ProfessionType;
import net.tangotek.tektopia.common.TekWorkerStatus;
import net.tangotek.tektopia.entities.TekChildEntity;
import net.tangotek.tektopia.entities.TekNecromancerEntity;
import net.tangotek.tektopia.entities.TekVillagerEntity;

public final class TekCraftStudioAnimationLibrary {
    private static final Map<String, ResourceLocation> ANIMATIONS = createAnimations();
    private static final Map<String, Optional<TekCraftStudioAnimation>> CACHE = new HashMap<>();

    private TekCraftStudioAnimationLibrary() {
    }

    public static Map<String, ResourceLocation> animations() {
        return ANIMATIONS;
    }

    public static Optional<TekCraftStudioAnimation> load(String key) {
        String normalized = normalizeKey(key);
        ResourceLocation location = ANIMATIONS.get(normalized);
        if (location == null) {
            return Optional.empty();
        }
        return CACHE.computeIfAbsent(normalized, ignored -> {
            Optional<TekCraftStudioAnimation> animation = TekCraftStudioModelLoader.loadAnimation(location);
            animation.ifPresent(clip -> TekTopiaPort.LOGGER.info(
                    "Loaded TekTopia CraftStudio animation {} title={} duration={} nodes={} keyframes={}",
                    clip.source(),
                    clip.title(),
                    clip.duration(),
                    clip.nodeCount(),
                    clip.keyframeCount()
            ));
            return animation;
        });
    }

    public static Optional<String> resolve(MobEntity entity, float limbSwingAmount) {
        boolean walking = limbSwingAmount > 0.08F;
        if (entity instanceof TekNecromancerEntity) {
            return Optional.of(walking ? "necro_walk" : "necro_idle");
        }
        if (!(entity instanceof TekVillagerEntity)) {
            return walking ? Optional.of("villager_walk") : Optional.empty();
        }

        TekVillagerEntity villager = (TekVillagerEntity) entity;
        if (villager.isSleepingState()) {
            return Optional.of("villager_sleep");
        }
        if (villager.isSittingState()) {
            return Optional.of("villager_sit");
        }
        String thought = villager.getThoughtKey().toLowerCase(Locale.ROOT);
        if (thought.contains("ate") || thought.contains("food")) {
            return Optional.of("villager_eat");
        }
        TekWorkerStatus status = villager.getWorkerStatus();
        if (status == TekWorkerStatus.COMBAT) {
            return Optional.of(walking ? "villager_run" : "villager_salute");
        }
        if (status == TekWorkerStatus.WORKING) {
            Optional<String> workClip = workClip(villager.getProfessionType());
            if (workClip.isPresent()) {
                return workClip;
            }
        }
        if (walking) {
            return Optional.of(entity instanceof TekChildEntity ? "child_walk" : "villager_walk");
        }
        return Optional.empty();
    }

    public static void logCoverage() {
        int present = 0;
        int missing = 0;
        for (Map.Entry<String, ResourceLocation> entry : ANIMATIONS.entrySet()) {
            if (TekCraftStudioModelLoader.resourceExists(entry.getValue())) {
                present++;
            } else {
                missing++;
                TekTopiaPort.LOGGER.warn("Missing TekTopia animation mapping {} -> {}", entry.getKey(), entry.getValue());
            }
        }
        TekTopiaPort.LOGGER.info("TekTopia animation coverage check: present={} missing={}", present, missing);
    }

    private static Optional<String> workClip(ProfessionType profession) {
        switch (profession) {
            case BARD:
                return Optional.of("villager_flute_1");
            case BLACKSMITH:
                return Optional.of("villager_hammer");
            case CHEF:
            case BUTCHER:
                return Optional.of("villager_cook");
            case CLERIC:
                return Optional.of("villager_cast_bless");
            case DRUID:
                return Optional.of("villager_cast_grow");
            case ENCHANTER:
                return Optional.of("villager_cast_forward");
            case FARMER:
                return Optional.of("villager_hoe");
            case GUARD:
                return Optional.of("villager_salute");
            case LUMBERJACK:
                return Optional.of("villager_chop");
            case MINER:
                return Optional.of("villager_craft");
            case RANCHER:
                return Optional.of("villager_take");
            case TEACHER:
                return Optional.of("villager_teach");
            default:
                return Optional.empty();
        }
    }

    private static Map<String, ResourceLocation> createAnimations() {
        Map<String, ResourceLocation> animations = new LinkedHashMap<>();
        register(animations, "child_walk");
        register(animations, "necro_cast_forward");
        register(animations, "necro_idle");
        register(animations, "necro_siphon");
        register(animations, "necro_summon");
        register(animations, "necro_walk");
        register(animations, "villager_cast_bless");
        register(animations, "villager_cast_forward");
        register(animations, "villager_cast_grow");
        register(animations, "villager_chop");
        register(animations, "villager_cook");
        register(animations, "villager_craft");
        register(animations, "villager_eat");
        register(animations, "villager_flute_1");
        register(animations, "villager_hammer");
        register(animations, "villager_hoe");
        register(animations, "villager_pickup");
        register(animations, "villager_read");
        register(animations, "villager_run");
        register(animations, "villager_salute");
        register(animations, "villager_sit");
        register(animations, "villager_sit_cheer");
        register(animations, "villager_sit_cheer_old");
        register(animations, "villager_sit_old");
        register(animations, "villager_sit_raise");
        register(animations, "villager_sit_raise_old");
        register(animations, "villager_skip");
        register(animations, "villager_sleep");
        register(animations, "villager_summon");
        register(animations, "villager_take");
        register(animations, "villager_teach");
        register(animations, "villager_thor_jump");
        register(animations, "villager_walk");
        register(animations, "villager_walk_sad");
        register(animations, "villager_wave");
        return Collections.unmodifiableMap(animations);
    }

    private static void register(Map<String, ResourceLocation> animations, String key) {
        animations.put(normalizeKey(key), new ResourceLocation(TekTopiaPort.MODID, "craftstudio/animations/entity/" + key + ".csjsmodelanim"));
    }

    private static String normalizeKey(String key) {
        return key == null ? "" : key.toLowerCase(Locale.ROOT);
    }
}
