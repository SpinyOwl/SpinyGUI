package com.spinyowl.spinygui.core.style.types.grid;

import com.spinyowl.spinygui.core.style.types.length.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
public class GridFraction implements Unit {
  @NonNull private final Float value;

  public GridFraction(@NonNull Float value) {
    if (!Float.isFinite(value) || value < 0f) {
      throw new IllegalArgumentException("Grid fraction must be a finite non-negative value");
    }
    this.value = value;
  }

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
