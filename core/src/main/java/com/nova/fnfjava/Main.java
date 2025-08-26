package com.nova.fnfjava;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nova.fnfjava.system.frontEnds.SoundManager;
import com.nova.fnfjava.ui.TitleScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {
    public SpriteBatch spriteBatch;
    public FitViewport viewport;
    public static SoundManager sound;

    // Game constants
    public static final int SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 720;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);

        sound = new SoundManager();

        setScreen(new TitleScreen(this));
    }
}
