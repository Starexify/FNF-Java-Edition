package com.nova.fnfjava.ui.story;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.AnimatedSprite;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.data.story.level.LevelRegistry;
import com.nova.fnfjava.sound.FunkinSound;
import com.nova.fnfjava.text.FlxText;
import com.nova.fnfjava.ui.MusicBeatState;
import com.nova.fnfjava.ui.transition.stickers.StickerSubState;
import com.nova.fnfjava.util.Constants;

public class StoryMenuState extends MusicBeatState {
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.valueOf("F9CF51");
    public static final int BACKGROUND_HEIGHT = 400;

    public String currentDifficultyId = "normal";

    public String currentLevelId = "tutorial";
    public Level currentLevel;
    public boolean isLevelUnlocked;
    public LevelTitle currentLevelTitle;

    public int highScore = 42069420;
    public int highScoreLerp = 12345678;

    boolean exitingMenu = false;
    boolean selectedLevel = false;

    public FlxText levelTitleText;
    public FlxText scoreText;
    public FlxText modeText;
    public FlxText tracklistText;
    public Group levelTitles;
    public Group levelProps;
    public AnimatedSprite levelBackground;
    public AnimatedSprite leftDifficultyArrow;
    public AnimatedSprite rightDifficultyArrow;
    public AnimatedSprite difficultySprite;
    public Array<String> levelList = new Array<>();
    public ObjectMap<String, AnimatedSprite> difficultySprites;
    public StickerSubState stickerSubState;
    public static String rememberedLevelId = null;
    public static String rememberedDifficulty = Constants.DEFAULT_DIFFICULTY;

    public StoryMenuState(Main main, StickerSubState stickers) {
        super(main);
        if (stickers != null && stickers.stage != null) stickerSubState = stickers;
    }

    public StoryMenuState(Main main) {
        this(main, null);
    }

    @Override
    public void show() {
        super.show();

        levelList = LevelRegistry.instance.listSortedLevelIds();
        Array<String> filteredList = new Array<>();
        for (String id : levelList) {
            Level level = LevelRegistry.instance.fetchEntry(id);
            if (level != null && level.isVisible()) filteredList.add(id);
        }
        levelList = filteredList;

        if (levelList.size == 0) levelList.add("tutorial");

        difficultySprites = new ObjectMap<>();

        playMenuMusic();

        if (stickerSubState != null) {
            this.persistentUpdate = true;
            this.persistentDraw = true;

            openSubState(stickerSubState);
            stickerSubState.degenStickers();
        }

        persistentUpdate = persistentDraw = true;

        rememberSelection();

        updateData();

        levelTitles = new Group();
        levelTitles.setZIndex(15);
        add(levelTitles);

    }

    public void rememberSelection() {
        if (rememberedLevelId != null) currentLevelId = rememberedLevelId;
        if (rememberedDifficulty != null) currentDifficultyId = rememberedDifficulty;
    }

    public void playMenuMusic() {
        Main.sound.playMusic("freakyMenu", new FunkinSound.FunkinSoundPlayMusicParams.Builder()
            .overrideExisting(true)
            .restartTrack(false)
            .persist(true)
            .build());
    }

    public void updateData() {
        currentLevel = LevelRegistry.instance.fetchEntry(currentLevelId);
        if (currentLevel == null) throw new IllegalArgumentException("Could not fetch data for level: " + currentLevelId);
        isLevelUnlocked = currentLevel == null ? false : currentLevel.isUnlocked();
    }
}
