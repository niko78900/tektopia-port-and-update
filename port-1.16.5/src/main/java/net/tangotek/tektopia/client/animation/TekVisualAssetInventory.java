package net.tangotek.tektopia.client.animation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.tangotek.tektopia.TekTopiaPort;

import java.util.Optional;

public final class TekVisualAssetInventory {
    private static final ResourceLocation MANIFEST =
            new ResourceLocation(TekTopiaPort.MODID, "visual_coverage.json");

    private TekVisualAssetInventory() {
    }

    public static void logClientCoverage() {
        Optional<JsonObject> manifest = TekCraftStudioModelLoader.loadJson(MANIFEST);
        if (!manifest.isPresent()) {
            TekTopiaPort.LOGGER.warn("TekTopia visual coverage manifest is missing");
            return;
        }

        JsonArray entities = getArray(manifest.get(), "entities");
        int complete = 0;
        int partial = 0;
        int missing = 0;
        for (JsonElement element : entities) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            String id = getString(entry, "id", "unknown");
            boolean modelOk = hasOptionalResource(entry, "model");
            boolean texturesOk = hasAllTextures(entry);
            if (modelOk && texturesOk) {
                complete++;
            } else if (modelOk || texturesOk) {
                partial++;
                TekTopiaPort.LOGGER.warn("TekTopia visual coverage partial for {}", id);
            } else {
                missing++;
            }
        }

        TekTopiaPort.LOGGER.info(
                "TekTopia visual coverage check: complete={} partial={} missing={} manifest={}",
                complete,
                partial,
                missing,
                MANIFEST
        );
    }

    private static boolean hasOptionalResource(JsonObject entry, String key) {
        String value = getString(entry, key, "");
        if (value.isEmpty()) {
            return false;
        }
        return TekCraftStudioModelLoader.resourceExists(new ResourceLocation(value));
    }

    private static boolean hasAllTextures(JsonObject entry) {
        JsonArray textures = getArray(entry, "textures");
        if (textures.size() == 0) {
            return false;
        }
        for (JsonElement texture : textures) {
            if (!texture.isJsonPrimitive()
                    || !TekCraftStudioModelLoader.resourceExists(new ResourceLocation(texture.getAsString()))) {
                return false;
            }
        }
        return true;
    }

    private static JsonArray getArray(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonArray() ? element.getAsJsonArray() : new JsonArray();
    }

    private static String getString(JsonObject object, String key, String fallback) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : fallback;
    }
}
