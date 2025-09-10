package com.nova.fnfjava.data.event;

import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.data.song.SongData;

public class SongEventRegistry {

    public static void resetEvents(Array<SongData.SongEventData> events) {
        for (SongData.SongEventData event : events) event.activated = false;
    }
}
