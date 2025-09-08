package com.nova.fnfjava.data.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.animation.AnimationController;

import java.util.Comparator;

public class AnimationData {
    public String name;
    public String prefix;
    public String assetPath = null;
    public float[] offsets = {0f, 0f};
    public Boolean looped = false;
    public Boolean flipX = false;
    public Boolean flipY = false;
    public Integer frameRate = 24;
    public Integer[] frameIndices = null;

    public transient Animation<TextureRegion> gdxAnimation;
    public transient Array<TextureRegion> frames;
    public transient float stateTime = 0f;
    public transient boolean paused = true;
    public transient boolean finished = false;

    public AnimationData() {
    }

    public AnimationData(String name, String prefix, int frameRate, boolean looped) {
        this.name = name;
        this.prefix = prefix;
        this.frameRate = frameRate;
        this.looped = looped;
    }

    public void createAnimation(TextureAtlas atlas) {
        if (atlas == null) {
            Main.logger.setTag("AnimationData").warn("Cannot create animation '" + name + "': no atlas provided");
            return;
        }

        frames = new Array<>();

        if (frameIndices != null && frameIndices.length > 0) {
            // Use specific frame indices
            for (int index : frameIndices) {
                TextureAtlas.AtlasRegion region = atlas.findRegion(prefix, index);
                if (region != null) {
                    if (region.rotate) {
                        // Create a properly rotated TextureRegion
                        TextureRegion rotatedFrame = new TextureRegion(region.getTexture(), region.getRegionX(), region.getRegionY(), region.getRegionHeight(), region.getRegionWidth());
                        frames.add(rotatedFrame);
                    } else {
                        TextureAtlas.AtlasRegion frame = new TextureAtlas.AtlasRegion(region);
                        if (flipX || flipY) frame.flip(flipX, flipY);
                        frames.add(frame);
                    }
                }
            }
        } else {
            // Use all frames with the prefix
            Array<TextureAtlas.AtlasRegion> foundRegions = atlas.findRegions(prefix);
            foundRegions.sort(Comparator.comparingInt(a -> a.index));

            for (TextureAtlas.AtlasRegion region : foundRegions) {
                if (region.rotate) {
                    TextureRegion rotatedFrame = new TextureRegion(region.getTexture(), region.getRegionX(), region.getRegionY(), region.getRegionHeight(), region.getRegionWidth());
                    frames.add(rotatedFrame);
                } else {
                    TextureAtlas.AtlasRegion frame = new TextureAtlas.AtlasRegion(region);
                    if (flipX || flipY) frame.flip(flipX, flipY);
                    frames.add(frame);
                }
            }
        }

        if (frames.size > 0) {
            gdxAnimation = new Animation<>(
                1.0f / frameRate,
                frames,
                looped ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL
            );
        } else {
            Main.logger.setTag("AnimationData").warn("No frames found for animation: " + name + " with prefix: " + prefix + " in atlas: " + atlas);
        }
    }

    public void update(float delta, AnimationController parent) {
        if (gdxAnimation == null || finished || paused) return;

        float oldStateTime = stateTime;
        stateTime += delta;

        // Handle callbacks
        if (parent != null) {
            int oldFrame = gdxAnimation.getKeyFrameIndex(oldStateTime);
            int newFrame = gdxAnimation.getKeyFrameIndex(stateTime);

            if (oldFrame != newFrame) {
                parent.fireFrameCallback();
            }

            // Check if finished (non-looped animations)
            if (!looped && gdxAnimation.isAnimationFinished(stateTime)) {
                if (!finished) {
                    finished = true;
                    parent.fireFinishCallback(name);
                }
            }
        }
    }

    public void play(boolean force, int startFrame) {
        if (gdxAnimation == null) return;

        if (force || finished) {
            stateTime = startFrame >= 0 ? startFrame * (1.0f / frameRate) : 0f;
            finished = false;
        }

        paused = false;
    }

    public TextureRegion getCurrentFrame() {
        if (gdxAnimation == null) return null;
        return gdxAnimation.getKeyFrame(stateTime, looped);
    }

    public int getCurrentFrameIndex() {
        if (gdxAnimation == null) return -1;
        return gdxAnimation.getKeyFrameIndex(stateTime);
    }

    public Vector2 getOffsets() {
        return new Vector2(offsets[0], offsets[1]);
    }

    public void setOffsets(float x, float y) {
        offsets[0] = x;
        offsets[1] = y;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void stop() {
        finished = true;
        paused = true;
    }

    public void restart() {
        play(true, 0);
    }
}
