package com.nova.fnfjava.ui.transition.stickers;

import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.stickers.StickerData;
import com.nova.fnfjava.data.stickers.StickerRegistry;

public class StickerPack implements IRegistryEntry<StickerData> {
    public final String id;
    public StickerData data;

    public StickerPack(String id, Object... params) {
        this.id = id;
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

    @Override
    public void loadData(StickerData data) {
        if (data == null) throw new IllegalArgumentException("StickerData cannot be null");
        this.data = data;
    }

    public static StickerData fetchData(String id) {
        StickerRegistry registry = StickerRegistry.instance;
        if (registry == null) throw new IllegalStateException("StickerRegistry is not initialized!");

        StickerPack entry = registry.entries.get(id);
        if (entry != null) return entry.getData();

        return registry.parseEntryData(id);
    }
}
