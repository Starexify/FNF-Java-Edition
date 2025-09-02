package com.nova.fnfjava;

import com.nova.fnfjava.util.Constants;

public class Paths {
    public static String image(String key) {
        return "images/" + key + ".png";
    }

    public static String getAtlas(String key) {
        return "images/" + key + ".atlas";
    }

    public static String txt(String key) {
        return "data/" + key + ".txt";
    }

    public static String json(String key) {
        return "data/" + key + ".json";
    }

    public static String sound(String key) {
        return "sounds/" + key + "." + Constants.EXT_SOUND;
    }

    public static String font(String key) {
        return "fonts/" + key;
    }
}
