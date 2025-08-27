package com.nova.fnfjava;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nova.fnfjava.system.frontEnds.SoundManager;
import com.nova.fnfjava.ui.TitleState;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {
    public SpriteBatch spriteBatch;
    public FitViewport viewport;
    public static SoundManager sound = new SoundManager();

    public static AssetManager assetManager = new AssetManager();

    // Game constants
    public static final int SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 720;

    @Override
    public void create() {
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
}
