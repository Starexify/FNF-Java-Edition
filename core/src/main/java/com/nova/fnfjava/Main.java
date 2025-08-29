package com.nova.fnfjava;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nova.fnfjava.math.FlxRandom;
import com.nova.fnfjava.sound.FunkinSound;
import com.nova.fnfjava.ui.TitleState;
import com.nova.fnfjava.util.camera.CameraFlash;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {
    public static Main instance;

    public SpriteBatch spriteBatch;
    public FitViewport viewport;

    public static FunkinSound sound = new FunkinSound();
    public static AssetManager assetManager = new AssetManager();
    public static FlxRandom random = new FlxRandom();

    public boolean focused = true;

    // Game constants
    public static final int SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 720;

    @Override
    public void create() {
        instance = this;

        Preferences.init();
        PlayerSettings.init();

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);

        setScreen(new TitleState(this));
    }

    public void switchState(Screen screen) {
        if (this.screen != null) this.screen.dispose();
        this.screen = screen;
        if (this.screen != null) {
            this.screen.show();
            this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    @Override
    public void pause() {
        super.pause();

        sound.pauseMusic();
    }

    @Override
    public void resume() {
        super.resume();

        sound.resumeMusic();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (sound.music != null) sound.music.dispose();
        FunkinSound.dispose();
        spriteBatch.dispose();
        assetManager.dispose();
        CameraFlash.getInstance().dispose();
    }
}
