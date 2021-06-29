package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorderItem {

  @NonNull private Color color;
  @NonNull private BorderStyle style;
  @NonNull private Length<?> width;
}
