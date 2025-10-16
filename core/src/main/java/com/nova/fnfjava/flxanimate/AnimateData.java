package com.nova.fnfjava.flxanimate;

public class AnimateData {
    public SpriteMapData spritemap;

    public class SpriteMapData {
        public AtlasData atlas;
    }

    public record AtlasData() {}
}
