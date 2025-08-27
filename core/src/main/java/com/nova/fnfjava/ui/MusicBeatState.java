package com.nova.fnfjava.ui;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nova.fnfjava.Conductor;
import com.nova.fnfjava.Main;

public class MusicBeatState implements Screen {
    protected final Main main;
    protected Stage stage;

    public MusicBeatState(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        stage = new Stage(main.viewport, main.spriteBatch);

        Conductor.beatHit.add(this::beatHit);
        Conductor.stepHit.add(this::stepHit);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();
    }

    protected void add(Actor actor) {
        stage.addActor(actor);
    }

    public void stepHit(Signal<Integer> integerSignal, Integer step) {
    }

    public void beatHit(Signal<Integer> integerSignal, Integer beat) {
    }

    @Override
    public void resize(int width, int height) {
        main.viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();

        Conductor.beatHit.remove(this::beatHit);
        Conductor.stepHit.remove(this::stepHit);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
