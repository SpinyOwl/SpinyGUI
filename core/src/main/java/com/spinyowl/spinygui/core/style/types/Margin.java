package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.types.length.Unit;

public class Margin extends SideStyle<Unit> {

  public Margin() {
    super(Unit.AUTO);
  }

  public Margin(Unit allSides) {
    super(allSides);
  }

  public Margin(Unit sideTopBottom, Unit sideRightLeft) {
    super(sideTopBottom, sideRightLeft);
  }

  public Margin(Unit sideTop, Unit sideRightLeft,
      Unit sideBottom) {
    super(sideTop, sideRightLeft, sideBottom);
  }

  public Margin(Unit sideTop, Unit sideRight,
      Unit sideBottom, Unit sideLeft) {
    super(sideTop, sideRight, sideBottom, sideLeft);
  }
}
