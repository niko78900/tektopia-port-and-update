package net.tangotek.tektopia.client.animation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.tangotek.tektopia.TekTopiaPort;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class TekCraftStudioModelLoader {
    private static final Gson GSON = new Gson();

    private TekCraftStudioModelLoader() {
    }

    public static Optional<TekCraftStudioModel> load(ResourceLocation location) {
        Optional<JsonObject> root = loadJson(location);
        if (!root.isPresent()) {
            return Optional.empty();
        }

        try {
            String title = getString(root.get(), "title", location.toString());
            List<TekCraftStudioModel.Cube> roots = new ArrayList<>();
            JsonArray tree = getArray(root.get(), "tree");
            Counter counter = new Counter();
            for (JsonElement element : tree) {
                if (element.isJsonObject()) {
                    roots.add(readCube(element.getAsJsonObject(), counter));
                }
            }
            return Optional.of(new TekCraftStudioModel(location, title, roots, counter.cubes, counter.armorCubes));
        } catch (RuntimeException ex) {
            TekTopiaPort.LOGGER.warn("Failed to parse CraftStudio model {}", location, ex);
            return Optional.empty();
        }
    }

    public static Optional<TekCraftStudioAnimation> loadAnimation(ResourceLocation location) {
        Optional<JsonObject> root = loadJson(location);
        if (!root.isPresent()) {
            return Optional.empty();
        }

        try {
            String title = getString(root.get(), "title", location.toString());
            int duration = getInt(root.get(), "duration", 1);
            boolean holdLastKeyframe = getBoolean(root.get(), "holdLastKeyframe", false);
            JsonObject nodeAnimations = getObject(root.get(), "nodeAnimations");
            Map<String, TekCraftStudioAnimation.NodeAnimation> nodes = new LinkedHashMap<>();
            Counter counter = new Counter();
            for (Map.Entry<String, JsonElement> entry : nodeAnimations.entrySet()) {
                if (!entry.getValue().isJsonObject()) {
                    continue;
                }
                TekCraftStudioAnimation.NodeAnimation node = readNodeAnimation(entry.getValue().getAsJsonObject(), counter);
                nodes.put(TekCraftStudioAnimation.normalize(entry.getKey()), node);
            }
            return Optional.of(new TekCraftStudioAnimation(location, title, duration, holdLastKeyframe, nodes, counter.keyframes));
        } catch (RuntimeException ex) {
            TekTopiaPort.LOGGER.warn("Failed to parse CraftStudio animation {}", location, ex);
            return Optional.empty();
        }
    }

    public static Optional<JsonObject> loadJson(ResourceLocation location) {
        IResourceManager manager = Minecraft.getInstance().getResourceManager();
        try (IResource resource = manager.getResource(location);
             Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return Optional.of(GSON.fromJson(reader, JsonObject.class));
        } catch (IOException | RuntimeException ex) {
            TekTopiaPort.LOGGER.warn("Missing or unreadable TekTopia client asset {}", location);
            return Optional.empty();
        }
    }

    public static boolean resourceExists(ResourceLocation location) {
        IResourceManager manager = Minecraft.getInstance().getResourceManager();
        try (IResource ignored = manager.getResource(location)) {
            return true;
        } catch (IOException | RuntimeException ex) {
            return false;
        }
    }

    private static TekCraftStudioModel.Cube readCube(JsonObject object, Counter counter) {
        String name = getString(object, "name", "unnamed");
        counter.cubes++;
        if (name.toLowerCase().contains("armor")) {
            counter.armorCubes++;
        }

        List<TekCraftStudioModel.Cube> children = new ArrayList<>();
        JsonArray childArray = getArray(object, "children");
        for (JsonElement child : childArray) {
            if (child.isJsonObject()) {
                children.add(readCube(child.getAsJsonObject(), counter));
            }
        }

        return new TekCraftStudioModel.Cube(
                name,
                getFloatArray(object, "position", 3),
                getFloatArray(object, "offsetFromPivot", 3),
                getFloatArray(object, "size", 3),
                getFloatArray(object, "rotation", 3),
                getIntArray(object, "texOffset", 2),
                children
        );
    }

    private static TekCraftStudioAnimation.NodeAnimation readNodeAnimation(JsonObject object, Counter counter) {
        TekCraftStudioAnimation.KeyframeTrack position = readTrack(getObject(object, "position"), counter);
        TekCraftStudioAnimation.KeyframeTrack rotation = readTrack(getObject(object, "rotation"), counter);
        return new TekCraftStudioAnimation.NodeAnimation(position, rotation);
    }

    private static TekCraftStudioAnimation.KeyframeTrack readTrack(JsonObject object, Counter counter) {
        List<TekCraftStudioAnimation.Keyframe> keyframes = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (!entry.getValue().isJsonArray()) {
                continue;
            }
            try {
                keyframes.add(new TekCraftStudioAnimation.Keyframe(
                        Integer.parseInt(entry.getKey()),
                        getFloatArray(entry.getValue().getAsJsonArray(), 3)
                ));
            } catch (NumberFormatException ignored) {
                TekTopiaPort.LOGGER.warn("Ignoring CraftStudio animation keyframe with non-numeric frame '{}'", entry.getKey());
            }
        }
        keyframes.sort(Comparator.comparingInt(TekCraftStudioAnimation.Keyframe::frame));
        counter.keyframes += keyframes.size();
        return new TekCraftStudioAnimation.KeyframeTrack(keyframes);
    }

    private static JsonArray getArray(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonArray() ? element.getAsJsonArray() : new JsonArray();
    }

    private static JsonObject getObject(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
    }

    private static String getString(JsonObject object, String key, String fallback) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : fallback;
    }

    private static int getInt(JsonObject object, String key, int fallback) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsInt() : fallback;
    }

    private static boolean getBoolean(JsonObject object, String key, boolean fallback) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsBoolean() : fallback;
    }

    private static float[] getFloatArray(JsonObject object, String key, int size) {
        return getFloatArray(getArray(object, key), size);
    }

    private static float[] getFloatArray(JsonArray array, int size) {
        float[] result = new float[size];
        for (int i = 0; i < size && i < array.size(); i++) {
            result[i] = array.get(i).getAsFloat();
        }
        return result;
    }

    private static int[] getIntArray(JsonObject object, String key, int size) {
        int[] result = new int[size];
        JsonArray array = getArray(object, key);
        for (int i = 0; i < size && i < array.size(); i++) {
            result[i] = array.get(i).getAsInt();
        }
        return result;
    }

    private static final class Counter {
        private int cubes;
        private int armorCubes;
        private int keyframes;
    }
}
