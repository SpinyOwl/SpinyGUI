package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.types.length.Length;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Background {

  @NonNull
  private Color color;

  private String image;

  private Length backgroundPositionX;
  private Length backgroundPositionY;

}
