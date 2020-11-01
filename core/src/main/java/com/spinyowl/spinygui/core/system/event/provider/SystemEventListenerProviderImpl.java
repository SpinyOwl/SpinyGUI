package com.spinyowl.spinygui.core.system.event.provider;

import com.spinyowl.spinygui.core.system.event.SystemEvent;
import com.spinyowl.spinygui.core.system.event.listener.SystemEventListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation based on {@link ConcurrentHashMap}.
 */
public class SystemEventListenerProviderImpl implements SystemEventListenerProvider {

  private Map<Class<? extends SystemEvent>,
      SystemEventListener<? extends SystemEvent>> listenerMap = new ConcurrentHashMap<>();

  /**
   * Used to obtain system event listener by system event class.
   *
   * @param eventClass system event class.
   * @return system event listener that correspond to specified system event class.
   */
  @Override
  public <E extends SystemEvent> SystemEventListener<E> listener(Class<E> eventClass) {
    return (SystemEventListener<E>) listenerMap.get(eventClass);
  }

  /**
   * Used to set system event listener for specified event class.
   *
   * @param eventClass          system event class.
   * @param systemEventListener system event listener.
   */
  @Override
  public <E extends SystemEvent> void listener(Class<E> eventClass, SystemEventListener<E> systemEventListener) {
    listenerMap.put(eventClass, systemEventListener);

  }
}
