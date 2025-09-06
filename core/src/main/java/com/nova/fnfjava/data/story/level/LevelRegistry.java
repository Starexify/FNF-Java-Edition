package com.nova.fnfjava.data.story.level;

import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.data.BaseRegistry;
import com.nova.fnfjava.ui.story.Level;
import com.nova.fnfjava.util.SortUtil;

public class LevelRegistry extends BaseRegistry<Level, LevelData, LevelRegistry.LevelEntryParams> {
    public static final LevelRegistry instance = new LevelRegistry();

    public LevelRegistry() {
        super("LEVEL", "levels", Level::new);
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
        return parseJsonData(id, LevelData.class);
    }

    @Override
    public LevelEntryParams getDefaultParams(String id, LevelData data) {
        return new LevelEntryParams();
    }

    public record LevelEntryParams() {}
}
