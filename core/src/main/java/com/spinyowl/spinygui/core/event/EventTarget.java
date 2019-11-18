package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.event.listener.EventListener;

import java.util.List;

public interface EventTarget {

    /**
     * Adds event listener to queue of listener for specified event class.
     * @param eventClass event class
     * @param listener listener
     * @param <T> type of event
     */
    <T extends Event> void addListener(Class<T> eventClass, EventListener<T> listener);

    <T extends Event> void removeListener(Class<T> eventClass, EventListener<T> listener);

    /**
     * Returns unmodified list of listener list for specified event class.
     *
     * @param eventClass event class.
     * @param <T>        type of event.
     * @return list of event listeners for specified event class.
     */
    <T extends Event> List<EventListener<T>> getListeners(Class<T> eventClass);

}
