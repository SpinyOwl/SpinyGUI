package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.WindowIconifyEvent;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemWindowIconifyEvent;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class SystemWindowIconifyEventListener
    extends AbstractSystemEventListener<SystemWindowIconifyEvent> {

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
            .timestamp(timeService.getCurrentTime())
            .iconified(event.iconified())
            .build());
  }
}
