package com.spinyowl.spinygui.core;

import com.spinyowl.spinygui.core.animation.TransitionImpact;
import com.spinyowl.spinygui.core.layout.LayoutResult;
import com.spinyowl.spinygui.core.style.manager.StyleImpact;

/** Result of backend-independent frame preparation. Rendering is still host-owned. */
public record FramePreparation(
    Status status,
    long revision,
    boolean styleExecuted,
    boolean transitionExecuted,
    boolean layoutExecuted,
    boolean transformExecuted,
    boolean renderRequired,
    StyleImpact styleImpact,
    TransitionImpact transitionImpact,
    LayoutResult layoutResult,
    Throwable failure) {

  public enum Status { READY, SUPERSEDED, UNCONVERGED, FAILED }

  public boolean renderable() {
    return status == Status.READY;
  }
}
