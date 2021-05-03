package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.input.KeyAction.CLICK;
import static com.spinyowl.spinygui.core.input.KeyAction.PRESS;
import static com.spinyowl.spinygui.core.input.KeyAction.RELEASE;
import static com.spinyowl.spinygui.core.util.NodeUtilities.getTargetElement;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import com.spinyowl.spinygui.core.event.FocusEvent;
import com.spinyowl.spinygui.core.event.MouseClickEvent;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemMouseClickEvent;
import java.util.List;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2fc;

@SuperBuilder(toBuilder = true)
public class SystemMouseClickEventListener
    extends AbstractSystemEventListener<SystemMouseClickEvent> {

  @NonNull private final MouseService mouseService;

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(SystemMouseClickEvent event, Frame frame) {
    mouseService.pressed(event.button(), event.action() != PRESS);
    var cursorPositions = mouseService.getCursorPositions(frame);
    var focusedElement = frame.getFocusedElement();
    var currentCursorPosition = cursorPositions.current();
    var target = getTargetElement(frame, currentCursorPosition);

    if (target == null) {
      processWithExistingTarget(event, frame, focusedElement, currentCursorPosition);
    } else {
      if (event.action() == PRESS) {
        updateFocusAndGenerateEvents(event, frame, focusedElement, currentCursorPosition, target);
      } else {
        updateReleasePosAndFocusedGui(focusedElement);

        if (focusedElement != null) {
          if (focusedElement == target) {
            generateClickEvent(event, frame, currentCursorPosition, target);
          }
          generateReleaseEvent(event, frame, focusedElement, currentCursorPosition);
        }
      }
    }
  }

  private void updateFocusAndGenerateEvents(
      SystemMouseClickEvent event,
      Frame frame,
      Element focusedElement,
      Vector2fc cursorPosition,
      Element target) {
    removeFocus(target, frame);
    target.pressed(true);

    if (focusedElement != target) {
      target.focused(true);
    }

    generatePressEvent(event, frame, cursorPosition, target);

    if (focusedElement != target) {
      generateFocusGainedEvent(frame, target);
    }
  }

  private void processWithExistingTarget(
      SystemMouseClickEvent event, Frame frame, Element focused, Vector2fc cursorPosition) {
    if (event.action() == PRESS) {
      if (focused != null) {
        updateReleasePosAndFocusedGui(focused);
        generateReleaseEvent(event, frame, focused, cursorPosition);
      }
    } else {
      if (focused != null) {
        focused.focused(false);
      }
    }
  }

  private void updateReleasePosAndFocusedGui(Element focusedElement) {
    if (focusedElement != null) {
      focusedElement.pressed(false);
    }
  }

  private void removeFocus(Element targetComponent, Frame frame) {
    List<Element> children = frame.children();
    for (Element child : children) {
      removeFocus(targetComponent, child, frame);
    }
  }

  private void removeFocus(Element focused, Element element, Frame frame) {
    if (element != focused && visible(element) && element.focused()) {
      element.focused(false);
      element.pressed(false);
      generateFocusLostEvent(focused, element, frame);
    }
    List<Element> children = element.children();
    for (Element child : children) {
      removeFocus(focused, child, frame);
    }
  }

  private void generateFocusGainedEvent(Frame frame, Element target) {
    eventProcessor.push(
        FocusEvent.builder()
            .source(frame)
            .target(target)
            .timestamp(timeService.getCurrentTime())
            .focused(true)
            .nextFocus(target)
            .build());
  }

  private void generatePressEvent(
      SystemMouseClickEvent event, Frame frame, Vector2fc cursorPosition, Element target) {
    eventProcessor.push(
        MouseClickEvent.builder()
            .source(frame)
            .target(target)
            .action(PRESS)
            .mouseButton(event.button())
            .position(positionInElement(cursorPosition, target))
            .absolutePosition(cursorPosition)
            .mods(event.mods())
            .build());
  }

  private void generateFocusLostEvent(Element focused, Element element, Frame frame) {
    eventProcessor.push(
        FocusEvent.builder()
            .source(frame)
            .target(element)
            .timestamp(timeService.getCurrentTime())
            .focused(false)
            .nextFocus(focused)
            .build());
  }

  private void generateReleaseEvent(
      SystemMouseClickEvent event, Frame frame, Element focusedElement, Vector2fc cursorPosition) {
    eventProcessor.push(
        MouseClickEvent.builder()
            .source(frame)
            .target(focusedElement)
            .action(RELEASE)
            .mouseButton(event.button())
            .position(positionInElement(cursorPosition, focusedElement))
            .absolutePosition(cursorPosition)
            .mods(event.mods())
            .build());
  }

  private void generateClickEvent(
      SystemMouseClickEvent event, Frame frame, Vector2fc cursorPosition, Element target) {
    eventProcessor.push(
        MouseClickEvent.builder()
            .source(frame)
            .target(target)
            .action(CLICK)
            .mouseButton(event.button())
            .position(positionInElement(cursorPosition, target))
            .absolutePosition(cursorPosition)
            .mods(event.mods())
            .build());
  }

  private Vector2fc positionInElement(Vector2fc cursorPos, Element element) {
    return element.absolutePosition().sub(cursorPos).negate();
  }
}
