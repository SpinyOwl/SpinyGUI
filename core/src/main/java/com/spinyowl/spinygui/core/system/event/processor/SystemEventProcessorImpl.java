package com.spinyowl.spinygui.core.system.event.processor;

import com.spinyowl.spinygui.core.system.event.SystemEvent;
import com.spinyowl.spinygui.core.system.event.listener.SystemEventListener;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProvider;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProviderImpl;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Default implementation based on two {@link ConcurrentLinkedQueue} queues which swapped every time
 * during processing.
 */
@RequiredArgsConstructor
public class SystemEventProcessorImpl implements SystemEventProcessor {

  @NonNull private SystemEventListenerProvider eventListenerProvider;
  @NonNull private Queue<SystemEvent> first;
  @NonNull private Queue<SystemEvent> second;

  public SystemEventProcessorImpl(SystemEventListenerProvider eventListenerProvider) {
    this(eventListenerProvider, new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>());
  }

  public SystemEventProcessorImpl() {
    this(new SystemEventListenerProviderImpl());
  }

  private void swap() {
    var temp = first;
    first = second;
    second = temp;
  }

  /** Used to process stored events in system event processor. */
  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void processEvents() {
    if (first.isEmpty()) {
      return;
    }

    swap();

    for (SystemEvent event = second.poll(); event != null; event = second.poll()) {
      SystemEventListener listener = eventListenerProvider.listener(event.getClass());
      if (listener != null) {
        listener.process(event, event.frame());
      }
    }
  }

  /**
   * Used to push new system event to {@link SystemEventProcessor}.
   *
   * @param event system event to push to queue.
   */
  @Override
  public void push(SystemEvent event) {
    first.add(event);
  }

  /**
   * Used to check if current system event processor has any system events to process.
   *
   * @return true if there is any not processed system event.
   */
  @Override
  public boolean hasEvents() {
    return !first.isEmpty() || !second.isEmpty();
  }
}
