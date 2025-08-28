package com.nova.fnfjava;

import com.badlogic.ashley.signals.Signal;
import com.nova.fnfjava.util.Constants;

public class Conductor {
    private static Conductor _instance;

    private float bpm;
    private float stepLength;
    private int stepsPerMeasure = 16;

    private int currentStep = -1;
    public int currentBeat = 0;
    private int currentMeasure = -1;

    public static final Signal<Integer> stepHit = new Signal<>();
    public static final Signal<Integer> beatHit = new Signal<>();
    public static final Signal<Integer> measureHit = new Signal<>();

    public Conductor() {
        this.bpm = Constants.DEFAULT_BPM;
        this.stepLength = 60f / bpm / 4f;
    }

    public static Conductor getInstance() {
        if (_instance == null) _instance = new Conductor();
        return _instance;
    }

    public static void setInstance(Conductor newInstance) {
        _instance = newInstance;
    }

    public void update() {
        if (!Main.sound.isMusicPlaying()) return;

        float songPos = Main.sound.getMusicTime();

        int step = (int)(songPos / stepLength);
        int beat = step / 4;
        int measure = step / stepsPerMeasure;

        if (step != currentStep) {
            currentStep = step;
            stepHit.dispatch(step);
        }
        if (beat != currentBeat) {
            currentBeat = beat;
            beatHit.dispatch(beat);
        }
        if (measure != currentMeasure) {
            currentMeasure = measure;
            measureHit.dispatch(measure);
        }
    }
}
