package com.nova.fnfjava.ui.options.items;

import com.nova.fnfjava.Paths;
import com.nova.fnfjava.graphics.AnimatedSprite;

public class CheckboxPreferenceItem extends AnimatedSprite {
    public boolean currentValue;

    public CheckboxPreferenceItem(float x, float y, boolean defaultValue, boolean available) {
        super(x, y);

        setAtlas(Paths.getAtlas("checkboxThingie"));

        animation.addByPrefix("static", "Check Box unselected", 24, false);
        animation.addByPrefix("checked", "Check Box selecting animation", 24, false);

        setWidth(getWidth() * 0.7f);

        if (!available) setAlpha(0.5f);

        setCurrentValue(defaultValue);
    }

    public CheckboxPreferenceItem(float x, float y) {
        this(x, y, false, true);
    }

    public void setCurrentValue(boolean value) {
        if (value) animation.play("checked", true);
        else animation.play("static");

        currentValue = value;
    }
}
