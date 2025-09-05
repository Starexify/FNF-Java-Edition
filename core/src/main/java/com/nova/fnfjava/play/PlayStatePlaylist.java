package com.nova.fnfjava.play;

import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.util.Constants;

public class PlayStatePlaylist {
    public static boolean isStoryMode = false;

    public static Array<String> playlistSongIds = new Array<>();

    public static int campaignScore = 0;

    public static String campaignTitle = "UNKNOWN";

    public static String campaignId = null;

    public static String campaignDifficulty = Constants.DEFAULT_DIFFICULTY;

    public static void reset() {
        isStoryMode = false;
        playlistSongIds = new Array<>();
        campaignScore = 0;
        campaignTitle = "UNKNOWN";
        campaignId = null;
        campaignDifficulty = Constants.DEFAULT_DIFFICULTY;
    }
}
