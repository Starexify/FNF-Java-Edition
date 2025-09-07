package com.nova.fnfjava.modding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.api.discord.DiscordClient;
import com.nova.fnfjava.modding.api.LoadedMod;
import com.nova.fnfjava.modding.loader.FunkyModLoader;
import com.nova.fnfjava.ui.MusicBeatState;

public class FunkyLoadingScreen extends MusicBeatState {
    private final Screen nextScreen;
    private final FunkyModLoader modLoader;

    private Table rootTable;
    private Label titleLabel;
    private Label statusLabel;
    private Label modsCountLabel;
    private Label progressLabel;
    private ProgressBar progressBar;

    private float progress = 0f;
    private String currentStatus = "Initializing...";
    private Array<String> loadedMods = new Array<>();
    private boolean loadingComplete = false;
    private float transitionTimer = 0f;
    private static final float TRANSITION_TIME = 1.5f;

    public FunkyLoadingScreen(Main main, Screen nextScreen, FunkyModLoader modLoader) {
        super(main);
        this.nextScreen = nextScreen;
        this.modLoader = modLoader;
    }

    @Override
    public void show() {
        super.show();
        DiscordClient.setPresence(new DiscordClient.DiscordPresenceParams(null, "Loading Mods"));

        createUI();

        Thread loadingThread = new Thread(this::loadMods);
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    private void createUI() {
        // Create skin for UI elements (you might want to use your game's skin)
        Skin skin = createLoadingSkin();

        // Create root table
        rootTable = new Table();
        rootTable.setFillParent(true);
        add(rootTable);

        // Title
        titleLabel = new Label("Loading Mods...", skin, "title");
        titleLabel.setAlignment(Align.center);
        rootTable.add(titleLabel).padBottom(50).row();

        // Progress bar
        progressBar = new ProgressBar(0f, 1f, 0.01f, false, skin);
        progressBar.setSize(400, 20);
        rootTable.add(progressBar).width(400).height(20).padBottom(10).row();

        // Progress percentage
        progressLabel = new Label("0%", skin, "default");
        progressLabel.setAlignment(Align.center);
        rootTable.add(progressLabel).padBottom(30).row();

        // Current status
        statusLabel = new Label(currentStatus, skin, "default");
        statusLabel.setAlignment(Align.center);
        rootTable.add(statusLabel).padBottom(20).row();

        // Mods count
        modsCountLabel = new Label("Loaded 0 mods", skin, "default");
        modsCountLabel.setAlignment(Align.center);
        rootTable.add(modsCountLabel).padBottom(40).row();
    }

    private Skin createLoadingSkin() {
        Skin skin = new Skin();

        // Create fonts
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(2.0f);

        BitmapFont defaultFont = new BitmapFont();
        defaultFont.getData().setScale(1.2f);

        BitmapFont tipFont = new BitmapFont();
        tipFont.getData().setScale(0.9f);

        skin.add("title-font", titleFont);
        skin.add("default-font", defaultFont);
        skin.add("tip-font", tipFont);

        // Create colors
        Color cyan = new Color(0.0f, 0.8f, 1.0f, 1.0f);
        Color white = Color.WHITE;
        Color gray = Color.GRAY;
        Color darkGray = new Color(0.3f, 0.3f, 0.3f, 1.0f);

        // Create label styles
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = titleFont;
        titleStyle.fontColor = cyan;
        skin.add("title", titleStyle);

        Label.LabelStyle defaultStyle = new Label.LabelStyle();
        defaultStyle.font = defaultFont;
        defaultStyle.fontColor = white;
        skin.add("default", defaultStyle);

        Label.LabelStyle tipStyle = new Label.LabelStyle();
        tipStyle.font = tipFont;
        tipStyle.fontColor = gray;
        skin.add("tip", tipStyle);

        // Create progress bar style
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

        pixmap.setColor(darkGray);
        pixmap.fill();
        skin.add("progress-bg", new Texture(pixmap));

        pixmap.setColor(cyan);
        pixmap.fill();
        skin.add("progress-fg", new Texture(pixmap));

        pixmap.dispose();

        ProgressBar.ProgressBarStyle progressStyle = new ProgressBar.ProgressBarStyle();
        progressStyle.background = skin.newDrawable("progress-bg");
        progressStyle.knobBefore = skin.newDrawable("progress-fg");
        skin.add("default-horizontal", progressStyle);

        return skin;
    }

    private void loadMods() {
        updateStatus("Scanning mod directory...");
        setProgress(0.1f);

        modLoader.loadAllModsWithProgress(this::onModProgress);

        // Count actual loaded mods after loading is complete
        Gdx.app.postRunnable(() -> {
            Array<LoadedMod> actualLoadedMods = modLoader.getLoadedMods();
            loadedMods.clear();
            for (LoadedMod mod : actualLoadedMods) loadedMods.add(mod.getMetadata().title);
            updateModsCount();
        });

        updateStatus("Loading complete!");
        setProgress(1.0f);
        loadingComplete = true;
    }

    public void onModProgress(String modName, float progress, String status) {
        updateStatus(status + (modName.isEmpty() ? "" : ": " + modName));
        setProgress(progress);
    }

    private void updateStatus(String status) {
        Gdx.app.postRunnable(() -> {
            currentStatus = status;
            statusLabel.setText(currentStatus);
        });
    }

    private void setProgress(float progress) {
        this.progress = Math.max(this.progress, progress);
        Gdx.app.postRunnable(() -> {
            progressBar.setValue(this.progress);
            progressLabel.setText(Math.round(this.progress * 100) + "%");
        });
    }

    private void updateModsCount() {
        modsCountLabel.setText("Loaded " + loadedMods.size + " mods");
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (loadingComplete) {
            transitionTimer += delta;

            // Fade effect or pulsing when complete
            float alpha = 0.5f + 0.5f * MathUtils.sin(transitionTimer * 8f);
            titleLabel.setColor(titleLabel.getColor().r, titleLabel.getColor().g, titleLabel.getColor().b, alpha);

            if (transitionTimer >= TRANSITION_TIME) {
                main.switchState(nextScreen, true, true);
            }
        } else {
            // Pulsing progress bar when loading
            float pulseAlpha = 0.7f + 0.3f * MathUtils.sin(delta * 4f);
            progressBar.setColor(1f, 1f, 1f, pulseAlpha);
        }
    }
}
