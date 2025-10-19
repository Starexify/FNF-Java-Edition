package com.nova.fnfjava.util.plugins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.data.song.SongRegistry;
import com.nova.fnfjava.data.stickers.StickerRegistry;
import com.nova.fnfjava.data.story.level.LevelRegistry;

public class ReloadAssetsDebugPlugin {
    public static void initialize() {
        Main.logger.info("ReloadAssetsDebugPlugin initialized");
    }

    public static void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) reload();
    }

    public static void reload() {
        Main.logger.setTag("ReloadAssetsDebugPlugin").info("Reloading registries...");
        Screen screen = Main.instance.getScreen();

        Main.instance.modLoader.forceReloadAllMods();

        // Reload registries
        SongRegistry.instance.loadEntries();
        LevelRegistry.instance.loadEntries();
        StickerRegistry.instance.loadEntries();

        Main.instance.switchState(screen);

        Main.logger.setTag("ReloadAssetsDebugPlugin").info("Registries reloaded!");
    }
}
