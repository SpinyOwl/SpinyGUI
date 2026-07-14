package com.spinyowl.spinygui.core.style.types.grid;

public record GridAutoFlow(Direction direction, boolean dense) {
  public static final GridAutoFlow ROW = new GridAutoFlow(Direction.ROW, false);
  public static final GridAutoFlow COLUMN = new GridAutoFlow(Direction.COLUMN, false);
  public static final GridAutoFlow ROW_DENSE = new GridAutoFlow(Direction.ROW, true);
  public static final GridAutoFlow COLUMN_DENSE = new GridAutoFlow(Direction.COLUMN, true);

  public enum Direction {
    ROW,
    COLUMN
  }
}
