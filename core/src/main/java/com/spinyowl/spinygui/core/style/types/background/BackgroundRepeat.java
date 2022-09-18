package com.spinyowl.spinygui.core.style.types.background;

import lombok.NonNull;

public class BackgroundRepeat {

  private Repeat repeatX;
  private Repeat repeatY;

  public BackgroundRepeat() {
    repeatY = repeatX = Repeat.REPEAT;
  }

  public BackgroundRepeat(@NonNull Repeat repeat) {
    repeatY = repeatX = repeat;
  }

  public BackgroundRepeat(@NonNull Repeat repeatX, @NonNull Repeat repeatY) {
    this.repeatX = repeatX;
    this.repeatY = repeatY;
  }

  public Repeat getRepeatX() {
    return repeatX;
  }

  public void setRepeatX(@NonNull Repeat repeatX) {
    this.repeatX = repeatX;
  }

  public Repeat getRepeatY() {
    return repeatY;
  }

  public void setRepeatY(@NonNull Repeat repeatY) {
    this.repeatY = repeatY;
  }

  @Override
  public String toString() {
    return repeatX.name().toLowerCase() + " " + repeatY.name().toLowerCase();
  }

  public enum Repeat {
    REPEAT,
    NO_REPEAT,
    SPACE,
    ROUND
  }
}
