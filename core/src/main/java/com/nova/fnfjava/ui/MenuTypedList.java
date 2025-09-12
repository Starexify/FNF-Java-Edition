package com.nova.fnfjava.ui;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.graphics.AnimatedSprite;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.PlayerSettings;
import com.nova.fnfjava.group.TypedActorGroup;
import com.nova.fnfjava.input.Controls;
import com.nova.fnfjava.ui.mainmenu.MainMenuState;

public class MenuTypedList<T extends MenuTypedList.MenuListItem> extends TypedActorGroup<T> {
    public static boolean pauseInput = false;

    public int selectedIndex = 0;

    public Signal<T> onChange = new Signal<>();
    public Signal<T> onAcceptPress = new Signal<>();

    public NavControls navControls;
    public boolean enabled = true;
    public WrapMode wrapMode = WrapMode.BOTH;

    public ObjectMap<String, T> byName = new ObjectMap<>();

    public boolean busy = false;
    public boolean isMainMenuState = false;
    public Array<T> items = new Array<>();

    public MenuTypedList(NavControls navControls, WrapMode wrapMode) {
        this.navControls = navControls;

        if (wrapMode != null) this.wrapMode = wrapMode;
        else {
            this.wrapMode = switch (navControls) {
                case HORIZONTAL -> WrapMode.HORIZONTAL;
                case VERTICAL -> WrapMode.VERTICAL;
                default -> WrapMode.BOTH;
            };
        }
        isMainMenuState = (Main.instance.getScreen() instanceof MainMenuState);
    }

    public MenuTypedList() {
        this(NavControls.VERTICAL, null);
    }

    public T addItem(String name, T item) {
        if (members.size == selectedIndex) item.select();

        byName.put(name, item);
        items.add(item);
        add(item);
        return item;
    }

    public T resetItem(String oldName, String newName, Runnable callback) {
        T item = byName.get(oldName);
        if (item == null) throw new IllegalArgumentException("No item named: " + oldName);
        byName.remove(oldName);
        byName.put(newName, item);
        item.setItem(newName, callback);

        return item;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (enabled && !busy && !pauseInput) updateControls();
    }

    public void updateControls() {
        Controls controls = PlayerSettings.player1.controls;

        boolean wrapX = wrapMode.match(WrapMode.HORIZONTAL, WrapMode.BOTH);
        boolean wrapY = wrapMode.match(WrapMode.VERTICAL, WrapMode.BOTH);

        int newIndex = 0;

        final boolean inputUp = controls.UI_UP_P() || !isMainMenuState;
        final boolean inputDown = controls.UI_DOWN_P() || !isMainMenuState;
        final boolean inputLeft = controls.UI_LEFT_P() || !isMainMenuState;
        final boolean inputRight = controls.UI_RIGHT_P() || !isMainMenuState;

        newIndex = switch (navControls) {
            case VERTICAL -> navList(inputUp, inputDown, wrapY);
            case HORIZONTAL -> navList(inputLeft, inputRight, wrapX);
            case BOTH -> navList(inputLeft || inputUp, inputRight || inputDown, !wrapMode.match(WrapMode.NONE));

            case COLUMNS -> 0;
            case ROWS -> 0;
        };

        if (newIndex != selectedIndex) {
            Main.sound.playOnce(Paths.sound("scrollMenu"), 0.4f);
            selectItem(newIndex);
        }

        if (controls.ACCEPT()) accept();

        return;
    }

    public int navAxis(int index, int size, boolean prev, boolean next, boolean allowWrap) {
        if (prev == next) return index;

        if (prev) {
            if (index > 0) index--;
            else if (allowWrap) index = size - 1;
        }
        else {
            if (index < size - 1) index++;
            else if (allowWrap) index = 0;
        }

        return index;
    }

    public int navList(boolean prev, boolean next, boolean allowWrap) {
        return navAxis(selectedIndex, getChildren().size, prev, next, allowWrap);
    }

    public void accept() {
        T menuItem = items.get(selectedIndex);

        if (!menuItem.available) return;

        onAcceptPress.dispatch(menuItem);

        if (menuItem.fireInstantly) menuItem.callback();
        else {
            busy = true;
            Main.sound.playOnce(Paths.sound("confirmMenu"));
            menuItem.callback();
        }
    }

    public void selectItem(int index) {
        ((MenuListItem) getChild(selectedIndex)).idle();

        if (!((MenuListItem) getChild(index)).available) {
            if (index < selectedIndex) {
                final int newIndex = (index - 1 < 0) ? index + 1 : index - 1;
                selectItem(newIndex);
                return;
            }
            else if (index > selectedIndex) {
                final int newIndex = (index + 1 > getChildren().size) ? index - 1 : index + 1;
                selectItem(newIndex);
                return;
            }
        }

        selectedIndex = index;

        T selectedMenuItem = (T) getChild(selectedIndex);
        selectedMenuItem.select();
        onChange.dispatch(selectedMenuItem);
    }

    @Override
    public boolean remove() {
        byName.clear();
        onChange.removeAllListeners();
        onAcceptPress.removeAllListeners();
        return super.remove();
    }

    public static class MenuListItem extends AnimatedSprite {
        public Runnable callback;
        public String name;
        public boolean available;

        public boolean fireInstantly = false;

        public boolean getSelected() {
            return getColor().a == 1.0f;
        }

        public MenuListItem(float x, float y, String name, Runnable callback, boolean available) {
            super(x, y);

            this.name = name;
            this.callback = callback;
            this.available = available;
            setData(name, callback, available);
            idle();
        }

        public void setData(String name, Runnable callback, boolean available) {
            this.name = name;
            if (callback != null) this.callback = callback;
            this.available = available;
        }

        public void setItem(String name, Runnable callback) {
            setData(name, callback, this.available);

            if (getSelected()) select();
            else idle();
        }

        public void idle() {
            getColor().a = 0.6f;
        }

        public void select() {
            getColor().a = 1.0f;
        }

        public void callback() {
            if (callback != null && available) callback.run();
        }
    }

    static class MenuTypedItem<T extends Actor> extends MenuListItem {
        public T label;

        public MenuTypedItem(float x, float y, T label, String name, Runnable callback, boolean available) {
            super(x, y, name, callback, available);
            this.label = label;
        }

        public MenuTypedItem(T label, String name, Runnable callback) {
            this(0, 0, label, name, callback, true);
        }

        public T setLabel(T value) {
            if (value != null) {
                value.setX(this.getX());
                value.setY(this.getY());
                value.getColor().a = this.getColor().a;
            }
            return this.label = value;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (label != null) label.act(delta);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);

            if (label != null) {
           /*     label.cameras = cameras;
                label.scrollFactor.copyFrom(scrollFactor);*/
                label.draw(batch, parentAlpha);
            }
        }


    }

    public enum NavControls {
        HORIZONTAL,
        VERTICAL,
        BOTH,
        COLUMNS,
        ROWS
    }

    public enum WrapMode {
        NONE,
        HORIZONTAL,
        VERTICAL,
        BOTH;

        public boolean match(WrapMode... modes) {
            for (WrapMode mode : modes) {
                if (this == mode) return true;
            }
            return false;
        }
    }
}
