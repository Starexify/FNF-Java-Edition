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

    public Color bgColor;
    public boolean bgVisible;

    public boolean created = false;

    public MusicBeatSubState(Main main) {
        this(main, Color.CLEAR);
    }

    public MusicBeatSubState(Main main, Color bgColor) {
        super(main);
        setBgColor(bgColor);
        this.openCallback = null;
        this.closeCallback = null;

        this.persistentDraw = false;
        this.persistentUpdate = false;
    }

    @Override
    public void show() {
        super.show();

        if (openCallback != null) openCallback.run();
    }

    @Override
    public void render(float delta) {
        if (bgVisible && bgColor.a > 0) ScreenUtils.clear(bgColor);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
            main.switchState(new MainMenuState(main));
        }

        tryUpdate(delta);


        if (subState != null) subState.render(delta);
    }

    public void close() {
        if (closeCallback != null) closeCallback.run();
        if (parentState != null && parentState.subState == this) parentState.closeSubState();
    }

    public void switchSubState(MusicBeatSubState newSubState) {
        this.close();
        if (parentState != null) parentState.openSubState(newSubState);
    }

    @Override
    public void dispose() {
        super.dispose();
        closeCallback = null;
        openCallback = null;
        parentState = null;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        this.bgVisible = bgColor.a > 0;
    }
}
