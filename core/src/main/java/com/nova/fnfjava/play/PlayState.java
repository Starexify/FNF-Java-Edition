package com.nova.fnfjava.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.nova.fnfjava.Conductor;
import com.nova.fnfjava.Highscore;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.api.discord.DiscordClient;
import com.nova.fnfjava.audio.VoicesGroup;
import com.nova.fnfjava.data.event.SongEventRegistry;
import com.nova.fnfjava.data.notestyle.NoteStyleRegistry;
import com.nova.fnfjava.data.song.SongData;
import com.nova.fnfjava.graphics.FunkinSprite;
import com.nova.fnfjava.play.components.HealthIcon;
import com.nova.fnfjava.play.notes.notekind.NoteKind;
import com.nova.fnfjava.play.notes.notekind.NoteKindManager;
import com.nova.fnfjava.play.notes.notestyle.NoteStyle;
import com.nova.fnfjava.play.song.Song;
import com.nova.fnfjava.play.stage.Stage;
import com.nova.fnfjava.text.FlxText;
import com.nova.fnfjava.ui.MusicBeatSubState;
import com.nova.fnfjava.ui.mainmenu.MainMenuState;
import com.nova.fnfjava.util.Constants;
import com.nova.fnfjava.util.StringTools;
import com.nova.fnfjava.util.WindowUtil;

import java.util.Objects;

public class PlayState extends MusicBeatSubState {
    public static PlayState instance;
    public static PlayStateParams lastParams;
    public Song currentSong;
    public String currentDifficulty = Constants.DEFAULT_DIFFICULTY;
    public String currentVariation = Constants.DEFAULT_VARIATION;
    public String currentInstrumental = "";
    public Stage currentStage = null;
    public boolean needsReset = false;
    public Timer vwooshTimer = new Timer();
    public int deathCounter = 0;
    public float health = Constants.HEALTH_STARTING;
    public int songScore = 0;
    public float startTimestamp = 0.0f;
    public float playbackRate = 1.0f;
    //public var cameraFollowPoint:FlxObject;
    //public var cameraFollowTween:Null<FlxTween>;
    //public var cameraZoomTween:Null<FlxTween>;
    //public var scrollSpeedTweens:Array<FlxTween> = [];
    //public var previousCameraFollowPoint:Null<FlxPoint>;
    public float currentCameraZoom = 1.0f;
    public float cameraBopMultiplier = 1.0f;
    public float stageZoom;
    public float defaultHUDCameraZoom = 1.0f * 1.0f;
    public float cameraBopIntensity = Constants.DEFAULT_BOP_INTENSITY;
    public float hudCameraZoomIntensity = 0.015f * 2.0f;
    public int cameraZoomRate = Constants.DEFAULT_ZOOM_RATE;
    public int cameraZoomRateOffset = Constants.DEFAULT_ZOOM_OFFSET;
    public boolean isInCountdown = false;
    public boolean isPracticeMode = false;
    public boolean isBotPlayMode = false;
    public boolean isPlayerDying = false;
    public boolean isMinimalMode = false;
    public boolean isInCutscene = false;
    public boolean disableKeys = false;
    public String previousDifficulty = Constants.DEFAULT_DIFFICULTY;
    //public Conversation currentConversation;
    //public Array<PreciseInputEvent> inputPressQueue = new Array<>();
    //var inputReleaseQueue:Array<PreciseInputEvent> = [];
    public boolean justUnpaused = false;
    public NoteStyle noteStyle;
    public Array<SongData.SongEventData> songEvents = new Array<>();
    public boolean mayPauseGame = true;
    public float healthLerp = Constants.HEALTH_STARTING;
    public float skipHeldTimer = 0f;
    public boolean overrideMusic = false;
    public boolean criticalFailure = false;
    public boolean startingSong = false;
    public boolean musicPausedBySubState = false;
    //public List<FlxTween> cameraTweensPausedBySubState = new List<FlxTween>();
    //public var soundsPausedBySubState:List<FlxSound> = new List<FlxSound>();
    public boolean initialized = false;
    public VoicesGroup vocals;

    // Discord RPC variables
    public String discordRPCAlbum = "";
    public String discordRPCIcon = "";
    public FlxText scoreText;
    //public FlxBar healthBar;
    public FunkinSprite healthBarBG;
    public HealthIcon iconP1;
    public HealthIcon iconP2;
    //public Strumline playerStrumline;
    //public Strumline opponentStrumline;
    //public FlxCamera camHUD;
    //public FlxCamera camGame;
    public boolean debugUnbindCameraZoom = false;
    //public FlxCamera camCutscene;
    //public FlxCamera camCutouts:FlxCamera;
    //public PopUpStuff comboPopUps;
    public boolean isSongEnd = false;
    public static final float RESYNC_THRESHOLD = 40;
    public static final float CONDUCTOR_DRIFT_THRESHOLD = 65;
    public static final float MUSIC_EASE_RATIO = 42;
    public boolean generatedMusic = false;
    public boolean skipEndingTransition = false;
    public static final Color BACKGROUND_COLOR = Color.BLACK;

    public PlayState(Main main, PlayStateParams params) {
        super(main);
        PlayStateParams paramsFinal;
        if (params != null) paramsFinal = params;
        else {
            Main.logger.setTag("PlayState").warn("PlayState constructor called with no parameters. Reusing previous parameters.");
            if (lastParams != null) paramsFinal = lastParams;
            else throw new IllegalArgumentException("PlayState constructor called with no available parameters.");
        }
        lastParams = params;

        // Apply parameters.
        if (params.targetSong != null) currentSong = params.targetSong;
        else throw new IllegalArgumentException("targetSong should not be null");

        if (params.targetDifficulty != null) currentDifficulty = params.targetDifficulty;
        previousDifficulty = currentDifficulty;
        if (params.targetVariation != null) currentVariation = params.targetVariation;
        //if (params.targetInstrumental != null) currentInstrumental = params.targetInstrumental;
/*        isPracticeMode = params.practiceMode ?? false;
        isBotPlayMode = params.botPlayMode ?? false;*/
        isMinimalMode = params.minimalMode != null ? params.minimalMode : false;
/*        startTimestamp = params.startTimestamp ?? 0.0f;
        playbackRate = params.playbackRate ?? 1.0f;*/
        overrideMusic = params.overrideMusic != null ? params.overrideMusic : false;
        //previousCameraFollowPoint = params.cameraFollowPoint;

        // Basic object initialization
        // TODO: Add something to toggle this on!
/*        if (false) {
            // Displays the camera follow point as a sprite for debug purposes.
            Image cameraFollowPoint = ImageUtil.createColored(8, 8,  Color.valueOf("#00FF00FF"));
            cameraFollowPoint.setVisible(false);
            cameraFollowPoint.setZIndex(1000000);
            this.cameraFollowPoint = cameraFollowPoint;
        } else {
            cameraFollowPoint = new FlxObject(0, 0);
        }*/

        //camGame = new FunkinCamera('playStateCamGame');
        //camHUD = new FlxCamera();
        //camCutscene = new FlxCamera();
        //camCutouts = new FlxCamera();

        Song.SongDifficulty currentChart = currentSong.getDifficulty(currentDifficulty, currentVariation);
        String noteStyleId = currentChart.noteStyle;
        NoteStyle nulNoteStyle = NoteStyleRegistry.instance.fetchEntry(noteStyleId != null ? noteStyleId : Constants.DEFAULT_NOTE_STYLE);
        if (nulNoteStyle == null) throw new IllegalArgumentException("Failed to retrieve both note style and default note style. This shouldn't happen!");
        noteStyle = nulNoteStyle;

        // Strumlines
        //playerStrumline = new Strumline(noteStyle, !isBotPlayMode, currentChart?.scrollSpeed);
        //opponentStrumline = new Strumline(noteStyle, false, currentChart?.scrollSpeed);

        // Healthbar
        //healthBarBG = FunkinSprite.create(0, 0, "healthBar");
        //healthBar = new FlxBar(0, 0, RIGHT_TO_LEFT, Std.int(healthBarBG.width - 8), Std.int(healthBarBG.height - 8), null, 0, 2);
        scoreText = new FlxText(0, 0, "");

        // Combo & Pop Up
        //comboPopUps = new PopUpStuff(noteStyle);
    }

    @Override
    public void show() {
        super.show();
        if (instance != null) Main.logger.setTag("PlayState").warn("PlayState instance already exists. This should not happen.");
        instance = this;

        if (!assertChartExists()) return;

        this.persistentUpdate = true;
        this.persistentDraw = true;

        if (!overrideMusic) {
            if (Main.sound.music != null) Main.sound.music.stop();
            getCurrentChart().cacheInst(currentInstrumental);
            getCurrentChart().cacheVocals();
        }

        Conductor.getInstance().forceBPM(null);

        if (getCurrentChart().offsets != null) Conductor.getInstance().instrumentalOffset = getCurrentChart().offsets.getInstrumentalOffset(currentInstrumental);

        Conductor.getInstance().mapTimeChanges(getCurrentChart().timeChanges);

        float pre = (Conductor.getInstance().getBeatLengthMs() * -5) + startTimestamp;

        Main.logger.setTag("PlayState").info("Attempting to start at " + pre);

        Conductor.getInstance().update(pre);

/*        initCameras();
        initHealthBar();
        if (!isMinimalMode) {
            initStage();
            initCharacters();
        } else initMinimalMode();
        initStrumlines();
        initPopups();*/

        initDiscord();

        generateSong();

        //resetCamera();

        //initPreciseInputs();

        //FlxG.worldBounds.set(0, 0, FlxG.width, FlxG.height);

        startingSong = true;

        if ((currentSong.id != null ? currentSong.id : "").toLowerCase().equals("winter-horrorland")) {
            // VanillaCutscenes will call startCountdown later.
            //VanillaCutscenes.playHorrorStartCutscene();
        } else {
            // Call a script event to start the countdown.
            // Songs with cutscenes should call event.cancel().
            // As long as they call `PlayState.instance.startCountdown()` later, the countdown will start.
            //startCountdown();
        }

        initialized = true;
    }

    @Override
    public void render(float delta) {
        if (criticalFailure) return;
        super.render(delta);

        /*updateHealthBar();
        updateScoreText();*/

        if (needsReset) {
            if (!assertChartExists()) return;
            prevScrollTargets = new Array<>();

            //var retryEvent = new SongRetryEvent(currentDifficulty);

            previousDifficulty = currentDifficulty;

            //dispatchEvent(retryEvent);

            //resetCamera();

            boolean fromDeathState = isPlayerDying;

            persistentUpdate = true;
            persistentDraw = true;

            startingSong = true;
            isPlayerDying = false;

            if (Main.sound.music != null) {
                Main.sound.music.pause();
                Main.sound.music.seekTo(startTimestamp);
                Main.sound.music.setPitch(playbackRate);
            }

            if (!overrideMusic && vocals != null) {
                vocals.stop();
                vocals = getCurrentChart().buildVocals(currentInstrumental);

                if (vocals.sounds.size == 0) Main.logger.setTag("PlayState").warn("No vocals found for this song.");
            }

            if (Main.sound.music != null) Main.sound.music.setVolume(1);

            if (vocals != null) {
/*                vocals.pause();
                vocals.time = startTimestamp - Conductor.getInstance().instrumentalOffset;

                vocals.volume = 1;
                vocals.playerVolume = 1;
                vocals.opponentVolume = 1;*/
            }

            currentStage.resetStage();

            if (!fromDeathState) {
                //playerStrumline.vwooshNotes();
                //opponentStrumline.vwooshNotes();
            }

            //playerStrumline.clean();
            //opponentStrumline.clean();

            regenNoteData();

            cameraBopIntensity = Constants.DEFAULT_BOP_INTENSITY;
            hudCameraZoomIntensity = (cameraBopIntensity - 1.0f) * 2.0f;
            cameraZoomRate = Constants.DEFAULT_ZOOM_RATE;

            health = Constants.HEALTH_STARTING;
            songScore = 0;
            Highscore.tallies.combo = 0;

            float vwooshDelay = 0.5f;
            Conductor.getInstance().update(-vwooshDelay * 1000 + startTimestamp + Conductor.getInstance().getBeatLengthMs() * -5);

            vwooshTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
/*                    if (playerStrumline.notes.length == 0) playerStrumline.updateNotes();
                    if (opponentStrumline.notes.length == 0) opponentStrumline.updateNotes();
                    playerStrumline.vwooshInNotes();
                    opponentStrumline.vwooshInNotes();*/
                    Countdown.performCountdown();
                }
            }, vwooshDelay);

            Countdown.stopCountdown();

            currentStage.getBoyfriend().initHealthIcon(false);
            currentStage.getDad().initHealthIcon(true);

            needsReset = false;
        }

        if (startingSong) {
            if (isInCountdown) {
                Conductor.getInstance().update(Conductor.getInstance().songPosition + delta * 1000, false);
                if (Conductor.getInstance().songPosition >= (startTimestamp + Conductor.getInstance().getCombinedOffset())) {
                    Main.logger.setTag("PlayState").info("started song at " + Conductor.getInstance().songPosition);
                    //startSong();
                }
            }
        } else {
            /*if (Constants.EXT_SOUND == "mp3") Conductor.getInstance().formatOffset = Constants.MP3_DELAY_MS;
            else Conductor.getInstance().formatOffset = 0.0f;*/

            if (Main.sound.music.isPlaying()) {
/*                final float audioDiff = Math.round(Math.abs(Main.sound.music.time - (Conductor.getInstance().songPosition - Conductor.getInstance().getCombinedOffset())));
                if (audioDiff <= CONDUCTOR_DRIFT_THRESHOLD) {
                    final float easeRatio = (float) (1.0f - Math.exp(-(MUSIC_EASE_RATIO * playbackRate) * delta));
                    Conductor.getInstance().update(Math.lerp(Conductor.getInstance().songPosition, Main.sound.music.time + Conductor.getInstance().getCombinedOffset(), easeRatio), false);
                } else {
                    Main.logger.setTag("PlayState").warn("Normal Conductor Update!! are you lagging?");
                    Conductor.getInstance().update();
                }*/
                Conductor.getInstance().update();
            }
        }

        boolean pauseButtonCheck = false;
        boolean androidPause = false;

        if ((Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || androidPause || pauseButtonCheck)) pause();

        if (health > Constants.HEALTH_MAX) health = Constants.HEALTH_MAX;
        if (health < Constants.HEALTH_MIN) health = Constants.HEALTH_MIN;
    }

    public Array<Object> prevScrollTargets = new Array<>();

    public boolean assertChartExists() {
        if (currentSong == null || getCurrentChart() == null || getCurrentChart().notes == null) {
            criticalFailure = true;
            String message = "There was a critical error. Click OK to return to the main menu.";
            if (currentSong == null) message = "'There was a critical error loading this song\'s chart. Click OK to return to the main menu.'";
            else if (currentDifficulty == null) message = "There was a critical error selecting a difficulty for this song. Click OK to return to the main menu.";
            else if (getCurrentChart() == null) message = "There was a critical error retrieving data for this song on \"" + currentDifficulty + "\" difficulty with variation \"" + currentVariation + "\". Click OK to return to the main menu.";
            else if (getCurrentChart().notes == null) message = "There was a critical error retrieving note data for this song on \"" + currentDifficulty + "\" difficulty with variation \"" + currentVariation + "\". Click OK to return to the main menu.";

            // Display a popup. This blocks the application until the user clicks OK.
            WindowUtil.showError("Error loading PlayState", message);

            // Force the user back to the main menu.
            if (getIsSubState()) close();
            else {
                if (currentStage != null) currentStage.remove();
                main.switchState(new MainMenuState(main));
            }
            return false;
        }
        return true;
    }

    public void initDiscord() {
        DiscordClient.getInstance().setPresence(
            new DiscordClient.DiscordPresenceParams(buildDiscordRPCState(), buildDiscordRPCDetails(), discordRPCAlbum, discordRPCIcon)
        );
    }

    public String buildDiscordRPCDetails() {
        if (PlayStatePlaylist.isStoryMode) return "Story Mode: " + PlayStatePlaylist.campaignTitle;
        else {
            /*if (isChartingMode) return "Chart Editor [Playtest]";
            else */if (isPracticeMode) return "Freeplay [Practice]";
            else if (isBotPlayMode) return "Freeplay [Bot Play]";
            else return "Freeplay";
        }
    }

    public String buildDiscordRPCState() {
        if (getCurrentChart() == null) Main.logger.setTag("PlayState").warn("Difficulty data for RPC is null.");
        var discordRPCDifficulty = PlayState.instance.currentDifficulty != null ? StringTools.toTitleCase(PlayState.instance.currentDifficulty.replace("-", " ")) : "???";
        return getCurrentChart().songName != null ? getCurrentChart().songName : "???" + "[" + discordRPCDifficulty + "]";
    }

    public void generateSong() {
        if (getCurrentChart() == null) throw new IllegalArgumentException("Song difficulty could not be loaded.");
        if (!overrideMusic) {
            // Stop the vocals if they already exist.
/*            vocals.stop();
            vocals = getCurrentChart().buildVocals(currentInstrumental);

            if (vocals.sounds.size == 0) Main.logger.setTag("PlayState").warn("No vocals found for this song.");*/
        }

        regenNoteData();

/*        var event:ScriptEvent = new ScriptEvent(CREATE, false);
        ScriptEventDispatcher.callEvent(currentSong, event);*/

        generatedMusic = true;
    }

    public void regenNoteData(float startTime) {
        if (getCurrentChart() == null) {
            Main.logger.setTag("PlayState").warn("Cannot regenerate note data for null chart");
            return;
        }

        Highscore.tallies.combo = 0;
        Highscore.tallies = new Highscore.Tallies();

        //var event:SongLoadScriptEvent = new SongLoadScriptEvent(currentChart.song.id, currentChart.difficulty, currentChart.notes.copy(), currentChart.getEvents());

        //dispatchEvent(event);

        Array<SongData.SongNoteData> builtNoteData = getCurrentChart().notes;
        Array<SongData.SongEventData> builtEventData = getCurrentChart().events;

        songEvents = builtEventData;
        SongEventRegistry.resetEvents(songEvents);

        Array<SongData.SongNoteData> playerNoteData = new Array<>();
        Array<SongData.SongNoteData> opponentNoteData = new Array<>();

        for (SongData.SongNoteData songNote : builtNoteData) {
            float strumTime = songNote.time;
            if (strumTime < startTime) continue;

            var scoreable = true;
            if (songNote.kind != null) {
                NoteKind noteKind = NoteKindManager.getNoteKind(songNote.kind != null ? songNote.kind  : "");
                if (noteKind != null) scoreable = noteKind.scoreable;
            }

            int noteData = songNote.getDirection();
            boolean playerNote = true;
            if (noteData > 3) playerNote = false;

            switch (songNote.getStrumlineIndex()) {
                case 0:
                    playerNoteData.add(songNote);
                    // increment totalNotes for total possible notes able to be hit by the player
                    if (scoreable) Highscore.tallies.totalNotes++;
                case 1:
                    opponentNoteData.add(songNote);
            }
        }

        //playerStrumline.applyNoteData(playerNoteData);
        //opponentStrumline.applyNoteData(opponentNoteData);
    }

    public void regenNoteData() {
        regenNoteData(0);
    }

    // Getters/Setters
    public float getStageZoom() {
        if (currentStage != null) return currentStage.getCamZoom();
        else return 1.0f * 1.05f;
    }

    public boolean getIsSubState() {
        return this.parentState != null;
    }

/*    public boolean getIsChartingMode() {
        return this.parentState != null && parentState instanceof ChartEditorState;
    }*/

    public boolean getIsGamePaused() {
        return this.subState != null;
    }

/*    public boolean getIsExitingViaPauseMenu() {
        if (this.subState == null) return false;
        if (!(this.subState instanceof PauseSubState)) return false;

        PauseSubState pauseSubState = this.subState;
        return !pauseSubState.allowInput;
    }*/

    public Song.SongDifficulty getCurrentChart() {
        if (currentSong == null || currentDifficulty == null) return null;
        return currentSong.getDifficulty(currentDifficulty, currentVariation);
    }

    public String getCurrentStageId() {
        String stage = getCurrentChart().stage != null ? getCurrentChart().stage : "";
        return Objects.equals(stage, "") ? Constants.DEFAULT_STAGE : stage;
    }

    public float get_currentSongLengthMs() {
        return Main.sound.music.getLength();
    }

    public record PlayStateParams(Song targetSong, String targetDifficulty, String targetVariation, Boolean overrideMusic, Boolean minimalMode) {
        public PlayStateParams {}

        public PlayStateParams(Song targetSong, String targetDifficulty, String targetVariation) {
            this(targetSong, targetDifficulty, targetVariation, false, false);
        }
    }
}
