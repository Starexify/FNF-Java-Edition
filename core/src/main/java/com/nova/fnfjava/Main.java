package com.nova.fnfjava;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nova.fnfjava.api.discord.DiscordClient;
import com.nova.fnfjava.data.freeplay.player.PlayerRegistry;
import com.nova.fnfjava.data.song.SongRegistry;
import com.nova.fnfjava.data.stickers.StickerRegistry;
import com.nova.fnfjava.data.story.level.LevelRegistry;
import com.nova.fnfjava.input.CursorHandler;
import com.nova.fnfjava.util.RandomUtil;
import com.nova.fnfjava.save.Save;
import com.nova.fnfjava.audio.FunkinSound;
import com.nova.fnfjava.ui.title.TitleState;
import com.nova.fnfjava.util.camera.CameraFlash;
import com.nova.fnfjava.util.plugins.ReloadAssetsDebugPlugin;
import games.rednblack.miniaudio.MiniAudio;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {
    public static Main instance;

    public static final int SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 720;

    public static FunkinLogger logger;

    public SpriteBatch spriteBatch;
    public FitViewport viewport;
    public TransitionManager transitionManager;

    public static FunkinSound sound = new FunkinSound(new MiniAudio());
    public static AssetManager assetManager = new AssetManager();
    public static RandomUtil random = new RandomUtil();

    public static Save save;

    @Override
    public void create() {
        try {
            instance = this;

            logger = new FunkinLogger("Funkin", 3);

            setupGame();
        } catch (Exception e) {
            Main.logger.setTag("Main").warn("Error during initialization", e);
        }
    }

    public static BitmapFont fpsCounter;
    public static BitmapFont memoryCounter;

    public void setupGame() {
        CursorHandler.initCursors();
        //CursorHandler.hide();

        fpsCounter = new BitmapFont();
        memoryCounter = new BitmapFont();

        save = Save.getInstance();

        Preferences.init();
        Gdx.graphics.setVSync(Preferences.getVSyncMode());
        Gdx.graphics.setForegroundFPS(Preferences.getFramerate());

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);

        DiscordClient.getInstance().init();

        transitionManager = new TransitionManager(this, SCREEN_WIDTH, SCREEN_HEIGHT);

        logger.info("Parsing game data...");
        SongRegistry.instance.loadEntries();
        LevelRegistry.instance.loadEntries();
        PlayerRegistry.instance.loadEntries();
        StickerRegistry.instance.loadEntries();

        ReloadAssetsDebugPlugin.initialize();

        PlayerSettings.init();

        setScreen(new TitleState(this));
    }

    public void switchState(Screen newScreen) {
        transitionManager.setScreen(newScreen);
    }

    @Override
    public void render() {
        super.render();

        ReloadAssetsDebugPlugin.update();

        if (Preferences.getDebugDisplay()) {
            spriteBatch.begin();
            fpsCounter.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 3);

            long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
            long totalMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
            long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
            memoryCounter.draw(spriteBatch, "Memory: " + usedMemory + "MB / " + totalMemory + "MB (max: " + maxMemory + "MB)", 10, Gdx.graphics.getHeight() - fpsCounter.getCapHeight() - 6);

            spriteBatch.end();
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
        if (DiscordClient.instance != null) DiscordClient.shutdown();
        if (sound != null) sound.dispose();
        if (spriteBatch != null) spriteBatch.dispose();
        if (assetManager != null) assetManager.dispose();
        if (CameraFlash.getInstance() != null) CameraFlash.getInstance().dispose();

        if (fpsCounter != null) fpsCounter.dispose();
        if (memoryCounter != null) memoryCounter.dispose();

        CursorHandler.dispose();
        Assets.dispose();

        if (logger != null) logger.shutdown();
    }
}
