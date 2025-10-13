package com.nova.fnfjava;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nova.fnfjava.api.discord.DiscordClient;
import com.nova.fnfjava.data.freeplay.player.PlayerRegistry;
import com.nova.fnfjava.data.notestyle.NoteStyleRegistry;
import com.nova.fnfjava.data.song.SongRegistry;
import com.nova.fnfjava.data.stage.StageRegistry;
import com.nova.fnfjava.data.stickers.StickerRegistry;
import com.nova.fnfjava.data.story.level.LevelRegistry;
import com.nova.fnfjava.input.CursorHandler;
import com.nova.fnfjava.modding.FunkyLoadingScreen;
import com.nova.fnfjava.modding.api.ModLoader;
import com.nova.fnfjava.ui.debug.FunkinDebugDisplay;
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

    public static class Signals {
        public Signal<Void> preStateSwitch = new Signal<>();
    }
    public static Signals signals;

    public ModLoader modLoader;

    public static final int SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 720;

    public static FunkinLogger logger;

    public Stage stage;
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public FitViewport viewport;
    public TransitionManager transitionManager;

    public static FunkinSound sound;
    public static AssetManager assetManager = new AssetManager();
    public static RandomUtil random = new RandomUtil();

    public static FunkinDebugDisplay debugDisplay;
    public static Save save;

    @Override
    public void create() {
        try {
            instance = this;
            signals = new Signals();
            logger = new FunkinLogger("Funkin", 3);
            setupGame();
        } catch (Exception e) {
            Main.logger.setTag("Main").warn("Error during initialization", e);
        }
    }

    public void setupGame() {
        CursorHandler.initCursors();
        //CursorHandler.hide();

        save = Save.getInstance();

        Preferences.init();
        Gdx.graphics.setVSync(Preferences.getVSyncMode());
        Gdx.graphics.setForegroundFPS(Preferences.getFramerate());

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);

        stage = new Stage(viewport, batch);
        debugDisplay = new FunkinDebugDisplay(10, 0, Color.WHITE, shapeRenderer);
        debugDisplay.setY(Gdx.graphics.getHeight() - debugDisplay.getCalculatedHeight() - 10);
        stage.addActor(debugDisplay);

        sound = new FunkinSound(new MiniAudio());

        DiscordClient.getInstance().init();

        startModLoading();

        transitionManager = new TransitionManager(this, SCREEN_WIDTH, SCREEN_HEIGHT);

        logger.info("Parsing game data...");
        //SongEventRegistry.loadEventCache();
        SongRegistry.instance.loadEntries();
        LevelRegistry.instance.loadEntries();
        NoteStyleRegistry.instance.loadEntries();
        PlayerRegistry.instance.loadEntries();
/*        ConversationRegistry.instance.loadEntries();
        DialogueBoxRegistry.instance.loadEntries();
        SpeakerRegistry.instance.loadEntries();
        FreeplayStyleRegistry.instance.loadEntries();
        AlbumRegistry.instance.loadEntries();*/
        StageRegistry.instance.loadEntries();
        StickerRegistry.instance.loadEntries();

        ReloadAssetsDebugPlugin.initialize();
        PlayerSettings.init();
    }

    public void startModLoading() {
        if (modLoader == null) {
            // Fallback - no mod support on this platform
            setScreen(new TitleState(this));
            return;
        }

        Screen titleScreen = new TitleState(this);
        setScreen(new SpriteTestState(this));
        //setScreen(new FunkyLoadingScreen(this, titleScreen, modLoader));
    }

    public void switchState(Screen newScreen, boolean skipOutTransition, boolean skipInTransition) {
        signals.preStateSwitch.dispatch(null);
        transitionManager.setScreen(newScreen, skipOutTransition, skipInTransition);
    }

    public void switchState(Screen newScreen) {
        switchState(newScreen, true, true);
    }

    @Override
    public void render() {
        super.render();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        //if (modLoader != null) modLoader.updateMods();
        ReloadAssetsDebugPlugin.update();
        //if (modLoader != null) modLoader.renderMods();
    }

    @Override
    public void pause() {
        super.pause();

        sound.pause();

        if (modLoader != null) modLoader.pauseMods();
    }

    @Override
    public void resume() {
        super.resume();

        sound.resume();

        if (modLoader != null) modLoader.resumeMods();
    }

    @Override
    public void dispose() {
        if (modLoader != null) modLoader.dispose();
        super.dispose();

        if (DiscordClient.instance != null) DiscordClient.shutdown();

        if (shapeRenderer != null) shapeRenderer.dispose();
        if (debugDisplay != null) debugDisplay.dispose();
        if (stage != null) stage.dispose();

        if (batch != null) batch.dispose();
        if (assetManager != null) assetManager.dispose();
        if (sound != null) sound.dispose();
        if (CameraFlash.getInstance() != null) CameraFlash.getInstance().dispose();

        CursorHandler.dispose();
        Assets.dispose();

        if (logger != null) logger.shutdown();
    }

    public void setModLoader(ModLoader modLoader) {
        this.modLoader = modLoader;
    }
}
