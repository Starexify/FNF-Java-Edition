package com.nova.fnfjava;

import com.badlogic.gdx.*;
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

    public static SpriteBatch spriteBatch;
    public static FitViewport viewport;

    public static FunkinSound sound = new FunkinSound(new MiniAudio());
    public static AssetManager assetManager = new AssetManager();
    public static FlxRandom random = new FlxRandom();

    // Game constants
    public static final int SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 720;

    @Override
    public void create() {
        instance = this;

        Preferences.init();
        PlayerSettings.init();

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);

        SongRegistry.initialize();

        setScreen(new TitleState(this));
    }

    public void switchState(Screen screen) {
        Screen oldScreen = this.screen;
        this.screen = null;

        if (oldScreen != null) oldScreen.dispose();

        this.screen = screen;
        if (this.screen != null) {
            this.screen.show();
            this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
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
        if (sound.music != null) sound.music.dispose();
        sound.dispose();
        spriteBatch.dispose();
        assetManager.dispose();
        CameraFlash.getInstance().dispose();
    }
}
