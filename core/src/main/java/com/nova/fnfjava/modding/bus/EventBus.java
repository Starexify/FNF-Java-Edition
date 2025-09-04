package com.nova.fnfjava.modding.bus;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.modding.events.*;
import com.nova.fnfjava.modding.events.handlers.IEventHandler;

public class EventBus {
    public static EventBus instance;
    public final ObjectMap<Class<? extends Event>, Array<Object>> listeners = new ObjectMap<>();

    public static EventBus getInstance() {
        if (instance == null) instance = new EventBus();
        return instance;
    }

    public <T extends Event> void register(Class<T> eventType, Object listener) {
        Array<Object> list = listeners.get(eventType);
        if (list == null) {
            list = new Array<>();
            listeners.put(eventType, list);
        }
        list.add(listener);
    }

    public <T extends Event> void unregister(Class<T> eventType, Object listener) {
        Array<Object> list = listeners.get(eventType);
        if (list != null) list.removeValue(listener, true);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void post(T event) {
        Array<Object> list = listeners.get(event.getClass());
        if (list != null) {
            for (Object listener : list) {
                if (event.isCancelled()) continue;
                if (listener instanceof IEventHandler<?>) ((IEventHandler<T>) listener).handle(event);
            }
        }
    }
}
