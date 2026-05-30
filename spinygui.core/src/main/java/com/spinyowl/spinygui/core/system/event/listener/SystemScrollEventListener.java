package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.ScrollEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemScrollEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import com.spinyowl.spinygui.core.util.OverflowUtils;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
    var hoveredChain = deepestFirst(NodeUtilities.getTargetElementList(frame, current));

    float horizontalDelta = -event.offsetX() * 50;
    float verticalDelta = -event.offsetY() * 50;
    Set<Element> changedElements = new LinkedHashSet<>();

    boolean horizontalConsumed = consumeHorizontal(hoveredChain, horizontalDelta, changedElements);
    boolean verticalConsumed = consumeVertical(hoveredChain, verticalDelta, changedElements);

    if (!horizontalConsumed || !verticalConsumed) {
      Element focusedElement = frame.getFocusedElement();
      if (focusedElement != null && !hoveredChain.contains(focusedElement)) {
        var focusedChain = focusedChain(focusedElement);
        if (!horizontalConsumed) {
          consumeHorizontal(focusedChain, horizontalDelta, changedElements);
        }
        if (!verticalConsumed) {
          consumeVertical(focusedChain, verticalDelta, changedElements);
        }
      }
    }

    for (Element target : changedElements) {
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

  private boolean consumeHorizontal(
      List<Element> candidates, float delta, Set<Element> changedElements) {
    if (delta == 0) {
      return true;
    }
    for (Element target : candidates) {
      if (canConsumeHorizontal(target, delta)) {
        float previous = target.scrollLeft();
        target.scrollLeft(previous + delta);
        OverflowUtils.clampScrollOffsets(target);
        if (target.scrollLeft() != previous) {
          changedElements.add(target);
          return true;
        }
      }
    }
    return false;
  }

  private boolean consumeVertical(
      List<Element> candidates, float delta, Set<Element> changedElements) {
    if (delta == 0) {
      return true;
    }
    for (Element target : candidates) {
      if (canConsumeVertical(target, delta)) {
        float previous = target.scrollTop();
        target.scrollTop(previous + delta);
        OverflowUtils.clampScrollOffsets(target);
        if (target.scrollTop() != previous) {
          changedElements.add(target);
          return true;
        }
      }
    }
    return false;
  }

  private boolean canConsumeHorizontal(Element target, float delta) {
    return OverflowUtils.acceptsWheelX(target)
        && (delta < 0 && target.scrollLeft() > 0
            || delta > 0 && target.scrollLeft() < OverflowUtils.maxScrollLeft(target));
  }

  private boolean canConsumeVertical(Element target, float delta) {
    return OverflowUtils.acceptsWheelY(target)
        && (delta < 0 && target.scrollTop() > 0
            || delta > 0 && target.scrollTop() < OverflowUtils.maxScrollTop(target));
  }

  private List<Element> deepestFirst(List<Element> elements) {
    var deepestFirst = new ArrayList<Element>(elements);
    deepestFirst.sort((left, right) -> Integer.compare(depth(right), depth(left)));
    return deepestFirst;
  }

  private List<Element> focusedChain(Element focusedElement) {
    var chain = new ArrayList<Element>();
    Element current = focusedElement;
    while (current != null) {
      chain.add(current);
      current = current.parent();
    }
    return chain;
  }

  private int depth(Element element) {
    int depth = 0;
    Element current = element.parent();
    while (current != null) {
      depth++;
      current = current.parent();
    }
    return depth;
  }
}
