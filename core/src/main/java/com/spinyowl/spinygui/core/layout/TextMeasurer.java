package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.font.Font;
import lombok.NonNull;

public interface TextMeasurer {

  float measure(@NonNull String text, @NonNull Font font, float fontSize);

  FontMetrics metrics(@NonNull Font font, float fontSize, float lineHeight);
}
