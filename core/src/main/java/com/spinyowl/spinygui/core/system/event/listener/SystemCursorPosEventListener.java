package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.CursorEnterEvent;
import com.spinyowl.spinygui.core.event.MouseDragEvent;
import com.spinyowl.spinygui.core.input.MouseButton;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@SuperBuilder
public class SystemCursorPosEventListener
    extends AbstractSystemEventListener<SystemCursorPosEvent> {

  @NonNull private final MouseService mouseService;

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(SystemCursorPosEvent event, Frame frame) {
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
    if (focusedElement != null
        && (mouseService.pressed(MouseButton.LEFT) || mouseService.pressed(MouseButton.RIGHT))) {
      Vector2f delta = current.sub(previous, new Vector2f());
      eventProcessor.push(
          MouseDragEvent.builder().source(frame).target(focusedElement).delta(delta).build());
    }
  }

  private void generateExitEvent(Frame frame, Vector2fc current, Element prevTarget) {
    if (prevTarget != null) {
      Vector2f intersection = prevTarget.absolutePosition().sub(current).negate();
      CursorEnterEvent exitEvent =
          CursorEnterEvent.builder()
              .source(frame)
              .target(prevTarget)
              .entered(false)
              .intersection(intersection)
              .cursorPosition(current)
              .build();
      eventProcessor.push(exitEvent);
      prevTarget.hovered(false);
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
              .entered(true)
              .intersection(intersection)
              .cursorPosition(current)
              .build();
      eventProcessor.push(enterEvent);
    }
  }
}
