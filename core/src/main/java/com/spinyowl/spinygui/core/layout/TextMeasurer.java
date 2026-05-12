package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import lombok.NonNull;

public interface TextMeasurer {

  TextLineMetrics measure(
      @NonNull String text, @NonNull Font font, float fontSize, float lineHeight);
}
