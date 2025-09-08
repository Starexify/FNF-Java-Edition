package com.nova.fnfjava.util;

import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.data.animation.AnimationData;
import com.nova.fnfjava.data.animation.UnnamedAnimationData;

public class AnimationDataUtil {
    public static AnimationData toNamed(UnnamedAnimationData data, String name) {
        if (data == null) return null;

        AnimationData result = new AnimationData();
        result.name = name;
        result.prefix = data.prefix;
        result.assetPath = data.assetPath;
        result.offsets = data.offsets != null ? data.offsets.clone() : new float[]{0f, 0f};
        result.looped = data.looped;
        result.flipX = data.flipX;
        result.flipY = data.flipY;
        result.frameRate = data.frameRate;
        result.frameIndices = data.frameIndices != null ? data.frameIndices.clone() : null;

        return result;
    }

    public static Array<AnimationData> toNamedArray(Array<UnnamedAnimationData> dataArray, String baseName) {
        Array<AnimationData> result = new Array<>();
        for (int i = 0; i < dataArray.size; i++) {
            AnimationData named = toNamed(dataArray.get(i), baseName + i);
            if (named != null) result.add(named);
        }
        return result;
    }

    public static UnnamedAnimationData toUnnamed(AnimationData data) {
        if (data == null) return null;

        UnnamedAnimationData result = new UnnamedAnimationData();
        result.prefix = data.prefix;
        result.assetPath = data.assetPath;
        result.offsets = data.offsets != null ? data.offsets.clone() : new float[]{0f, 0f};
        result.looped = data.looped;
        result.flipX = data.flipX;
        result.flipY = data.flipY;
        result.frameRate = data.frameRate;
        result.frameIndices = data.frameIndices != null ? data.frameIndices.clone() : null;

        return result;
    }

    public static Array<UnnamedAnimationData> toUnnamedArray(Array<AnimationData> dataArray) {
        Array<UnnamedAnimationData> result = new Array<>();
        for (AnimationData data : dataArray) {
            UnnamedAnimationData unnamed = toUnnamed(data);
            if (unnamed != null) result.add(unnamed);
        }
        return result;
    }
}
