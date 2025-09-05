package com.nova.fnfjava.ui.transition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.play.PlayState;
import com.nova.fnfjava.play.Song;
import com.nova.fnfjava.ui.MusicBeatState;
import com.nova.fnfjava.util.Constants;

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

    @Override
    public void show() {
        super.show();
    }

    public static void loadPlayState(PlayState.PlayStateParams params, boolean shouldStopMusic, boolean asSubState) {
        Gdx.app.log("LoadingState", params.toString());

        String targetDifficulty = (params.targetDifficulty() != null) ? params.targetDifficulty() : Constants.DEFAULT_DIFFICULTY;
        String targetVariation = (params.targetVariation() != null) ? params.targetVariation() : Constants.DEFAULT_VARIATION;

        Song.SongDifficulty daChart = params.targetSong().getDifficulty(targetDifficulty, targetVariation);

        Gdx.app.log("LoadingState", daChart.toString());
    }

    public static void loadPlayState(PlayState.PlayStateParams params, boolean shouldStopMusic) {
        loadPlayState(params, shouldStopMusic, false);
    }
}
