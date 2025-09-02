package com.nova.fnfjava.data.stickers;

import com.badlogic.gdx.utils.Array;

public class StickerData {
    public String version;
    public String name;
    public String artist;
    public Array<String> stickers;

    public StickerData() {}

    public StickerData(String version, String name, String artist, Array<String> stickers) {
        this.version = version;
        this.name = name;
        this.artist = artist;
        this.stickers = stickers;
    }

    @Override
    public String toString() {
        return "StickerData{" +
            "version='" + version + '\'' +
            ", name='" + name + '\'' +
            ", artist='" + artist + '\'' +
            ", stickers=" + stickers +
            '}';
    }
}
