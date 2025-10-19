package com.nova.fnfjava.modding.events.handlers;

import com.nova.fnfjava.modding.events.StateEvent;

public interface IStateEvent extends IEventHandler<StateEvent> {
    @Override
    default void handle(StateEvent event) {
        onCreate();
    }

    default void onCreate() {
    }
}
