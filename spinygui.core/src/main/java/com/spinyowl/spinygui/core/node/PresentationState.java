package com.spinyowl.spinygui.core.node;

import com.spinyowl.spinygui.core.style.types.AffineTransform;
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

  /** Returns the transform currently presented for the owning element. */
  public AffineTransform transform() {
    return transform;
  }

  /** Sets the transform currently presented for the owning element. */
  public void transform(AffineTransform transform) {
    this.transform = Objects.requireNonNull(transform, "transform must not be null");
  }

  /** Resets all presented values when an element is recalculated, hidden, or detached. */
  public void reset() {
    transform = AffineTransform.IDENTITY;
  }
}
