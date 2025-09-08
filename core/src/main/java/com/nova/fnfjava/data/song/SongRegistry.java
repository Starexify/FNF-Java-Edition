package com.nova.fnfjava.data.song;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.data.BaseRegistry;
import com.nova.fnfjava.data.DataAssets;
import com.nova.fnfjava.data.JsonFile;
import com.nova.fnfjava.play.song.Song;
import com.nova.fnfjava.util.Constants;

public class SongRegistry extends BaseRegistry<Song, SongData.SongMetadata, SongRegistry.SongEntryParams> {
    public static final SongRegistry instance = new SongRegistry();

    public static String DEFAULT_GENERATEDBY = Constants.TITLE + "-" + Constants.VERSION;

    public SongRegistry() {
        super("SONG", "songs", Song::new);
    }

    @Override
    public void setupParser() {
        super.setupParser();

        parser.setSerializer(SongData.SongTimeFormat.class, new Json.Serializer<>() {
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

        Main.logger.setTag(registryId).info("Parsing " + unscriptedEntryIds.size + " unscripted entries...");

        for (String entryId : unscriptedEntryIds) {
            try {
                SongEntryParams defaultParams = getDefaultParams(entryId, parseEntryData(entryId));
                Song entry = createEntry(entryId, parseEntryData(entryId), defaultParams);
                if (entry != null) {
                    entries.put(entry.id, entry);

                    Main.logger.setTag(registryId).info("Loaded entry data: " + entryId);
                }
            } catch (Exception e) {
                Main.logger.setTag(registryId).error("Failed to load entry data: " + entryId, e);
            }
        }
    }

    public SongData.SongMusicData parseMusicData(String id, String variation) {
        try {
            JsonFile entryFile = loadMusicDataFile(id, variation);
            SongData.SongMusicData musicData = parser.fromJson(SongData.SongMusicData.class, entryFile.contents());

            if (musicData != null) {
                musicData.variation = variation;
                return musicData;
            } else {
                Main.logger.setTag(registryId).warn("Failed to parse JSON song data from file: " + entryFile.fileName());
                return null;
            }

        } catch (Exception e) {
            Main.logger.setTag(registryId).error("Failed to parse JSON data for song: " + id, e);
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
            Main.logger.setTag(registryId).error("Error reading music file " + entryFilePath, e);
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
        try {
            JsonFile entryFile = loadEntryMetadataFile(id, variation);
            SongData.SongMetadata metadata = parser.fromJson(SongData.SongMetadata.class, entryFile.contents());

            if (metadata != null) {
                metadata.variation = variation;
                return metadata;
            } else {
                Main.logger.setTag(registryId).warn("Failed to parse JSON music data from file: " + entryFile.fileName());
                return null;
            }
        } catch (Exception e) {
            Main.logger.setTag(registryId).error("Failed to parse JSON data for music: " + id, e);
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
            Main.logger.setTag(registryId).error("Error reading file " + entryFilePath, e);
            return null;
        }
    }

    public record SongEntryParams(String variation) {}
}
