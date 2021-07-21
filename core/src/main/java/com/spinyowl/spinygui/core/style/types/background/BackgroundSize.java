package com.spinyowl.spinygui.core.style.types.background;

import com.spinyowl.spinygui.core.style.types.length.Unit;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class BackgroundSize {
  private final boolean unit;
  private final boolean contain;
  private final boolean cover;

  public static BackgroundSize createSize(Unit sizeX) {
    return new BackgroundSizeUnit(sizeX);
  }

  public static BackgroundSize createSize(Unit sizeX, Unit sizeY) {
    return new BackgroundSizeUnit(sizeX, sizeY);
  }

  public static BackgroundSize createContain() {
    return new BackgroundSizeContain();
  }

  public static BackgroundSize createCover() {
    return new BackgroundSizeCover();
  }

  public BackgroundSizeUnit asUnit() {
    return (BackgroundSizeUnit) this;
  }

  @Getter
  @EqualsAndHashCode
  @ToString
  public static class BackgroundSizeContain extends BackgroundSize {
    private BackgroundSizeContain() {
      super(false, true, false);
    }
  }

  @Getter
  @EqualsAndHashCode
  @ToString
  public static class BackgroundSizeCover extends BackgroundSize {

    private BackgroundSizeCover() {
      super(false, false, true);
    }
  }

  @Getter
  @EqualsAndHashCode
  @ToString
  public static class BackgroundSizeUnit extends BackgroundSize {
    private final Unit sizeX;
    private final Unit sizeY;

    private BackgroundSizeUnit(Unit size) {
      this(size, Unit.AUTO);
    }

    private BackgroundSizeUnit(Unit sizeX, Unit sizeY) {
      super(true, false, false);
      this.sizeX = sizeX;
      this.sizeY = sizeY;
    }
  }
}
