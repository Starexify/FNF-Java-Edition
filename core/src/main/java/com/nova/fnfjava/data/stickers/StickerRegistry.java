package com.nova.fnfjava.data.stickers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.nova.fnfjava.data.BaseRegistry;
import com.nova.fnfjava.data.JsonFile;
import com.nova.fnfjava.ui.transition.stickers.StickerPack;
import com.nova.fnfjava.util.Constants;

public class StickerRegistry extends BaseRegistry<StickerPack, StickerData, StickerRegistry.StickerEntryParams> {
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

    public StickerPack fetchDefault() {
        StickerPack stickerPack = fetchEntry(Constants.DEFAULT_STICKER_PACK);
        if (stickerPack == null) throw new IllegalArgumentException("Default sticker pack was null! This should not happen!");
        return stickerPack;
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

    public record StickerEntryParams() {}
}
