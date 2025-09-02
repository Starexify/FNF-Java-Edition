package com.nova.fnfjava;

public class FunkinSprite extends AnimatedSprite {
    public FunkinSprite(float x, float y) {
        super(x, y);
    }

    public static FunkinSprite create(float x, float y, String atlasPath) {
        FunkinSprite sprite = new FunkinSprite(x, y);
        sprite.atlas = Assets.getAtlas(atlasPath);
        return sprite;
    }

    public FunkinSprite loadTexture(String key) {
        String graphicKey = Paths.image(key);
        loadGraphic(graphicKey);

        return this;
    }
}
