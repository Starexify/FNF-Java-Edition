package com.nova.fnfjava.ui;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.input.Controls;

public class Page<T> extends Group {
    public final Signal<T> onSwitch = new Signal<>();
    public final Signal<Void> onExit = new Signal<>();

    public boolean enabled = true;
    public boolean canExit = true;

    public Codex<T> codex;

    public void switchPage(T name) {
        onSwitch.dispatch(name);
    }

    public void exit() {
        onExit.dispatch(null);
    }

    public void add(Actor actor) {
        addActor(actor);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (enabled) updateEnabled(delta);
    }

    public void updateEnabled(float delta) {
        if (canExit && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            exit();
            Main.sound.playOnce(Paths.sound("cancelMenu"));
        }
    }
}
