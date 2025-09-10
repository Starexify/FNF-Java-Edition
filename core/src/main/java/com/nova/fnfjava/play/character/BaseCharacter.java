package com.nova.fnfjava.play.character;

import com.nova.fnfjava.play.stage.Bopper;

public class BaseCharacter extends Bopper {
    public final CharacterData _data;

    public BaseCharacter(float x, float y, float danceEvery) {
        super(x, y, danceEvery);
    }

    public void resetCharacter(boolean resetCamera) {
        // Set the x and y to be their original values.
        this.resetPosition();

        this.dance(true); // Force to avoid the old animation playing with the wrong offset at the start of the song.
        // Make sure we are playing the idle animation
        // ...then update the hitbox so that this.width and this.height are correct.
        this.updateHitbox();

        // Reset the camera focus point while we're at it.
        //if (resetCamera) this.resetCameraFocusPoint();
    }

    public float getBaseScale() {
        return getData().scale;
    }

    public void resetCharacter() {
        resetCharacter(true);
    }
}
