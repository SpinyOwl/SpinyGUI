package com.spinyowl.spinygui.core.layout.impl;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FontServiceTextMeasurer implements TextMeasurer {
  @NonNull private final FontService fontService;

  @Override
  public TextLineMetrics measure(
      @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
    return fontService.measureText(text, font, fontSize, lineHeight).lines().get(0);
  }
}
