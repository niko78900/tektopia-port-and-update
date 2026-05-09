package net.tangotek.tektopia.client.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.util.ResourceLocation;

public final class TekCraftStudioAnimation {
    private final ResourceLocation source;
    private final String title;
    private final int duration;
    private final boolean holdLastKeyframe;
    private final Map<String, NodeAnimation> nodeAnimations;
    private final int keyframeCount;

    public TekCraftStudioAnimation(
            ResourceLocation source,
            String title,
            int duration,
            boolean holdLastKeyframe,
            Map<String, NodeAnimation> nodeAnimations,
            int keyframeCount
    ) {
        this.source = source;
        this.title = title;
        this.duration = Math.max(1, duration);
        this.holdLastKeyframe = holdLastKeyframe;
        this.nodeAnimations = Collections.unmodifiableMap(nodeAnimations);
        this.keyframeCount = keyframeCount;
    }

    public ResourceLocation source() {
        return this.source;
    }

    public String title() {
        return this.title;
    }

    public int duration() {
        return this.duration;
    }

    public boolean holdLastKeyframe() {
        return this.holdLastKeyframe;
    }

    public int nodeCount() {
        return this.nodeAnimations.size();
    }

    public int keyframeCount() {
        return this.keyframeCount;
    }

    public NodeAnimation node(String name) {
        return this.nodeAnimations.get(normalize(name));
    }

    public static String normalize(String name) {
        return name == null ? "" : name.toLowerCase(Locale.ROOT);
    }

    public static final class NodeAnimation {
        private final KeyframeTrack position;
        private final KeyframeTrack rotation;

        public NodeAnimation(KeyframeTrack position, KeyframeTrack rotation) {
            this.position = position;
            this.rotation = rotation;
        }

        public float[] samplePosition(float frame, int duration, boolean holdLastKeyframe) {
            return this.position.sample(frame, duration, holdLastKeyframe);
        }

        public float[] sampleRotation(float frame, int duration, boolean holdLastKeyframe) {
            return this.rotation.sample(frame, duration, holdLastKeyframe);
        }

        public int keyframeCount() {
            return this.position.size() + this.rotation.size();
        }
    }

    public static final class KeyframeTrack {
        private final List<Keyframe> keyframes;

        public KeyframeTrack(List<Keyframe> keyframes) {
            this.keyframes = Collections.unmodifiableList(new ArrayList<>(keyframes));
        }

        public int size() {
            return this.keyframes.size();
        }

        public float[] sample(float frame, int duration, boolean holdLastKeyframe) {
            if (this.keyframes.isEmpty()) {
                return null;
            }
            if (this.keyframes.size() == 1) {
                return this.keyframes.get(0).copyValue();
            }

            float sampledFrame = frame;
            if (holdLastKeyframe) {
                sampledFrame = Math.max(0.0F, Math.min(sampledFrame, duration));
            } else {
                sampledFrame = sampledFrame % duration;
                if (sampledFrame < 0.0F) {
                    sampledFrame += duration;
                }
            }

            Keyframe first = this.keyframes.get(0);
            if (sampledFrame <= first.frame) {
                return first.copyValue();
            }

            for (int i = 1; i < this.keyframes.size(); i++) {
                Keyframe previous = this.keyframes.get(i - 1);
                Keyframe next = this.keyframes.get(i);
                if (sampledFrame <= next.frame) {
                    return interpolate(previous, next, sampledFrame);
                }
            }

            Keyframe last = this.keyframes.get(this.keyframes.size() - 1);
            if (holdLastKeyframe) {
                return last.copyValue();
            }
            Keyframe wrappedFirst = first.offset(duration);
            return interpolate(last, wrappedFirst, sampledFrame);
        }

        private static float[] interpolate(Keyframe previous, Keyframe next, float frame) {
            float span = Math.max(1.0F, next.frame - previous.frame);
            float t = Math.max(0.0F, Math.min(1.0F, (frame - previous.frame) / span));
            return new float[] {
                    lerp(previous.value[0], next.value[0], t),
                    lerp(previous.value[1], next.value[1], t),
                    lerp(previous.value[2], next.value[2], t)
            };
        }

        private static float lerp(float from, float to, float t) {
            return from + (to - from) * t;
        }
    }

    public static final class Keyframe {
        private final int frame;
        private final float[] value;

        public Keyframe(int frame, float[] value) {
            this.frame = frame;
            this.value = value;
        }

        public int frame() {
            return this.frame;
        }

        private float[] copyValue() {
            return new float[] {this.value[0], this.value[1], this.value[2]};
        }

        private Keyframe offset(int duration) {
            return new Keyframe(this.frame + duration, this.copyValue());
        }
    }
}
