package com.spinyowl.spinygui.core.style.stylesheet.util;

import com.spinyowl.spinygui.core.font.FontSize;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.pseudo.Scrollbar.Orientation;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Length.PercentLength;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;

public final class StyleUtils {

  private StyleUtils() {}

  /**
   * Used to extract float value of length from {@link Length} value.
   *
   * @param length length
   * @param base base length to calculate length using percentage.
   * @return float(pixels) representation of length.
   */
  public static Optional<Float> getFloatLengthOptional(Unit length, float base) {
    return Optional.ofNullable(getFloatLength(length, base));
  }

  /**
   * Used to extract float value of length from {@link Length} value.
   *
   * @param length length
   * @param base base length to calculate length using percentage.
   * @return float(pixels) representation of length.
   */
  public static float getFloatLengthNullSafe(Unit length, float base) {
    var floatLength = getFloatLength(length, base);
    return floatLength == null ? 0 : floatLength;
  }

  /**
   * Used to extract float value of length from {@link Length} value.
   *
   * @param length length
   * @param base base length to calculate length using percentage.
   * @return float(pixels) representation of length.
   */
  public static Float getFloatLength(Unit length, float base) {
    if (length == null) return null;
    else if (length.isAuto()) return base;
    else if (length.isLength()) {
      var l = length.asLength();
      return l.convert(base);
    }
    return 0F;
  }

  public static Map<String, Object> getOneFour(
      @NonNull Object[] values,
      @NonNull String top,
      @NonNull String right,
      @NonNull String bottom,
      @NonNull String left) {
    switch (values.length) {
      case 0:
        return Map.of();
      case 1:
        {
          return Map.of(top, values[0], right, values[0], bottom, values[0], left, values[0]);
        }
      case 2:
        {
          return Map.of(top, values[0], right, values[1], bottom, values[0], left, values[1]);
        }
      case 3:
        {
          return Map.of(top, values[0], right, values[1], bottom, values[2], left, values[1]);
        }
      case 4:
      default:
        {
          return Map.of(top, values[0], right, values[1], bottom, values[2], left, values[3]);
        }
    }
  }

  public static Float getFontSize(@NonNull Node node) {
    Element element;
    if (node instanceof Text && node.parent() != null) {
      element = node.parent();
    } else if (node instanceof Element e) {
      element = e;
    } else {
      return (float) FontSize.MEDIUM.size();
    }

    var fontSize = element.resolvedStyle().fontSize();
    if (fontSize instanceof PercentLength) {
      Element parent = element.parent();
      Float parentFontSize =
          parent != null ? getFontSize(parent) : Float.valueOf(FontSize.MEDIUM.size());
      return getFloatLength(fontSize, parentFontSize);
    }
    return fontSize.convert();
  }

  @SuppressWarnings("squid:S1452")
  public static List<Length<?>> getOneFourLengths(Term<?> term) {
    List<Length<?>> values = new ArrayList<>();
    if (term instanceof TermLength) {
      values.add(((TermLength) term).value());
    } else if (term instanceof TermList termList) {
      values =
          termList.terms().stream()
              .filter(TermLength.class::isInstance)
              .map(TermLength.class::cast)
              .map(Term::value)
              .collect(Collectors.toList());
    }
    return values;
  }

  public static void setOneFour(
      @NonNull Object[] values,
      @NonNull String top,
      @NonNull String right,
      @NonNull String bottom,
      @NonNull String left,
      @NonNull Map<String, Object> styles) {
    if (values.length == 1) {
      styles.put(top, values[0]);
      styles.put(right, values[0]);
      styles.put(bottom, values[0]);
      styles.put(left, values[0]);
    } else if (values.length == 2) {
      styles.put(top, values[0]);
      styles.put(right, values[1]);
      styles.put(bottom, values[0]);
      styles.put(left, values[1]);
    } else if (values.length == 3) {
      styles.put(top, values[0]);
      styles.put(right, values[1]);
      styles.put(bottom, values[2]);
      styles.put(left, values[1]);
    } else if (values.length > 3) {
      styles.put(top, values[0]);
      styles.put(right, values[1]);
      styles.put(bottom, values[2]);
      styles.put(left, values[3]);
    }
  }

  public static boolean contains(String termValue, List<String> allowedValues) {
    return allowedValues.stream().anyMatch(termValue::equalsIgnoreCase);
  }

  public static int indexOf(String termValue, List<String> allowedValues) {
    for (int i = 0; i < allowedValues.size(); i++) {
      if (termValue.equalsIgnoreCase(allowedValues.get(i))) return i;
    }
    return -1;
  }

  public static boolean scrollable(Node node, Orientation orientation) {
    if (node instanceof Element element) {
      var style = element.resolvedStyle();
      var overflow = orientation == Orientation.HORIZONTAL ? style.overflowX() : style.overflowY();
      return overflow == Overflow.SCROLL || overflow == Overflow.AUTO;
    }
    return false;
  }
}
