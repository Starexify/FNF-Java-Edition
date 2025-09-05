package com.nova.fnfjava.data.stickers;

import com.badlogic.gdx.utils.Json;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.data.BaseRegistry;
import com.nova.fnfjava.data.JsonFile;
import com.nova.fnfjava.ui.transition.stickers.StickerPack;
import com.nova.fnfjava.util.Constants;

public class StickerRegistry extends BaseRegistry<StickerPack, StickerData, StickerRegistry.StickerEntryParams> {
    public static StickerRegistry instance;

    public final Json parser = new Json();

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
        try {
            JsonFile entryFile = loadEntryFile(id);
            StickerData stickerData = parser.fromJson(StickerData.class, entryFile.contents());

            if (stickerData != null) {
                return stickerData;
            } else {
                Main.logger.setTag(registryId).warn("Failed to parse JSON sticker data from file: " + entryFile.fileName());
                return null;
            }
        } catch (Exception e) {
            Main.logger.setTag(registryId).error("Failed to parse JSON data for sticker: " + id, e);
            return null;
        }
    }

    @Override
    public StickerEntryParams getDefaultParams(String id, StickerData data) {
        return new StickerEntryParams();
    }

    public record StickerEntryParams() {}
}
