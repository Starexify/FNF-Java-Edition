package com.nova.fnfjava.play;

import com.nova.fnfjava.Main;
import com.nova.fnfjava.play.song.Song;
import com.nova.fnfjava.ui.MusicBeatSubState;

public class PlayState extends MusicBeatSubState {
    public static PlayState instance;

    public PlayState(Main main, PlayStateParams params) {
        super(main);
    }

    public record PlayStateParams(Song targetSong, String targetDifficulty, String targetVariation, boolean overrideMusic) {
        public PlayStateParams {}

        public PlayStateParams(Song targetSong, String targetDifficulty, String targetVariation) {
            this(targetSong, targetDifficulty, targetVariation, false);
        }
    }
}
