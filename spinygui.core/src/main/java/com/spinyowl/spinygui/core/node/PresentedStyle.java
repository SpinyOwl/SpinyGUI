package com.spinyowl.spinygui.core.node;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OPACITY;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSFORM;

import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Transform;
import java.util.Objects;

/**
 * Typed paint-facing view of an element's current presentation values.
 *
 * <p>Values supplied by {@link PresentationState} take precedence for the current frame. When a
 * property has no presentation override, this view returns the exact value from {@link
 * ResolvedStyle}. Layout must continue to read {@code ResolvedStyle} directly.
 */
public final class PresentedStyle {

  private final ResolvedStyle resolvedStyle;
  private final PresentationState presentationState;

  public PresentedStyle(ResolvedStyle resolvedStyle, PresentationState presentationState) {
    this.resolvedStyle = Objects.requireNonNull(resolvedStyle, "resolvedStyle must not be null");
    this.presentationState =
        Objects.requireNonNull(presentationState, "presentationState must not be null");
  }

  public Float opacity() {
    return presentationState.value(OPACITY, resolvedStyle.opacity());
  }

  public Color color() {
    return presentationState.value(COLOR, resolvedStyle.color());
  }

  public Color backgroundColor() {
    return presentationState.value(BACKGROUND_COLOR, resolvedStyle.backgroundColor());
  }

  public Color borderLeftColor() {
    return presentationState.value(BORDER_LEFT_COLOR, resolvedStyle.borderLeftColor());
  }

  public Color borderRightColor() {
    return presentationState.value(BORDER_RIGHT_COLOR, resolvedStyle.borderRightColor());
  }

  public Color borderTopColor() {
    return presentationState.value(BORDER_TOP_COLOR, resolvedStyle.borderTopColor());
  }

  public Color borderBottomColor() {
    return presentationState.value(BORDER_BOTTOM_COLOR, resolvedStyle.borderBottomColor());
  }

  public Transform transform() {
    return presentationState.value(TRANSFORM, resolvedStyle.transform());
  }
}
