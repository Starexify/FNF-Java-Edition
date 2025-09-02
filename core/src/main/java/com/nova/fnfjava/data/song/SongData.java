package com.nova.fnfjava.data.song;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.nova.fnfjava.util.Constants;

public class SongData {
    public class SongMetadata {
        public String songName;
        public String artist;
        public String charter;
        public float bpm;
        public String difficulty;

        public SongMetadata() {
        }

        public SongMetadata(String songName, String artist, float bpm) {
            this.songName = songName;
            this.artist = artist;
            this.bpm = bpm;
        }
    }

    public static class SongMusicData {
        public static final String DEFAULT_GENERATEDBY = "Friday Night Funkin': Java Edition";

        public String songName;
        public String artist;
        public String charter;
        public Integer divisions;
        public Boolean looped;
        public String generatedBy;
        public SongTimeFormat timeFormat;
        public Array<SongTimeChange> timeChanges;

        public transient String variation;

        public SongMusicData(String songName, String artist) {
            this(songName, artist, null, Constants.DEFAULT_VARIATION);
        }

        public SongMusicData(String songName, String artist, String charter, String variation) {
            this.songName = songName != null ? songName : "Unknown";
            this.artist = artist != null ? artist : "Unknown";
            this.charter = charter;
            this.timeFormat = SongTimeFormat.MILLISECONDS;
            this.divisions = null;
            this.timeChanges = new Array<>();
            this.timeChanges.add(new SongTimeChange.Builder(0, 100).build());
            this.looped = false;
            this.generatedBy = DEFAULT_GENERATEDBY;
            this.variation = variation != null ? variation : Constants.DEFAULT_VARIATION;
        }

        public void toJson(Json json) {
            json.writeObjectStart();
            json.writeValue("songName", songName);
            json.writeValue("artist", artist);
            json.writeValue("divisions", divisions);
            json.writeValue("looped", looped);
            json.writeValue("generatedBy", generatedBy);
            json.writeValue("timeFormat", timeFormat);
            json.writeValue("timeChanges", timeChanges);
            json.writeObjectEnd();
        }

        public static SongMusicData fromJson(JsonValue jsonData) {
            String songName = jsonData.getString("songName", "Unknown");
            String artist = jsonData.getString("artist", "Unknown");
            String charter = jsonData.getString("charter", null);
            String variation = Constants.DEFAULT_VARIATION;

            SongMusicData data = new SongMusicData(songName, artist, charter, variation);
            data.divisions = jsonData.has("divisions") ? jsonData.getInt("divisions") : null;
            data.looped = jsonData.getBoolean("looped", false);
            data.generatedBy = jsonData.getString("generatedBy", DEFAULT_GENERATEDBY);

            String timeFormatStr = jsonData.getString("timeFormat", "ms");
            data.timeFormat = SongTimeFormat.fromString(timeFormatStr);

            if (jsonData.has("timeChanges")) {
                JsonValue timeChangesJson = jsonData.get("timeChanges");
                if (timeChangesJson.isArray()) {
                    data.timeChanges.clear(); // Clear the default one added in constructor
                    for (JsonValue timeChangeJson : timeChangesJson) {
                        SongTimeChange timeChange = SongTimeChange.fromJson(timeChangeJson);
                        data.timeChanges.add(timeChange);
                    }
                }
            }

            return data;
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

    public static class SongTimeChange {
        public static final Array<Integer> DEFAULT_BEAT_TUPLETS = new Array<Integer>(new Integer[]{4, 4, 4, 4});
        public static final SongTimeChange DEFAULT_SONGTIMECHANGE = new Builder(0, 100).build();
        public static final Array<SongTimeChange> DEFAULT_SONGTIMECHANGES = new Array<SongTimeChange>(new SongTimeChange[]{DEFAULT_SONGTIMECHANGE});

        public float timeStamp;
        public Float beatTime;
        public float bpm;
        public int timeSignatureNum;
        public int timeSignatureDen;
        public Array<Integer> beatTuplets;


        public SongTimeChange(Builder builder) {
            this.timeStamp = builder.timeStamp;
            this.bpm = builder.bpm;

            this.timeSignatureNum = builder.timeSignatureNum;
            this.timeSignatureDen = builder.timeSignatureDen;

            this.beatTime = builder.beatTime;
            this.beatTuplets = new Array<Integer>(builder.beatTuplets);
        }

        public static class Builder {
            public float timeStamp;
            public float bpm;
            public Float beatTime = null;
            public int timeSignatureNum = 4;
            public int timeSignatureDen = 4;
            public Array<Integer> beatTuplets = new Array<Integer>(DEFAULT_BEAT_TUPLETS);

            public Builder(float timeStamp, float bpm) {
                this.timeStamp = timeStamp;
                this.bpm = bpm;
            }

            public SongTimeChange build() {
                return new SongTimeChange(this);
            }
        }

        public void toJson(Json json) {
            json.writeObjectStart();
            json.writeValue("t", timeStamp);
            json.writeValue("bpm", bpm);
            json.writeValue("n", timeSignatureNum);
            json.writeValue("d", timeSignatureDen);
            if (beatTime != null) json.writeValue("b", beatTime);
            if (beatTuplets != null) json.writeValue("bt", beatTuplets);
            json.writeObjectEnd();
        }

        public static SongTimeChange fromJson(JsonValue jsonData) {
            float timeStamp = jsonData.getFloat("t", 0);
            float bpm = jsonData.getFloat("bpm", 100);
            int n = jsonData.getInt("n", 4);
            int d = jsonData.getInt("d", 4);

            Builder builder = new Builder(timeStamp, bpm);
            builder.timeSignatureNum = n;
            builder.timeSignatureDen = d;

            if (jsonData.has("b")) builder.beatTime = jsonData.getFloat("b");

            if (jsonData.has("bt")) {
                JsonValue btValue = jsonData.get("bt");
                if (btValue.isArray()) {
                    Array<Integer> tuplets = new Array<>();
                    for (JsonValue item : btValue) {
                        tuplets.add(item.asInt());
                    }
                    builder.beatTuplets = tuplets;
                }
            }

            return builder.build();
        }
    }
}

enum SongTimeFormat {
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

