package com.nova.fnfjava.flxanimate;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;

public class AnimateSprite extends Actor {
    public AnimateAtlas atlas;
    public AnimateData.Timeline timeline;

    public AnimateSprite(float x, float y, String assetPath) {
        loadTextureAtlas(AnimateAtlas.fromAnimate(assetPath));
        setPosition(x, y);
    }

    public AnimateSprite(String assetPath) {
        this(0, 0, assetPath);
    }

    public void loadTextureAtlas(AnimateAtlas atlas) {
        if (atlas == null) {
            Main.logger.setTag("AnimateSprite").warn("No Animation.json file was found for this AnimateSprite.");
            return;
        }

        this.atlas = atlas;
    }
}
