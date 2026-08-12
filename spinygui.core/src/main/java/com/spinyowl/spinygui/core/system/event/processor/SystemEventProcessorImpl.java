package com.spinyowl.spinygui.core.system.event.processor;

import com.spinyowl.spinygui.core.event.processor.InputProcessingBatch;
import com.spinyowl.spinygui.core.event.processor.InputProcessingCounters;
import com.spinyowl.spinygui.core.event.processor.InputProcessingResult;
import com.spinyowl.spinygui.core.system.event.SystemEvent;
import com.spinyowl.spinygui.core.system.event.listener.SystemEventListener;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProvider;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProviderImpl;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Builder;
import lombok.Builder.Default;

/**
 * Default implementation based on two {@link ConcurrentLinkedQueue} queues which swapped every time
 * during processing.
 */
@Builder
public class SystemEventProcessorImpl implements SystemEventProcessor {

  @Default
  private SystemEventListenerProvider eventListenerProvider = new SystemEventListenerProviderImpl();

  @Default private Queue<SystemEvent> first = new ConcurrentLinkedQueue<>();
  @Default private Queue<SystemEvent> second = new ConcurrentLinkedQueue<>();

  @Default
  private InputProcessingCounters inputProcessingCounters = new InputProcessingCounters();

  public static SystemEventProcessorImpl create() {
    return SystemEventProcessorImpl.builder().build();
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
    processEventsWithResult();
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public InputProcessingResult processEventsWithResult() {
    InputProcessingBatch batch = new InputProcessingBatch();
    if (first.isEmpty()) {
      inputProcessingCounters.record(batch);
      return batch.result();
    }

    swap();

    for (SystemEvent event = second.poll(); event != null; event = second.poll()) {
      SystemEventListener listener = eventListenerProvider.listener(event.getClass());
      if (listener != null) {
        listener.processWithImpact(event, event.frame(), batch);
      } else {
        batch.markUnknownFallback();
      }
    }
    inputProcessingCounters.record(batch);
    return batch.result();
  }

  @Override
  public InputProcessingCounters.Snapshot inputProcessingCounters() {
    return inputProcessingCounters.snapshot();
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
