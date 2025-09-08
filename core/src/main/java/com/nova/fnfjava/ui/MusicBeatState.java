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
import com.nova.fnfjava.modding.events.SongTimeEvent;
import com.nova.fnfjava.text.FlxText;
import com.nova.fnfjava.ui.transition.TransitionableScreenAdapter;

import java.util.Comparator;

public class MusicBeatState extends TransitionableScreenAdapter {
    public static Main main;
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
        stage.setParentState(this);
        createWatermarkText();

        beatHitListener = new Listener<Integer>() {
            @Override
            public void receive(Signal<Integer> signal, Integer beat) {
                beatHit(signal, beat);
                SongTimeEvent.postBeatHit(beat);
            }
        };
        stepHitListener = new Listener<Integer>() {
            @Override
            public void receive(Signal<Integer> signal, Integer step) {
                stepHit(signal, step);
                SongTimeEvent.postStepHit(step);
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
        leftWatermarkText.setFormat("VCR OSD Mono", 16, Color.WHITE, FlxText.FlxTextBorderStyle.OUTLINE, Color.BLACK);
        rightWatermarkText.setFormat("VCR OSD Mono", 16, Color.WHITE, FlxText.FlxTextBorderStyle.OUTLINE, Color.BLACK);

        add(leftWatermarkText);
        add(rightWatermarkText);
    }

    public void add(Actor actor) {
        stage.addActor(actor);
    }

    public void addWithScrollFactor(Actor actor, float scrollFactorX, float scrollFactorY) {
        stage.addActorWithScrollFactor(actor, scrollFactorX, scrollFactorY);
    }

    public void addWithScrollFactor(Actor actor) {
        addWithScrollFactor(actor, 1.0f, 1.0f);
    }

    public void stepHit(Signal<Integer> integerSignal, Integer step) {
    }

    public void beatHit(Signal<Integer> integerSignal, Integer beat) {
    }

    public boolean handleMouseWheel(float amountY) {
        return false;
    }

    public void refresh() {
        stage.getActors().sort(Comparator.comparingInt(Actor::getZIndex));
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
