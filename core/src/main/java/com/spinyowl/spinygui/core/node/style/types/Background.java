package com.spinyowl.spinygui.core.node.style.types;

import com.spinyowl.spinygui.core.node.style.types.background.BackgroundOrigin;
import com.spinyowl.spinygui.core.node.style.types.background.BackgroundPosition;
import com.spinyowl.spinygui.core.node.style.types.background.BackgroundSize;
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
  private BackgroundOrigin origin = BackgroundOrigin.PADDING_BOX;

}
