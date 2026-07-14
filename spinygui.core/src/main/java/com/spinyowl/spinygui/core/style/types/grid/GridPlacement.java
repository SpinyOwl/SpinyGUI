package com.spinyowl.spinygui.core.style.types.grid;

import java.util.Objects;

/** One side of a grid item placement declaration. */
public sealed interface GridPlacement
    permits GridPlacement.Auto, GridPlacement.Line, GridPlacement.Span {

  Auto AUTO = new Auto();

  static Line line(int index) {
    return new Line(index, null);
  }

  static Line line(String name) {
    return new Line(null, name);
  }

  static Span span(int count) {
    return new Span(count, null);
  }

  static Span span(String name) {
    return new Span(null, name);
  }

  record Auto() implements GridPlacement {}

  record Line(Integer index, String name) implements GridPlacement {
    public Line {
      if (index == null && (name == null || name.isBlank())) {
        throw new IllegalArgumentException("Grid line placement requires an index or name");
      }
      if (index != null && index == 0) {
        throw new IllegalArgumentException("Grid line index cannot be zero");
      }
      if (name != null && name.isBlank()) {
        throw new IllegalArgumentException("Grid line name must be non-blank");
      }
    }
  }

  record Span(Integer count, String name) implements GridPlacement {
    public Span {
      if (count == null && (name == null || name.isBlank())) {
        throw new IllegalArgumentException("Grid span requires a count or name");
      }
      if (count != null && count <= 0) {
        throw new IllegalArgumentException("Grid span count must be positive");
      }
      if (name != null && name.isBlank()) {
        throw new IllegalArgumentException("Grid span name must be non-blank");
      }
    }
  }

  record Area(String name) {
    public Area {
      Objects.requireNonNull(name, "name");
      if (name.isBlank() || ".".equals(name)) {
        throw new IllegalArgumentException("Grid area name must be non-blank and not '.'");
      }
    }
  }
}
