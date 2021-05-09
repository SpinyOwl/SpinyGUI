package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.WindowPosEvent;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemWindowPosEvent;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class SystemWindowPosEventListener
    extends AbstractSystemEventListener<SystemWindowPosEvent> {

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(@NonNull SystemWindowPosEvent event, @NonNull Frame frame) {
    eventProcessor.push(
        WindowPosEvent.builder()
            .source(frame)
            .target(frame)
            .timestamp(timeService.getCurrentTime())
            .posX(event.posX())
            .posY(event.posY())
            .build());
  }
}
