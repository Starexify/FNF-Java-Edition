package com.nova.fnfjava.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.AnimatedSprite;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.data.animation.AnimationData;

public class AnimationController {
    public AnimationData curAnim;
    public String currentAnimName;
    public AnimatedSprite sprite;
    public ObjectMap<String, AnimationData> animations = new ObjectMap<>();

    public interface FrameCallback {
        void onFrame(String animName, int frameNumber, int frameIndex);
    }

    public interface FinishCallback {
        void onFinish(String animName);
    }

    public interface LoopCallback {
        void onLoop(String animName);
    }

    public FrameCallback frameCallback;
    public FinishCallback finishCallback;
    public LoopCallback loopCallback;

    public AnimationController(AnimatedSprite sprite) {
        this.sprite = sprite;
    }

    public void update(float delta) {
        if (curAnim != null) curAnim.update(delta, this);
    }

    public void addAnimation(AnimationData animData) {
        if (sprite.atlas != null) {
            animData.createAnimation(sprite.atlas);
            animations.put(animData.name, animData);
        }
    }

    public void add(String name, Integer[] frames, float frameRate, boolean looped, boolean flipX, boolean flipY) {
        AnimationData animData = new AnimationData();
        animData.name = name;
        animData.prefix = "frame";  // Assuming your atlas uses "frame" prefix
        animData.frameRate = (int)frameRate;
        animData.looped = looped;
        animData.flipX = flipX;
        animData.flipY = flipY;
        animData.frameIndices = frames;

        addAnimation(animData);
    }

    public void add(String name, Integer[] frames, float frameRate) {
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
        AnimationData animData = new AnimationData();
        animData.name = name;
        animData.prefix = prefix;
        animData.frameRate = (int) frameRate;
        animData.looped = looped;
        animData.flipX = flipX;
        animData.flipY = flipY;
        animData.frameIndices = indices.toArray();

        addAnimation(animData);
    }

    public void addByIndices(String name, String prefix, Array<Integer> indices, float frameRate) {
        addByIndices(name, prefix, indices, frameRate, true, false, false);
    }

    /**
     * Adds a new animation to the sprite.
     *
     * @param name      What this animation should be called (e.g. `"run"`).
     * @param prefix    Common beginning of image names in atlas (e.g. `"tiles-"`).
     * @param frameRate The animation speed in frames per second.
     *                  Note: individual frames have their own duration, which overrides this value.
     * @param looped    Whether or not the animation is looped or just plays once.
     * @param flipX     Whether the frames should be flipped horizontally.
     * @param flipY     Whether the frames should be flipped vertically.
     */
    public void addByPrefix(String name, String prefix, float frameRate, boolean looped, boolean flipX, boolean flipY) {
        AnimationData animData = new AnimationData();
        animData.name = name;
        animData.prefix = prefix;
        animData.frameRate = (int)frameRate;
        animData.looped = looped;
        animData.flipX = flipX;
        animData.flipY = flipY;
        // frameIndices is null, so it will use all frames with the prefix

        addAnimation(animData);
    }

    public void addByPrefix(String name, String prefix, float frameRate, boolean looped) {
        addByPrefix(name, prefix, frameRate, looped, false, false);
    }

    public void addByPrefix(String name, String prefix, float frameRate) {
        addByPrefix(name, prefix, frameRate, true, false, false);
    }

    public void addByPrefix(String name, String prefix) {
        addByPrefix(name, prefix, 30f, true, false, false);
    }

    public void play(String name, boolean force) {
        if (!animations.containsKey(name)) {
            Main.logger.setTag("AnimationController/" + sprite).warn("Actor has no animation called: " + name);
            return;
        }

        AnimationData newAnim = animations.get(name);
        if (force || curAnim != newAnim) {
            curAnim = newAnim;
            currentAnimName = name;
            curAnim.play(force, 0);

            sprite.dirty = true;
        }
    }

    public void play(String name) {
        play(name, false);
    }

    public AnimationData getByName(String name) {
        return animations.get(name);
    }

    public TextureRegion getCurrentFrame() {
        return curAnim != null ? curAnim.getCurrentFrame() : null;
    }

    public boolean exists(String name) {
        return animations.containsKey(name);
    }

    public void fireFrameCallback() {
        if (frameCallback != null && curAnim != null) {
            frameCallback.onFrame(curAnim.name, curAnim.getCurrentFrameIndex(), curAnim.getCurrentFrameIndex());
        }
    }

    public void fireFinishCallback(String animName) {
        if (finishCallback != null) {
            finishCallback.onFinish(animName);
        }
    }

    public void fireLoopCallback(String animName) {
        if (loopCallback != null) {
            loopCallback.onLoop(animName);
        }
    }

    // Getters and Setters
    public boolean setPaused(boolean value) {
        if (curAnim != null) {
            if (value) curAnim.pause();
            else curAnim.resume();
        }
        return value;
    }
}
