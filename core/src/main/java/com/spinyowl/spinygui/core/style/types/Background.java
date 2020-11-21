package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.types.background.BackgroundPosition;
import com.spinyowl.spinygui.core.style.types.background.BackgroundSize;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Background {

  @NonNull
  private Color color = Color.TRANSPARENT;
  private String image;
  private BackgroundPosition position;
  private BackgroundSize size;

}
