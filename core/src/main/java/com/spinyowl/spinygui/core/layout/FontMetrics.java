package com.spinyowl.spinygui.core.layout;

import lombok.Value;

@Value
public class FontMetrics {
  float ascent;
  float descent;
  float lineGap;

  public float lineHeight(float requestedLineHeight) {
    return Math.max(requestedLineHeight, ascent + descent + lineGap);
  }
}
