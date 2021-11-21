package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.util.NodeUtilities.getTargetElement;
import com.spinyowl.spinygui.core.event.ScrollEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemScrollEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.joml.Vector2fc;

@EqualsAndHashCode
public class SystemScrollEventListener extends AbstractSystemEventListener<SystemScrollEvent> {

  @NonNull private final MouseService mouseService;

  @Builder
  public SystemScrollEventListener(
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
  public void process(@NonNull SystemScrollEvent event, @NonNull Frame frame) {
    Vector2fc current = mouseService.getCursorPositions(frame).current();
    var target = getTargetElement(frame, current);
    if (target != null) {
      eventProcessor.push(
          ScrollEvent.builder()
              .source(frame)
              .target(target)
              .timestamp(timeService.currentTime())
              .offsetX(event.offsetX())
              .offsetY(event.offsetY())
              .build());
    }
  }
}
