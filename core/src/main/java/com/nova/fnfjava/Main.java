package com.nova.fnfjava;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nova.fnfjava.data.song.SongRegistry;
import com.nova.fnfjava.data.stickers.StickerRegistry;
import com.nova.fnfjava.math.FlxRandom;
import com.nova.fnfjava.sound.FunkinSound;
import com.nova.fnfjava.ui.title.TitleState;
import com.nova.fnfjava.util.camera.CameraFlash;
import com.nova.fnfjava.util.plugins.ReloadAssetsDebugPlugin;
import games.rednblack.miniaudio.MiniAudio;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {
    public static Main instance;

    public SpriteBatch spriteBatch;
    public FitViewport viewport;
    public TransitionManager transitionManager;

    public static FunkinSound sound = new FunkinSound(new MiniAudio());
    public static AssetManager assetManager = new AssetManager();
    public static FlxRandom random = new FlxRandom();

    // Game constants
    public static final int SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 720;

    @Override
    public void create() {
        try {
            instance = this;

            Preferences.init();
            PlayerSettings.init();

            spriteBatch = new SpriteBatch();
            viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);

            transitionManager = new TransitionManager(this, SCREEN_WIDTH, SCREEN_HEIGHT);

            SongRegistry.initialize();
            SongRegistry.instance.loadEntries();

            StickerRegistry.initialize();
            StickerRegistry.instance.loadEntries();

            ReloadAssetsDebugPlugin.initialize();

            setScreen(new TitleState(this));
        } catch (Exception e) {
            Gdx.app.error("Main", "Error during initialization", e);
        }
    }

    public void switchState(Screen newScreen) {
        transitionManager.setScreen(newScreen);
    }

    @Override
    public void render() {
        super.render();

        ReloadAssetsDebugPlugin.update();
    }

    @Override
    public void pause() {
        super.pause();

        sound.pause();
    }

    @Override
    public void resume() {
        super.resume();

        sound.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (sound != null) sound.dispose();
        if (spriteBatch != null) spriteBatch.dispose();
        if (assetManager != null) assetManager.dispose();
        if (CameraFlash.getInstance() != null) CameraFlash.getInstance().dispose();
    }
}
