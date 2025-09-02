package com.nova.fnfjava.util.plugins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nova.fnfjava.data.song.SongRegistry;
import com.nova.fnfjava.data.stickers.StickerRegistry;

public class ReloadAssetsDebugPlugin {
    public static void initialize() {
        Gdx.app.log("DEBUG", "ReloadAssetsDebugPlugin initialized");
    }

    public static void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            reload();
        }
    }

    private static void reload() {
        Gdx.app.log("DEBUG", "Reloading registries...");

        // Reload registries
        StickerRegistry.instance.loadEntries();
        SongRegistry.instance.loadEntries();

        Gdx.app.log("DEBUG", "Registries reloaded!");
    }
}
