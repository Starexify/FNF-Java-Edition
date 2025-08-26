package com.nova.fnfjava.ui;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MenuTypedList<T extends MenuListItem> extends Group {
    private Array<T> items = new Array<>();
    private int selectedIndex = 0;
    private ObjectMap<String, T> byName = new ObjectMap<>();

    public Signal<T> onChange = new Signal<>();
    public Signal<T> onAcceptPress = new Signal<>();

    public NavControls navControls = NavControls.VERTICAL;
    public WrapMode wrapMode = WrapMode.BOTH;

    public boolean enabled = true;
    public boolean busy = false;
    public static boolean pauseInput = false;

    public MenuTypedList(NavControls navControls, WrapMode wrapMode) {
        this.navControls = navControls;

        if (wrapMode != null) this.wrapMode = wrapMode;
        else {
            // Auto-set wrap mode based on nav controls
            switch (navControls) {
                case HORIZONTAL:
                    this.wrapMode = WrapMode.HORIZONTAL;
                    break;
                case VERTICAL:
                    this.wrapMode = WrapMode.VERTICAL;
                    break;
                default:
                    this.wrapMode = WrapMode.BOTH;
                    break;
            }
        }
    }

    public MenuTypedList() {
        this(NavControls.VERTICAL, null);
    }
}

enum NavControls {
    HORIZONTAL,
    VERTICAL,
    BOTH,
    COLUMNS,
    ROWS
}

enum WrapMode {
    NONE,
    HORIZONTAL,
    VERTICAL,
    BOTH
}
