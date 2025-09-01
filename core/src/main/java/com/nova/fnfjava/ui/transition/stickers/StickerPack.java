package com.nova.fnfjava.ui.transition.stickers;

import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.stickers.StickerData;

public class StickerPack implements IRegistryEntry<StickerData> {
    public final String id;
    public final StickerData data;

    public StickerPack(String id, StickerData data) {
        this.id = id;
        this.data = data;

        if (data == null) throw new IllegalArgumentException("Could not parse sticker pack data for id: " + id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void destroy() {

    }

    @Override
    public StickerData getData() {
        return data;
    }
}
