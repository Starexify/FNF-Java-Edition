package com.nova.fnfjava.ui.transition;

import com.badlogic.gdx.Screen;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.data.stage.StageRegistry;
import com.nova.fnfjava.play.PlayState;
import com.nova.fnfjava.play.song.Song;
import com.nova.fnfjava.play.stage.Stage;
import com.nova.fnfjava.ui.MusicBeatState;
import com.nova.fnfjava.util.Constants;

import java.util.function.Consumer;
import java.util.function.Function;
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
    public static void loadPlayState(PlayState.PlayStateParams params, boolean shouldStopMusic, boolean asSubState, Consumer<PlayState> onConstruct) {
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

        if (!params.overrideMusic()) {
            params.targetSong().cacheCharts(true);
        }

        Main.logger.setTag("LoadingState").info(daChart + "\n" + daStage);
    }

    public static void loadPlayState(PlayState.PlayStateParams params, boolean shouldStopMusic) {
        loadPlayState(params, shouldStopMusic, false, null);
    }
}
