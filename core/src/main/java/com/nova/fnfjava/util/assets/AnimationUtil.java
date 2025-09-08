package com.nova.fnfjava.util.assets;

import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.graphics.AnimatedSprite;
import com.nova.fnfjava.data.animation.AnimationData;

public class AnimationUtil {
    /**
     * Properly adds an animation to a sprite based on the provided animation data.
     */
    public static void addAtlasAnimation(AnimatedSprite target, AnimationData anim) {
        if (anim.prefix == null) return;
        float frameRate = anim.frameRate == null ? 24 : anim.frameRate;
        boolean looped = anim.looped == null ? false : anim.looped;
        boolean flipX = anim.flipX == null ? false : anim.flipX;
        boolean flipY = anim.flipY == null ? false : anim.flipY;

        if (anim.frameIndices != null && anim.frameIndices.length > 0)
            target.animation.addByIndices(anim.name, anim.prefix, new Array<>(anim.frameIndices), frameRate, looped, flipX, flipY);
        else
            target.animation.addByPrefix(anim.name, anim.prefix, frameRate, looped, flipX, flipY);
    }

    /**
     * Properly adds multiple animations to a sprite based on the provided animation data.
     */
    public static void addAtlasAnimations(AnimatedSprite target, Array<AnimationData> animations) {
        for (AnimationData anim : animations) addAtlasAnimation(target, anim);
    }
}
