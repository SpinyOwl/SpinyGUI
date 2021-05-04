package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.util.NodeUtilities.getTargetElement;
import com.spinyowl.spinygui.core.event.ScrollEvent;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemScrollEvent;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2fc;

@SuperBuilder(toBuilder = true)
public class SystemScrollEventListener extends AbstractSystemEventListener<SystemScrollEvent> {

  @NonNull private final MouseService mouseService;

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(SystemScrollEvent event, Frame frame) {
    Vector2fc current = mouseService.getCursorPositions(frame).current();
    var target = getTargetElement(frame, current);
    if (target != null) {
      eventProcessor.push(
          ScrollEvent.builder()
              .source(frame)
              .target(target)
              .timestamp(timeService.getCurrentTime())
              .offsetX(event.offsetX())
              .offsetY(event.offsetY())
              .build());
    }
  }
}
