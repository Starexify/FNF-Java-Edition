package com.nova.fnfjava;

import com.badlogic.gdx.Gdx;

public class Preferences {
    public static final String PREFS_NAME = "Funkin";
    public static com.badlogic.gdx.Preferences prefs;

    public static final boolean FLASHING_LIGHTS = true;
    public static final int GLOBAL_OFFSET = 0;
    public static final boolean VSYNC_MODE = false;
    public static final int FRAMERATE = 60;
    public static final boolean DEBUG_DISPLAY = false;

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

    public static int setGlobalOffset(int value) {
        prefs.putInteger("globalOffset", value);
        prefs.flush();
        return value;
    }

    public static boolean getVSyncMode() {
        return prefs.getBoolean("vsyncMode", VSYNC_MODE);
    }

    public static boolean setVSyncMode(boolean value) {
        prefs.putBoolean("vsyncMode", value);
        prefs.flush();
        Gdx.graphics.setVSync(value);
        return value;

    }

    public static int getFramerate() {
        return prefs.getInteger("framerate", FRAMERATE);
    }

    public static int setFramerate(int value) {
        prefs.putInteger("framerate", value);
        prefs.flush();
        Gdx.graphics.setForegroundFPS(value);
        return value;
    }

    public static boolean getDebugDisplay() {
        return prefs.getBoolean("debugDisplay", DEBUG_DISPLAY);
    }

    public static boolean setDebugDisplay(boolean value) {
        prefs.putBoolean("debugDisplay", value);
        prefs.flush();
        return value;
    }
}
