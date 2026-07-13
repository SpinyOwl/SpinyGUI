package com.spinyowl.spinygui.core.node;

import com.spinyowl.spinygui.core.style.types.AffineTransform;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Mutable per-element values presented during a frame without changing computed CSS state.
 *
 * <p>The owning {@link Element} creates this state and frame/style lifecycle code is its only
 * mutation boundary. Render and input code may read it during that same frame. Future animation
 * tracks belong here; they must never write into {@code ResolvedStyle}.
 */
public final class PresentationState {

  private AffineTransform transform = AffineTransform.IDENTITY;
  private final Map<String, Object> values = new HashMap<>();

  /** Returns the transform currently presented for the owning element. */
  public AffineTransform transform() {
    return transform;
  }

  /** Sets the transform currently presented for the owning element. */
  public void transform(AffineTransform transform) {
    this.transform = Objects.requireNonNull(transform, "transform must not be null");
  }

  /** Returns the currently presented value for a CSS property, or its computed fallback. */
  public <T> T value(String property, T defaultValue) {
    @SuppressWarnings("unchecked")
    T value = (T) values.getOrDefault(property, defaultValue);
    return value;
  }

  /** Sets a non-layout presentation overlay value. */
  public void setValue(String property, Object value) {
    if (value == null) {
      values.remove(property);
    } else {
      values.put(property, value);
    }
  }

  /** Clears all property presentation overlays. */
  public void clearValues() {
    values.clear();
  }

  /** Resets all presented values when an element is recalculated, hidden, or detached. */
  public void reset() {
    transform = AffineTransform.IDENTITY;
    values.clear();
  }
}
