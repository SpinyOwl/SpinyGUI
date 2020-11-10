package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Background {

  @NonNull
  private Color color;

  private String image;

  private Length positionX;
  private Length positionY;

  private BackgroundSize size;

  @Data
  @NoArgsConstructor
  public static final class BackgroundSize {

    private boolean cover;
    private boolean contain;

    private Unit width;
    private Unit height;

    public void cover(boolean cover) {
      if (cover) {
        contain = false;
        width = null;
        height = null;
      }
    }

    public void contain(boolean contain) {
      if (contain) {
        cover = false;
        width = null;
        height = null;
      }
    }
  }

}
