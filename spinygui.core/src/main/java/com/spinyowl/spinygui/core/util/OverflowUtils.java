package com.spinyowl.spinygui.core.util;

import static lombok.AccessLevel.PRIVATE;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.types.Overflow;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = PRIVATE)
public final class OverflowUtils {

  public static boolean clipsX(@NonNull Element element) {
    return clips(element.resolvedStyle().overflowX());
  }

  public static boolean clipsY(@NonNull Element element) {
    return clips(element.resolvedStyle().overflowY());
  }

  public static boolean clipsAny(@NonNull Element element) {
    return clipsX(element) || clipsY(element);
  }

  public static boolean acceptsWheelX(@NonNull Element element) {
    return acceptsWheel(element.resolvedStyle().overflowX(), maxScrollLeft(element));
  }

  public static boolean acceptsWheelY(@NonNull Element element) {
    return acceptsWheel(element.resolvedStyle().overflowY(), maxScrollTop(element));
  }

  public static float maxScrollLeft(@NonNull Element element) {
    return Math.max(0, element.scrollWidth() - element.clientWidth());
  }

  public static float maxScrollTop(@NonNull Element element) {
    return Math.max(0, element.scrollHeight() - element.clientHeight());
  }

  public static void clampScrollOffsets(@NonNull Element element) {
    element.scrollLeft(clamp(element.scrollLeft(), maxScrollLeft(element)));
    element.scrollTop(clamp(element.scrollTop(), maxScrollTop(element)));
  }

  private static boolean clips(Overflow overflow) {
    return Overflow.HIDDEN.equals(overflow)
        || Overflow.AUTO.equals(overflow)
        || Overflow.SCROLL.equals(overflow);
  }

  private static boolean acceptsWheel(Overflow overflow, float maxScroll) {
    return maxScroll > 0 && (Overflow.AUTO.equals(overflow) || Overflow.SCROLL.equals(overflow));
  }

  private static float clamp(float value, float max) {
    if (value < 0) {
      return 0;
    }
    return Math.min(value, max);
  }
}
