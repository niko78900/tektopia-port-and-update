package net.tangotek.tektopia.client.animation;

import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public final class TekCraftStudioModel {
    private final ResourceLocation source;
    private final String title;
    private final List<Cube> roots;
    private final int cubeCount;
    private final int armorCubeCount;

    public TekCraftStudioModel(ResourceLocation source, String title, List<Cube> roots, int cubeCount, int armorCubeCount) {
        this.source = source;
        this.title = title;
        this.roots = Collections.unmodifiableList(roots);
        this.cubeCount = cubeCount;
        this.armorCubeCount = armorCubeCount;
    }

    public ResourceLocation source() {
        return this.source;
    }

    public String title() {
        return this.title;
    }

    public List<Cube> roots() {
        return this.roots;
    }

    public int cubeCount() {
        return this.cubeCount;
    }

    public int armorCubeCount() {
        return this.armorCubeCount;
    }

    public static final class Cube {
        private final String name;
        private final float[] position;
        private final float[] offsetFromPivot;
        private final float[] size;
        private final float[] rotation;
        private final int[] texOffset;
        private final List<Cube> children;

        public Cube(
                String name,
                float[] position,
                float[] offsetFromPivot,
                float[] size,
                float[] rotation,
                int[] texOffset,
                List<Cube> children
        ) {
            this.name = name;
            this.position = position;
            this.offsetFromPivot = offsetFromPivot;
            this.size = size;
            this.rotation = rotation;
            this.texOffset = texOffset;
            this.children = Collections.unmodifiableList(children);
        }

        public String name() {
            return this.name;
        }

        public float[] position() {
            return this.position;
        }

        public float[] offsetFromPivot() {
            return this.offsetFromPivot;
        }

        public float[] size() {
            return this.size;
        }

        public float[] rotation() {
            return this.rotation;
        }

        public int[] texOffset() {
            return this.texOffset;
        }

        public List<Cube> children() {
            return this.children;
        }
    }
}
