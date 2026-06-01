package com.spinyowl.spinygui.core.util;

import static lombok.AccessLevel.PRIVATE;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.style.types.ScrollbarPart;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = PRIVATE)
public final class ScrollbarGeometry {

  public static final float DEFAULT_THICKNESS = 12;
  public static final float MIN_THUMB_SIZE = 16;
  public static final Color DEFAULT_TRACK_COLOR = Color.LIGHTGRAY;
  public static final Color DEFAULT_THUMB_COLOR = Color.GRAY;
  public static final Color DEFAULT_CORNER_COLOR = Color.LIGHTGRAY;

  public static Metrics compute(@NonNull Element element, float scrollWidth, float scrollHeight) {
    float verticalThickness = verticalThickness(element);
    float horizontalThickness = horizontalThickness(element);
    float baseClientWidth = element.box().content().width();
    float baseClientHeight = element.box().content().height();

    boolean vertical = false;
    boolean horizontal = false;
    float clientWidth = baseClientWidth;
    float clientHeight = baseClientHeight;

    for (int i = 0; i < 3; i++) {
      boolean nextVertical = shouldShowVertical(element, scrollHeight, clientHeight);
      boolean nextHorizontal = shouldShowHorizontal(element, scrollWidth, clientWidth);
      float nextClientWidth =
          Math.max(0, baseClientWidth - (nextVertical ? verticalThickness : 0));
      float nextClientHeight =
          Math.max(0, baseClientHeight - (nextHorizontal ? horizontalThickness : 0));

      if (nextVertical == vertical
          && nextHorizontal == horizontal
          && nextClientWidth == clientWidth
          && nextClientHeight == clientHeight) {
        break;
      }

      vertical = nextVertical;
      horizontal = nextHorizontal;
      clientWidth = nextClientWidth;
      clientHeight = nextClientHeight;
    }

    Rect content = element.box().content();
    Rect verticalTrack =
        vertical
            ? new Rect(
                content.x() + clientWidth,
                content.y(),
                verticalThickness,
                Math.max(0, clientHeight))
            : null;
    Rect horizontalTrack =
        horizontal
            ? new Rect(
                content.x(),
                content.y() + clientHeight,
                Math.max(0, clientWidth),
                horizontalThickness)
            : null;
    Rect corner =
        vertical && horizontal
            ? new Rect(content.x() + clientWidth, content.y() + clientHeight, verticalThickness,
                horizontalThickness)
            : null;

    return new Metrics(
        vertical,
        horizontal,
        clientWidth,
        clientHeight,
        verticalThickness,
        horizontalThickness,
        verticalTrack,
        horizontalTrack,
        verticalThumb(element, verticalTrack, scrollHeight, clientHeight),
        horizontalThumb(element, horizontalTrack, scrollWidth, clientWidth),
        corner);
  }

  private static Rect verticalThumb(
      Element element, Rect track, float scrollHeight, float clientHeight) {
    if (track == null) {
      return null;
    }
    float maxScroll = Math.max(0, scrollHeight - clientHeight);
    if (maxScroll == 0) {
      return new Rect(track.x(), track.y(), track.width(), track.height());
    }
    float thumbHeight = thumbSize(track.height(), clientHeight, scrollHeight);
    float travel = Math.max(0, track.height() - thumbHeight);
    float y = track.y() + travel * Math.min(1, Math.max(0, element.scrollTop() / maxScroll));
    return new Rect(track.x(), y, track.width(), thumbHeight);
  }

  private static Rect horizontalThumb(
      Element element, Rect track, float scrollWidth, float clientWidth) {
    if (track == null) {
      return null;
    }
    float maxScroll = Math.max(0, scrollWidth - clientWidth);
    if (maxScroll == 0) {
      return new Rect(track.x(), track.y(), track.width(), track.height());
    }
    float thumbWidth = thumbSize(track.width(), clientWidth, scrollWidth);
    float travel = Math.max(0, track.width() - thumbWidth);
    float x = track.x() + travel * Math.min(1, Math.max(0, element.scrollLeft() / maxScroll));
    return new Rect(x, track.y(), thumbWidth, track.height());
  }

  private static float thumbSize(float trackSize, float clientSize, float scrollSize) {
    if (trackSize <= 0 || scrollSize <= 0) {
      return 0;
    }
    return Math.min(trackSize, Math.max(MIN_THUMB_SIZE, trackSize * clientSize / scrollSize));
  }

  private static boolean shouldShowVertical(
      Element element, float scrollHeight, float clientHeight) {
    Overflow overflow = element.resolvedStyle().overflowY();
    return Overflow.SCROLL.equals(overflow)
        || Overflow.AUTO.equals(overflow) && scrollHeight > clientHeight;
  }

  private static boolean shouldShowHorizontal(
      Element element, float scrollWidth, float clientWidth) {
    Overflow overflow = element.resolvedStyle().overflowX();
    return Overflow.SCROLL.equals(overflow)
        || Overflow.AUTO.equals(overflow) && scrollWidth > clientWidth;
  }

  private static float verticalThickness(Element element) {
    return thickness(element, Unit.AUTO, true);
  }

  private static float horizontalThickness(Element element) {
    return thickness(element, Unit.AUTO, false);
  }

  private static float thickness(Element element, Unit fallback, boolean vertical) {
    ResolvedStyle style = element.scrollbarStyle(ScrollbarPart.SCROLLBAR);
    Unit unit = style == null ? fallback : vertical ? style.width() : style.height();
    if (unit instanceof Length<?> length) {
      float base = vertical ? element.box().content().width() : element.box().content().height();
      return Math.max(0, length.convert(base));
    }
    return DEFAULT_THICKNESS;
  }

  public record Metrics(
      boolean verticalVisible,
      boolean horizontalVisible,
      float clientWidth,
      float clientHeight,
      float verticalThickness,
      float horizontalThickness,
      Rect verticalTrack,
      Rect horizontalTrack,
      Rect verticalThumb,
      Rect horizontalThumb,
      Rect corner) {}
}
