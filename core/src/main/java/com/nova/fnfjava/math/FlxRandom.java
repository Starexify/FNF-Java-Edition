package com.nova.fnfjava.math;

import java.util.Random;

public class FlxRandom {
    private Random rand = new Random();

    public boolean bool(float chance) {
        return rand.nextFloat() * 100 < chance;
    }
}
