package com.nova.fnfjava.util;

import com.badlogic.gdx.math.MathUtils;

public class RandomUtil {
    public static boolean bool(double chancePercent) {
        return MathUtils.randomBoolean((float)(chancePercent / 100.0));
    }
}
