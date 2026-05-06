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
import java.util.List;
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

    private static JsonArray getArray(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonArray() ? element.getAsJsonArray() : new JsonArray();
    }

    private static String getString(JsonObject object, String key, String fallback) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : fallback;
    }

    private static float[] getFloatArray(JsonObject object, String key, int size) {
        float[] result = new float[size];
        JsonArray array = getArray(object, key);
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
    }
}
