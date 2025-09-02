package com.nova.fnfjava.data.song;

import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

public class SongDataUtils {
    public static Array<SongData.SongTimeChange> sortTimeChanges(Array<SongData.SongTimeChange> timeChanges) {
        return sortTimeChanges(timeChanges, false);
    }

    public static Array<SongData.SongTimeChange> sortTimeChanges(Array<SongData.SongTimeChange> timeChanges, boolean desc) {
        // TODO: Modifies the array in place. Is this okay?
        timeChanges.sort(new Comparator<SongData.SongTimeChange>() {
            @Override
            public int compare(SongData.SongTimeChange a, SongData.SongTimeChange b) {
                if (desc) return Float.compare(b.timeStamp, a.timeStamp);
                else return Float.compare(a.timeStamp, b.timeStamp);
            }
        });
        return timeChanges;
    }
}
