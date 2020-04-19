package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.types.length.Length;

public class Padding extends SideStyle<Length> {

  public Padding() {
    super(Length.ZERO);
  }

  public Padding(Length allSides) {
    super(allSides);
  }

  public Padding(Length sideTopBottom,
    Length sideRightLeft) {
    super(sideTopBottom, sideRightLeft);
  }

  public Padding(Length sideTop, Length sideRightLeft,
    Length sideBottom) {
    super(sideTop, sideRightLeft, sideBottom);
  }

  public Padding(Length sideTop, Length sideRight,
    Length sideBottom, Length sideLeft) {
    super(sideTop, sideRight, sideBottom, sideLeft);
  }
}
