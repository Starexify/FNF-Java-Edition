package com.nova.fnfjava.util;

import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.data.song.SongData;

public class Constants {
    public static final String TITLE = "Friday Night Funkin': Java Edition";

    public static String VERSION = "v0.0.1b";

    public static final String DEFAULT_DIFFICULTY = "normal";

    public static final Array<String> DEFAULT_DIFFICULTY_LIST = new Array<>(new String[]{"easy", "normal", "hard"});

    public static final String DEFAULT_STAGE = "mainStage";

    public static final String DEFAULT_VARIATION = "default";

    public static final Array<String> DEFAULT_VARIATION_LIST = new Array<>(new String[]{"default", "erect", "pico", "bf"});

    public static final String DEFAULT_STICKER_PACK = "default";

    public static final float DEFAULT_BOP_INTENSITY = 1.015f;

    public static final int DEFAULT_ZOOM_RATE = 4;

    public static final int DEFAULT_ZOOM_OFFSET = 0;

    public static final float DEFAULT_BPM = 100.0f;

    public static final String DEFAULT_SONGNAME = "Unknown";

    public static final String DEFAULT_ARTIST = "Unknown";

    public static final String DEFAULT_CHARTER = "Unknown";

    public static final String DEFAULT_NOTE_STYLE = "funkin";

    public static final String DEFAULT_FREEPLAY_STYLE = "bf";

    public static final SongData.SongTimeFormat DEFAULT_TIMEFORMAT = SongData.SongTimeFormat.MILLISECONDS;

    public static final float DEFAULT_SCROLLSPEED = 1.0f;

    public static final int DEFAULT_TIME_SIGNATURE_NUM = 4;

    public static final int DEFAULT_TIME_SIGNATURE_DEN = 4;

    public static final int SECS_PER_MIN = 60;

    public static final int MS_PER_SEC = 1000;

    public static final int STEPS_PER_BEAT = 4;

    public static final float TITLE_ATTRACT_DELAY = 37.5f;

    public static final float HEALTH_MAX = 2.0f;

    public static final float HEALTH_STARTING = HEALTH_MAX / 2.0f;

    public static final String EXT_SOUND = "ogg";

    public static final String EXT_VIDEO = "webm"; // mp4 not fully supported by gdx-video so we use webm videos if exists

    public static final String LIBRARY_SEPARATOR = ":";
}
