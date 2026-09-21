package com.spinyowl.spinygui.core.layout.impl;

/**
 * Resolves the final position of already-sized Grid tracks inside their content area.
 *
 * <p>This deliberately operates after track sizing. It therefore never stretches tracks and
 * falls back to the existing start geometry for unsupported values or non-positive free space.
 */
final class GridTrackAlignment {

  private static final float EPSILON = 0.0001f;

  private GridTrackAlignment() {}

  static Geometry resolve(float available, float tracksSize, int trackCount, float gap, String value) {
    float freeSpace = available - tracksSize - gap * Math.max(0, trackCount - 1);
    if (freeSpace <= EPSILON) {
      return new Geometry(0, gap);
    }
    return switch (value) {
      case "flex-end" -> new Geometry(freeSpace, gap);
      case "center" -> new Geometry(freeSpace / 2f, gap);
      case "space-between" ->
          trackCount > 1 ? new Geometry(0, gap + freeSpace / (trackCount - 1)) : new Geometry(0, gap);
      case "space-around" -> {
        float space = freeSpace / trackCount;
        yield new Geometry(space / 2f, gap + space);
      }
      case "space-evenly" -> {
        float space = freeSpace / (trackCount + 1);
        yield new Geometry(space, gap + space);
      }
      default -> new Geometry(0, gap);
    };
  }

  record Geometry(float offset, float gap) {}
}
