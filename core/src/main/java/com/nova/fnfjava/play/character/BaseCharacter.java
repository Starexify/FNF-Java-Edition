package com.nova.fnfjava.play.character;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.play.PlayState;
import com.nova.fnfjava.play.stage.Bopper;

public class BaseCharacter extends Bopper {
    public String characterId;

    public final CharacterData charData;

    public BaseCharacter(String id) {
        super(0,0, CharacterData.CharacterDataParser.DEFAULT_DANCEEVERY);

        this.characterId = id;

        ignoreExclusionPref = new Array<>(new String[]{"sing"});

        charData = CharacterData.CharacterDataParser.fetchCharacterData(this.characterId);
        if (charData == null) throw new IllegalArgumentException("Could not find character data for characterId: " + characterId);
        //else if (charData.renderType != renderType) throw new IllegalArgumentException("Render type mismatch for character ($characterId): expected ${renderType}, got ${_data.renderType}");
        else {
            //this.characterName = charData.name;
            this.name = charData.name;
            this.danceEvery = charData.danceEvery;
            //this.singTimeSteps = charData.singTime;
            this.globalOffsets = charData.offsets;
            //this.flipX = charData.flipX;
        }

        shouldBop = false;
    }

    public Vector2 cameraFocusPoint = new Vector2(0, 0);


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

    public void resetCharacter() {
        resetCharacter(true);
    }

    public void initHealthIcon(boolean isOpponent) {
        if (!isOpponent) {
            if (PlayState.instance.iconP1 == null) {
                Main.logger.setTag("BaseCharacter").warn("Player 1 health icon not found!");
                return;
            }
            PlayState.instance.iconP1.configure(charData.healthIcon);
            //PlayState.instance.iconP1.flipX = !PlayState.instance.iconP1.flipX;
        } else {
            if (PlayState.instance.iconP2 == null) {
                Main.logger.setTag("BaseCharacter").warn("Player 2 health icon not found!");
                return;
            }
            PlayState.instance.iconP2.configure(charData.healthIcon);
        }
    }

    // Getters/Setters
    public float getBaseScale() {
        return charData.scale;
    }

    public Float getDeathPreTransitionDelay() {
        return charData.death.preTransitionDelay != null ? charData.death.preTransitionDelay : 0.0f;
    }
}
