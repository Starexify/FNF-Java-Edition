package com.nova.fnfjava.data.song;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.nova.fnfjava.data.BaseRegistry;
import com.nova.fnfjava.data.JsonFile;
import com.nova.fnfjava.play.Song;
import com.nova.fnfjava.util.Constants;

public class SongRegistry extends BaseRegistry<Song, SongMetadata, SongEntryParams> {
    public static SongRegistry instance;

    public final Json parser;
    public final Array<String> parseErrors;

    public SongRegistry() {
        super("SONG", "songs");
        parser = new Json();
        parseErrors = new Array<>();

        setupParser();
    }

    public static void initialize() {
        if (instance == null) instance = new SongRegistry();
    }

    public void setupParser() {
        parser.setIgnoreUnknownFields(true);

        parser.setSerializer(SongTimeFormat.class, new Json.Serializer<SongTimeFormat>() {
            @Override
            public void write(Json json, SongTimeFormat object, Class knownType) {
                json.writeValue(object.getId());
            }

            @Override
            public SongTimeFormat read(Json json, JsonValue jsonData, Class type) {
                return SongTimeFormat.fromString(jsonData.asString());
            }
        });

        parser.setSerializer(SongTimeChange.class, new Json.Serializer<SongTimeChange>() {
            @Override
            public void write(Json json, SongTimeChange object, Class knownType) {
                object.toJson(json);
            }

            @Override
            public SongTimeChange read(Json json, JsonValue jsonData, Class type) {
                return SongTimeChange.fromJson(jsonData);
            }
        });

        parser.setSerializer(SongMusicData.class, new Json.Serializer<SongMusicData>() {
            @Override
            public void write(Json json, SongMusicData object, Class knownType) {
                object.toJson(json);
            }

            @Override
            public SongMusicData read(Json json, JsonValue jsonData, Class type) {
                return SongMusicData.fromJson(jsonData);
            }
        });
    }

    public SongMusicData parseMusicData(String id, String variation) {
        parseErrors.clear();

        JsonFile fileResult = loadMusicDataFile(id, variation);
        if (fileResult == null) return null;
        try {
            SongMusicData musicData = parser.fromJson(SongMusicData.class, fileResult.contents());
            if (musicData == null) {
                parseErrors.add("Failed to parse JSON from file: " + fileResult.fileName());
                printErrors(id);
                return null;
            }
            musicData.variation = variation;

            return musicData;
        } catch (Exception e) {
            parseErrors.add("JSON parsing error: " + e.getMessage());
            printErrors(id);
            return null;
        }
    }

    public SongMusicData parseMusicData(String id) {
        return this.parseMusicData(id, Constants.DEFAULT_VARIATION);
    }

    public JsonFile loadMusicDataFile(String id, String variation) {
        if (variation == null) variation = Constants.DEFAULT_VARIATION;
        String variationSuffix = variation.equals(Constants.DEFAULT_VARIATION) ? "" : "-" + variation;
        String entryFilePath = "music/" + id + "/" + id + "-metadata" + variationSuffix + ".json";
        try {
            FileHandle file = Gdx.files.internal(entryFilePath);
            if (!file.exists()) return null;
            String rawJson = file.readString("UTF-8");
            if (rawJson == null) return null;
            rawJson = rawJson.trim();
            return new JsonFile(entryFilePath, rawJson);
        } catch (Exception e) {
            Gdx.app.error("SongRegistry", "Error reading file " + entryFilePath + ": " + e.getMessage());
            return null;
        }
    }

    public void printErrors(String id) {
        if (parseErrors.size > 0) {
            Gdx.app.error("SongRegistry", "Errors parsing music data for ID: " + id);
            for (String error : parseErrors) Gdx.app.error("SongRegistry", "  - " + error);
        }
    }
}

record SongEntryParams(String variation) {
    public static SongEntryParams withVariation(String variation) {
        return new SongEntryParams(variation);
    }

    public static SongEntryParams defaultVariation() {
        return new SongEntryParams(null);
    }
}
