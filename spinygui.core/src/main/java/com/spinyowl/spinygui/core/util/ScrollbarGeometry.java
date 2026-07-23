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
import org.joml.Vector2f;
import org.joml.Vector2fc;

@NoArgsConstructor(access = PRIVATE)
public final class ScrollbarGeometry {

  public static final float DEFAULT_THICKNESS = 12;
  public static final float MIN_THUMB_SIZE = 16;
  public static final Color DEFAULT_TRACK_COLOR = Color.LIGHTGRAY;
  public static final Color DEFAULT_THUMB_COLOR = Color.GRAY;
  public static final Color DEFAULT_CORNER_COLOR = Color.LIGHTGRAY;

  public static boolean canShowScrollbars(@NonNull Element element) {
    return canShowScrollbar(element.resolvedStyle().overflowX())
        || canShowScrollbar(element.resolvedStyle().overflowY());
  }

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

    Rect scrollbarBox = element.box().paddingBox();
    Rect verticalTrack =
        vertical
            ? new Rect(
                scrollbarBox.x() + scrollbarBox.width() - verticalThickness,
                scrollbarBox.y(),
                verticalThickness,
                Math.max(0, scrollbarBox.height() - (horizontal ? horizontalThickness : 0)))
            : null;
    Rect horizontalTrack =
        horizontal
            ? new Rect(
                scrollbarBox.x(),
                scrollbarBox.y() + scrollbarBox.height() - horizontalThickness,
                Math.max(0, scrollbarBox.width() - (vertical ? verticalThickness : 0)),
                horizontalThickness)
            : null;
    Rect corner =
        vertical && horizontal
            ? new Rect(
                scrollbarBox.x() + scrollbarBox.width() - verticalThickness,
                scrollbarBox.y() + scrollbarBox.height() - horizontalThickness,
                verticalThickness,
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

  /**
   * Converts an element-local scrollbar rectangle to the renderer's layout-frame coordinates.
   *
   * <p>Presentation transforms are applied by the renderer around this result.
   */
  public static Rect toFrame(@NonNull Element element, @NonNull Rect localRect) {
    Rect borderBox = element.box().borderBox();
    Vector2f position = element.layoutAbsolutePosition();
    return new Rect(
        position.x + localRect.x() - borderBox.x(),
        position.y + localRect.y() - borderBox.y(),
        localRect.width(),
        localRect.height());
  }

  /**
   * Converts a viewport point to the local box coordinate space used by {@link Metrics}.
   *
   * <p>The conversion follows the same presentation transforms as normal element hit testing.
   * An uninvertible transform returns {@code null} because its scrollbar cannot be targeted.
   */
  public static Vector2f toLocal(@NonNull Element element, @NonNull Vector2fc viewportPoint) {
    return PresentationCoordinates.toLayout(element, viewportPoint)
        .map(
            point -> {
              Vector2f position = element.layoutAbsolutePosition();
              Rect borderBox = element.box().borderBox();
              return point.sub(position).add(borderBox.x(), borderBox.y());
            })
        .orElse(null);
  }

  public static Metrics withThumbs(@NonNull Element element, @NonNull Metrics metrics) {
    return new Metrics(
        metrics.verticalVisible(),
        metrics.horizontalVisible(),
        metrics.clientWidth(),
        metrics.clientHeight(),
        metrics.verticalThickness(),
        metrics.horizontalThickness(),
        metrics.verticalTrack(),
        metrics.horizontalTrack(),
        verticalThumb(
            element, metrics.verticalTrack(), element.scrollHeight(), metrics.clientHeight()),
        horizontalThumb(
            element, metrics.horizontalTrack(), element.scrollWidth(), metrics.clientWidth()),
        metrics.corner());
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

  private static boolean canShowScrollbar(Overflow overflow) {
    return Overflow.SCROLL.equals(overflow) || Overflow.AUTO.equals(overflow);
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

  /**
   * Scrollbar dimensions and rectangles in the scroll element's local box coordinate space.
   * Consumers must use {@link ScrollbarGeometry#toFrame(Element, Rect)} or
   * {@link ScrollbarGeometry#toLocal(Element, Vector2fc)} at coordinate-space boundaries.
   */
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
