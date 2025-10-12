package com.nova.fnfjava.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.nova.fnfjava.input.actions.GameAction;

public class Controls extends InputAdapter {
    public GameAction uiUp = new GameAction();
    public GameAction uiDown = new GameAction();
    public GameAction uiLeft = new GameAction();
    public GameAction uiRight = new GameAction();
    public GameAction accept = new GameAction();

    public boolean UI_UP_P() {
        return uiUp.isJustPressed();
    }

    public boolean UI_DOWN_P() {
        return uiDown.isJustPressed();
    }

    public boolean UI_LEFT_P() {
        return uiLeft.isJustPressed();
    }

    public boolean UI_RIGHT_P() {
        return uiRight.isJustPressed();
    }

    public boolean ACCEPT() {
        return accept.isJustPressed();
    }

    public Controls(String name, KeyboardScheme scheme) {
        if (scheme == null) scheme = KeyboardScheme.NONE;
    }

    public void update() {
        uiUp.update(Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP));
        uiDown.update(Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN));
        uiLeft.update(Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT));
        uiRight.update(Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT));
        accept.update(Gdx.input.isKeyJustPressed(Input.Keys.Z) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER));
    }
}

enum Action {
    // UI
    UI_UP_P("ui_up-press");

    public final String value;

    Action(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

