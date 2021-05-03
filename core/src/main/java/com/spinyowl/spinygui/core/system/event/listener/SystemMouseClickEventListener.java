package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.system.input.SystemKeyAction.PRESS;
import static com.spinyowl.spinygui.core.system.input.SystemKeyAction.RELEASE;
import static com.spinyowl.spinygui.core.util.NodeUtilities.getTargetElement;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import com.spinyowl.spinygui.core.event.FocusEvent.FocusInEvent;
import com.spinyowl.spinygui.core.event.FocusEvent.FocusOutEvent;
import com.spinyowl.spinygui.core.event.MouseClickEvent;
import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemMouseClickEvent;
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
    mouseService.pressed(event.button().mouseButton(), event.action() != RELEASE);
    var cursorPositions = mouseService.getCursorPositions(frame);
    var focusedElement = frame.getFocusedElement();
    var currentCursorPosition = cursorPositions.current();
    var target = getTargetElement(frame, currentCursorPosition);

    if (target == null) {
      processWithNoTarget(event, frame, focusedElement, currentCursorPosition);
    } else {
      processWithExistingTarget(event, frame, focusedElement, currentCursorPosition, target);
    }
  }

  private void processWithNoTarget(
      SystemMouseClickEvent event, Frame frame, Element focused, Vector2fc cursorPosition) {
    if (focused != null) {
      if (event.action() == PRESS) {
        generateReleaseEvent(event, frame, focused, cursorPosition);
      }
      focused.focused(false);
    }
  }

  private void processWithExistingTarget(
      SystemMouseClickEvent event,
      Frame frame,
      Element focusedElement,
      Vector2fc cursorPosition,
      Element target) {
    if (event.action() == PRESS) {
      updateFocusAndGenerateEvents(event, frame, focusedElement, cursorPosition, target);
    } else {
      if (focusedElement != null) {
        focusedElement.pressed(false);
        if (focusedElement == target) {
          generateClickEvent(event, frame, cursorPosition, target);
        }
        generateReleaseEvent(event, frame, focusedElement, cursorPosition);
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
      generateFocusGainedEvent(frame, target, focusedElement);
    }
  }

  private void removeFocus(Element newFocusedElement, Frame frame) {
    removeFocus(newFocusedElement, frame, frame);
  }

  private void removeFocus(Element newFocusedElement, Element element, Frame frame) {
    checkElementAndGenerateFocusLost(newFocusedElement, element, frame);
    element.children().forEach(child -> removeFocus(newFocusedElement, child, frame));
  }

  private void checkElementAndGenerateFocusLost(Element focused, Element element, Frame frame) {
    if (element != focused && visible(element) && element.focused()) {
      element.focused(false);
      element.pressed(false);
      generateFocusLostEvent(focused, element, frame);
    }
  }

  private void generateFocusGainedEvent(Frame frame, Element target, Element prevFocus) {
    eventProcessor.push(
        FocusInEvent.builder()
            .source(frame)
            .target(target)
            .prevFocus(prevFocus)
            .timestamp(timeService.getCurrentTime())
            .build());
  }

  private void generatePressEvent(
      SystemMouseClickEvent event, Frame frame, Vector2fc cursorPosition, Element target) {
    eventProcessor.push(
        MouseClickEvent.builder()
            .source(frame)
            .target(target)
            .action(KeyAction.PRESS)
            .timestamp(timeService.getCurrentTime())
            .mouseButton(event.button().mouseButton())
            .position(positionInElement(cursorPosition, target))
            .absolutePosition(cursorPosition)
            .mods(event.mappedMods())
            .build());
  }

  private void generateFocusLostEvent(Element focused, Element element, Frame frame) {
    eventProcessor.push(
        FocusOutEvent.builder()
            .source(frame)
            .target(element)
            .timestamp(timeService.getCurrentTime())
            .nextFocus(focused)
            .build());
  }

  private void generateReleaseEvent(
      SystemMouseClickEvent event, Frame frame, Element focusedElement, Vector2fc cursorPosition) {
    eventProcessor.push(
        MouseClickEvent.builder()
            .source(frame)
            .target(focusedElement)
            .action(KeyAction.RELEASE)
            .timestamp(timeService.getCurrentTime())
            .mouseButton(event.button().mouseButton())
            .position(positionInElement(cursorPosition, focusedElement))
            .absolutePosition(cursorPosition)
            .mods(event.mappedMods())
            .build());
  }

  private void generateClickEvent(
      SystemMouseClickEvent event, Frame frame, Vector2fc cursorPosition, Element target) {
    eventProcessor.push(
        MouseClickEvent.builder()
            .source(frame)
            .target(target)
            .action(KeyAction.CLICK)
            .timestamp(timeService.getCurrentTime())
            .mouseButton(event.button().mouseButton())
            .position(positionInElement(cursorPosition, target))
            .absolutePosition(cursorPosition)
            .mods(event.mappedMods())
            .build());
  }

  private static Vector2fc positionInElement(Vector2fc cursorPos, Element element) {
    return element.absolutePosition().sub(cursorPos).negate();
  }
}
