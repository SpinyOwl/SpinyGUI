package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.input.MouseButton.LEFT;
import static com.spinyowl.spinygui.core.input.MouseButton.RIGHT;
import com.spinyowl.spinygui.core.event.CursorEnterEvent;
import com.spinyowl.spinygui.core.event.CursorExitEvent;
import com.spinyowl.spinygui.core.event.MouseDragEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@EqualsAndHashCode
public class SystemCursorPosEventListener
    extends AbstractSystemEventListener<SystemCursorPosEvent> {

  @NonNull private final MouseService mouseService;

  @Builder
  public SystemCursorPosEventListener(
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
  public void process(@NonNull SystemCursorPosEvent event, @NonNull Frame frame) {
    Vector2fc current = new Vector2f(event.posX(), event.posY());
    Vector2fc previous = mouseService.getCursorPositions(frame).current();
    mouseService.setCursorPositions(frame, new CursorPositions(current, previous));

    var focusedElement = frame.getFocusedElement();

    var prevTarget = NodeUtilities.getTargetElement(frame, previous);
    var targetElement = NodeUtilities.getTargetElement(frame, current);

    // Generate enter / exit events.
    if (targetElement != prevTarget) {
      generateEnterEvent(frame, current, targetElement);
      generateExitEvent(frame, current, prevTarget);
    }

    // Generate drag events.
    if (focusedElement != null && (mouseService.pressed(LEFT) || mouseService.pressed(RIGHT))) {
      Vector2f delta = current.sub(previous, new Vector2f());
      eventProcessor.push(
          MouseDragEvent.builder().source(frame).target(focusedElement).delta(delta).build());
    }
  }

  private void generateEnterEvent(Frame frame, Vector2fc current, Element targetElement) {
    if (targetElement != null) {
      targetElement.hovered(true);
      Vector2f intersection = targetElement.absolutePosition().sub(current).negate();
      CursorEnterEvent enterEvent =
          CursorEnterEvent.builder()
              .source(frame)
              .target(targetElement)
              .intersection(intersection)
              .cursorPosition(current)
              .build();
      eventProcessor.push(enterEvent);
    }
  }

  private void generateExitEvent(Frame frame, Vector2fc current, Element prevTarget) {
    if (prevTarget != null) {
      Vector2f intersection = prevTarget.absolutePosition().sub(current).negate();
      CursorExitEvent exitEvent =
          CursorExitEvent.builder()
              .source(frame)
              .target(prevTarget)
              .intersection(intersection)
              .cursorPosition(current)
              .build();
      eventProcessor.push(exitEvent);
      prevTarget.hovered(false);
    }
  }
}
