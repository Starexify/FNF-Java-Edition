package com.nova.fnfjava.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nova.fnfjava.Main;

public class MusicBeatSubState extends MusicBeatState {
    public Runnable openCallback;
    public Runnable closeCallback;

    public MusicBeatState parentState;

    private Color backgroundColor = Color.CLEAR;

    private boolean created = false;

    public MusicBeatSubState(Main main) {
        this(main, Color.CLEAR);
    }

    public MusicBeatSubState(Main main, Color bgColor) {
        super(main);
        this.backgroundColor = bgColor;
        this.openCallback = null;
        this.closeCallback = null;
    }

    @Override
    public void show() {
        super.show();
        if (!created) {
            created = true;
            create();
        }

        if (openCallback != null) {
            openCallback.run();
        }
    }

    public void create() {}

    @Override
    public void render(float delta) {
        if (backgroundColor.a > 0) ScreenUtils.clear(backgroundColor);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
            main.switchState(new MainMenuState(main));
        }

        super.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        closeCallback = null;
        openCallback = null;
        parentState = null;
    }

    public void close() {
        if (closeCallback != null) {
            closeCallback.run();
        }

        if (parentState != null && parentState.currentSubState == this) {
            parentState.closeSubState();
        }
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }
}
