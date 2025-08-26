package com.nova.fnfjava.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationData {
    public final Animation<TextureRegion> animation;

    public boolean flipX = false;
    public boolean flipY = false;

    public AnimationData(Animation<TextureRegion> animation) {
        this.animation = animation;
    }
}
