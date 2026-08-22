package com.spinyowl.spinygui.core.layout;

/** Truthful result of a complete layout attempt. */
public record LayoutResult(Status status, int passes, Throwable failure) {
  public enum Status { CONVERGED, UNCONVERGED, FAILED }

  public LayoutResult {
    if (status == null) throw new NullPointerException("status");
    if (passes < 0) throw new IllegalArgumentException("passes must be non-negative");
    if ((status == Status.FAILED) != (failure != null)) {
      throw new IllegalArgumentException("only failed layout may carry a failure");
    }
  }

  public static LayoutResult converged(int passes) {
    return new LayoutResult(Status.CONVERGED, passes, null);
  }

  public static LayoutResult unconverged(int passes) {
    return new LayoutResult(Status.UNCONVERGED, passes, null);
  }

  public static LayoutResult failed(Throwable failure) {
    return new LayoutResult(Status.FAILED, 0, java.util.Objects.requireNonNull(failure));
  }

  public boolean successful() {
    return status == Status.CONVERGED;
  }
}
