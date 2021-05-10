package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.WindowFocusEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemWindowFocusEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import lombok.Builder;
import lombok.NonNull;

public class SystemWindowFocusEventListener
    extends AbstractSystemEventListener<SystemWindowFocusEvent> {

  @Builder
  public SystemWindowFocusEventListener(
      @NonNull EventProcessor eventProcessor, @NonNull TimeService timeService) {
    super(eventProcessor, timeService);
  }

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(@NonNull SystemWindowFocusEvent event, @NonNull Frame frame) {
    eventProcessor.push(
        WindowFocusEvent.builder()
            .source(frame)
            .target(frame)
            .timestamp(timeService.getCurrentTime())
            .build());
  }
}
