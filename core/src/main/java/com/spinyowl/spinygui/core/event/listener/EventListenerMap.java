package com.spinyowl.spinygui.core.event.listener;

import com.spinyowl.spinygui.core.event.Event;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventListenerMap {

    /**
     * Map of listeners attached that should be attached for node and processed if any event
     * performed.
     */
    private Map<Class<? extends Event>, List<? extends EventListener>> listenerMap = new ConcurrentHashMap<>();

    public <T extends Event> void addEventListener(Class<T> eventClass, EventListener<T> listener) {
        getOrCreate(eventClass).add(listener);
    }

    public <T extends Event> void removeEventListener(Class<T> eventClass,
        EventListener<T> listener) {
        getOrCreate(eventClass).remove(listener);
    }

    /**
     * Returns listener list for specified event class.
     *
     * @param eventClass event class.
     * @param <T>        type of event.
     * @return list of event listeners for specified event class.
     */
    public <T extends Event> List<EventListener<T>> getListeners(Class<T> eventClass) {
        return Collections.unmodifiableList(getOrCreate(eventClass));
    }

    private <T extends Event> List<EventListener<T>> getOrCreate(Class<T> eventClass) {
        return (List<EventListener<T>>) listenerMap
            .computeIfAbsent(eventClass, aClass -> new CopyOnWriteArrayList<>());
    }

    /**
     * Returns true if there is at least one event listener for specified event class.
     *
     * @param eventClass event class.
     * @return true if there is at least one event listener for specified event class.
     */
    public boolean hasListenersFor(Class<? extends Event> eventClass) {
        return listenerMap.containsKey(eventClass) && !listenerMap.get(eventClass).isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventListenerMap that = (EventListenerMap) o;
        return Objects.equals(listenerMap, that.listenerMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listenerMap);
    }
}
