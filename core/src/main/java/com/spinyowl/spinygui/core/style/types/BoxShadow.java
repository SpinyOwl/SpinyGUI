package com.spinyowl.spinygui.core.style.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class BoxShadow {

  public static final BoxShadow NO_SHADOW = new BoxShadow();

  private boolean inset;

  private float hOffset;
  private float vOffset;
  private float blur;
  private float spread;

  @NonNull private Color color = Color.TRANSPARENT;
}
