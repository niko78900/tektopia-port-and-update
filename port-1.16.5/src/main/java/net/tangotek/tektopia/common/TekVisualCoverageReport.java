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
        return summarize().formatLines();
    }

    public static CoverageSummary summarize() {
        List<String> lines = new ArrayList<>();
        JsonObject manifest = readManifest();
        if (manifest == null) {
            lines.add("Visual assets: missing manifest " + MANIFEST_PATH);
            return new CoverageSummary(0, 0, 1, lines);
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

        AssetCounters itemMappings = validateItemFallbackMappings(manifest, recovered, gaps);
        complete += itemMappings.complete;
        partial += itemMappings.partial;
        missing += itemMappings.missing;

        AssetCounters particleRenderers = validateParticleRenderers(manifest, recovered, gaps);
        complete += particleRenderers.complete;
        partial += particleRenderers.partial;
        missing += particleRenderers.missing;

        AssetCounters animations = validateAnimationMappings(manifest, recovered, gaps);
        complete += animations.complete;
        partial += animations.partial;
        missing += animations.missing;

        lines.add("Visual assets: complete=" + complete + " partial=" + partial + " missing=" + missing
                + " manifest=" + MANIFEST_PATH);
        for (String line : recovered) {
            lines.add("Recovered: " + line);
        }
        for (String line : gaps) {
            lines.add("Gap: " + line);
        }
        return new CoverageSummary(complete, partial, missing, lines);
    }

    public static final class CoverageSummary {
        private final int complete;
        private final int partial;
        private final int missing;
        private final List<String> lines;

        private CoverageSummary(int complete, int partial, int missing, List<String> lines) {
            this.complete = complete;
            this.partial = partial;
            this.missing = missing;
            this.lines = lines;
        }

        public boolean isStrictPass() {
            return this.partial == 0 && this.missing == 0;
        }

        public int getComplete() {
            return this.complete;
        }

        public int getPartial() {
            return this.partial;
        }

        public int getMissing() {
            return this.missing;
        }

        public List<String> formatLines() {
            return this.lines;
        }
    }

    private static AssetCounters validateItemFallbackMappings(JsonObject manifest, List<String> recovered, List<String> gaps) {
        AssetCounters counters = new AssetCounters();
        JsonArray mappings = getArray(manifest, "itemFallbackMappings");
        for (JsonElement element : mappings) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            String model = getString(entry, "model", "");
            String texture = getString(entry, "texture", "");
            boolean modelOk = !model.isEmpty() && resourceExists(model);
            boolean textureOk = !texture.isEmpty() && modelTextureExists(texture);
            if (modelOk && textureOk) {
                counters.complete++;
                recovered.add("Item fallback model=" + model + " texture=" + texture);
            } else if (modelOk || textureOk) {
                counters.partial++;
                gaps.add("Item fallback partial modelOk=" + modelOk + " textureOk=" + textureOk + " model=" + model + " texture=" + texture);
            } else {
                counters.missing++;
                gaps.add("Item fallback missing model=" + model + " texture=" + texture);
            }
        }
        return counters;
    }

    private static AssetCounters validateParticleRenderers(JsonObject manifest, List<String> recovered, List<String> gaps) {
        AssetCounters counters = new AssetCounters();
        JsonArray renderers = getArray(manifest, "particleRenderers");
        for (JsonElement element : renderers) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            String id = getString(entry, "id", "unknown");
            boolean texturesOk = hasAllTextures(entry);
            if (texturesOk) {
                counters.complete++;
                recovered.add("Particle renderer=" + id + " textures=" + getArray(entry, "textures").size());
            } else {
                counters.missing++;
                gaps.add("Particle renderer missing texture id=" + id);
            }
        }
        return counters;
    }

    private static AssetCounters validateAnimationMappings(JsonObject manifest, List<String> recovered, List<String> gaps) {
        AssetCounters counters = new AssetCounters();
        JsonArray mappings = getArray(manifest, "animationMappings");
        for (JsonElement element : mappings) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            String key = getString(entry, "key", "unknown");
            String resource = getString(entry, "resource", "");
            if (!resource.isEmpty() && resourceExists(resource)) {
                counters.complete++;
                recovered.add("Animation key=" + key + " resource=" + resource);
            } else {
                counters.missing++;
                gaps.add("Animation missing key=" + key + " resource=" + resource);
            }
        }
        return counters;
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

    private static boolean modelTextureExists(String textureReference) {
        int split = textureReference.indexOf(':');
        if (split <= 0 || split >= textureReference.length() - 1) {
            return false;
        }
        String namespace = textureReference.substring(0, split);
        String path = textureReference.substring(split + 1);
        return TekVisualCoverageReport.class.getResource("/assets/" + namespace + "/textures/" + path + ".png") != null;
    }

    private static JsonArray getArray(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonArray() ? element.getAsJsonArray() : new JsonArray();
    }

    private static String getString(JsonObject object, String key, String fallback) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : fallback;
    }

    private static final class AssetCounters {
        private int complete;
        private int partial;
        private int missing;
    }
}
