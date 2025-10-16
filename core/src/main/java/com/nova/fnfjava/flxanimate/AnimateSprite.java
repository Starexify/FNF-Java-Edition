package com.nova.fnfjava.flxanimate;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.nova.fnfjava.Paths;

public class AnimateSprite extends Actor {
    public AnimateAtlas atlas;
    public AnimateData.Timeline timeline;

    public AnimateSprite(float x, float y, String assetPath) {
        setAtlas(AnimateAtlas.fromAnimate(assetPath));
        setPosition(x, y);
    }

    public AnimateSprite(String assetPath) {
        this(0, 0, assetPath);
    }

    public void setAtlas(AnimateAtlas atlas) {
        this.atlas = atlas;
        timeline = atlas.animateData.AN.TL;
        timeline.currentFrame = animation.frameIndex;
    }
}
