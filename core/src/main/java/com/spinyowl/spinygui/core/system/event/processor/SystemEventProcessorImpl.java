package com.spinyowl.spinygui.core.system.event.processor;

import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemEvent;
import com.spinyowl.spinygui.core.system.event.listener.SystemEventListener;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProvider;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;

/**
 * Default implementation based on two {@link ConcurrentLinkedQueue} queues which swapped every time
 * during processing.
 */
@RequiredArgsConstructor
public class SystemEventProcessorImpl implements SystemEventProcessor {

  private final SystemEventListenerProvider eventListenerProvider;

  private Queue<SystemEvent> first = new ConcurrentLinkedQueue<>();
  private Queue<SystemEvent> second = new ConcurrentLinkedQueue<>();

  private void swap() {
    Queue<SystemEvent> temp = first;
    first = second;
    second = temp;
  }

  /**
   * Used to process stored events in system event processor.
   *
   * @param frame target frame to which events should be applied.
   */
  @Override
  public void processEvents(Frame frame) {
    swap();

    for (SystemEvent event = second.poll(); event != null; event = second.poll()) {
      SystemEventListener listener = eventListenerProvider.listener(event.getClass());
      if (listener != null) {
        listener.process(event, frame);
      }
    }
  }

  /**
   * Used to push new system event to {@link SystemEventProcessor}.
   *
   * @param event system event to push to queue.
   */
  @Override
  public void pushEvent(SystemEvent event) {
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
