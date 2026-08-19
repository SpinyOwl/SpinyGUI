package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.input.MouseButton.LEFT;
import static com.spinyowl.spinygui.core.system.input.SystemKeyAction.PRESS;
import static com.spinyowl.spinygui.core.system.input.SystemKeyAction.RELEASE;
import static com.spinyowl.spinygui.core.util.NodeUtilities.getTargetElement;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.event.FocusInEvent;
import com.spinyowl.spinygui.core.event.FocusOutEvent;
import com.spinyowl.spinygui.core.event.MouseClickEvent;
import com.spinyowl.spinygui.core.event.ScrollEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.node.ButtonElement;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.event.SystemMouseClickEvent;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.input.MultilineTextControlMetrics;
import com.spinyowl.spinygui.core.system.input.ScrollbarInteraction;
import com.spinyowl.spinygui.core.system.input.ScrollbarInteraction.HitPart;
import com.spinyowl.spinygui.core.system.input.ScrollbarInteraction.ScrollDelta;
import com.spinyowl.spinygui.core.system.input.SystemKeyMod;
import com.spinyowl.spinygui.core.system.input.TextInputMouseCaretBehavior;
import com.spinyowl.spinygui.core.system.input.TextareaMouseCaretBehavior;
import com.spinyowl.spinygui.core.time.TimeService;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.joml.Vector2fc;

@EqualsAndHashCode
public class SystemMouseClickEventListener
    extends AbstractSystemEventListener<SystemMouseClickEvent> {

  @NonNull private final MouseService mouseService;
  @EqualsAndHashCode.Exclude
  private final TextInputMouseCaretBehavior textInputMouseCaretBehavior;
  @EqualsAndHashCode.Exclude
  private final TextareaMouseCaretBehavior textareaMouseCaretBehavior;
  @EqualsAndHashCode.Exclude private final ScrollbarInteraction scrollbarInteraction;

  @Builder
  public SystemMouseClickEventListener(
      @NonNull EventProcessor eventProcessor,
      @NonNull TimeService timeService,
      @NonNull MouseService mouseService,
      TextMeasurer textMeasurer,
      ScrollbarInteraction scrollbarInteraction) {
    super(eventProcessor, timeService);
    this.mouseService = mouseService;
    this.scrollbarInteraction =
        scrollbarInteraction == null ? new ScrollbarInteraction() : scrollbarInteraction;
    textInputMouseCaretBehavior =
        textMeasurer == null ? null : new TextInputMouseCaretBehavior(textMeasurer);
    textareaMouseCaretBehavior =
        textMeasurer == null
            ? null
            : new TextareaMouseCaretBehavior(new MultilineTextControlMetrics(textMeasurer));
  }

  private static Vector2fc positionInElement(Vector2fc cursorPos, Element element) {
    return element.box().borderBoxPosition().sub(cursorPos).negate();
  }

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(@NonNull SystemMouseClickEvent event, @NonNull Frame frame) {
    mouseService.pressed(event.button().mouseButton(), event.action() != RELEASE);
    var cursorPositions = mouseService.getCursorPositions(frame);
    var focusedElement = frame.getFocusedElement();
    var currentCursorPosition = cursorPositions.current();
    var target = getTargetElement(frame, currentCursorPosition);

    if (processScrollbarInteraction(event, frame, currentCursorPosition)) {
      return;
    }

    if (target == null) {
      processWithNoTarget(event, frame, focusedElement, currentCursorPosition);
    } else {
      target = buttonOwner(target);
      if (target.disabled()) {
        target.pressed(false);
        return;
      }
      processWithExistingTarget(event, frame, focusedElement, currentCursorPosition, target);
    }
  }

  private boolean processScrollbarInteraction(
      SystemMouseClickEvent event, Frame frame, Vector2fc cursorPosition) {
    if (event.button().mouseButton() != LEFT) {
      return false;
    }
    if (event.action() == RELEASE && scrollbarInteraction.dragging()) {
      scrollbarInteraction.endDrag();
      return true;
    }
    if (event.action() != PRESS) {
      return false;
    }

    var hit =
        scrollbarInteraction.hit(
            NodeUtilities.getTargetElementList(frame, cursorPosition), cursorPosition);
    if (hit == null || HitPart.CORNER.equals(hit.part())) {
      return false;
    }
    if (HitPart.THUMB.equals(hit.part())) {
      scrollbarInteraction.beginDrag(hit, cursorPosition);
      return true;
    }
    ScrollDelta delta = scrollbarInteraction.clickTrack(hit, cursorPosition);
    pushScrollEvent(frame, delta);
    return true;
  }

  private void pushScrollEvent(Frame frame, ScrollDelta delta) {
    if (!delta.changed()) {
      return;
    }
    eventProcessor.push(
        ScrollEvent.builder()
            .source(frame)
            .target(delta.element())
            .timestamp(timeService.currentTime())
            .offsetX(delta.x())
            .offsetY(delta.y())
            .build());
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
    } else { // event.action == RELEASE
      if (focusedElement != null) {
        focusedElement.pressed(false);
        if (focusedElement == target) {
          generateClickEvent(event, frame, cursorPosition, target);
          generateActionEvent(frame, target);
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
    placeInputCaret(target, cursorPosition, event.mods().contains(SystemKeyMod.SHIFT));

    if (focusedElement != target) {
      target.focused(true);
    }

    generatePressEvent(event, frame, cursorPosition, target);

    if (focusedElement != target) {
      generateFocusGainedEvent(frame, target, focusedElement);
    }
  }

  private void placeInputCaret(Element target, Vector2fc cursorPosition, boolean extendSelection) {
    if (textInputMouseCaretBehavior != null && target instanceof InputElement input) {
      textInputMouseCaretBehavior.placeCaret(input, cursorPosition, extendSelection);
    } else if (textareaMouseCaretBehavior != null && target instanceof TextareaElement textarea) {
      textareaMouseCaretBehavior.placeCaret(textarea, cursorPosition, extendSelection);
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
      generateFocusLostEvent(frame, element, focused);
    }
  }

  private void generateFocusGainedEvent(Frame frame, Element target, Element prevFocus) {
    eventProcessor.push(
        FocusInEvent.builder()
            .source(frame)
            .target(target)
            .timestamp(timeService.currentTime())
            .prevFocus(prevFocus)
            .build());
  }

  private void generatePressEvent(
      SystemMouseClickEvent event, Frame frame, Vector2fc cursorPosition, Element target) {
    eventProcessor.push(
        MouseClickEvent.builder()
            .source(frame)
            .target(target)
            .action(KeyAction.PRESS)
            .timestamp(timeService.currentTime())
            .mouseButton(event.button().mouseButton())
            .position(positionInElement(cursorPosition, target))
            .absolutePosition(cursorPosition)
            .mods(event.mappedMods())
            .build());
  }

  private void generateFocusLostEvent(Frame frame, Element target, Element focused) {
    eventProcessor.push(
        FocusOutEvent.builder()
            .source(frame)
            .target(target)
            .timestamp(timeService.currentTime())
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
            .timestamp(timeService.currentTime())
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
            .timestamp(timeService.currentTime())
            .mouseButton(event.button().mouseButton())
            .position(positionInElement(cursorPosition, target))
            .absolutePosition(cursorPosition)
            .mods(event.mappedMods())
            .build());
  }

  private void generateActionEvent(Frame frame, Element target) {
    if (!activatable(target)) {
      return;
    }
    eventProcessor.push(
        ActionEvent.builder()
            .source(frame)
            .target(target)
            .timestamp(timeService.currentTime())
            .build());
  }

  private boolean activatable(Element target) {
    return target instanceof ButtonElement button && button.activatable()
        || target instanceof InputElement input && input.buttonInput() && !input.disabled();
  }

  private Element buttonOwner(Element target) {
    for (Element current = target; current != null; current = current.parent()) {
      if (current instanceof ButtonElement) {
        return current;
      }
    }
    return target;
  }
}
