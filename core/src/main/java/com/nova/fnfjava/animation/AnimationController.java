package com.nova.fnfjava.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.AnimatedSprite;

public class AnimationController {
    public ObjectMap<String, AnimationData> animations = new ObjectMap<>();
    public AnimatedSprite sprite;
    public AnimationData curAnim;
    public float stateTime;
    public boolean playing;
    public boolean looping = true;

    public AnimationController(AnimatedSprite sprite) {
        this.sprite = sprite;
        stateTime = 0f;
    }

    public void update(float delta) {
        if (playing && curAnim != null) {
            stateTime += delta;
        }
    }

    public void add(String name, int[] frames, float frameRate, boolean looped, boolean flipX, boolean flipY) {
        if (sprite.atlas == null) {
            System.err.println("Could not create animation: " + name + ", sprite has no atlas");
            return;
        }

        Array<Integer> frameIndices = new Array<>();
        for (int frame : frames) frameIndices.add(frame);

        addByIndices(name, "frame", frameIndices, frameRate, looped, flipX, flipY);
    }

    public void add(String name, int[] frames, float frameRate) {
        this.add(name, frames, frameRate, true, false, false);
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
                AnimationData anim = new AnimationData(new Animation<TextureRegion>(1.0f / frameRate, animFrames, looped ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL));
                animations.put(name, anim);
            } else
                Gdx.app.log("AnimationControllerWarning", "Could not create animation: " + name + ", no frames were found with the prefix " + prefix);
        }
    }

    public void addByIndices(String name, String prefix, Array<Integer> indices, float frameRate) {
        addByIndices(name, prefix, indices, frameRate, true, false, false);
    }

    /**
     * Adds a new animation to the sprite.
     *
     * @param   name        What this animation should be called (e.g. `"run"`).
     * @param   prefix      Common beginning of image names in atlas (e.g. `"tiles-"`).
     * @param   frameRate   The animation speed in frames per second.
     *                      Note: individual frames have their own duration, which overrides this value.
     * @param   looped      Whether or not the animation is looped or just plays once.
     * @param   flipX       Whether the frames should be flipped horizontally.
     * @param   flipY       Whether the frames should be flipped vertically.
     */
    public void addByPrefix(String name, String prefix, float frameRate, boolean looped, boolean flipX, boolean flipY) {
        if (sprite.atlas != null) {
            final Array<TextureAtlas.AtlasRegion> animFrames = sprite.atlas.findRegions(prefix);
            final Array<TextureRegion> processedFrames = new Array<>();

            for (TextureAtlas.AtlasRegion region : animFrames) {
                TextureRegion frame = new TextureRegion(region);
                if (flipX || flipY) frame.flip(flipX, flipY);
                processedFrames.add(frame);
            }

            if (animFrames.size > 0) {
                AnimationData anim = new AnimationData(new Animation<TextureRegion>(1.0f / frameRate, processedFrames, looped ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL));
                animations.put(name, anim);
            }
        }
    }

    public void addByPrefix(String name, String prefix, float frameRate) {
        addByPrefix(name, prefix, frameRate, true, false, false);
    }

    public void play(String name, boolean reset) {
        if (animations.containsKey(name)) {
            curAnim = animations.get(name);

            if (reset) {
                stateTime = 0f;
            }

            playing = true;
        }
    }

    public void play(String name) {
        play(name, true);
    }

    public boolean exists(String name) {
        return animations.containsKey(name);
    }

    public TextureRegion getCurrentFrame() {
        if (curAnim == null) return null;
        return curAnim.animation.getKeyFrame(stateTime, looping);
    }
}
