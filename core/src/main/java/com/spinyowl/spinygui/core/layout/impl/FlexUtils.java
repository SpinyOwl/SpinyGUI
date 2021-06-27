package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.style.types.length.LengthType.PERCENT;
import static com.spinyowl.spinygui.core.style.types.length.LengthType.PIXEL;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.function.BiConsumer;
import java.util.function.LongConsumer;
import java.util.function.ObjIntConsumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FlexUtils {

  public static void setLength(
      Length<?> l,
      long node,
      ObjIntConsumer<Long> pixelConsumer,
      BiConsumer<Long, Float> percentConsumer) {
    if (l != null) {
      if (PIXEL.equals(l.type()) || Length.ZERO.equals(l)) {
        pixelConsumer.accept(node, (Integer) l.value());
      } else if (PERCENT.equals(l.type())) {
        percentConsumer.accept(node, (Float) l.value());
      }
    }
  }

  public static void setLength(
      Length<?> l,
      long node,
      int side,
      TriConsumer<Long, Integer, Integer> pixelConsumer,
      TriConsumer<Long, Integer, Float> percentConsumer) {
    if (l != null) {
      if (PIXEL.equals(l.type()) || Length.ZERO.equals(l)) {
        pixelConsumer.accept(node, side, (Integer) l.value());
      } else if (PERCENT.equals(l.type())) {
        percentConsumer.accept(node, side, (Float) l.value());
      }
    }
  }

  public static void setUnit(
      Unit unit,
      long node,
      int side,
      TriConsumer<Long, Integer, Integer> pixelConsumer,
      TriConsumer<Long, Integer, Float> percentConsumer) {
    if (unit != null && !unit.isAuto()) {
      applyPixelOrPercentToSide(unit, node, side, pixelConsumer, percentConsumer);
    }
  }

  public static void setUnit(
      Unit unit,
      long node,
      LongConsumer autoConsumer,
      ObjIntConsumer<Long> pixelConsumer,
      BiConsumer<Long, Float> percentConsumer) {
    if (unit != null) {
      if (unit.isAuto()) {
        autoConsumer.accept(node);
      } else {
        Length<?> l = unit.asLength();
        if (PIXEL.equals(l.type()) || Length.ZERO.equals(l)) {
          pixelConsumer.accept(node, (Integer) l.value());
        } else if (PERCENT.equals(l.type())) {
          percentConsumer.accept(node, (Float) l.value());
        }
      }
    }
  }

  public static void applyPixelOrPercentToSide(
      Unit unit,
      long node,
      int side,
      TriConsumer<Long, Integer, Integer> pixelConsumer,
      TriConsumer<Long, Integer, Float> percentConsumer) {
    Length<?> l = unit.asLength();
    if (PIXEL.equals(l.type()) || Length.ZERO.equals(l)) {
      pixelConsumer.accept(node, side, (Integer) l.value());
    } else if (PERCENT.equals(l.type())) {
      percentConsumer.accept(node, side, (Float) l.value());
    }
  }

  public static void setUnit(
      Unit unit,
      long node,
      int side,
      ObjIntConsumer<Long> autoConsumer,
      TriConsumer<Long, Integer, Integer> pixelConsumer,
      TriConsumer<Long, Integer, Float> percentConsumer) {
    if (unit != null) {
      if (unit.isAuto()) {
        autoConsumer.accept(node, side);
      } else {
        applyPixelOrPercentToSide(unit, node, side, pixelConsumer, percentConsumer);
      }
    }
  }

  public interface TriConsumer<T, U, V> {

    void accept(T t, U u, V v);
  }
}
