package com.nova.fnfjava;

import com.nova.fnfjava.util.Constants;

public class Paths {
    public static String currentLevel = null;

    public static void setCurrentLevel(String name) {
        if (name == null) currentLevel = null;
        else currentLevel = name.toLowerCase();
    }

    public static String getAtlas(String key) {
        return "assets/images/" + key + ".atlas";
    }

    public static String txt(String key) {
        return "assets/data/" + key + ".txt";
    }

    public static String json(String key) {
        return "assets/data/" + key + ".json";
    }

    public static String sound(String key) {
        return "assets/sounds/" + key + "." + Constants.EXT_SOUND;
    }

    public static String sharedSound(String key) {
        return "assets/shared/sounds/" + key + "." + Constants.EXT_SOUND;
    }

    public static String music(String key) {
        return "assets/music/" + key + "." + Constants.EXT_SOUND;
    }

    public static String videos(String key) {
        return "assets/videos/" + key + "." + Constants.EXT_VIDEO;
    }

    public static String inst(String song, String suffix, boolean withExtension) {
        String ext = withExtension ? "." + Constants.EXT_SOUND : "";
        return "assets/songs/" + song.toLowerCase() + "/Inst" + suffix + ext;
    }

    public static String inst(String song, String suffix) {
        return inst(song, suffix, true);
    }

    public static String image(String key) {
        return "assets/images/" + key + ".png";
    }

    public static String font(String key) {
        return "assets/fonts/" + key;
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
