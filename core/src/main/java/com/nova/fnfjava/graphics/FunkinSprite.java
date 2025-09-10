package com.nova.fnfjava.graphics;

import com.nova.fnfjava.Assets;
import com.nova.fnfjava.Paths;

public class FunkinSprite extends AnimatedSprite {
    public FunkinSprite(float x, float y) {
        super(x, y);
    }

    public FunkinSprite() {
        this(0, 0);
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
