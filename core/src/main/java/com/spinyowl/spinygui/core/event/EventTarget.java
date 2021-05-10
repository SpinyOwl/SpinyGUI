package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.event.listener.EventListener;
import java.util.List;

/** Event target interface. Event target should allow to store and retrieve listeners for events. */
public interface EventTarget {

  /**
   * Adds event listener to queue of listener for specified event class.
   *
   * @param eventClass event class.
   * @param listener listener to add.
   * @param <T> type of event.
   */
  <T extends NodeEvent> void addListener(Class<T> eventClass, EventListener<T> listener);

  /**
   * Removes specified event listener for specified event.
   *
   * @param eventClass event class.
   * @param listener listener to remove.
   * @param <T> type of event.
   */
  <T extends NodeEvent> void removeListener(Class<T> eventClass, EventListener<T> listener);

  /**
   * Returns list of listeners for specified event class.
   *
   * @param eventClass event class.
   * @param <T> type of event.
   * @return list of event listeners for specified event class.
   */
  <T extends NodeEvent> List<EventListener<T>> getListeners(Class<T> eventClass);
}
