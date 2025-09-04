package com.nova.fnfjava.data.song;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.util.Constants;

public class SongData {
    public static class SongMetadata {
        // Required
        public String songName = "Unknown";
        public String artist = "Unknown";
        public SongPlayData playData = new SongPlayData();

        // Optional
        public String charter = null;
        public Integer divisions = 96;
        public Boolean looped = false;
        //public SongOffsets offsets;
        public String generatedBy = SongRegistry.DEFAULT_GENERATEDBY;
        public SongTimeFormat timeFormat = SongTimeFormat.MILLISECONDS;
        public Array<SongTimeChange> timeChanges = new Array<>(new SongTimeChange[]{new SongTimeChange(0, 100)});
        public transient String variation;

        public SongMetadata() {}

        public SongMetadata(String songName, String artist, String charter, String variation) {
            this.songName = songName != null ? songName : "Unknown";
            this.artist = artist != null ? artist : "Unknown";
            this.charter = charter;
            this.timeFormat = SongTimeFormat.MILLISECONDS;
            this.divisions = 96;
            this.looped = false;
            this.playData = new SongPlayData();
            this.timeChanges = new Array<>(new SongTimeChange[]{new SongTimeChange(0, 100)});
            this.generatedBy = SongRegistry.DEFAULT_GENERATEDBY;
            this.variation = variation != null ? variation : Constants.DEFAULT_VARIATION;
        }

        @Override
        public String toString() {
            return "SongMetadata{" +
                "songName='" + songName + '\'' +
                ", artist='" + artist + '\'' +
                ", playData=" + playData +
                ", charter='" + charter + '\'' +
                ", divisions=" + divisions +
                ", looped=" + looped +
                ", generatedBy='" + generatedBy + '\'' +
                ", timeFormat=" + timeFormat +
                ", timeChanges=" + timeChanges +
                ", variation='" + variation + '\'' +
                '}';
        }
    }

    public enum SongTimeFormat {
        TICKS("ticks"),
        FLOAT("float"),
        MILLISECONDS("ms");

        public final String id;

        SongTimeFormat(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static SongTimeFormat fromString(String id) {
            for (SongTimeFormat format : values()) if (format.getId().equals(id)) return format;
            return MILLISECONDS;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    public static class SongTimeChange {
        public static final Array<Integer> DEFAULT_BEAT_TUPLETS = new Array<Integer>(new Integer[]{4, 4, 4, 4});
        public static final SongTimeChange DEFAULT_SONGTIMECHANGE = new SongTimeChange(0, 100);
        public static final Array<SongTimeChange> DEFAULT_SONGTIMECHANGES = new Array<SongTimeChange>(new SongTimeChange[]{DEFAULT_SONGTIMECHANGE});

        // Required
        public float timeStamp;
        public float bpm;

        // Optional
        public Float beatTime = null;
        public int timeSignatureNum = 4;
        public int timeSignatureDen = 4;
        public Array<Integer> beatTuplets = new Array<>(DEFAULT_BEAT_TUPLETS);

        public SongTimeChange() {}

        public SongTimeChange(float timeStamp, float bpm) {
            this.timeStamp = timeStamp;
            this.bpm = bpm;
        }

        public SongTimeChange(float timeStamp, float bpm, int timeSignatureNum, int timeSignatureDen, Float beatTime, Array<Integer> beatTuplets) {
            this.timeStamp = timeStamp;
            this.bpm = bpm;
            this.timeSignatureNum = timeSignatureNum;
            this.timeSignatureDen = timeSignatureDen;
            this.beatTime = beatTime;
            this.beatTuplets = (beatTuplets != null) ? new Array<>(beatTuplets) : new Array<>(DEFAULT_BEAT_TUPLETS);
        }
    }

    public static class SongMusicData {
        // Required fields
        public String songName = "Unknown";
        public String artist = "Unknown";

        // Optional fields
        public Integer divisions = 96;
        public Boolean looped = false;

        public String generatedBy = SongRegistry.DEFAULT_GENERATEDBY;

        public SongTimeFormat timeFormat = SongTimeFormat.MILLISECONDS;
        public Array<SongTimeChange> timeChanges = new Array<>(new SongTimeChange[]{new SongTimeChange(0, 100)});

        public transient String variation;

        public SongMusicData() {
            this("Unknown", "Unknown", Constants.DEFAULT_VARIATION);
        }

        public SongMusicData(String songName, String artist, String variation) {
            this.songName = songName != null ? songName : "Unknown";
            this.artist = artist != null ? artist : "Unknown";
            this.variation = variation != null ? variation : Constants.DEFAULT_VARIATION;
        }

        @Override
        public String toString() {
            return "SongMusicData{" +
                "songName='" + songName + '\'' +
                ", artist='" + artist + '\'' +
                ", divisions=" + divisions +
                ", looped=" + looped +
                ", generatedBy='" + generatedBy + '\'' +
                ", timeFormat=" + timeFormat +
                ", timeChanges=" + timeChanges +
                ", variation='" + variation + '\'' +
                '}';
        }
    }

    public static class SongPlayData {
        // Required
        public Array<String> difficulties = new Array<>();
        public String stage;
        public String noteStyle;

        // Optional
        public Array<String> songVariations = new Array<>();
        //public SongCharacterData characters;
        public ObjectMap<String, Integer> ratings = new ObjectMap<String, Integer>() {{
            put("normal", 0);
        }};
        public String album = null;
        public String stickerPack = null;
        public int previewStart = 0;
        public int previewEnd = 15000;

        public SongPlayData() {}

        @Override
        public String toString() {
            return "SongPlayData{" +
                "songVariations=" + songVariations +
                ", difficulties=" + difficulties +
                ", stage='" + stage + '\'' +
                ", noteStyle='" + noteStyle + '\'' +
                ", ratings=" + ratings +
                ", album='" + album + '\'' +
                ", stickerPack='" + stickerPack + '\'' +
                ", previewStart=" + previewStart +
                ", previewEnd=" + previewEnd +
                '}';
        }
    }
}

