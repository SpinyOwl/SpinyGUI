package com.spinyowl.spinygui.core.node;

/** Immutable observation of source revision and pending frame work. */
public record FrameInvalidation(
    long revision,
    boolean styleDirty,
    boolean layoutDirty,
    boolean transformDirty,
    boolean paintDirty) {

  public boolean clean() {
    return !styleDirty && !layoutDirty && !transformDirty && !paintDirty;
  }
}
