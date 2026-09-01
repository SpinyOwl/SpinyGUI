package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.util.OverflowUtils;
import com.spinyowl.spinygui.core.util.ScrollbarGeometry;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class ScrollbarInteraction {

  private Drag activeDrag;

  public Hit hit(List<Element> elements, Vector2fc point) {
    var candidates = new ArrayList<>(elements);
    candidates.sort((left, right) -> Integer.compare(depth(right), depth(left)));
    for (Element element : candidates) {
      Hit hit = hit(element, point);
      if (hit != null) {
        return hit;
      }
    }
    return null;
  }

  public void beginDrag(Hit hit, Vector2fc point) {
    if (hit == null || !HitPart.THUMB.equals(hit.part())) {
      activeDrag = null;
      return;
    }
    Vector2f localPoint = ScrollbarGeometry.toLocal(hit.element(), point);
    if (localPoint == null) {
      activeDrag = null;
      return;
    }
    float trackSize = trackSize(hit);
    float thumbSize = thumbSize(hit);
    float maxScroll = maxScroll(hit);
    activeDrag =
        new Drag(
            hit.element(),
            hit.axis(),
            Axis.VERTICAL.equals(hit.axis()) ? localPoint.y() : localPoint.x(),
            Axis.VERTICAL.equals(hit.axis())
                ? hit.element().scrollTop()
                : hit.element().scrollLeft(),
            Math.max(0, trackSize - thumbSize),
            maxScroll);
  }

  public ScrollDelta dragTo(Vector2fc point) {
    if (activeDrag == null) {
      return ScrollDelta.NONE;
    }
    Vector2f localPoint = ScrollbarGeometry.toLocal(activeDrag.element(), point);
    if (localPoint == null) {
      return ScrollDelta.NONE;
    }
    float pointer = Axis.VERTICAL.equals(activeDrag.axis()) ? localPoint.y() : localPoint.x();
    float pointerDelta = pointer - activeDrag.pointerStart();
    float scrollDelta =
        activeDrag.thumbTravel() == 0
            ? 0
            : pointerDelta * activeDrag.maxScroll() / activeDrag.thumbTravel();
    return scrollTo(activeDrag.element(), activeDrag.axis(), activeDrag.scrollStart() + scrollDelta);
  }

  public ScrollDelta clickTrack(Hit hit, Vector2fc point) {
    if (hit == null || !HitPart.TRACK.equals(hit.part())) {
      return ScrollDelta.NONE;
    }
    Element element = hit.element();
    Vector2f localPoint = ScrollbarGeometry.toLocal(element, point);
    if (localPoint == null) {
      return ScrollDelta.NONE;
    }
    if (Axis.VERTICAL.equals(hit.axis())) {
      float pageDelta =
          localPoint.y() < hit.metrics().verticalThumb().y()
              ? -element.clientHeight()
              : element.clientHeight();
      return scrollTo(element, Axis.VERTICAL, element.scrollTop() + pageDelta);
    }
    float pageDelta =
        localPoint.x() < hit.metrics().horizontalThumb().x()
            ? -element.clientWidth()
            : element.clientWidth();
    return scrollTo(element, Axis.HORIZONTAL, element.scrollLeft() + pageDelta);
  }

  public void endDrag() {
    activeDrag = null;
  }

  public boolean dragging() {
    return activeDrag != null;
  }

  /**
   * Returns the element captured by the active scrollbar drag.
   *
   * @return captured element, or {@code null} when no drag is active
   */
  public Element draggedElement() {
    return activeDrag == null ? null : activeDrag.element();
  }

  private Hit hit(Element element, Vector2fc point) {
    if (!ScrollbarGeometry.canShowScrollbars(element)) {
      return null;
    }
    Vector2f localPoint = ScrollbarGeometry.toLocal(element, point);
    if (localPoint == null) {
      return null;
    }
    ScrollbarGeometry.Metrics metrics = scrollbarMetrics(element);
    if (contains(metrics.corner(), localPoint)) {
      return new Hit(element, Axis.BOTH, HitPart.CORNER, metrics);
    }
    if (contains(metrics.verticalThumb(), localPoint)) {
      return new Hit(element, Axis.VERTICAL, HitPart.THUMB, metrics);
    }
    if (contains(metrics.horizontalThumb(), localPoint)) {
      return new Hit(element, Axis.HORIZONTAL, HitPart.THUMB, metrics);
    }
    if (contains(metrics.verticalTrack(), localPoint)) {
      return new Hit(element, Axis.VERTICAL, HitPart.TRACK, metrics);
    }
    if (contains(metrics.horizontalTrack(), localPoint)) {
      return new Hit(element, Axis.HORIZONTAL, HitPart.TRACK, metrics);
    }
    return null;
  }

  private ScrollbarGeometry.Metrics scrollbarMetrics(Element element) {
    ScrollbarGeometry.Metrics metrics = element.scrollbarMetrics();
    return metrics == null
        ? ScrollbarGeometry.compute(element, element.scrollWidth(), element.scrollHeight())
        : ScrollbarGeometry.withThumbs(element, metrics);
  }

  private ScrollDelta scrollTo(Element element, Axis axis, float value) {
    float previousLeft = element.scrollLeft();
    float previousTop = element.scrollTop();
    if (Axis.VERTICAL.equals(axis)) {
      element.scrollTop(value);
    } else if (Axis.HORIZONTAL.equals(axis)) {
      element.scrollLeft(value);
    }
    OverflowUtils.clampScrollOffsets(element);
    return new ScrollDelta(
        element, element.scrollLeft() - previousLeft, element.scrollTop() - previousTop);
  }

  private float trackSize(Hit hit) {
    return Axis.VERTICAL.equals(hit.axis())
        ? hit.metrics().verticalTrack().height()
        : hit.metrics().horizontalTrack().width();
  }

  private float thumbSize(Hit hit) {
    return Axis.VERTICAL.equals(hit.axis())
        ? hit.metrics().verticalThumb().height()
        : hit.metrics().horizontalThumb().width();
  }

  private float maxScroll(Hit hit) {
    return Axis.VERTICAL.equals(hit.axis())
        ? OverflowUtils.maxScrollTop(hit.element())
        : OverflowUtils.maxScrollLeft(hit.element());
  }

  private boolean contains(Rect rect, Vector2fc point) {
    return rect != null
        && point.x() >= rect.x()
        && point.x() < rect.x() + rect.width()
        && point.y() >= rect.y()
        && point.y() < rect.y() + rect.height();
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

  public enum Axis {
    HORIZONTAL,
    VERTICAL,
    BOTH
  }

  public enum HitPart {
    TRACK,
    THUMB,
    CORNER
  }

  public record Hit(
      Element element, Axis axis, HitPart part, ScrollbarGeometry.Metrics metrics) {}

  public record ScrollDelta(Element element, float x, float y) {
    public static final ScrollDelta NONE = new ScrollDelta(null, 0, 0);

    public boolean changed() {
      return element != null && (x != 0 || y != 0);
    }
  }

  @Getter
  private static final class Drag {
    private final Element element;
    private final Axis axis;
    private final float pointerStart;
    private final float scrollStart;
    private final float thumbTravel;
    private final float maxScroll;

    private Drag(
        Element element,
        Axis axis,
        float pointerStart,
        float scrollStart,
        float thumbTravel,
        float maxScroll) {
      this.element = element;
      this.axis = axis;
      this.pointerStart = pointerStart;
      this.scrollStart = scrollStart;
      this.thumbTravel = thumbTravel;
      this.maxScroll = maxScroll;
    }
  }
}
