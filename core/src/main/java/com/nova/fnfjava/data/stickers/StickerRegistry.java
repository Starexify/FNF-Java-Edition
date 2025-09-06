package com.nova.fnfjava.data.stickers;

import com.nova.fnfjava.data.BaseRegistry;
import com.nova.fnfjava.ui.transition.stickers.StickerPack;
import com.nova.fnfjava.util.Constants;

public class StickerRegistry extends BaseRegistry<StickerPack, StickerData, StickerRegistry.StickerEntryParams> {
    public static final StickerRegistry instance = new StickerRegistry();

    public StickerRegistry() {
        super("STICKER", "stickerpacks", StickerPack::new);
    }

    public StickerPack fetchDefault() {
        StickerPack stickerPack = fetchEntry(Constants.DEFAULT_STICKER_PACK);
        if (stickerPack == null) throw new IllegalArgumentException("Default sticker pack was null! This should not happen!");
        return stickerPack;
    }

    @Override
    public StickerData parseEntryData(String id) {
        return parseJsonData(id, StickerData.class);
    }

    @Override
    public StickerEntryParams getDefaultParams(String id, StickerData data) {
        return new StickerEntryParams();
    }

    public record StickerEntryParams() {}
}
