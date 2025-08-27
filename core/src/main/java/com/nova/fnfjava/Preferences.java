package com.nova.fnfjava;

import com.badlogic.gdx.Gdx;

public class Preferences {
    private static final String PREFS_NAME = "Funkin";
    private static com.badlogic.gdx.Preferences prefs;

    private static final boolean FLASHING_LIGHTS = true;

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
}
