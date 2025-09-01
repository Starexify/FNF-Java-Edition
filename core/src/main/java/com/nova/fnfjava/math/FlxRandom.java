package com.nova.fnfjava.math;

import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class FlxRandom {
    public Random rand = new Random();

    public boolean bool(double chance) {
        return rand.nextDouble() * 100 < chance;
    }

    public boolean bool() {
        return bool(50.0);
    }

    public <T> T getObject(Array<T> objects) {
        if (objects == null || objects.size == 0) return null;
        int index = rand.nextInt(objects.size);
        return objects.get(index);
    }
}
