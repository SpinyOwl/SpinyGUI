package com.spinyowl.spinygui.core.layout.impl;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.util.List;
import lombok.NonNull;

abstract class AbstractFixedTextMeasurer implements TextMeasurer {

  @Override
  public TextMetrics measureText(
      @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
    TextLineMetrics line = getTextLineMetrics(text, font, fontSize, lineHeight);
    return TextMetrics.builder()
        .lines(List.of(line))
        .width(line.width())
        .height(line.height())
        .lineHeight(line.height())
        .fontMetrics(line.fontMetrics())
        .build();
  }

  @Override
  public TextMetrics measureText(
      @NonNull String text,
      float offsetX,
      @NonNull Font font,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    return measureText(text, font, fontSize, lineHeight);
  }

  @Override
  public TextMetrics getTextMetrics(
      @NonNull String text,
      float offsetX,
      @NonNull Font font,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    return measureText(text, offsetX, font, fontSize, lineHeight, maxWidth, wordWrap);
  }
}
