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
        uiUp.update(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP));
        uiDown.update(Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN));
        uiLeft.update(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT));
        uiRight.update(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT));
        accept.update(Gdx.input.isKeyPressed(Input.Keys.Z) || Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.ENTER));
    }
}

enum Action {

    // UI
    UI_UP_P("ui_up-press");

    private final String value;

    Action(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

