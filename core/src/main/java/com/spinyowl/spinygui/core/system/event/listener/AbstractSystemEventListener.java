package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.system.event.SystemEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class AbstractSystemEventListener<E extends SystemEvent>
    implements SystemEventListener<E> {
  /** Event processor to pass generated events to. */
  @NonNull final EventProcessor eventProcessor;

  /** Time provider to use by event listeners. */
  @NonNull final TimeService timeService;
}
