package com.spinyowl.spinygui.core.node.style.types.background;

import com.spinyowl.spinygui.core.node.style.types.length.Length;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class BackgroundPosition {

  private Length<?> x;
  private Length<?> y;

  public BackgroundPosition(Length<?> x) {
    this.x = x;
    this.y = Length.percent(50);
  }
}
