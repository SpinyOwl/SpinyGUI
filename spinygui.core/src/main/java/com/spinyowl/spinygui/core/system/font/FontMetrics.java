package com.spinyowl.spinygui.core.system.font;

import lombok.Value;

/** Vertical font metrics in line-local coordinates. */
@Value
public class FontMetrics {
  /** Distance from the top of the font box to the baseline, in pixels. */
  float ascent;

  /** Distance below the baseline, in pixels. */
  float descent;

  /** Additional line gap, in pixels. */
  float lineGap;

  /** Resolved line-box height, in pixels. */
  float lineHeight;

  /** Line-local baseline offset from the top of the line, in pixels. */
  float baseline;
}
