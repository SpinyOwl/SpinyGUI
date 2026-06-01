package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.util.OverflowUtils;
import com.spinyowl.spinygui.core.util.ScrollbarGeometry;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
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
    float trackSize = trackSize(hit);
    float thumbSize = thumbSize(hit);
    float maxScroll = maxScroll(hit);
    activeDrag =
        new Drag(
            hit.element(),
            hit.axis(),
            Axis.VERTICAL.equals(hit.axis()) ? point.y() : point.x(),
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
    float pointer = Axis.VERTICAL.equals(activeDrag.axis()) ? point.y() : point.x();
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
    if (Axis.VERTICAL.equals(hit.axis())) {
      float pageDelta =
          point.y() < hit.metrics().verticalThumb().y()
              ? -element.clientHeight()
              : element.clientHeight();
      return scrollTo(element, Axis.VERTICAL, element.scrollTop() + pageDelta);
    }
    float pageDelta =
        point.x() < hit.metrics().horizontalThumb().x()
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

  private Hit hit(Element element, Vector2fc point) {
    ScrollbarGeometry.Metrics metrics =
        ScrollbarGeometry.compute(element, element.scrollWidth(), element.scrollHeight());
    if (contains(metrics.corner(), point)) {
      return new Hit(element, Axis.BOTH, HitPart.CORNER, metrics);
    }
    if (contains(metrics.verticalThumb(), point)) {
      return new Hit(element, Axis.VERTICAL, HitPart.THUMB, metrics);
    }
    if (contains(metrics.horizontalThumb(), point)) {
      return new Hit(element, Axis.HORIZONTAL, HitPart.THUMB, metrics);
    }
    if (contains(metrics.verticalTrack(), point)) {
      return new Hit(element, Axis.VERTICAL, HitPart.TRACK, metrics);
    }
    if (contains(metrics.horizontalTrack(), point)) {
      return new Hit(element, Axis.HORIZONTAL, HitPart.TRACK, metrics);
    }
    return null;
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
