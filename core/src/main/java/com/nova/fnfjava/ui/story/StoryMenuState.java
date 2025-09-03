package com.nova.fnfjava.ui.story;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.*;
import com.nova.fnfjava.api.discord.DiscordClient;
import com.nova.fnfjava.data.story.level.LevelRegistry;
import com.nova.fnfjava.group.TypedActorGroup;
import com.nova.fnfjava.save.Save;
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
    public TypedActorGroup<LevelTitle> levelTitles;
    public Group levelProps;
    public Actor levelBackground;
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

        levelTitles = new TypedActorGroup<>();
        levelTitles.setZIndex(15);
        add(levelTitles);

        updateBackground();

        levelProps = new Group();
        levelProps.setZIndex(1000);
        add(levelProps);

        updateProps();

        tracklistText = new FlxText(Gdx.graphics.getWidth() * 0.05f,  0, "Tracks");
        tracklistText.setY(Gdx.graphics.getHeight() - tracklistText.getHeight() - (levelBackground.getX() + levelBackground.getHeight() + 100));
        tracklistText.setFormat("VCR OSD Mono", 32);
        //tracklistText.alignment = CENTER;
        tracklistText.setColor("#E55777FF");
        add(tracklistText);

        scoreText = new FlxText(Math.max(0, 10), 10, "HIGH SCORE: 42069420");
        scoreText.setY(Gdx.graphics.getHeight() - scoreText.getHeight() - scoreText.getY());
        scoreText.setFormat("VCR OSD Mono", 32);
        scoreText.setZIndex(1000);
        add(scoreText);

        levelTitleText = new FlxText(Math.max((Gdx.graphics.getWidth() * 0.7f), 0), 0, "LEVEL 1");
        levelTitleText.setY(Gdx.graphics.getHeight() - levelTitleText.getHeight() - 10);
        levelTitleText.setFormat("VCR OSD Mono", 32, Color.WHITE);
        levelTitleText.getColor().a = 0.7f;
        levelTitleText.setZIndex(1000);
        add(levelTitleText);

        buildLevelTitles();

        leftDifficultyArrow = new AnimatedSprite(Gdx.graphics.getWidth() - 410, 480);
        leftDifficultyArrow.setAtlas(Paths.getAtlas("storymenu/ui/arrows"));
        leftDifficultyArrow.setFlxY(480);
        leftDifficultyArrow.animation.addByPrefix("idle", "leftIdle");
        leftDifficultyArrow.animation.addByPrefix("press", "leftConfirm");
        leftDifficultyArrow.animation.play("idle");
        add(leftDifficultyArrow);

        buildDifficultySprite(Constants.DEFAULT_DIFFICULTY);
        buildDifficultySprite();

        rightDifficultyArrow = new AnimatedSprite(Gdx.graphics.getWidth() - 35, leftDifficultyArrow.getY());
        rightDifficultyArrow.atlas = leftDifficultyArrow.atlas;
        rightDifficultyArrow.animation.addByPrefix("idle", "rightIdle0");
        rightDifficultyArrow.animation.addByPrefix("press", "rightConfirm0");
        rightDifficultyArrow.animation.play("idle");
        add(rightDifficultyArrow);

        add(difficultySprite);

        updateText();

        DiscordClient.instance.setPresence(new DiscordClient.DiscordPresenceParams("In the Menus", null));
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

    public void buildDifficultySprite(String diff) {
        if (diff == null) diff = currentDifficultyId;
        if (difficultySprite != null) difficultySprite.remove();
        difficultySprite = difficultySprites.get(diff);

        if (difficultySprite == null) {
            difficultySprite = new AnimatedSprite(leftDifficultyArrow.getX() + leftDifficultyArrow.getWidth() + 10, leftDifficultyArrow.getY());
            if (Assets.exists(Paths.getAtlas("storymenu/difficulties/" + diff))) {
                difficultySprite.setAtlas(Paths.getAtlas("storymenu/difficulties/" + diff));
                difficultySprite.animation.addByPrefix("idle", "idle0", 24, true);
                if (Preferences.getFlashingLights()) difficultySprite.animation.play("idle");
            } else difficultySprite.loadGraphic(Paths.image("storymenu/difficulties/" + diff));

            difficultySprites.put(diff, difficultySprite);
            difficultySprite.addX((difficultySprites.get(Constants.DEFAULT_DIFFICULTY).getWidth() - difficultySprite.getWidth()) / 2);
        }
        difficultySprite.getColor().a = 0;

        difficultySprite.setY(leftDifficultyArrow.getY() - 15);
        float targetY = leftDifficultyArrow.getY() + 10;
        targetY -= (difficultySprite.getHeight() - difficultySprites.get(Constants.DEFAULT_DIFFICULTY).getHeight()) / 2;
        difficultySprite.addAction(Actions.parallel(
            Actions.moveTo(difficultySprite.getX(), targetY, 0.07f),
            Actions.alpha(1f, 0.07f)
        ));

        add(difficultySprite);
    }

    public void buildDifficultySprite() {
        buildDifficultySprite(null);
    }

    public void buildLevelTitles() {
        levelTitles.clear();
        for (int levelIndex = 0; levelIndex < levelList.size; levelIndex++) {
            String levelId = levelList.get(levelIndex);
            Level level = LevelRegistry.instance.fetchEntry(levelId);
            if (level == null || !level.isVisible()) continue;

            // TODO: Readd lock icon if unlocked is false.

            LevelTitle levelTitleItem = new LevelTitle(0, 0, level);
            float baseY = levelBackground.getY() - levelTitleItem.getHeight() - 10;
            levelTitleItem.setY((int) baseY);
            levelTitleItem.targetY = baseY - (levelTitleItem.getHeight() + 20) * levelIndex;
            levelTitleItem.screenCenter(Axes.X);
            levelTitles.add(levelTitleItem);
        }
    }

    public void updateBackground(String previousLevelId) {
        if (levelBackground == null || previousLevelId.isEmpty()) {
            levelBackground = currentLevel.buildBackground();
            levelBackground.setX(0);
            levelBackground.setY(Gdx.graphics.getHeight() - levelBackground.getHeight() - 56);
            levelBackground.setZIndex(100);
            levelBackground.getColor().a = 1.0f; // Not hidden.
            add(levelBackground);
        } else {
            Level previousLevel = LevelRegistry.instance.fetchEntry(previousLevelId);
            if (currentLevel.isBackgroundSimple() && previousLevel.isBackgroundSimple()) {
                Color previousColor = previousLevel.getBackgroundColor();
                Color currentColor = currentLevel.getBackgroundColor();
                if (previousColor != currentColor) {

                } else {

                }
            } else {
                Actor oldBackground = levelBackground;
                oldBackground.remove();

                levelBackground = currentLevel.buildBackground();
                levelBackground.setX(0);
                levelBackground.setY(56);
                levelBackground.getColor().a = 0.0f; // Hidden to start.
                levelBackground.setZIndex(100);
                add(levelBackground);
            }
        }
    }

    public void updateBackground() {
        updateBackground("");
    }

    public void updateProps() {
/*
        for (ind => prop in currentLevel.buildProps(levelProps.members)){
            prop.x += (FullScreenScaleMode.gameCutoutSize.x / 4);
            prop.zIndex = 1000;
            if (levelProps.members[ind] != prop) levelProps.replace(levelProps.members[ind], prop) ?? levelProps.add(prop);
        }
*/

        //refresh();
    }

    public void updateText() {
        tracklistText.setText("TRACKS\n\n");
        tracklistText.addText(currentLevel.getSongDisplayNames(currentDifficultyId).toString());

        //tracklistText.screenCenter(X);
        //tracklistText.setX(tracklistText.getX() - (Gdx.graphics.getWidth() * 0.35f));

        Save.SaveScoreData levelScore = Save.instance.getLevelScore(currentLevelId, currentDifficultyId);
        highScore = levelScore != null ? levelScore.score() : 0;
        // levelScore.accuracy
    }
}
