package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.types.length.Length;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class BoxShadow {

  public static final BoxShadow NO_SHADOW = new BoxShadow();

  private boolean inset;

  private Length<?> hOffset;
  private Length<?> vOffset;
  private Length<?> blur;
  private Length<?> spread;

  @NonNull private Color color = Color.TRANSPARENT;
}
