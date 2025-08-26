package com.nova.fnfjava.ui;

import com.badlogic.ashley.signals.Signal;
import com.nova.fnfjava.AnimatedSprite;

public abstract class MenuListItem extends AnimatedSprite {
    public String name;
    public Signal<MenuListItem> callback;
    public boolean available;

    public boolean fireInstantly = false;

    public MenuListItem(float x, float y, String name, Signal<MenuListItem> callback, boolean available) {
        super(x, y);
        this.name = name;
        this.callback = callback;
        this.available = available;
        setData(name, callback, available);
        idle();
    }

    public void execute() {
        if (callback != null && available) callback.dispatch(this);
    }

    public void setData(String name, Signal<MenuListItem> callback, boolean available) {
        this.name = name;
        if (callback != null) this.callback = callback;
        this.available = available;
    }

    public void setItem(String name, Signal<MenuListItem> callback) {
        setData(name, callback, this.available);
        if (isSelected()) select();
        else idle();
    }

    public boolean isSelected() {
        return getColor().a == 1.0f;
    }

    public void idle() {
        getColor().a = 0.6f;
    }

    public void select() {
        getColor().a = 1.0f;
    }
}
