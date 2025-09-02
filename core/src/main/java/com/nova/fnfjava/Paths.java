package com.nova.fnfjava;

import com.nova.fnfjava.util.Constants;

public class Paths {
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

    public static String sharedSound(String key) {
        return "shared/sounds/" + key + "." + Constants.EXT_SOUND;
    }

    public static String music(String key) {
        return "music/" + key + "." + Constants.EXT_SOUND;
    }

    public static String inst(String song, String suffix, boolean withExtension) {
        String ext = withExtension ? "." + Constants.EXT_SOUND : "";
        return "songs/" + song.toLowerCase() + "/Inst" + suffix + ext;
    }

    public static String inst(String song, String suffix) {
        return inst(song, suffix, true);
    }

    public static String image(String key) {
        return "images/" + key + ".png";
    }

    public static String font(String key) {
        return "fonts/" + key;
    }

    public enum PathsFunction {
        MUSIC("MUSIC"),
        INST("INST"),
        VOICES("VOICES"),
        SOUND("SOUND");

        private final String value;

        PathsFunction(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
