package com.nova.fnfjava.data.stickers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.nova.fnfjava.data.BaseRegistry;
import com.nova.fnfjava.data.JsonFile;
import com.nova.fnfjava.ui.transition.stickers.StickerPack;

public class StickerRegistry extends BaseRegistry<StickerPack, StickerData, StickerEntryParams> {
    public static StickerRegistry instance;

    public final Json parser = new Json();
    public final Array<String> parseErrors = new Array<>();

    public StickerRegistry() {
        super("STICKER", "stickerpacks", StickerPack::new);

        setupParser();
    }

    public static void initialize() {
        if (instance == null) instance = new StickerRegistry();
    }

    public void setupParser() {
        parser.setIgnoreUnknownFields(true);
    }

    @Override
    public StickerData parseEntryData(String id) {
        parseErrors.clear();

        try {
            JsonFile fileResult = loadEntryFile(id);
            StickerData stickerData = parser.fromJson(StickerData.class, fileResult.contents());

            if (stickerData != null) {
                return stickerData;
            } else {
                parseErrors.add("Failed to parse JSON data");
                printErrors(parseErrors, id);
                return null;
            }

        } catch (Exception e) {
            parseErrors.add("Exception during parsing: " + e.getMessage());
            printErrors(parseErrors, id);
            return null;
        }
    }
}

record StickerEntryParams() {}
