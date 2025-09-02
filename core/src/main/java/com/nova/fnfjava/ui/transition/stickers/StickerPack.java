package com.nova.fnfjava.ui.transition.stickers;

import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.stickers.StickerData;

public class StickerPack implements IRegistryEntry<StickerData> {
    public final String id;
    public StickerData data;

    public StickerPack(String id, Object... params) {
        this.id = id;
    }

    public Array<String> getStickers() {
        return data.stickers;
    }

    public String getRandomStickerPath(boolean last) {
        return getStickers().random();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public StickerData getData() {
        return data;
    }

    @Override
    public void loadData(StickerData data) {
        if (data == null) throw new IllegalArgumentException("StickerData cannot be null");
        this.data = data;
    }

    @Override
    public void destroy() {

    }
}
