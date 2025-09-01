package com.nova.fnfjava;

import com.badlogic.gdx.Gdx;

public class Preferences {
    public static final String PREFS_NAME = "Funkin";
    public static com.badlogic.gdx.Preferences prefs;

    public static final boolean FLASHING_LIGHTS = true;
    public static final int GLOBAL_OFFSET = 0;

    public static void init() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    public static boolean getFlashingLights() {
        return prefs.getBoolean("flashingLights", FLASHING_LIGHTS);
    }

    public static boolean setFlashingLights(boolean value) {
        prefs.putBoolean("flashingLights", value);
        prefs.flush();
        return value;
    }

    public static int getGlobalOffset() {
        return prefs.getInteger("globalOffset", GLOBAL_OFFSET);
    }

    public static int setFlashingLights(int value) {
        prefs.putInteger("globalOffset", value);
        prefs.flush();
        return value;
    }
}
