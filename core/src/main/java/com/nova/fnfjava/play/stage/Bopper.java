package com.nova.fnfjava.play.stage;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.modding.bus.EventBus;
import com.nova.fnfjava.modding.events.SongTimeEvent;
import com.nova.fnfjava.modding.events.handlers.IBPMSyncedClass;
import com.nova.fnfjava.util.Constants;

public class Bopper extends StageProp implements IBPMSyncedClass {
    public float danceEvery = 0.0f;
    public Boolean shouldAlternate = null;
    public ObjectMap<String, Array<Float>> animationOffsets = new ObjectMap<>();
    public String idleSuffix = "";
    public boolean isPixel = false;
    public boolean shouldBop = true;
    public Array<Float> globalOffsets = new Array<>(new Float[]{0f, 0f});
    public Array<Float> animOffsets = new Array<>(new Float[]{0f, 0f});
    public Vector2 originalPosition = new Vector2(0, 0);
    public boolean hasDanced = false;

    public Bopper(float x, float y, float danceEvery) {
        super(x, y);
        this.danceEvery = danceEvery;

        if (this.animation != null) {
            //this.animation.onFrameChange.add(this.onAnimationFrame);
            //this.animation.onFinish.add(this.onAnimationFinished);
        }

        EventBus.getInstance().register(SongTimeEvent.class, this);
    }

    public void resetPosition() {
        this.setX(originalPosition.x);
        this.setY(originalPosition.y);
    }

    public void update_shouldAlternate() {
        this.shouldAlternate = hasAnimation("danceLeft");
    }

    @Override
    public void onStepHit(int step) {
        if (danceEvery > 0 && (step % (danceEvery * Constants.STEPS_PER_BEAT)) == 0) dance(shouldBop);
    }

    @Override
    public void onBeatHit(int beat) {
    }

    /**
     * Called every `danceEvery` beats of the song.
     */
    public void dance(boolean forceRestart) {
        if (this.animation == null) return;

        if (shouldAlternate == null) update_shouldAlternate();

        if (shouldAlternate) {
            if (hasDanced) playAnimation("danceRight" + idleSuffix, forceRestart);
            else playAnimation("danceLeft" + idleSuffix, forceRestart);
            hasDanced = !hasDanced;
        } else {
            playAnimation("idle" + idleSuffix, forceRestart);
        }
    }

    public void dance() {
        dance(false);
    }

    public boolean hasAnimation(String id) {
        if (this.animation == null) return false;

        return this.animation.getByName(id) != null;
    }

    /**
     * Ensure that a given animation exists before playing it.
     * Will gracefully check for name, then name with stripped suffixes, then fail to play.
     * @param name The animation name to attempt to correct.
     * @param fallback Instead of failing to play, try to play this animation instead.
     */
    public String correctAnimationName(String name, String fallback) {
        if (hasAnimation(name)) return name;

        if (name.lastIndexOf('-') != -1) {
            var correctName = name.substring(0, name.lastIndexOf('-'));
            Main.logger.setTag(this.getClass().getSimpleName()).warn("Bopper tried to play animation \"" + name + "\" that does not exist, stripping suffixes (" + correctName + ")...");
            return correctAnimationName(correctName);
        } else {
            if (fallback != null) {
                if (fallback.equals(name)) {
                    Main.logger.setTag(this.getClass().getSimpleName()).warn("Bopper tried to play animation \"" + name + "\" that does not exist! This is bad!");
                    return null;
                } else {
                    Main.logger.setTag(this.getClass().getSimpleName()).warn("Bopper tried to play animation \"" + name + "\" that does not exist, fallback to idle...");
                    return correctAnimationName("idle");
                }
            } else {
                Main.logger.setTag(this.getClass().getSimpleName()).warn("Bopper tried to play animation \"" + name + "\" that does not exist! This is bad!");
                return null;
            }
        }
    }

    public String correctAnimationName(String name) {
        return correctAnimationName(name, null);
    }

    public boolean canPlayOtherAnims = true;
    public Array<String> ignoreExclusionPref = new Array<>();

    /**
     * @param name The name of the animation to play.
     * @param restart Whether to restart the animation if it is already playing.
     * @param ignoreOther Whether to ignore all other animation inputs, until this one is done playing
     * @param reversed If true, play the animation backwards, from the last frame to the first.
     */
    public void playAnimation(String name, boolean restart, boolean ignoreOther, boolean reversed) {
        if (!canPlayOtherAnims) {
            String id = name;
            if (getCurrentAnimation() == id && restart) {}
            else if (ignoreExclusionPref != null && ignoreExclusionPref.size > 0) {
                boolean detected = false;
                for (String entry : ignoreExclusionPref) {
                    if (id.startsWith(entry)) {
                        detected = true;
                        break;
                    }
                }
                if (!detected) return;
            } else return;
        }

        var correctName = correctAnimationName(name);
        if (correctName == null) return;

        this.animation.play(correctName, restart);
        if (ignoreOther) canPlayOtherAnims = false;

        applyAnimationOffsets(correctName);
    }

    public void playAnimation(String name, boolean restart, boolean ignoreOther) {
        playAnimation(name, restart, ignoreOther, false);
    }

    public void playAnimation(String name, boolean restart) {
        playAnimation(name, restart, false, false);
    }

    public void applyAnimationOffsets(String name) {
        Array<Float> offsets = animationOffsets.get(name);
        this.animOffsets = offsets;
    }

    /**
     * Returns the name of the animation that is currently playing.
     * If no animation is playing (usually this means the character is BROKEN!),
     *   returns an empty string to prevent NPEs.
     */
    public String getCurrentAnimation() {
        if (this.animation == null || this.animation.curAnim == null) return "";
        return this.animation.curAnim.name;
    }

    // Getters and Setters
    public boolean setIsPixel(boolean value) {
        if (isPixel == value) return value;
        return isPixel = value;
    }

    public String setIdleSuffix(String value) {
        this.idleSuffix = value;
        this.dance();
        return value;
    }

    public Array<Float> setGlobalOffsets(Array<Float> value) {
        if (globalOffsets == null) globalOffsets = new Array<>(new Float[]{0f, 0f});
        if (globalOffsets == value) return value;

        return globalOffsets = value;
    }

    public Array<Float> setAnimOffsets(Array<Float> value) {
        if (animOffsets == null) animOffsets = new Array<>(new Float[]{0f, 0f});
        if ((animOffsets.get(0) == value.get(0)) && (animOffsets.get(1) == value.get(1))) return value;

        return animOffsets = value;
    }

    @Override
    public boolean remove() {
        EventBus.getInstance().unregister(SongTimeEvent.class, this);
        return super.remove();
    }
}
