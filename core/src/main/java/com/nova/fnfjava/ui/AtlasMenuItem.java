package com.nova.fnfjava.ui;

import com.badlogic.ashley.signals.Signal;

public class AtlasMenuItem extends MenuListItem {
    public AtlasMenuItem(float x, float y, String name, Signal<MenuListItem> callback, boolean available) {
        super(x, y, name, callback, available);
    }
}
