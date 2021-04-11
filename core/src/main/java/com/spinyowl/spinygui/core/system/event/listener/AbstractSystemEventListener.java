package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.system.event.SystemEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractSystemEventListener<E extends SystemEvent>
    implements SystemEventListener<E> {
  /** Event processor to pass generated events to. */
  @NonNull protected final EventProcessor eventProcessor;

}
