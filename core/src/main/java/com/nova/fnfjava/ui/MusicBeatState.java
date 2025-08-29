package com.nova.fnfjava.ui;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nova.fnfjava.Conductor;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.text.FlxText;
import com.nova.fnfjava.text.FlxTextBorderStyle;

public class MusicBeatState extends ScreenAdapter {
    public final Main main;
    public Stage stage;

    public FlxText leftWatermarkText = null;
    public FlxText rightWatermarkText = null;

    public MusicBeatState(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        stage = new Stage(main.viewport, main.spriteBatch);

        createWatermarkText();

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

    public void createWatermarkText() {
        leftWatermarkText = new FlxText(10, 10, "");
        rightWatermarkText = new FlxText(stage.getWidth() - 10, 10, "");
        rightWatermarkText.setX(stage.getWidth() - rightWatermarkText.getWidth());

        leftWatermarkText.setFormat("VCR_OSD_MONO", 16, Color.WHITE, FlxTextBorderStyle.OUTLINE, Color.BLACK);
        rightWatermarkText.setFormat("VCR_OSD_MONO", 16, Color.WHITE, FlxTextBorderStyle.OUTLINE, Color.BLACK);

        add(leftWatermarkText);
        add(rightWatermarkText);
    }

    public void add(Actor actor) {
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
}
