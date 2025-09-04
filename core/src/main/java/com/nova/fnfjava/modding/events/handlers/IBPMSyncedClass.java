package com.nova.fnfjava.modding.events.handlers;

import com.nova.fnfjava.modding.events.SongTimeEvent;

public interface IBPMSyncedClass extends IEventHandler<SongTimeEvent> {
    @Override
    default void handle(SongTimeEvent event) {
        onBeatHit(event.beat);
        onStepHit(event.step);
    }

    default void onBeatHit(int beat) { }
    default void onStepHit(int step) { }
}
