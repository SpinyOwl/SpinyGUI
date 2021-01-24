package com.spinyowl.spinygui.core.node.style.types.background;

import java.util.Objects;

public class BackgroundRepeat {

  private Repeat repeatX;
  private Repeat repeatY;

  public BackgroundRepeat() {
    repeatY = repeatX = Repeat.REPEAT;
  }

  public BackgroundRepeat(Repeat repeat) {
    repeatY = repeatX = Objects.requireNonNull(repeat);
  }

  public BackgroundRepeat(Repeat repeatX, Repeat repeatY) {
    this.repeatX = Objects.requireNonNull(repeatX);
    this.repeatY = Objects.requireNonNull(repeatY);
  }

  public Repeat getRepeatX() {
    return repeatX;
  }

  public void setRepeatX(Repeat repeatX) {
    this.repeatX = Objects.requireNonNull(repeatX);
  }

  public Repeat getRepeatY() {
    return repeatY;
  }

  public void setRepeatY(Repeat repeatY) {
    this.repeatY = Objects.requireNonNull(repeatY);
  }

  public enum Repeat {
    REPEAT,
    NO_REPEAT,
    SPACE,
    ROUND
  }
}
