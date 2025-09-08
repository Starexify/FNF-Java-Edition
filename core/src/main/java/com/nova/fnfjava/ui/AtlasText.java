package com.nova.fnfjava.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.nova.fnfjava.graphics.AnimatedSprite;
import com.nova.fnfjava.Assets;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.group.TypedActorGroup;

import java.util.HashMap;
import java.util.Map;

public class AtlasText extends TypedActorGroup<AtlasText.AtlasChar> {
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

    public class AtlasChar extends AnimatedSprite {
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
                if (animation.exists("anim")) animation.play("anim");
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

    public enum Case {
        UPPER, LOWER, BOTH
    }

    public class AtlasFontData {
        public TextureAtlas atlas;
        public float maxHeight = 0f;
        public Case caseAllowed = Case.BOTH;

        public AtlasFontData(AtlasFont name) {
            String fontName = name.toString().toLowerCase();
            atlas = Assets.getAtlas("assets/images/fonts/" + fontName + ".atlas");
            if (atlas == null) {
                Main.logger.setTag(this.getClass().getSimpleName()).warn("Could not find font atlas for font \"" + name + "\".");
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

    public enum AtlasFont {
        DEFAULT, BOLD, PIXEL
    }
}
