package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.CursorEnterEvent;
import com.spinyowl.spinygui.core.event.CursorExitEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemCursorEnterEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@EqualsAndHashCode
public class SystemCursorEnterEventListener
    extends AbstractSystemEventListener<SystemCursorEnterEvent> {

  @NonNull private final MouseService mouseService;

  @Builder
  public SystemCursorEnterEventListener(
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
  public void process(@NonNull SystemCursorEnterEvent event, @NonNull Frame frame) {
    Vector2fc current = mouseService.getCursorPositions(frame).current();
    if (event.entered()) {
      List<Element> targetElements = NodeUtilities.getTargetElementList(frame, current);
      for (var element : targetElements) {
        if (!element.hovered()) {
          element.hovered(true);
          Vector2f intersection = element.absolutePosition().sub(current).negate();
          CursorEnterEvent enterEvent =
              CursorEnterEvent.builder()
                  .source(frame)
                  .target(element)
                  .timestamp(timeService.currentTime())
                  .intersection(intersection)
                  .cursorPosition(current)
                  .build();
          eventProcessor.push(enterEvent);
        }
      }
    } else {
      List<Element> previousTargetElements = NodeUtilities.getTargetElementList(frame, current);

      for (Element prevTarget : previousTargetElements) {
        Vector2f intersection = prevTarget.absolutePosition().sub(current).negate();
        CursorExitEvent exitEvent =
            CursorExitEvent.builder()
                .source(frame)
                .target(prevTarget)
                .intersection(intersection)
                .timestamp(timeService.currentTime())
                .cursorPosition(current)
                .build();
        eventProcessor.push(exitEvent);
        prevTarget.hovered(false);
      }
    }
    // Generate enter / exit events.
  }
}
