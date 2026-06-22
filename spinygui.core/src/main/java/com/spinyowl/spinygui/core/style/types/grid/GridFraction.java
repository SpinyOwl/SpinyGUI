package com.spinyowl.spinygui.core.style.types.grid;

import com.spinyowl.spinygui.core.style.types.length.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class GridFraction implements Unit {
  @NonNull private final Float value;

  public static GridFraction fr(float value) {
    return new GridFraction(value);
  }

  @Override
  public boolean isAuto() {
    return false;
  }

  @Override
  public String toString() {
    return value + "fr";
  }
}
