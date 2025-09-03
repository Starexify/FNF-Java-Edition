package com.nova.fnfjava.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.nova.fnfjava.Paths;

public class FlxText extends Label implements Disposable {
    public String font;
    public int size;
    public Color color;

    public FlxTextBorderStyle borderStyle = FlxTextBorderStyle.NONE;
    public Color borderColor = Color.CLEAR;
    public float borderSize = 1.5f;

    public boolean regen = true;

    public BitmapFont currentFont;

    public FlxText(float x, float y, CharSequence text) {
        super(text, new LabelStyle(new BitmapFont(), Color.WHITE));
        setPosition(x, y);
    }

    public void setColor(String color) {
        this.setColor(Color.valueOf(color));
    }

    public FlxText setFormat(String font, int size, Color color, FlxTextBorderStyle borderStyle, Color borderColor) {
        this.font = font;
        this.size = size;
        this.color = color;

        setBorderStyling(borderStyle, borderColor);
        regenerateFont();

        return this;
    }

    public FlxText setFormat(String font, int size, Color color) {
        return setFormat(font, size, color, FlxTextBorderStyle.NONE, Color.CLEAR);
    }

    public FlxText setFormat(String font, int size) {
        return setFormat(font, size, Color.WHITE, FlxTextBorderStyle.NONE, Color.CLEAR);
    }

    public FlxText setBorderStyling(FlxTextBorderStyle style, Color color) {
        setBorderStyle(style);
        setBorderColor(color);

        return this;
    }

    public FlxTextBorderStyle setBorderStyle(FlxTextBorderStyle style) {
        if (style != borderStyle) regen = true;
        return borderStyle = style;
    }

    public Color setBorderColor(Color color) {
        if (borderColor != color && borderStyle != FlxTextBorderStyle.NONE) regen = true;
        return borderColor = color;
    }

    public void regenerateFont() {
        if (currentFont != null) currentFont.dispose();

        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Paths.font(font) + ".ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

            parameter.size = size;
            parameter.color = color;

            switch (borderStyle) {
                case SHADOW:
                    parameter.shadowOffsetX = (int) borderSize;
                    parameter.shadowOffsetY = -(int) borderSize;
                    parameter.shadowColor = borderColor;
                    break;

                case SHADOW_XY:
                    parameter.shadowOffsetX = (int) borderStyle.offsetX;
                    parameter.shadowOffsetY = (int) borderStyle.offsetY;
                    parameter.shadowColor = borderColor;
                    break;

                case OUTLINE:
                case OUTLINE_FAST:
                    parameter.borderWidth = borderSize;
                    parameter.borderColor = borderColor;
                    break;

                case NONE:
                default:
                    // No effects
                    break;
            }

            currentFont = generator.generateFont(parameter);
            generator.dispose();

            this.setStyle(new Label.LabelStyle(currentFont, color));

        } catch (Exception e) {
            System.err.println("Error loading font: " + font);
            this.setStyle(new Label.LabelStyle(new BitmapFont(), color));
        }
    }

    @Override
    public void dispose() {
        if (currentFont != null) {
            currentFont.dispose();
            currentFont = null;
        }
    }

    public void addText(String text) {
        setText(getText() + text);
    }

    public enum FlxTextBorderStyle {
        NONE,

        /** A simple shadow to the lower-right */
        SHADOW,

        /** A shadow that allows custom placement */
        SHADOW_XY,

        /** Outline on all 8 sides */
        OUTLINE,

        /** Outline, optimized using only 4 draw calls */
        OUTLINE_FAST;

        // For SHADOW_XY style
        public float offsetX = 1.0f;
        public float offsetY = -1.0f;

        public static FlxTextBorderStyle shadowXY(float offsetX, float offsetY) {
            FlxTextBorderStyle style = SHADOW_XY;
            style.offsetX = offsetX;
            style.offsetY = offsetY;
            return style;
        }
    }
}
