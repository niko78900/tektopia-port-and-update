package net.tangotek.tektopia.common;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class TekVisualCoverageReport {
    private static final Gson GSON = new Gson();
    private static final String MANIFEST_PATH = "/assets/tektopia/visual_coverage.json";

    private TekVisualCoverageReport() {
    }

    public static List<String> formatReport() {
        List<String> lines = new ArrayList<>();
        JsonObject manifest = readManifest();
        if (manifest == null) {
            lines.add("Visual assets: missing manifest " + MANIFEST_PATH);
            return lines;
        }

        JsonArray entities = getArray(manifest, "entities");
        int complete = 0;
        int partial = 0;
        int missing = 0;
        List<String> recovered = new ArrayList<>();
        List<String> gaps = new ArrayList<>();

        for (JsonElement element : entities) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            String id = getString(entry, "id", "unknown");
            String model = getString(entry, "model", "");
            int textureCount = getArray(entry, "textures").size();
            boolean modelOk = !model.isEmpty() && resourceExists(model);
            boolean texturesOk = hasAllTextures(entry);

            if (modelOk && texturesOk) {
                complete++;
                recovered.add(id + " model=" + model + " textures=" + textureCount);
            } else if (modelOk || texturesOk) {
                partial++;
                gaps.add(id + " partial modelOk=" + modelOk + " texturesOk=" + texturesOk);
            } else {
                missing++;
                gaps.add(id + " missing required animated model/texture, fallback=" + getString(entry, "fallback", "biped"));
            }
        }

        lines.add("Visual assets: complete=" + complete + " partial=" + partial + " missing=" + missing
                + " manifest=" + MANIFEST_PATH);
        for (String line : recovered) {
            lines.add("Recovered: " + line);
        }
        for (String line : gaps) {
            lines.add("Gap: " + line);
        }
        return lines;
    }

    private static JsonObject readManifest() {
        try (InputStream stream = TekVisualCoverageReport.class.getResourceAsStream(MANIFEST_PATH)) {
            if (stream == null) {
                return null;
            }
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, JsonObject.class);
            }
        } catch (RuntimeException | java.io.IOException ex) {
            return null;
        }
    }

    private static boolean hasAllTextures(JsonObject entry) {
        JsonArray textures = getArray(entry, "textures");
        if (textures.size() == 0) {
            return false;
        }
        for (JsonElement texture : textures) {
            if (!texture.isJsonPrimitive() || !resourceExists(texture.getAsString())) {
                return false;
            }
        }
        return true;
    }

    private static boolean resourceExists(String resourceLocation) {
        int split = resourceLocation.indexOf(':');
        if (split <= 0 || split >= resourceLocation.length() - 1) {
            return false;
        }
        String namespace = resourceLocation.substring(0, split);
        String path = resourceLocation.substring(split + 1);
        return TekVisualCoverageReport.class.getResource("/assets/" + namespace + "/" + path) != null;
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
