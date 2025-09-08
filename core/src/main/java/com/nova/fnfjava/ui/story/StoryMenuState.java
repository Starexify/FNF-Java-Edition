package com.nova.fnfjava.ui.story;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;
import com.nova.fnfjava.*;
import com.nova.fnfjava.api.discord.DiscordClient;
import com.nova.fnfjava.audio.FunkinSound;
import com.nova.fnfjava.data.song.SongRegistry;
import com.nova.fnfjava.data.story.level.LevelRegistry;
import com.nova.fnfjava.graphics.AnimatedSprite;
import com.nova.fnfjava.group.TypedActorGroup;
import com.nova.fnfjava.play.PlayState;
import com.nova.fnfjava.play.PlayStatePlaylist;
import com.nova.fnfjava.play.song.Song;
import com.nova.fnfjava.save.Save;
import com.nova.fnfjava.text.FlxText;
import com.nova.fnfjava.ui.MusicBeatState;
import com.nova.fnfjava.ui.mainmenu.MainMenuState;
import com.nova.fnfjava.ui.transition.LoadingState;
import com.nova.fnfjava.ui.transition.stickers.StickerSubState;
import com.nova.fnfjava.util.Axes;
import com.nova.fnfjava.util.Constants;
import com.nova.fnfjava.util.MathUtil;

import java.util.Objects;

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
    public TypedActorGroup<LevelProp> levelProps;
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

 /*       Image black = ImageUtil.createColored(Gdx.graphics.getWidth(), 400 + levelBackground.getY(), Color.BLACK);
        black.setZIndex(levelBackground.getZIndex() - 1);
        add(black);*/

        levelProps = new TypedActorGroup<>();
        levelProps.setZIndex(1000);
        add(levelProps);

        updateProps();

        tracklistText = new FlxText(Gdx.graphics.getWidth() * 0.05f, 0, "Tracks");
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
        rightDifficultyArrow.animation.addByPrefix("idle", "rightIdle");
        rightDifficultyArrow.animation.addByPrefix("press", "rightConfirm");
        rightDifficultyArrow.animation.play("idle");
        add(rightDifficultyArrow);

        add(difficultySprite);

        updateText();
        changeDifficulty();
        changeLevel();
        //refresh();

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
        if (currentLevel == null)
            throw new IllegalArgumentException("Could not fetch data for level: " + currentLevelId);
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

    @Override
    public void render(float delta) {
        super.render(delta);

        Conductor.getInstance().update();

        highScoreLerp = (int) (MathUtil.snap(MathUtil.smoothLerpPrecision(highScoreLerp, highScore, delta, 0.307f), highScore, 1));
        scoreText.setText("LEVEL SCORE: " + Math.round(highScoreLerp));
        levelTitleText.setText(currentLevel.getTitle());
        levelTitleText.setX(Gdx.graphics.getWidth() - (levelTitleText.getWidth())); // Right align.

        handleKeyPresses();
    }

    public void handleKeyPresses() {
        if (!exitingMenu) {
            if (!selectedLevel) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                    changeLevel(-1);
                    changeDifficulty(0);
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                    changeLevel(1);
                    changeDifficulty(0);
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_UP)) {
                    changeLevel(levelList.size);
                    changeDifficulty(0);
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_DOWN)) {
                    changeLevel(-levelList.size);
                    changeDifficulty(0);
                }
/*                if (getMouseWheel() < 0) {
                    changeLevel(-Math.round(getMouseWheel() / 8));
                } else if (getMouseWheel() > 0) {
                    changeLevel(-Math.round(getMouseWheel() / 8));
                }*/

                if (Gdx.input.isKeyJustPressed(Input.Keys.D)) changeDifficulty(1);
                if (Gdx.input.isKeyJustPressed(Input.Keys.A)) changeDifficulty(-1);

                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) rightDifficultyArrow.animation.play("press");
                else rightDifficultyArrow.animation.play("idle");

                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) leftDifficultyArrow.animation.play("press");
                else leftDifficultyArrow.animation.play("idle");
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) selectLevel();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) goBack();
    }

    @Override
    public boolean handleMouseWheel(float amountY) {
        if (!exitingMenu && !selectedLevel && amountY != 0) {
            int change = amountY > 0 ? -1 : 1;
            changeLevel(change);
            return true;
        }
        return false;
    }

    /**
     * Changes the selected level.
     *
     * @param change +1 (down), -1 (up)
     */
    public void changeLevel(int change) {
        int currentIndex = levelList.indexOf(currentLevelId, false);
        int prevIndex = currentIndex;

        currentIndex += change;

        if (currentIndex < 0) currentIndex = levelList.size - 1;
        if (currentIndex >= levelList.size) currentIndex = 0;

        String previousLevelId = currentLevelId;
        currentLevelId = levelList.get(currentIndex);
        rememberedLevelId = currentLevelId;

        updateData();

        for (int index = 0; index < levelTitles.members.size; index++) {
            LevelTitle item = levelTitles.members.get(index);
            if (index == currentIndex) {
                currentLevelTitle = item;
                item.getColor().a = 1.0f;
            } else item.getColor().a = 0.6f;
        }

        if (currentIndex != prevIndex) Main.sound.playOnce(Paths.sound("scrollMenu"), 0.4f);
        repositionTitles();
        updateText();
        updateBackground(previousLevelId);
        updateProps();
        //refresh();
    }

    public void changeLevel() {
        changeLevel(0);
    }

    /**
     * Changes the selected difficulty.
     *
     * @param change +1 (right) to increase difficulty, -1 (left) to decrease difficulty
     */
    public void changeDifficulty(int change) {
        Array<String> difficultyList = Constants.DEFAULT_DIFFICULTY_LIST;
        int currentIndex = difficultyList.indexOf(currentDifficultyId, false);

        currentIndex += change;

        if (currentIndex < 0) currentIndex = difficultyList.size - 1;
        if (currentIndex >= difficultyList.size) currentIndex = 0;

        boolean hasChanged = !Objects.equals(currentDifficultyId, difficultyList.get(currentIndex));
        currentDifficultyId = difficultyList.get(currentIndex);
        rememberedDifficulty = currentDifficultyId;
        if (difficultyList.size <= 1) {
            leftDifficultyArrow.setVisible(false);
            rightDifficultyArrow.setVisible(false);
        } else {
            leftDifficultyArrow.setVisible(true);
            rightDifficultyArrow.setVisible(true);
        }

        if (hasChanged) {
            buildDifficultySprite();
            Main.sound.playOnce(Paths.sound("scrollMenu"), 0.4f);
        }

        updateText();
        //refresh();
    }

    public void changeDifficulty() {
        changeDifficulty(0);
    }

    public void selectLevel() {
        if (!currentLevel.isUnlocked()) {
            Main.sound.playOnce(Paths.sound("cancelMenu"));
            return;
        }

        if (selectedLevel) return;

        selectedLevel = true;

        Main.sound.playOnce(Paths.sound("confirmMenu"));

        currentLevelTitle.isFlashing = true;

        for (LevelProp prop : levelProps.members) prop.playConfirm();

        Paths.setCurrentLevel(currentLevel.id);

        PlayStatePlaylist.playlistSongIds = currentLevel.getSongs();
        PlayStatePlaylist.isStoryMode = true;
        PlayStatePlaylist.campaignScore = 0;

        String targetSongId = PlayStatePlaylist.playlistSongIds.removeIndex(0);

        Song targetSong = SongRegistry.instance.fetchEntry(targetSongId, new SongRegistry.SongEntryParams(Constants.DEFAULT_VARIATION));

        PlayStatePlaylist.campaignId = currentLevel.id;
        PlayStatePlaylist.campaignTitle = currentLevel.getTitle();
        PlayStatePlaylist.campaignDifficulty = currentDifficultyId;

        Highscore.talliesLevel = new Highscore.Tallies();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                //FlxTransitionableState.skipNextTransIn = false;
                //FlxTransitionableState.skipNextTransOut = false;

                String targetVariation = targetSong.getFirstValidVariation(PlayStatePlaylist.campaignDifficulty);

                LoadingState.loadPlayState(new PlayState.PlayStateParams(targetSong, PlayStatePlaylist.campaignDifficulty, targetVariation), true);
            }
        }, 1f);
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
                    levelBackground.clearActions();
                    levelBackground.addAction(Actions.color(currentColor, 0.9f, Interpolation.pow4Out));
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
        Array<LevelProp> builtProps = currentLevel.buildProps(levelProps.members);

        for (int ind = 0; ind < builtProps.size; ind++) {
            LevelProp prop = builtProps.get(ind);

            //prop.addX(Gdx.graphics.getWidth() / 4);
            prop.setZIndex(1000);
            LevelProp existingProp = null;
            if (ind < levelProps.members.size) existingProp = levelProps.members.get(ind);

            if (existingProp != prop) {
                if (existingProp != null) levelProps.replace(existingProp, prop);
                else levelProps.add(prop);
            }
        }

        //refresh();
    }

    public void updateText() {
        StringBuilder trackText = new StringBuilder("TRACKS\n\n");
        Array<String> songNames = currentLevel.getSongDisplayNames(currentDifficultyId);
        for (String songName : songNames) trackText.append(songName).append("\n");
        tracklistText.setText(trackText.toString());

        //tracklistText.screenCenter(Axes.X);
        //tracklistText.setX(tracklistText.getX() - (Gdx.graphics.getWidth() * 0.35f));

        Save.SaveScoreData levelScore = Save.instance.getLevelScore(currentLevelId, currentDifficultyId);
        highScore = levelScore != null ? levelScore.score() : 0;
        // levelScore.accuracy
    }

    public void goBack() {
        if (exitingMenu || selectedLevel) return;

        exitingMenu = true;
        //FlxG.keys.enabled = false;
        main.switchState(new MainMenuState(main));
        Main.sound.playOnce(Paths.sound("cancelMenu"));
    }

    public void repositionTitles() {
        int currentIndex = levelList.indexOf(currentLevelId, false);
        levelTitles.members.get(currentIndex).targetY = Gdx.graphics.getHeight() - levelTitles.members.get(currentIndex).getHeight() - 480;

        if (currentIndex > 0) {
            for (int i = 0; i < currentIndex; i++) {
                int itemIndex = currentIndex - 1 - i;
                LevelTitle nextItem = levelTitles.members.get(itemIndex + 1);
                levelTitles.members.get(itemIndex).targetY = nextItem.targetY + nextItem.getHeight() + Math.max(levelTitles.members.get(itemIndex).getHeight() + 20, 125);
            }
        }

        if (currentIndex < levelTitles.members.size - 1) {
            for (int i = (currentIndex + 1); i < levelTitles.members.size; i++) {
                LevelTitle previousItem = levelTitles.members.get(i - 1);
                LevelTitle currentItem = levelTitles.members.get(i);
                levelTitles.members.get(i).targetY = previousItem.targetY - currentItem.getHeight() - 20;
            }
        }
    }
}
