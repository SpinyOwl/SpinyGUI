package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.style.types.Color;

/** Exact, fail-closed text-state knowledge limited to a mediated save/restore scope. */
final class NvgTextStateTracker {
  private boolean scoped;
  private Font face;
  private Integer fontSizeBits;
  private Color color;
  private Integer alignment;

  void beginScope() {
    scoped = true;
    invalidate();
  }

  void endScope() {
    invalidate();
    scoped = false;
  }

  void invalidate() {
    face = null;
    fontSizeBits = null;
    color = null;
    alignment = null;
  }

  boolean selectFace(Font requested) {
    if (scoped && requested.equals(face)) return false;
    face = scoped ? requested : null;
    return true;
  }

  boolean fontSize(float requested) {
    int bits = Float.floatToRawIntBits(requested);
    if (scoped && Integer.valueOf(bits).equals(fontSizeBits)) return false;
    fontSizeBits = scoped ? bits : null;
    return true;
  }

  boolean color(Color requested) {
    if (scoped && requested.equals(color)) return false;
    color = scoped ? requested : null;
    return true;
  }

  boolean alignment(int requested) {
    if (scoped && Integer.valueOf(requested).equals(alignment)) return false;
    alignment = scoped ? requested : null;
    return true;
  }
}
