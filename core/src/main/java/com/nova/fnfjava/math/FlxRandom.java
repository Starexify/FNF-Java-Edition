package com.nova.fnfjava.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class FlxRandom {
    public static boolean bool(double chancePercent) {
        return MathUtils.randomBoolean((float)(chancePercent / 100.0));
    }
}
