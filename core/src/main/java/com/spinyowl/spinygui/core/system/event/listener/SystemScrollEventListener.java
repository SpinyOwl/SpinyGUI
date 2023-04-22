package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.ScrollEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.pseudo.Scrollbar.Orientation;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.system.event.SystemScrollEvent;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.time.TimeService;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.joml.Vector2fc;

@EqualsAndHashCode
public class SystemScrollEventListener extends AbstractSystemEventListener<SystemScrollEvent> {

  @NonNull private final MouseService mouseService;
  @NonNull private final FontService fontService;

  @Builder
  public SystemScrollEventListener(
      @NonNull EventProcessor eventProcessor,
      @NonNull TimeService timeService,
      @NonNull MouseService mouseService,
      @NonNull FontService fontService) {
    super(eventProcessor, timeService);
    this.mouseService = mouseService;
    this.fontService = fontService;
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
    var currentTargetElements = NodeUtilities.getTargetElementList(frame, current);
    for (var target : currentTargetElements) {
      float multiplier = StyleUtils.getFontSize(target) * 3;
      // TODO:
      //  Instead of direct updating of scrollTop and scrollLeft as another option we can start
      //     scroll animation.

      if (StyleUtils.scrollable(target, Orientation.VERTICAL)) {
        float scrollTop = target.scrollTop() - event.offsetY() * multiplier;
        if (target.scrollHeight() > target.clientHeight()) {
          target.scrollTop(scrollTop);
        }
      }

      if (StyleUtils.scrollable(target, Orientation.HORIZONTAL)) {
        float scrollLeft = target.scrollLeft() - event.offsetX() * multiplier;
        if (target.scrollWidth() > target.clientWidth()) {
          target.scrollLeft(scrollLeft);
        }
      }
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
}
