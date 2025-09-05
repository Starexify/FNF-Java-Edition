package com.nova.fnfjava.data.story.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.nova.fnfjava.data.BaseRegistry;
import com.nova.fnfjava.data.JsonFile;
import com.nova.fnfjava.ui.story.Level;
import com.nova.fnfjava.util.SortUtil;

public class LevelRegistry extends BaseRegistry<Level, LevelData, LevelRegistry.LevelEntryParams> {
    public static LevelRegistry instance;

    public final Json parser = new Json();

    public LevelRegistry() {
        super("LEVEL", "levels", Level::new);

        setupParser();
    }

    public static void initialize() {
        if (instance == null) instance = new LevelRegistry();
    }

    public void setupParser() {
        parser.setIgnoreUnknownFields(true);
    }

    public Array<String> listBaseGameEntryIds() {
        return new Array<>(new String[]{
            "tutorial",
            "week1",
            "week2",
            "week3",
            "week4",
            "week5",
            "week6",
            "week7",
            "weekend1"
        });
    }

    public Array<String> listSortedLevelIds() {
        Array<String> result = listEntryIds();
        result.sort(SortUtil.defaultsThenAlphabetically(listBaseGameEntryIds()));
        return result;
    }

    @Override
    public LevelData parseEntryData(String id) {
        try {
            JsonFile entryFile = loadEntryFile(id);
            LevelData levelData = parser.fromJson(LevelData.class, entryFile.contents());
            return levelData;
        } catch (Exception e) {
            Gdx.app.error("LevelRegistry", "Failed to parse level data for: " + id, e);
            return null;
        }
    }

    @Override
    public LevelEntryParams getDefaultParams(String id, LevelData data) {
        return new LevelEntryParams();
    }

    public record LevelEntryParams() {}
}
