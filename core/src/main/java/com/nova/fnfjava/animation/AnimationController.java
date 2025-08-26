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
    private boolean looping = true;

    public AnimationController(AnimatedSprite sprite) {
        this.sprite = sprite;
        stateTime = 0f;
        playing = true;
    }

    public void addByPrefix(String name, String prefix, float frameRate) {
        if (sprite.atlas != null) {
            Array<TextureAtlas.AtlasRegion> animFrames = sprite.atlas.findRegions(prefix);
            Animation<TextureRegion> animation = new Animation<TextureRegion>(1.0f / frameRate, animFrames);
            addAnimation(name, animation);
        }
    }

    /**
     * Adds a new animation to the sprite.
     *
     * @param name      What this animation should be called (e.g. `"run"`).
     * @param prefix    Common beginning of image names in the atlas (e.g. "tiles-").
     * @param indices   An array of numbers indicating what frames to play in what order (e.g. `[0, 1, 2]`).
     * @param frameRate The speed in frames per second that the animation should play at (e.g. `40` fps).
     * @param looped    Whether or not the animation is looped or just plays once.
     * @param flipX     Whether the frames should be flipped horizontally.
     * @param flipY     Whether the frames should be flipped vertically.
     */
    public void addByIndices(String name, String prefix, Array<Integer> indices, float frameRate, boolean looped, boolean flipX, boolean flipY) {
        if (sprite.atlas != null) {
            final Array<TextureAtlas.AtlasRegion> animFrames = new Array<TextureAtlas.AtlasRegion>();

            for (int index : indices) {
                TextureAtlas.AtlasRegion region = sprite.atlas.findRegion(prefix, index);
                if (region != null) {
                    TextureAtlas.AtlasRegion frame = new TextureAtlas.AtlasRegion(region);
                    if (flipX || flipY) frame.flip(flipX, flipY);
                    animFrames.add(frame);
                }
            }

            if (animFrames.size > 0) {
                Animation<TextureRegion> animation = new Animation<TextureRegion>(1.0f / frameRate, animFrames, looped ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);
                addAnimation(name, animation);
            } else
                Gdx.app.log("AnimationControllerWarning", "Could not create animation: " + name + ", no frames were found with the prefix " + prefix);
        }
    }

    // Convenience overloads
    public void addByIndices(String name, String prefix, Array<Integer> indices) {
        addByIndices(name, prefix, indices, 30f, true, false, false);
    }

    public void addByIndices(String name, String prefix, Array<Integer> indices, float frameRate) {
        addByIndices(name, prefix, indices, frameRate, true, false, false);
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
}
