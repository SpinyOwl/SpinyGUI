package com.spinyowl.spinygui.core.node.style.types.background;

import com.spinyowl.spinygui.core.node.style.types.length.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class BackgroundSize {

  private final Type type;

  private final Unit sizeX;
  private final Unit sizeY;

  private BackgroundSize(Type type) {
    this.type = type;
    this.sizeX = this.sizeY = null;
  }

  private BackgroundSize(Unit size) {
    this.type = Type.UNIT;
    this.sizeX = size;
    this.sizeY = Unit.AUTO;
  }

  private BackgroundSize(Unit sizeX, Unit sizeY) {
    this.type = Type.UNIT;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
  }


  public static BackgroundSize size(Unit sizeX) {
    return new BackgroundSize(sizeX);
  }

  public static BackgroundSize size(Unit sizeX, Unit sizeY) {
    return new BackgroundSize(sizeX, sizeY);
  }

  public static BackgroundSize contain() {
    return new BackgroundSize(Type.CONTAIN);
  }

  public static BackgroundSize cover() {
    return new BackgroundSize(Type.COVER);
  }

  public enum Type {
    CONTAIN,
    COVER,
    UNIT
  }
}


