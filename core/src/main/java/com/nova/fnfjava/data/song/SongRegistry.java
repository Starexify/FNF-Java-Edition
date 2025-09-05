package com.nova.fnfjava.data.song;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.data.BaseRegistry;
import com.nova.fnfjava.data.DataAssets;
import com.nova.fnfjava.data.JsonFile;
import com.nova.fnfjava.play.Song;
import com.nova.fnfjava.util.Constants;

public class SongRegistry extends BaseRegistry<Song, SongData.SongMetadata, SongRegistry.SongEntryParams> {
    public static SongRegistry instance;

    public static String DEFAULT_GENERATEDBY = Constants.TITLE + "-" + Constants.VERSION;

    public final Json parser;
    public final Array<String> parseErrors;

    public SongRegistry() {
        super("SONG", "songs", Song::new);
        parser = new Json();
        parseErrors = new Array<>();

        setupParser();
    }

    @Override
    public void loadEntries() {
        clearEntries();

        Array<String> rawPaths = DataAssets.listDataFilesInPath("songs/", "-metadata.json");
        Array<String> entryIdList = new Array<>();
        for (String path : rawPaths) {
            String entryId = path.contains("/") ? path.split("/")[0] : path;
            if (!entryIdList.contains(entryId, false)) entryIdList.add(entryId);
        }

        Array<String> unscriptedEntryIds = new Array<>();
        for (String entryId : entryIdList) if (!entries.containsKey(entryId)) unscriptedEntryIds.add(entryId);

        Gdx.app.log(registryId, "Parsing " + unscriptedEntryIds.size + " unscripted entries...");

        for (String entryId : unscriptedEntryIds) {
            try {
                SongEntryParams defaultParams = getDefaultParams(entryId, parseEntryData(entryId));
                Song entry = createEntry(entryId, parseEntryData(entryId), defaultParams);
                if (entry != null) {
                    entries.put(entry.id, entry);

                    Gdx.app.log(registryId, "Loaded entry data: " + entryId);
                }
            } catch (Exception e) {
                Gdx.app.error(registryId, "Failed to load entry data: " + entryId, e);
            }
        }
    }

    public static void initialize() {
        if (instance == null) instance = new SongRegistry();
    }

    public void setupParser() {
        parser.setIgnoreUnknownFields(true);

        parser.setSerializer(SongData.SongTimeFormat.class, new Json.Serializer<SongData.SongTimeFormat>() {
            @Override
            public void write(Json json, SongData.SongTimeFormat object, Class knownType) {
                json.writeValue(object.getId());
            }

            @Override
            public SongData.SongTimeFormat read(Json json, JsonValue jsonData, Class type) {
                return SongData.SongTimeFormat.fromString(jsonData.asString());
            }
        });
    }

    public SongData.SongMusicData parseMusicData(String id, String variation) {
        parseErrors.clear();

        JsonFile fileResult = loadMusicDataFile(id, variation);
        if (fileResult == null) return null;
        try {
            SongData.SongMusicData musicData = parser.fromJson(SongData.SongMusicData.class, fileResult.contents());
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

    public SongData.SongMusicData parseMusicData(String id) {
        return this.parseMusicData(id, Constants.DEFAULT_VARIATION);
    }

    public JsonFile loadMusicDataFile(String id, String variation) {
        if (variation == null) variation = Constants.DEFAULT_VARIATION;
        String variationSuffix = variation.equals(Constants.DEFAULT_VARIATION) ? "" : "-" + variation;
        String entryFilePath = "assets/music/" + id + "/" + id + "-metadata" + variationSuffix + ".json";
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

    @Override
    public SongData.SongMetadata parseEntryData(String id) {
        return parseEntryMetadata(id);
    }

    @Override
    public SongEntryParams getDefaultParams(String id, SongData.SongMetadata data) {
        return new SongEntryParams(Constants.DEFAULT_VARIATION);
    }

    public SongData.SongMetadata parseEntryMetadata(String id, String variation) {
        parseErrors.clear();

        JsonFile fileResult = loadEntryMetadataFile(id, variation);
        if (fileResult == null) return null;
        try {
            SongData.SongMetadata metadata = parser.fromJson(SongData.SongMetadata.class, fileResult.contents());
            if (metadata == null) {
                parseErrors.add("Failed to parse JSON from file: " + fileResult.fileName());
                printErrors(id);
                return null;
            }
            metadata.variation = variation;

            return metadata;
        } catch (Exception e) {
            parseErrors.add("JSON parsing error: " + e.getMessage());
            printErrors(id);
            return null;
        }
    }

    public SongData.SongMetadata parseEntryMetadata(String id) {
        return parseEntryMetadata(id, Constants.DEFAULT_VARIATION);
    }

    public JsonFile loadEntryMetadataFile(String id, String variation) {
        variation = variation == null ? Constants.DEFAULT_VARIATION : variation;
        String variationSuffix = variation.equals(Constants.DEFAULT_VARIATION) ? "" : "-" + variation;
        String entryFilePath = Paths.json(dataFilePath +"/" + id + "/" + id + "-metadata" + variationSuffix);
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
            Gdx.app.error("SongRegistry", "Errors parsing song data for ID: " + id);
            for (String error : parseErrors) Gdx.app.error("SongRegistry", "  - " + error);
        }
    }

    public record SongEntryParams(String variation) {}
}
