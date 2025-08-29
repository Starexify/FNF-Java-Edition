package com.nova.fnfjava.text;

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
