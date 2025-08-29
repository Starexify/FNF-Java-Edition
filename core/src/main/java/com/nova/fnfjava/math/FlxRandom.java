package com.nova.fnfjava.math;

import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class FlxRandom {
    private Random rand = new Random();

    public boolean bool(float chance) {
        return rand.nextFloat() * 100 < chance;
    }

    public <T> T getObject(Array<T> array) {
        if (array.size == 0) return null;
        int index = rand.nextInt(array.size);
        return array.get(index);
    }

}
