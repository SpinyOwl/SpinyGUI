package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import com.spinyowl.spinygui.core.FramePreparation;

/** Stops presentation when a frame could not be prepared safely. */
public final class FramePreparationException extends RuntimeException {
  private final FramePreparation preparation;

  public FramePreparationException(FramePreparation preparation) {
    super("Frame preparation is not renderable: " + preparation.status(), preparation.failure());
    this.preparation = preparation;
  }

  public FramePreparation preparation() {
    return preparation;
  }
}
