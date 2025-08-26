package com.nova.fnfjava.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.AnimatedSprite;

public class AnimationController {
    private ObjectMap<String, Animation<TextureRegion>> animations = new ObjectMap<>();
    private AnimatedSprite sprite;
    private String currentAnimationName;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private boolean playing;
    private boolean looping;

    public AnimationController(AnimatedSprite sprite) {
        this.sprite = sprite;
        stateTime = 0f;
        playing = true;
        looping = true;
    }

    public void addByPrefix(String name, String prefix, float frameRate) {
        if (sprite.atlas != null) {
            Array<TextureAtlas.AtlasRegion> animFrames = sprite.atlas.findRegions(prefix);
            Animation<TextureRegion> animation = new Animation<TextureRegion>(1.0f / frameRate, animFrames);
            addAnimation(name, animation);
        }
    }

    public void addByIndices(String name, String prefix, Array<Integer> indices, float frameRate) {
        if (sprite.atlas != null) {
            Array<TextureAtlas.AtlasRegion> animFrames = new Array<>();
            for (int index : indices) {
                TextureAtlas.AtlasRegion region = sprite.atlas.findRegion(prefix, index);
                if (region != null) animFrames.add(region);
                else Gdx.app.log("AtlasWarning", "Region not found: " + prefix + " index " + index);
            }
            Animation<TextureRegion> animation = new Animation<TextureRegion>(1.0f / frameRate, animFrames);
            addAnimation(name, animation);
        }
    }

    public void addAnimation(String name, Animation<TextureRegion> anim) {
        animations.put(name, anim);

        // If this is the first animation, set it as current
        if (currentAnimation == null) {
            play(name);
        }
    }

    public void play(String name) {
        play(name, true);
    }

    public void play(String name, boolean reset) {
        if (animations.containsKey(name)) {
            currentAnimationName = name;
            currentAnimation = animations.get(name);

            if (reset) {
                stateTime = 0f;
            }

            playing = true;
        }
    }

    public void update(float delta) {
        if (playing && currentAnimation != null) {
            stateTime += delta;
        }
    }

    public TextureRegion getCurrentFrame() {
        if (currentAnimation == null) return null;
        return currentAnimation.getKeyFrame(stateTime, looping);
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }
}
