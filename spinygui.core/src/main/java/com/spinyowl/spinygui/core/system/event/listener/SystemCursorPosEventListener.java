package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.input.MouseButton.LEFT;
import static com.spinyowl.spinygui.core.input.MouseButton.RIGHT;

import com.spinyowl.spinygui.core.event.CursorEnterEvent;
import com.spinyowl.spinygui.core.event.CursorExitEvent;
import com.spinyowl.spinygui.core.event.MouseDragEvent;
import com.spinyowl.spinygui.core.event.ScrollEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.event.processor.InputProcessingBatch;
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
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
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
  @EqualsAndHashCode.Exclude
  private final Map<Frame, HitPathState> hitPaths = new IdentityHashMap<>();

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
    processInternal(event, frame, null);
  }

  @Override
  public void processWithImpact(
      @NonNull SystemCursorPosEvent event,
      @NonNull Frame frame,
      @NonNull InputProcessingBatch batch) {
    processInternal(event, frame, batch);
  }

  private void processInternal(
      SystemCursorPosEvent event, Frame frame, InputProcessingBatch batch) {
    Vector2fc current = new Vector2f(event.posX(), event.posY());
    Vector2fc previous = mouseService.getCursorPositions(frame).current();
    mouseService.setCursorPositions(frame, new CursorPositions(current, previous));

    var focusedElement = frame.getFocusedElement();
    HitPathState hitPath = hitPaths.computeIfAbsent(frame, ignored -> new HitPathState());
    boolean sameHitPath = hitPath.refresh(frame, current, previous);

    // Generate enter / exit events.
    if (!sameHitPath) {
      boolean unknownBoundaryListener =
          hasDispatchedBoundaryListener(hitPath.current(), hitPath.previous());
      generateEnterEvent(frame, current, hitPath.current());
      generateExitEvent(frame, current, hitPath.current(), hitPath.previous());
      if (unknownBoundaryListener) {
        markUnknownFallback(batch);
      } else {
        markHoverStyleEffect(batch);
      }
    }
    hitPath.advance();

    if (scrollbarInteraction.dragging() && mouseService.pressed(LEFT)) {
      markUnknownFallback(batch);
      pushScrollEvent(frame, scrollbarInteraction.dragTo(current));
      return;
    }

    boolean leftPressed = mouseService.pressed(LEFT);
    boolean rightPressed = mouseService.pressed(RIGHT);
    if (scrollbarInteraction.dragging()) {
      markUnknownFallback(batch);
    }
    if (leftPressed || rightPressed) {
      markKnownEffect(batch);
    }
    if (focusedElement != null && focusedElement.pressed()) {
      markUnknownFallback(batch);
    }

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

  private boolean hasDispatchedBoundaryListener(
      List<Element> currentPath, List<Element> previousPath) {
    for (Element element : currentPath) {
      if (!element.hovered() && element.hasListenersFor(CursorEnterEvent.class)) return true;
    }
    for (Element element : previousPath) {
      if (!currentPath.contains(element) && element.hasListenersFor(CursorExitEvent.class)) {
        return true;
      }
    }
    return false;
  }

  private void markKnownEffect(InputProcessingBatch batch) {
    if (batch != null) {
      batch.markKnownEffect();
    }
  }

  private void markHoverStyleEffect(InputProcessingBatch batch) {
    if (batch != null) {
      batch.markHoverStyleEffect();
    }
  }

  private void markUnknownFallback(InputProcessingBatch batch) {
    if (batch != null) {
      batch.markUnknownFallback();
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

    for (Element prevTarget : previousTargetElements) {
      if (currentTargetElements.contains(prevTarget)) {
        continue;
      }
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

  private static final class HitPathState {
    private List<Element> current = new ArrayList<>();
    private List<Element> previous = new ArrayList<>();
    private boolean initialized;

    private boolean refresh(Frame frame, Vector2fc currentPoint, Vector2fc previousPoint) {
      if (!initialized) {
        NodeUtilities.replaceTargetElementList(frame, previousPoint, previous);
        initialized = true;
      }
      NodeUtilities.replaceTargetElementList(frame, currentPoint, current);
      return current.equals(previous);
    }

    private List<Element> current() {
      return current;
    }

    private List<Element> previous() {
      return previous;
    }

    private void advance() {
      List<Element> path = previous;
      previous = current;
      current = path;
    }
  }
}
