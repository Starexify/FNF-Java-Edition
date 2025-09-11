package com.nova.fnfjava.play.components;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.nova.fnfjava.Assets;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.graphics.FunkinSprite;
import com.nova.fnfjava.play.PlayState;
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

            if (data.offsets != null && data.offsets.size == 2)
                this.iconOffset.set(data.offsets.get(0), data.offsets.get(1));
            else this.iconOffset.set(0, 0);

            //this.flipX = data.flipX ?? false;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        this.updatePosition();
    }

    public void updatePosition() {
        if (autoUpdate && PlayState.instance != null) {
            switch (playerId) {
                case 0: // Boyfriend
                    // Update the animation based on the current state.
                    updateHealthIcon(PlayState.instance.health);
                    // Update the position to match the health bar.
                    //this.setX(PlayState.instance.healthBar.x
                    //    + (PlayState.instance.healthBar.width * (MathUtils.map(0, 2, 100, 0, PlayState.instance.healthBar.value) * 0.01f) - POSITION_OFFSET));
                case 1: // Dad
                    // Update the animation based on the current state.
                    updateHealthIcon(MAXIMUM_HEALTH - PlayState.instance.health);
                    // Update the position to match the health bar.
                    //this.setX(PlayState.instance.healthBar.x
                    //    + (PlayState.instance.healthBar.width * (MathUtils.map(0, 2, 100, 0, PlayState.instance.healthBar.value) * 0.01f))
                    //    - (this.getWidth() - POSITION_OFFSET));
            }

            //this.setFlxY(PlayState.instance.healthBar.y - (this.getHeight() / 2)); // - (PlayState.instance.healthBar.height / 2)

            offset.add(iconOffset);
        }
    }

    public void updateHealthIcon(float health) {
        switch (getCurrentAnimation()) {
            /*switch (getCurrentAnimation()) {
                case HealthIconState.IDLE:
                    if (health < LOSING_THRESHOLD) playAnimation(ToLosing, Losing);
                    else if (health > WINNING_THRESHOLD) playAnimation(ToWinning, Winning);
                    else playAnimation(Idle);
                    break;

                case HealthIconState.WINNING:
                    if (health < WINNING_THRESHOLD) playAnimation(FromWinning, Idle);
                    else playAnimation(Winning, Idle);
                    break;

                case HealthIconState.LOSING:
                    if (health > LOSING_THRESHOLD) playAnimation(FromLosing, Idle);
                    else playAnimation(Losing, Idle);
                    break;

                case HealthIconState.TO_LOSING:
                    if (isAnimationFinished()) playAnimation(Losing, Idle);
                    break;

                case HealthIconState.TO_WINNING:
                    if (isAnimationFinished()) playAnimation(Winning, Idle);
                    break;

                case HealthIconState.FROM_LOSING | HealthIconState.FROM_WINNING:
                    if (isAnimationFinished()) playAnimation(Idle);
                    break;

                case "":
                    playAnimation(Idle);
                    break;

                default:
                    playAnimation(Idle);
                    break;
            }*/
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

    // Getters/Setters
    public String getCurrentAnimation() {
        if (this.animation == null || this.animation.curAnim == null) return "";
        return this.animation.curAnim.name;
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
