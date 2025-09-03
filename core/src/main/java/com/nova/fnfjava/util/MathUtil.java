package com.nova.fnfjava.util;

import com.badlogic.gdx.math.MathUtils;

public class MathUtil {

    /**
     * Exponential decay interpolation.
     * <p>
     * Framerate-independent because the rate-of-change is proportional to the difference, so you can
     * use the time elapsed since the last frame as `deltaTime` and the function will be consistent.
     * <p>
     * Equivalent to `smoothLerpDecay(base, target, deltaTime, -duration / logBase(2, precision))`.
     *
     * @param base The starting or current value.
     * @param target The value this function approaches.
     * @param deltaTime The change in time along the function in seconds.
     * @param duration Time in seconds to reach `target` within `precision`, relative to the original distance.
     * @param precision Relative target precision of the interpolation. Defaults to 1% distance remaining.
     *
     * @see https://twitter.com/FreyaHolmer/status/1757918211679650262
     *
     * @return The interpolated value.
     */
    public static float smoothLerpPrecision(float base, float target, float deltaTime, float duration, float precision) {
        if (deltaTime == 0) return base;
        if (base == target) return target;
        return MathUtils.lerp(target, base, (float) Math.pow(precision, deltaTime / duration));
    }

    public static float smoothLerpPrecision(float base, float target, float deltaTime, float duration) {
        return smoothLerpPrecision(base, target, deltaTime, duration, 0.01f);
    }
}
