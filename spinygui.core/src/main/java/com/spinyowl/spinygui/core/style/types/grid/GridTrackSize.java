package com.spinyowl.spinygui.core.style.types.grid;

import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.Objects;

/** Layout-facing representation of supported CSS Grid track sizing functions. */
public sealed interface GridTrackSize
    permits GridTrackSize.Fixed,
        GridTrackSize.Flexible,
        GridTrackSize.Auto,
        GridTrackSize.MinMax,
        GridTrackSize.FitContent {

  Auto AUTO = new Auto();

  static Fixed fixed(Length<?> length) {
    return new Fixed(length);
  }

  static Flexible flexible(GridFraction fraction) {
    return new Flexible(fraction);
  }

  static MinMax minmax(GridTrackSize min, GridTrackSize max) {
    return new MinMax(min, max);
  }

  static FitContent fitContent(Length<?> limit) {
    return new FitContent(limit);
  }

  record Fixed(Length<?> length) implements GridTrackSize {
    public Fixed {
      Objects.requireNonNull(length, "length");
      if (!Float.isFinite(length.value().floatValue())) {
        throw new IllegalArgumentException("Grid track length must be finite");
      }
    }
  }

  record Flexible(GridFraction fraction) implements GridTrackSize {
    public Flexible {
      Objects.requireNonNull(fraction, "fraction");
    }
  }

  record Auto() implements GridTrackSize {}

  record MinMax(GridTrackSize min, GridTrackSize max) implements GridTrackSize {
    public MinMax {
      Objects.requireNonNull(min, "min");
      Objects.requireNonNull(max, "max");
      if (min instanceof Flexible) {
        throw new IllegalArgumentException("Grid minmax minimum cannot be flexible");
      }
      if (min instanceof Fixed minFixed && max instanceof Fixed maxFixed) {
        Length<?> minLength = minFixed.length();
        Length<?> maxLength = maxFixed.length();
        if (minLength.type().equals(maxLength.type())
            && minLength.value().floatValue() > maxLength.value().floatValue()) {
          throw new IllegalArgumentException("Grid minmax minimum cannot exceed maximum");
        }
      }
    }
  }

  record FitContent(Length<?> limit) implements GridTrackSize {
    public FitContent {
      Objects.requireNonNull(limit, "limit");
      if (!Float.isFinite(limit.value().floatValue()) || limit.value().floatValue() < 0f) {
        throw new IllegalArgumentException("Grid fit-content limit must be finite and non-negative");
      }
    }
  }
}
