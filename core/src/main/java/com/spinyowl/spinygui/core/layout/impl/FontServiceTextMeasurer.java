package com.spinyowl.spinygui.core.layout.impl;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.FontMetrics;
import com.spinyowl.spinygui.core.layout.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.FontService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FontServiceTextMeasurer implements TextMeasurer {
  @NonNull private final FontService fontService;

  @Override
  public float measure(@NonNull String text, @NonNull Font font, float fontSize) {
    return fontService.getTextLineMetrics(text, font, fontSize, 1).width();
  }

  @Override
  public FontMetrics metrics(@NonNull Font font, float fontSize, float lineHeight) {
    return fontService.getFontMetrics(font, fontSize, lineHeight);
  }
}
