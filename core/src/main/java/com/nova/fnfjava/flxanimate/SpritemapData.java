package com.nova.fnfjava.flxanimate;

import com.badlogic.gdx.utils.Array;

public class SpritemapData {
    public AtlasData ATLAS;
    public MetaData meta;

    public static class AtlasData {
        public Array<SpriteWrapper> SPRITES;
    }

    public static class SpriteWrapper {
        public SpriteData SPRITE;
    }

    public static class SpriteData {
        public String name;
        public float x;
        public float y;
        public float w;
        public float h;
        public boolean rotated;
    }

    public static class MetaData {
        public String app;
        public String version;
        public String image;
        public String format;
        public SizeData size;
        public String resolution;
    }

    public static class SizeData {
        public int w;
        public int h;
    }
}
