package com.nova.fnfjava;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nova.fnfjava.data.song.SongRegistry;
import com.nova.fnfjava.math.FlxRandom;
import com.nova.fnfjava.sound.FunkinSound;
import com.nova.fnfjava.ui.TitleState;
import com.nova.fnfjava.util.camera.CameraFlash;
import games.rednblack.miniaudio.MiniAudio;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {
    public static Main instance;

    public SpriteBatch spriteBatch;
    public FitViewport viewport;

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

            SongRegistry.initialize();

            setScreen(new TitleState(this));
        } catch (Exception e) {
            Gdx.app.error("Main", "Error during initialization", e);
        }
    }

    public void switchState(Screen screen) {
        Screen oldScreen = this.screen;
        setScreen(null);
        if (oldScreen != null) oldScreen.dispose();
        setScreen(screen);
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
        if (sound != null) {
            if (sound.music != null) sound.music.dispose();
            sound.dispose();
        }
        if (spriteBatch != null) spriteBatch.dispose();
        if (assetManager != null) assetManager.dispose();
        if (CameraFlash.getInstance() != null) CameraFlash.getInstance().dispose();
    }
}
