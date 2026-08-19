package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.node.InputElement;
import java.math.BigDecimal;
import java.util.Locale;
import org.joml.Vector2fc;

/** Backend-agnostic value, keyboard and pointer behavior for {@code input[type=range]}. */
public final class RangeBehavior {

  private static final String ATTR_MIN = "min";
  private static final String ATTR_MAX = "max";
  private static final String ATTR_STEP = "step";
  private static final double DEFAULT_MIN = 0.0;
  private static final double DEFAULT_MAX = 100.0;
  private static final double DEFAULT_STEP = 1.0;

  public boolean handleKey(InputElement input, KeyCode keyCode, KeyAction action) {
    if (input.disabled()
        || InputBehaviorRegistry.kind(input) != InputBehaviorRegistry.Kind.RANGE
        || (action != KeyAction.PRESS && action != KeyAction.REPEAT)) {
      return false;
    }

    double current = value(input);
    return switch (keyCode) {
      case LEFT, DOWN -> setValue(input, current - effectiveStep(input));
      case RIGHT, UP -> setValue(input, current + effectiveStep(input));
      case PAGE_DOWN -> setValue(input, current - effectiveStep(input) * 10.0);
      case PAGE_UP -> setValue(input, current + effectiveStep(input) * 10.0);
      case HOME -> setValue(input, min(input));
      case END -> setValue(input, max(input));
      default -> false;
    };
  }

  public boolean setFromPointer(InputElement input, Vector2fc cursorPosition) {
    if (input.disabled() || InputBehaviorRegistry.kind(input) != InputBehaviorRegistry.Kind.RANGE) {
      return false;
    }
    float width = input.box().contentSize().x();
    if (!Float.isFinite(width) || width <= 0f) {
      return false;
    }
    float contentX =
        input.absolutePosition().x()
            + input.box().border().left()
            + input.box().padding().left();
    double ratio = Math.max(0.0, Math.min(1.0, (cursorPosition.x() - contentX) / width));
    return setValue(input, min(input) + ratio * (max(input) - min(input)));
  }

  public boolean setValue(InputElement input, double candidate) {
    if (InputBehaviorRegistry.kind(input) != InputBehaviorRegistry.Kind.RANGE) {
      return false;
    }
    double normalized = normalize(input, candidate);
    String serialized = serialize(normalized);
    if (serialized.equals(input.value())) {
      return false;
    }
    input.value(serialized);
    return true;
  }

  public double value(InputElement input) {
    double fallback = min(input) + (max(input) - min(input)) / 2.0;
    return normalize(input, parseFinite(input.value(), fallback));
  }

  public double min(InputElement input) {
    return parseFinite(input.getAttribute(ATTR_MIN), DEFAULT_MIN);
  }

  public double max(InputElement input) {
    double min = min(input);
    return Math.max(min, parseFinite(input.getAttribute(ATTR_MAX), DEFAULT_MAX));
  }

  public double fraction(InputElement input) {
    double min = min(input);
    double max = max(input);
    if (max <= min) {
      return 0.0;
    }
    return (value(input) - min) / (max - min);
  }

  private double normalize(InputElement input, double candidate) {
    double min = min(input);
    double max = max(input);
    double clamped = Math.max(min, Math.min(max, candidate));
    if (stepAny(input)) {
      return clamped;
    }
    double step = effectiveStep(input);
    double snapped = min + Math.round((clamped - min) / step) * step;
    return Math.max(min, Math.min(max, snapped));
  }

  private double effectiveStep(InputElement input) {
    double parsed = parseFinite(input.getAttribute(ATTR_STEP), DEFAULT_STEP);
    return parsed > 0.0 ? parsed : DEFAULT_STEP;
  }

  private boolean stepAny(InputElement input) {
    String value = input.getAttribute(ATTR_STEP);
    return value != null && "any".equals(value.toLowerCase(Locale.ROOT));
  }

  private double parseFinite(String value, double fallback) {
    if (value == null || value.isBlank()) {
      return fallback;
    }
    try {
      double parsed = Double.parseDouble(value);
      return Double.isFinite(parsed) ? parsed : fallback;
    } catch (NumberFormatException ignored) {
      return fallback;
    }
  }

  private String serialize(double value) {
    return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
  }
}
