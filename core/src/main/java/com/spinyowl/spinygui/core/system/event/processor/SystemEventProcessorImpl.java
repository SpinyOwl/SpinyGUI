package com.spinyowl.spinygui.core.system.event.processor;

import com.spinyowl.spinygui.core.system.FrameWindowService;
import com.spinyowl.spinygui.core.system.event.SystemEvent;
import com.spinyowl.spinygui.core.system.event.listener.SystemEventListener;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProvider;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;

/**
 * Default implementation based on two {@link ConcurrentLinkedQueue} queues which swapped every time
 * during processing.
 */
@Builder
public class SystemEventProcessorImpl implements SystemEventProcessor {

  @NonNull private final SystemEventListenerProvider eventListenerProvider;

  @Default private Queue<SystemEvent> first = new ConcurrentLinkedQueue<>();
  @Default private Queue<SystemEvent> second = new ConcurrentLinkedQueue<>();

  @NonNull private final FrameWindowService frameWindowService;

  private void swap() {
    Queue<SystemEvent> temp = first;
    first = second;
    second = temp;
  }

  /**
   * Used to process stored events in system event processor.
   *
   * <p>Target frame to which events should be applied will be obtained from {@link
   * #frameWindowService} (see {@link FrameWindowService}).
   */
  @Override
  public void processEvents() {
    swap();

    for (SystemEvent event = second.poll(); event != null; event = second.poll()) {
      SystemEventListener listener = eventListenerProvider.listener(event.getClass());
      if (listener != null) {
        var frame = frameWindowService.getFrame(event.window());
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
