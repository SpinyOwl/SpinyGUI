package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.WindowSizeEvent;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemWindowSizeEvent;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class SystemWindowSizeEventListener
    extends AbstractSystemEventListener<SystemWindowSizeEvent> {

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(@NonNull SystemWindowSizeEvent event, @NonNull Frame frame) {
    eventProcessor.push(
        WindowSizeEvent.builder()
            .source(frame)
            .target(frame)
            .timestamp(timeService.getCurrentTime())
            .width(event.width())
            .height(event.height())
            .build());
  }
}
