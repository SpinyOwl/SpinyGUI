package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.input.MouseButton.LEFT;
import static com.spinyowl.spinygui.core.input.MouseButton.RIGHT;

import com.spinyowl.spinygui.core.event.CursorEnterEvent;
import com.spinyowl.spinygui.core.event.CursorExitEvent;
import com.spinyowl.spinygui.core.event.MouseDragEvent;
import com.spinyowl.spinygui.core.event.ScrollEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.input.MultilineTextControlMetrics;
import com.spinyowl.spinygui.core.system.input.ScrollbarInteraction;
import com.spinyowl.spinygui.core.system.input.ScrollbarInteraction.ScrollDelta;
import com.spinyowl.spinygui.core.system.input.TextInputMouseCaretBehavior;
import com.spinyowl.spinygui.core.system.input.TextInputViewportBehavior;
import com.spinyowl.spinygui.core.system.input.TextareaMouseCaretBehavior;
import com.spinyowl.spinygui.core.system.input.TextareaViewportBehavior;
import com.spinyowl.spinygui.core.time.TimeService;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@EqualsAndHashCode
public class SystemCursorPosEventListener
    extends AbstractSystemEventListener<SystemCursorPosEvent> {

  @NonNull private final MouseService mouseService;
  @EqualsAndHashCode.Exclude private final ScrollbarInteraction scrollbarInteraction;
  @EqualsAndHashCode.Exclude
  private final TextInputMouseCaretBehavior textInputMouseCaretBehavior;
  @EqualsAndHashCode.Exclude private final TextInputViewportBehavior textInputViewportBehavior;
  @EqualsAndHashCode.Exclude
  private final TextareaMouseCaretBehavior textareaMouseCaretBehavior;
  @EqualsAndHashCode.Exclude private final TextareaViewportBehavior textareaViewportBehavior;

  @Builder
  public SystemCursorPosEventListener(
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
    textInputViewportBehavior =
        textMeasurer == null ? null : new TextInputViewportBehavior(textMeasurer);
    MultilineTextControlMetrics textareaMetrics =
        textMeasurer == null ? null : new MultilineTextControlMetrics(textMeasurer);
    textareaMouseCaretBehavior =
        textareaMetrics == null ? null : new TextareaMouseCaretBehavior(textareaMetrics);
    textareaViewportBehavior =
        textareaMetrics == null ? null : new TextareaViewportBehavior(textareaMetrics);
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

    // Generate enter / exit events.
    generateEnterAndExitEvents(frame, current, previous);

    if (scrollbarInteraction.dragging() && mouseService.pressed(LEFT)) {
      pushScrollEvent(frame, scrollbarInteraction.dragTo(current));
      return;
    }

    boolean leftPressed = mouseService.pressed(LEFT);
    boolean rightPressed = mouseService.pressed(RIGHT);

    // Generate drag events.
    if (focusedElement != null && (leftPressed || rightPressed)) {
      if (leftPressed && focusedElement.pressed()) {
        extendTextSelection(focusedElement, current);
      }
      Vector2f delta = current.sub(previous, new Vector2f());
      eventProcessor.push(
          MouseDragEvent.builder().source(frame).target(focusedElement).delta(delta).build());
    }
  }

  private void extendTextSelection(Element element, Vector2fc cursorPosition) {
    if (element instanceof InputElement input && textInputMouseCaretBehavior != null) {
      if (textInputMouseCaretBehavior.placeCaret(input, cursorPosition, true)
          && textInputViewportBehavior != null) {
        textInputViewportBehavior.ensureCaretVisible(input);
      }
    } else if (element instanceof TextareaElement textarea && textareaMouseCaretBehavior != null) {
      if (textareaMouseCaretBehavior.placeCaret(textarea, cursorPosition, true)
          && textareaViewportBehavior != null) {
        textareaViewportBehavior.ensureCaretVisible(textarea);
      }
    }
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

  private void generateEnterAndExitEvents(Frame frame, Vector2fc current, Vector2fc previous) {
    var currentTargetElements = NodeUtilities.getTargetElementList(frame, current);
    var prevTargetElements = NodeUtilities.getTargetElementList(frame, previous);
    if (!currentTargetElements.equals(prevTargetElements)) {
      generateEnterEvent(frame, current, currentTargetElements);
      generateExitEvent(frame, current, currentTargetElements, prevTargetElements);
    }
  }

  private void generateEnterEvent(
      Frame frame, Vector2fc current, List<Element> currentTargetElements) {
    for (Element element : currentTargetElements) {
      if (!element.hovered()) {
        element.hovered(true);
        Vector2f intersection = element.box().borderBoxPosition().sub(current).negate();
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
  }

  private void generateExitEvent(
      Frame frame,
      Vector2fc current,
      List<Element> currentTargetElements,
      List<Element> previousTargetElements) {

    previousTargetElements.removeAll(currentTargetElements);
    for (Element prevTarget : previousTargetElements) {
      Vector2f intersection = prevTarget.box().borderBoxPosition().sub(current).negate();
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
}
