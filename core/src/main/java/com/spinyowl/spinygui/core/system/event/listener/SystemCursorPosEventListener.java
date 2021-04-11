package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.CursorEnterEvent;
import com.spinyowl.spinygui.core.event.MouseDragEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.Mouse;
import com.spinyowl.spinygui.core.input.MouseButton;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import lombok.NonNull;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class SystemCursorPosEventListener
    extends AbstractSystemEventListener<SystemCursorPosEvent> {

  public SystemCursorPosEventListener(@NonNull EventProcessor eventProcessor) {
    super(eventProcessor);
  }

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(SystemCursorPosEvent event, Frame frame) {
    Vector2fc current = new Vector2f(event.posX(), event.posY());
    Vector2fc previous = Mouse.getCursorPositions(frame).current();
    Mouse.mouseService().setCursorPositions(frame, new CursorPositions(current, previous));

    Element focusedElement = frame.getFocusedElement();

    Element prevTarget = NodeUtilities.getTargetElement(frame, previous);
    Element targetElement = NodeUtilities.getTargetElement(frame, current);

    // Generate enter / exit events.
    if (targetElement != prevTarget) {
      if (targetElement != null) {
        targetElement.hovered(true);
        Vector2f intersection = targetElement.absolutePosition().sub(current).negate();
        CursorEnterEvent enterEvent =
            CursorEnterEvent.builder()
                .target(targetElement)
                .source(frame)
                .entered(true)
                .intersection(intersection)
                .cursorPosition(current)
                .build();
        eventProcessor.pushEvent(enterEvent);
      }

      // Generate
      if (prevTarget != null) {
        Vector2f intersection = prevTarget.absolutePosition().sub(current).negate();
        CursorEnterEvent exitEvent =
            CursorEnterEvent.builder()
                .target(prevTarget)
                .source(frame)
                .entered(false)
                .intersection(intersection)
                .cursorPosition(current)
                .build();
        eventProcessor.pushEvent(exitEvent);
        prevTarget.hovered(false);
      }
    }

    // Generate drag events.
    if (focusedElement != null
        && (Mouse.pressed(MouseButton.LEFT) || Mouse.pressed(MouseButton.RIGHT))) {
      Vector2f delta = current.sub(previous, new Vector2f());
      eventProcessor.pushEvent(
          MouseDragEvent.builder().target(focusedElement).source(frame).delta(delta).build());
    }
  }
}