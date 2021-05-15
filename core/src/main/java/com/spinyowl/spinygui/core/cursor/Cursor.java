package com.spinyowl.spinygui.core.cursor;

import com.spinyowl.spinygui.core.image.Image;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Cursor {

  public static final Cursor ARROW = new Cursor();
  public static final Cursor H_RESIZE = new Cursor();
  public static final Cursor V_RESIZE = new Cursor();
  public static final Cursor CROSSHAIR = new Cursor();
  public static final Cursor HAND = new Cursor();
  public static final Cursor IBEAM = new Cursor();

  private final int xHot;
  private final int yHot;
  private final Image image;

  private Cursor() {
    xHot = 0;
    yHot = 0;
    image = null;
  }

  public Cursor(int xHot, int yHot, @NonNull Image image) {
    this.xHot = xHot;
    this.yHot = yHot;
    this.image = image;
  }
}
