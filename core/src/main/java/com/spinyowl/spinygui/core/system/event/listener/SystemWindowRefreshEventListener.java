package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.WindowRefreshEvent;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemWindowRefreshEvent;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class SystemWindowRefreshEventListener
    extends AbstractSystemEventListener<SystemWindowRefreshEvent> {

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(@NonNull SystemWindowRefreshEvent event, @NonNull Frame frame) {
    eventProcessor.push(
        WindowRefreshEvent.builder()
            .source(frame)
            .target(frame)
            .timestamp(timeService.getCurrentTime())
            .build());
  }
}
