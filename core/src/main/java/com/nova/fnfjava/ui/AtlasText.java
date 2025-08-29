package com.nova.fnfjava.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.nova.fnfjava.AnimatedSprite;
import com.nova.fnfjava.Assets;
import com.nova.fnfjava.Axes;

import java.util.HashMap;
import java.util.Map;

public class AtlasText extends Group {
    public static Map<AtlasFont, AtlasFontData> fonts = new HashMap<>();
    public Case getCaseAllowed() {
        return font != null ? font.caseAllowed : Case.BOTH;
    }

    public String text = "";

    public AtlasFontData font = new AtlasFontData(AtlasFont.DEFAULT);

    public TextureAtlas getAtlas() {
        return font.atlas;
    }

    public float getMaxHeight() {
        return font != null ? font.maxHeight : 0f;
    }

    public AtlasText(float x, float y, String text, AtlasFont fontName) {
        super();
        setPosition(x, y);

        if (!fonts.containsKey(fontName)) fonts.put(fontName, new AtlasFontData(fontName));
        font = fonts.get(fontName);

        setText(text);
    }

    public void setText(String value) {
        if (value == null) value = "";

        String caseValue = restrictCase(value);
        String caseText = restrictCase(this.text);

        this.text = value;
        if (caseText.equals(caseValue)) return;

        if (caseValue.indexOf(caseText) == 0) {
            appendTextCased(caseValue.substring(caseText.length()));
            return;
        }

        value = caseValue;

        kill();

        if (value.isEmpty()) return;
        appendTextCased(caseValue);
    }

    public String restrictCase(String text) {
        return switch (getCaseAllowed()) {
            case BOTH -> text;
            case UPPER -> text.toUpperCase();
            case LOWER -> text.toLowerCase();
        };
    }

    public void appendTextCased(String text) {
        int charCount = countLiving();
        float xPos = 0f;
        float yPos = 0f;

        if (charCount == -1) charCount = 0;
        else if (charCount > 0) {
            Actor lastChar = getChild(charCount - 1);
            xPos = lastChar.getX() + lastChar.getWidth() - getX();
            yPos = lastChar.getY() + lastChar.getHeight() - getMaxHeight() - getY();
        }

        char[] splitValues = text.toCharArray();
        for (int i = 0; i < splitValues.length; i++) {
            char currentChar = splitValues[i];
            switch (currentChar) {
                case ' ':
                    xPos += 40;
                    break;
                case '\n':
                    xPos = 0;
                    yPos += getMaxHeight();
                    break;
                default:
                    AtlasChar charSprite;
                    if (getChildren().size <= charCount) charSprite = new AtlasChar(getAtlas(), String.valueOf(currentChar));
                    else {
                        charSprite = (AtlasChar) getChild(charCount);
                        reviveCharacter(charSprite);
                        charSprite.setChar(String.valueOf(currentChar));
                        charSprite.getColor().a = 1f;
                    }
                    charSprite.setX(xPos);
                    charSprite.setY(yPos + getMaxHeight() - charSprite.getHeight());
                    add(charSprite);

                    xPos += charSprite.getWidth();
                    charCount++;
            }
        }
    }

    public AtlasText screenCenter(Axes axes) {
        if (axes.hasX()) setX((Gdx.graphics.getWidth() - getWidth()) / 2f);
        if (axes.hasY()) setY((Gdx.graphics.getHeight() - getHeight()) / 2f);
        return this;
    }

    public AtlasText screenCenter() {
        return screenCenter(Axes.XY);
    }

    public int countLiving() {
        int count = -1;
        for (Actor child : getChildren()) {
            if (child != null) {
                if (count < 0) count = 0;
                if (child instanceof AtlasChar && isCharacterAlive((AtlasChar) child)) {
                    count++;
                }
            }
        }
        return count;
    }

    public void kill() {
        for (Actor child : getChildren()) if (child instanceof AtlasChar) killCharacter((AtlasChar) child);
    }

    public void killCharacter(AtlasChar character) {
        // Similar to Flixel's kill() - hide but don't remove for reuse
        character.setVisible(false);
        character.getColor().a = 0f;
    }

    public boolean isCharacterAlive(AtlasChar character) {
        // In libGDX, we use visibility and alpha to simulate Flixel's exists/alive
        return character.isVisible() && character.getColor().a > 0f;
    }

    public void reviveCharacter(AtlasChar character) {
        // Similar to Flixel's revive() - make visible again
        character.setVisible(true);
        character.getColor().a = 1f;
    }

    public void add(AtlasChar character) {
        if (!getChildren().contains(character, true)) {
            addActor(character);
        }
        character.setVisible(true);
    }

    @Override
    public float getWidth() {
        if (getChildren().size == 0) return 0;
        return findMaxX() - findMinX();
    }

    @Override
    public float getHeight() {
        if (getChildren().size == 0) return 0;
        return findMaxY() - findMinY();
    }

    public float findMinX() {
        float value = Float.POSITIVE_INFINITY;
        for (Actor child : getChildren()) {
            if (child instanceof AtlasChar && isCharacterAlive((AtlasChar) child)) {
                if (child.getX() < value) {
                    value = child.getX();
                }
            }
        }
        return value == Float.POSITIVE_INFINITY ? getX() : value;
    }

    public float findMaxX() {
        float value = Float.NEGATIVE_INFINITY;
        for (Actor child : getChildren()) {
            if (child instanceof AtlasChar && isCharacterAlive((AtlasChar) child)) {
                float maxX = child.getX() + child.getWidth();
                if (maxX > value) {
                    value = maxX;
                }
            }
        }
        return value == Float.NEGATIVE_INFINITY ? getX() : value;
    }

    public float findMinY() {
        float value = Float.POSITIVE_INFINITY;
        for (Actor child : getChildren()) {
            if (child instanceof AtlasChar && isCharacterAlive((AtlasChar) child)) {
                if (child.getY() < value) {
                    value = child.getY();
                }
            }
        }
        return value == Float.POSITIVE_INFINITY ? getY() : value;
    }

    public float findMaxY() {
        float value = Float.NEGATIVE_INFINITY;
        for (Actor child : getChildren()) {
            if (child instanceof AtlasChar && isCharacterAlive((AtlasChar) child)) {
                float maxY = child.getY() + child.getHeight();
                if (maxY > value) {
                    value = maxY;
                }
            }
        }
        return value == Float.NEGATIVE_INFINITY ? getY() : value;
    }
}

class AtlasChar extends AnimatedSprite {
    public String character;

    public AtlasChar(float x, float y, TextureAtlas atlas, String character) {
        super(x, y);
        this.atlas = atlas;
        setChar(character);
    }

    public AtlasChar(TextureAtlas atlas, String  character) {
        this(0, 0, atlas, character);
    }

    public String setChar(String value) {
        if (!value.equals(this.character)) {
            String prefix = getAnimPrefix(value);
            animation.addByPrefix("anim", prefix, 24);
            if (animation.exists("anim")) {
                animation.play("anim");
            }
            updateHitboxFromCurrentFrame();
        }
        return this.character = value;
    }

    public String getAnimPrefix(String character) {
        return switch (character) {
            case "&" -> "-andpersand-";
            case "😠" -> "-angry faic-"; // TODO: Do multi-flag characters work?
            case "'" -> "-apostraphie-";
            case "\\" -> "-back slash-";
            case "," -> "-comma-";
            case "-" -> "-dash-";
            case "↓" -> "-down arrow-"; // U+2193
            case "”" -> "-end quote-"; // U+0022
            case "!" -> "-exclamation point-"; // U+0021
            case "/" -> "-forward slash-"; // U+002F
            case ">" -> "-greater than-"; // U+003E
            case "♥" -> "-heart-"; // U+2665
            case "♡" -> "-heart-";
            case "←" -> "-left arrow-"; // U+2190
            case "<" -> "-less than-"; // U+003C
            case "*" -> "-multiply x-";
            case "." -> "-period-"; // U+002E
            case "?" -> "-question mark-";
            case "→" -> "-right arrow-"; // U+2192
            case "“" -> "-start quote-";
            case "↑" -> "-up arrow-"; // U+2191

            // Default to getting the character itself.
            default -> character;
        };
    }
}

class AtlasFontData {
    public TextureAtlas atlas;
    public float maxHeight = 0f;
    public Case caseAllowed = Case.BOTH;

    public AtlasFontData(AtlasFont name) {
        String fontName = name.toString().toLowerCase();
        atlas = Assets.getAtlas("images/fonts/" + fontName + ".atlas");
        if (atlas == null) {
            Gdx.app.log("AtlasFontData", "[WARN] Could not find font atlas for font \"" + name + "\".");
            return;
        }

        boolean containsUpper = false;
        boolean containsLower = false;

        for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
            maxHeight = Math.max(maxHeight, region.getRegionHeight());
            if (!containsUpper && Character.isUpperCase(region.name.charAt(0))) containsUpper = true;
            if (!containsLower && Character.isLowerCase(region.name.charAt(0))) containsLower = true;
        }

        if (containsUpper != containsLower) caseAllowed = containsUpper ? Case.UPPER : Case.LOWER;
    }
}

enum Case {
    UPPER, LOWER, BOTH
}

enum AtlasFont {
    DEFAULT, BOLD, PIXEL
}
