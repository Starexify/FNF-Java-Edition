package com.nova.fnfjava.data.song;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.nova.fnfjava.util.Constants;

public class SongMusicData {
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
