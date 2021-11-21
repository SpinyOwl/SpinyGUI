package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.WindowIconifyEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemWindowIconifyEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
public class SystemWindowIconifyEventListener
    extends AbstractSystemEventListener<SystemWindowIconifyEvent> {

  @Builder
  public SystemWindowIconifyEventListener(
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
  public void process(@NonNull SystemWindowIconifyEvent event, @NonNull Frame frame) {
    eventProcessor.push(
        WindowIconifyEvent.builder()
            .source(frame)
            .target(frame)
            .timestamp(timeService.currentTime())
            .iconified(event.iconified())
            .build());
  }
}
