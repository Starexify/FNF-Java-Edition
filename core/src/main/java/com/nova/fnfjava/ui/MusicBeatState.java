package com.nova.fnfjava.ui;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nova.fnfjava.Conductor;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.ScrollableStage;
import com.nova.fnfjava.text.FlxText;
import com.nova.fnfjava.text.FlxTextBorderStyle;
import com.nova.fnfjava.ui.transition.TransitionableScreenAdapter;

public class MusicBeatState extends TransitionableScreenAdapter {
    public final Main main;
    public ScrollableStage stage;

    public Listener<Integer> beatHitListener;
    public Listener<Integer> stepHitListener;

    public FlxText leftWatermarkText = null;
    public FlxText rightWatermarkText = null;

    public boolean persistentUpdate = false;
    public boolean persistentDraw = true;

    public MusicBeatSubState subState = null;
    public MusicBeatSubState requestedSubState = null;
    public boolean requestSubStateReset = false;

    public MusicBeatState(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        stage = new ScrollableStage(main.viewport, main.spriteBatch);
        createWatermarkText();

        beatHitListener = new Listener<Integer>() {
            @Override
            public void receive(Signal<Integer> signal, Integer object) {
                beatHit(signal, object);
            }
        };
        stepHitListener = new Listener<Integer>() {
            @Override
            public void receive(Signal<Integer> signal, Integer object) {
                stepHit(signal, object);
            }
        };

        Conductor.beatHit.add(beatHitListener);
        Conductor.stepHit.add(stepHitListener);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (requestSubStateReset) {
            requestSubStateReset = false;
            resetSubState();
        }

        tryUpdate(delta);

        if (subState == null || subState.getBgColor().a > 0) ScreenUtils.clear(Color.BLACK);
        if (persistentDraw || subState == null) stage.draw();
        if (subState != null) subState.render(delta);
    }

    public void tryUpdate(float delta) {
        // Update this state if no substate or if persistentUpdate is true
        if (persistentUpdate || subState == null) stage.act(delta);

        // Update substate if it exists
        if (subState != null) subState.tryUpdate(delta);
    }

    public void openSubState(MusicBeatSubState subState) {
        requestSubStateReset = true;
        requestedSubState = subState;
    }

    public void closeSubState() {
        requestSubStateReset = true;
        requestedSubState = null;
    }

    public void resetSubState() {
        // Close old substate
        if (subState != null) {
            if (subState.closeCallback != null) {
                subState.closeCallback.run();
            }
            subState.dispose();
        }

        // Set new substate
        subState = requestedSubState;
        requestedSubState = null;

        // Initialize new substate
        if (subState != null) {
            subState.parentState = this;
            if (!subState.created) {
                subState.created = true;
                subState.show();
            }
        }
    }

    public void createWatermarkText() {
        leftWatermarkText = new FlxText(10, 10, "");
        rightWatermarkText = new FlxText(stage.getWidth() - 10, 10, "");
        rightWatermarkText.setX(stage.getWidth() - rightWatermarkText.getWidth());

/*        leftWatermarkText.setScrollFactor(0, 0);
        rightWatermarkText.setScrollFactor(0, 0);*/
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
        if (beatHitListener != null) {
            Conductor.beatHit.remove(beatHitListener);
            beatHitListener = null;
        }

        if (stepHitListener != null) {
            Conductor.stepHit.remove(stepHitListener);
            stepHitListener = null;
        }

        if (subState != null) {
            subState.dispose();
            subState = null;
        }

        stage.dispose();
    }
}
