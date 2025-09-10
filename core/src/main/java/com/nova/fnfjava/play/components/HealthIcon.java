package com.nova.fnfjava.play.components;

import com.badlogic.gdx.math.Vector2;
import com.nova.fnfjava.Assets;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.graphics.FunkinSprite;
import com.nova.fnfjava.play.character.CharacterData;
import com.nova.fnfjava.util.Constants;

import java.util.Objects;

public class HealthIcon extends FunkinSprite {
    public String characterId = Constants.DEFAULT_HEALTH_ICON;
    public boolean autoUpdate = true;
    public Vector2 size;
    public int bopEvery = Constants.STEPS_PER_BEAT;
    public float bopAngle = 0.0f;
    public int playerId = 0;
    public boolean isPixel = false;
    public boolean isLegacyStyle = false;
    public static final float WINNING_THRESHOLD = 0.8f * 2;
    public static final float LOSING_THRESHOLD = 0.2f * 2;
    public static final float MAXIMUM_HEALTH = 2;
    public static final int HEALTH_ICON_SIZE = 150;
    public static final int PIXEL_ICON_SIZE = 32;
    public static final float BOP_SCALE = 0.2f;
    public static final int POSITION_OFFSET = 26;
    public Vector2 iconOffset = Vector2.Zero;

    public HealthIcon(String character, int playerId) {
        this.playerId = playerId;
        //this.size = new FlxCallbackPoint(onSetSize);
        //this.scrollFactor.set();
        size.set(1.0f, 1.0f);
        setCharacterId(character);
    }

    public void configure(CharacterData.HealthIconData data) {
        if (data == null) {
            setCharacterId(Constants.DEFAULT_HEALTH_ICON);
            setIsPixel(false);

            loadCharacter(characterId);

            this.size.set(1.0f, 1.0f);
            this.iconOffset.set(Vector2.Zero);
            //this.flipX = false;
        } else {
            setCharacterId(data.id);
            setIsPixel(data.isPixel);

            loadCharacter(characterId);

            this.size.set(data.scale, data.scale);

            if (data.offsets != null && data.offsets.size == 2) this.iconOffset.set(data.offsets.get(0), data.offsets.get(1));
            else this.iconOffset.set(0, 0);

            //this.flipX = data.flipX ?? false;
        }
    }

    public void loadAnimationNew() {
        this.animation.addByPrefix(HealthIconState.IDLE.toString(), HealthIconState.IDLE.toString(), 24, true);
        this.animation.addByPrefix(HealthIconState.WINNING.toString(), HealthIconState.WINNING.toString(), 24, true);
        this.animation.addByPrefix(HealthIconState.LOSING.toString(), HealthIconState.LOSING.toString(), 24, true);
        this.animation.addByPrefix(HealthIconState.TO_WINNING.toString(), HealthIconState.TO_WINNING.toString(), 24, false);
        this.animation.addByPrefix(HealthIconState.TO_LOSING.toString(), HealthIconState.TO_LOSING.toString(), 24, false);
        this.animation.addByPrefix(HealthIconState.FROM_WINNING.toString(), HealthIconState.FROM_WINNING.toString(), 24, false);
        this.animation.addByPrefix(HealthIconState.FROM_LOSING.toString(), HealthIconState.FROM_LOSING.toString(), 24, false);
    }

    public void loadAnimationOld() {
        // Don't flip BF's icon here! That's done later.
        this.animation.add(HealthIconState.IDLE.toString(), new Integer[0], 0, false, false);
        this.animation.add(HealthIconState.LOSING.toString(), new Integer[1], 0, false, false);
/*        if (animation.numFrames >= 3)
            this.animation.add(HealthIconState.WINNING.toString(), new Integer[2], 0, false, false);*/
    }

    public boolean iconExists(String charId) {
        return Assets.exists(Paths.image("icons/icon-" + charId));
    }

    public boolean isNewSpritesheet(String charId) {
        return Assets.exists(Paths.getAtlas("icons/icon-" + characterId));
    }

    public void loadCharacter(String charId) {
        if (charId == null || !iconExists(charId)) {
            Main.logger.setTag("HealthIcon").warn("No icon for character: " + charId + " : using default placeholder face instead!");
            characterId = Constants.DEFAULT_HEALTH_ICON;
            charId = characterId;
        }
        isLegacyStyle = !isNewSpritesheet(charId);

        Main.logger.setTag("HealthIcon").info(" Loading health icon for character: " + charId + " (legacy: " + isLegacyStyle + ")");
        if (!isLegacyStyle) {
            //loadAtlas("icons/icon-" + charId);
            loadAnimationNew();
        } else {
            loadGraphic(Paths.image("icons/icon-" + charId), true, isPixel ? PIXEL_ICON_SIZE : HEALTH_ICON_SIZE, isPixel ? PIXEL_ICON_SIZE : HEALTH_ICON_SIZE);
            loadAnimationOld();
        }

        //this.antialiasing = !isPixel;
    }

    public String setCharacterId(String value) {
        if (Objects.equals(value, characterId)) return value;

        characterId = value != null ? value : Constants.DEFAULT_HEALTH_ICON;
        return characterId;
    }

    public boolean setIsPixel(boolean value) {
        if (value == isPixel) return value;

        isPixel = value;
        return isPixel;
    }

    public enum HealthIconState {
        IDLE("idle"),
        WINNING("winning"),
        LOSING("losing"),
        TO_WINNING("toWinning"),
        TO_LOSING("toLosing"),
        FROM_WINNING("fromWinning"),
        FROM_LOSING("fromLosing");

        public final String state;

        HealthIconState(String state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return state;
        }
    }
}
