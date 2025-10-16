package com.nova.fnfjava.flxanimate;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.nova.fnfjava.Paths;

public class AnimateSprite extends Actor {
    public AnimateAtlas frames;

    public AnimateSprite(float x, float y, String assetPath) {
        frames = AnimateAtlas.fromAnimate(assetPath);
        setPosition(x, y);
    }

    public AnimateSprite(String assetPath) {
        this(0, 0, assetPath);
    }
}
