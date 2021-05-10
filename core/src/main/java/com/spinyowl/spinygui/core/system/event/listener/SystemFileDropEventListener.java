package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.FileDropEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemFileDropEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.joml.Vector2fc;

@EqualsAndHashCode
public class SystemFileDropEventListener extends AbstractSystemEventListener<SystemFileDropEvent> {

  @NonNull private final MouseService mouseService;

  @Builder
  public SystemFileDropEventListener(
      @NonNull EventProcessor eventProcessor,
      @NonNull TimeService timeService,
      @NonNull MouseService mouseService) {
    super(eventProcessor, timeService);
    this.mouseService = mouseService;
  }

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(@NonNull SystemFileDropEvent event, @NonNull Frame frame) {
    Vector2fc currentMousePosition = mouseService.getCursorPositions(frame).current();
    var targetElement = NodeUtilities.getTargetElement(frame, currentMousePosition);
    if (targetElement != null) {
      eventProcessor.push(
          FileDropEvent.builder()
              .source(frame)
              .target(targetElement)
              .timestamp(timeService.getCurrentTime())
              .paths(event.paths())
              .build());
    }
  }
}
