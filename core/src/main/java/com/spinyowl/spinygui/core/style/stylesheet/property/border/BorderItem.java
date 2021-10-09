package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length.PixelLength;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BorderItem {

  private Color color;
  private BorderStyle style;
  private PixelLength width;
}
