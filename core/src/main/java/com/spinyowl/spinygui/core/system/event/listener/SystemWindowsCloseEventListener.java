package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemWindowCloseEvent;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/** Generates {@link WindowCloseEvent} for frame and pushes it to {@link EventProcessor}. */
@SuperBuilder(toBuilder = true)
public class SystemWindowsCloseEventListener
    extends AbstractSystemEventListener<SystemWindowCloseEvent> {

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(@NonNull SystemWindowCloseEvent event, @NonNull Frame frame) {
    eventProcessor.push(
        WindowCloseEvent.builder()
            .timestamp(timeService.getCurrentTime())
            .source(frame)
            .target(frame)
            .build());
  }
}
