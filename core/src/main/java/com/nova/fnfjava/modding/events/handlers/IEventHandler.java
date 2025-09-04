package com.nova.fnfjava.modding.events.handlers;

import com.nova.fnfjava.modding.events.Event;

public interface IEventHandler<T extends Event> {
    void handle(T event);
}
