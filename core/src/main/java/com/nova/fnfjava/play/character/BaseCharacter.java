package com.nova.fnfjava.play.character;

import com.nova.fnfjava.play.stage.Bopper;

public class BaseCharacter extends Bopper {
    public String characterId;

    public final CharacterData charData;

    public BaseCharacter(String id, float x, float y, float danceEvery) {
        super(0,0, CharacterData.CharacterDataParser.DEFAULT_DANCEEVERY);

        this.characterId = id;

        charData = CharacterData.CharacterDataParser.fetchCharacterData(this.characterId);
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
        return charData.scale;
    }

    public void resetCharacter() {
        resetCharacter(true);
    }
}
