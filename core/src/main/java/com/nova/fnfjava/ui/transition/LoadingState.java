package com.nova.fnfjava.ui.transition;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.Assets;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.data.notestyle.NoteStyleRegistry;
import com.nova.fnfjava.data.stage.StageRegistry;
import com.nova.fnfjava.play.PlayState;
import com.nova.fnfjava.play.notes.notestyle.NoteStyle;
import com.nova.fnfjava.play.song.Song;
import com.nova.fnfjava.play.stage.Stage;
import com.nova.fnfjava.ui.MusicBeatState;
import com.nova.fnfjava.util.Constants;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LoadingState extends MusicBeatState {
    public static float MIN_TIME = 1.0f;

    public Screen target;
    public PlayState.PlayStateParams playParams;
    public boolean stopMusic = false;
    public boolean danceLeft = false;

    public LoadingState(Main main, Screen target, boolean stopMusic, PlayState.PlayStateParams playParams) {
        super(main);
        this.target = target;
        this.playParams = playParams;
        this.stopMusic = stopMusic;
    }

    public static String stageDirectory = "shared";
    public static void loadPlayState(MusicBeatState parentState, PlayState.PlayStateParams params, boolean shouldStopMusic, boolean asSubState, Consumer<PlayState> onConstruct) {
        String targetDifficulty = params.targetDifficulty() != null ? params.targetDifficulty() : Constants.DEFAULT_DIFFICULTY;
        String targetVariation = params.targetVariation() != null ? params.targetVariation() : Constants.DEFAULT_VARIATION;
        Song.SongDifficulty daChart = params.targetSong().getDifficulty(targetDifficulty, targetVariation);

        String targetStage = daChart.stage != null ? daChart.stage : Constants.DEFAULT_STAGE;
        Stage daStage = StageRegistry.instance.fetchEntry(targetStage);

        stageDirectory = daStage.getData().directory != null ? daStage.getData().directory : "shared";

        Supplier<PlayState> playStateCtor = () -> new PlayState(main, params);

        if (onConstruct != null) {
            Supplier<PlayState> originalCtor = playStateCtor;
            playStateCtor = () -> {
                PlayState result = originalCtor.get();
                onConstruct.accept(result);
                return result;
            };
        }

        if (shouldStopMusic && Main.sound.music != null) {
            Main.sound.music.dispose();
            Main.sound.music = null;
        }

        if (!params.overrideMusic()) params.targetSong().cacheCharts(true);

        boolean shouldPreloadLevelAssets = !params.minimalMode();
        if (shouldPreloadLevelAssets) {
            preloadLevelAssets();

            // Cache the note style.
            var songDifficulty = params.targetSong().getDifficulty(params.targetDifficulty(), params.targetVariation());
            if (songDifficulty != null) {
                NoteStyle noteStyle = NoteStyleRegistry.instance.fetchEntry(songDifficulty.noteStyle != null ? songDifficulty.noteStyle : "");
                if (noteStyle == null) noteStyle = NoteStyleRegistry.instance.fetchDefault();
                Assets.cacheNoteStyle(noteStyle);
            }

            if (Objects.equals(params.targetSong().songName, "2hot")) {
                Array<String> spritesToCache = new Array<>(new String[]{
                    "wked1_cutscene_1_can",
                    "spraypaintExplosionEZ",
                    "SpraypaintExplosion",
                    "CanImpactParticle",
                    "spraycanAtlas/spritemap1"
                });

                Array<String> soundsToCache = new Array<>(new String[]{
                    "Darnell_Lighter",
                    "fuse_burning",
                    "Gun_Prep",
                    "Kick_Can_FORWARD",
                    "Kick_Can_UP",
                    "Lightning1",
                    "Lightning2",
                    "Lightning3",
                    "Pico_Bonk",
                    "Shoot_1",
                    "shot1",
                    "shot2",
                    "shot3",
                    "shot4"
                });

                for (String sprite : spritesToCache) {
                    Main.logger.setTag("LoadingState").info("Queueing " + sprite + " to preload.");
                    var path = Paths.image(sprite, "weekend1");
                    Assets.cacheTexture(path);

                    if (path.endsWith("spritemap1.png")) {
                        Main.logger.setTag("LoadingState").info("Preloading FlxAnimate asset: " + path);
                        //Assets.getBitmapData(path, true);
                    }
                }

                for (String sound : soundsToCache) {
                    Main.logger.setTag("LoadingState").info("Queueing " + sound + " to preload.");
                    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                        var path = Paths.sound(sound, "weekend1");
                        Assets.cacheSound(path);
                        return path + " successfully loaded.";
                    });
                    future.thenAccept(result -> {
                        Main.logger.setTag("LoadingState").info(result);
                        // Handle the result
                    });
                }
            }

            if (asSubState && parentState != null) parentState.openSubState(playStateCtor.get());
            else {
                Listener<Void> preStateListener = new Listener<Void>() {
                    @Override
                    public void receive(Signal<Void> signal, Void object) {
                        Assets.clearFreeplay();
                        Assets.purgeCache(true);
                        // Remove itself after running
                        signal.remove(this);
                    }
                };
                Main.signals.preStateSwitch.add(preStateListener);
                main.switchState(playStateCtor.get());
            }
        }
    }

    public static void preloadLevelAssets() {

    }

    public static void loadPlayState(MusicBeatState parentState, PlayState.PlayStateParams params, boolean shouldStopMusic) {
        loadPlayState(parentState, params, shouldStopMusic, false, null);
    }
}
