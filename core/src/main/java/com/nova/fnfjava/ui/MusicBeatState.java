package com.nova.fnfjava.ui;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Screen;
import com.nova.fnfjava.Conductor;

public class MusicBeatState implements Screen {
    @Override
    public void show() {
        Conductor.beatHit.add(this::beatHit);
        Conductor.stepHit.add(this::stepHit);
    }

    public void stepHit(Signal<Integer> integerSignal, Integer step) {
    }

    public void beatHit(Signal<Integer> integerSignal, Integer beat) {
    }

    @Override
    public void render(float delta) {
    }

    @Override
    public void resize(int width, int height) {
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

    @Override
    public void dispose() {
    }
}
