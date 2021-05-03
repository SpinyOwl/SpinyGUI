package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.DropEvent;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemDropEvent;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2fc;

@SuperBuilder
public class SystemDropEventListener extends AbstractSystemEventListener<SystemDropEvent> {

  @NonNull private final MouseService mouseService;
  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(SystemDropEvent event, Frame frame) {
    Vector2fc currentMousePosition = mouseService.getCursorPositions(frame).current();
    var targetElement = NodeUtilities.getTargetElement(frame, currentMousePosition);
    if (targetElement != null) {
      eventProcessor.push(
          DropEvent.builder()
              .source(frame)
              .target(targetElement)
              .timestamp(timeService.getCurrentTime())
              .paths(event.paths())
              .build());
    }
  }
}
