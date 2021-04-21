package com.spinyowl.spinygui.core.system.event.provider;

import com.spinyowl.spinygui.core.system.event.SystemEvent;
import com.spinyowl.spinygui.core.system.event.listener.SystemEventListener;

/**
 * Used to store system event class to system event listener mapping that would be used by SpinyGUI.
 */
public interface SystemEventListenerProvider {

  /**
   * Used to obtain system event listener by system event class.
   *
   * @param aClass system event class.
   * @param <E> type of system event.
   * @return system event listener that correspond to specified system event class.
   */
  <E extends SystemEvent> SystemEventListener<E> listener(Class<E> aClass);

  /**
   * Used to set system event listener for specified event class.
   *
   * @param eventClass system event class.
   * @param systemEventListener system event listener.
   * @param <E> type of system event.
   */
  <E extends SystemEvent> void listener(
      Class<E> eventClass, SystemEventListener<E> systemEventListener);
}
