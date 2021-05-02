package com.spinyowl.spinygui.core.node.style.types.border;

import com.spinyowl.spinygui.core.node.style.types.Color;
import com.spinyowl.spinygui.core.node.style.types.SideStyle;
import com.spinyowl.spinygui.core.node.style.types.length.Length;

public class Border extends SideStyle<BorderItem> {

  public Border() {
    super(new BorderItem());
  }

  public Border(BorderItem allSides) {
    super(allSides);
  }

  public Border(BorderItem sideTopBottom, BorderItem sideRightLeft) {
    super(sideTopBottom, sideRightLeft);
  }

  public Border(BorderItem sideTop, BorderItem sideRightLeft, BorderItem sideBottom) {
    super(sideTop, sideRightLeft, sideBottom);
  }

  public Border(
      BorderItem sideTop, BorderItem sideRight, BorderItem sideBottom, BorderItem sideLeft) {
    super(sideTop, sideRight, sideBottom, sideLeft);
  }

  /**
   * Copies width from provided border object.
   *
   * @param border border to copy width from.
   */
  public void width(Border border) {
    top().width(border.top().width());
    bottom().width(border.bottom().width());
    left().width(border.left().width());
    right().width(border.right().width());
  }

  public void width(Length<?> width) {
    top().width(width);
    bottom().width(width);
    left().width(width);
    right().width(width);
  }

  public void width(Length<?> widthTopBottom, Length<?> widthRightLeft) {
    top().width(widthTopBottom);
    bottom().width(widthTopBottom);
    left().width(widthRightLeft);
    right().width(widthRightLeft);
  }

  public void width(Length<?> widthTop, Length<?> widthRightLeft, Length<?> widthBottom) {
    top().width(widthTop);
    bottom().width(widthRightLeft);
    left().width(widthRightLeft);
    right().width(widthBottom);
  }

  public void width(
      Length<?> widthTop, Length<?> widthRight, Length<?> widthBottom, Length<?> widthLeft) {
    top().width(widthTop);
    bottom().width(widthLeft);
    left().width(widthBottom);
    right().width(widthRight);
  }

  /**
   * Copies color from provided border object.
   *
   * @param border border to copy color from.
   */
  public void color(Border border) {
    top().color(border.top().color());
    bottom().color(border.bottom().color());
    left().color(border.left().color());
    right().color(border.right().color());
  }

  public void color(Color color) {
    top().color(color);
    bottom().color(color);
    left().color(color);
    right().color(color);
  }

  public void color(Color colorTopBottom, Color colorRightLeft) {
    top().color(colorTopBottom);
    bottom().color(colorTopBottom);
    left().color(colorRightLeft);
    right().color(colorRightLeft);
  }

  public void color(Color colorTop, Color colorRightLeft, Color colorBottom) {
    top().color(colorTop);
    bottom().color(colorRightLeft);
    left().color(colorRightLeft);
    right().color(colorBottom);
  }

  public void color(Color colorTop, Color colorRight, Color colorBottom, Color colorLeft) {
    top().color(colorTop);
    bottom().color(colorLeft);
    left().color(colorBottom);
    right().color(colorRight);
  }

  /**
   * Copies style from provided border object.
   *
   * @param border border to copy style from.
   */
  public void style(Border border) {
    top().style(border.top().style());
    bottom().style(border.bottom().style());
    left().style(border.left().style());
    right().style(border.right().style());
  }

  public void style(BorderStyle style) {
    top().style(style);
    bottom().style(style);
    left().style(style);
    right().style(style);
  }

  public void style(BorderStyle styleTopBottom, BorderStyle styleRightLeft) {
    top().style(styleTopBottom);
    bottom().style(styleTopBottom);
    left().style(styleRightLeft);
    right().style(styleRightLeft);
  }

  public void style(BorderStyle styleTop, BorderStyle styleRightLeft, BorderStyle styleBottom) {
    top().style(styleTop);
    bottom().style(styleRightLeft);
    left().style(styleRightLeft);
    right().style(styleBottom);
  }

  public void style(
      BorderStyle styleTop,
      BorderStyle styleRight,
      BorderStyle styleBottom,
      BorderStyle styleLeft) {
    top().style(styleTop);
    bottom().style(styleLeft);
    left().style(styleBottom);
    right().style(styleRight);
  }
}
