package com.nova.fnfjava.modding.events;

import com.nova.fnfjava.modding.bus.EventBus;

public class StateEvent extends Event {
    public StateEvent() {
    }

    public static void postCreate() {
        EventBus.getInstance().post(new StateEvent());
    }
}
