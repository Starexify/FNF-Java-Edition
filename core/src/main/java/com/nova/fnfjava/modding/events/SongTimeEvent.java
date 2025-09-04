package com.nova.fnfjava.modding.events;

import com.nova.fnfjava.modding.bus.EventBus;

public class SongTimeEvent extends Event {
    public final int beat;
    public final int step;

    public static int currentBeat = 0;
    public static int currentStep = 0;

    public SongTimeEvent(int beat, int step) {
        this.beat = beat;
        this.step = step;
    }

    public static void postBeatHit(int beat) {
        currentBeat = beat;
        EventBus.getInstance().post(new SongTimeEvent(beat, currentStep));
    }

    public static void postStepHit(int step) {
        currentStep = step;
        EventBus.getInstance().post(new SongTimeEvent(currentBeat, step));
    }
}
