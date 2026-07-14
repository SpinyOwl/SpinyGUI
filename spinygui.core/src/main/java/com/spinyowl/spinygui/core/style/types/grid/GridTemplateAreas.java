package com.spinyowl.spinygui.core.style.types.grid;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Rectangular grid-template-areas model. */
public record GridTemplateAreas(List<List<String>> rows, Map<String, AreaRange> areas) {

  public static final GridTemplateAreas NONE = new GridTemplateAreas(List.of(), Map.of());
  public static final String EMPTY_CELL = ".";

  public GridTemplateAreas {
    Objects.requireNonNull(rows, "rows");
    Objects.requireNonNull(areas, "areas");
    rows = rows.stream().map(List::copyOf).toList();
    areas = Map.copyOf(areas);
  }

  public static GridTemplateAreas of(List<List<String>> rows) {
    if (rows == null || rows.isEmpty()) {
      return NONE;
    }
    int width = rows.getFirst().size();
    if (width == 0) {
      throw new IllegalArgumentException("Grid template area rows cannot be empty");
    }
    List<List<String>> copiedRows = new ArrayList<>();
    for (List<String> row : rows) {
      if (row.size() != width) {
        throw new IllegalArgumentException("Grid template area rows must have equal width");
      }
      List<String> copied = new ArrayList<>();
      for (String cell : row) {
        if (cell == null || cell.isBlank()) {
          throw new IllegalArgumentException("Grid template area cells must be non-blank");
        }
        copied.add(cell);
      }
      copiedRows.add(List.copyOf(copied));
    }

    Map<String, MutableAreaRange> ranges = new LinkedHashMap<>();
    for (int row = 0; row < copiedRows.size(); row++) {
      for (int column = 0; column < width; column++) {
        String name = copiedRows.get(row).get(column);
        if (EMPTY_CELL.equals(name)) {
          continue;
        }
        MutableAreaRange range = ranges.get(name);
        if (range == null) {
          range = new MutableAreaRange(row, column);
          ranges.put(name, range);
        }
        range.include(row, column);
      }
    }

    Map<String, AreaRange> immutableRanges = new LinkedHashMap<>();
    ranges.forEach((name, range) -> immutableRanges.put(name, range.toRange()));
    immutableRanges.forEach((name, range) -> validateRectangle(name, range, copiedRows));
    return new GridTemplateAreas(copiedRows, immutableRanges);
  }

  private static void validateRectangle(String name, AreaRange range, List<List<String>> rows) {
    for (int row = range.rowStart(); row < range.rowEnd(); row++) {
      for (int column = range.columnStart(); column < range.columnEnd(); column++) {
        if (!name.equals(rows.get(row).get(column))) {
          throw new IllegalArgumentException("Grid template area '" + name + "' must be rectangular");
        }
      }
    }
  }

  public record AreaRange(int rowStart, int rowEnd, int columnStart, int columnEnd) {}

  private static final class MutableAreaRange {
    private int rowStart;
    private int rowEnd;
    private int columnStart;
    private int columnEnd;

    private MutableAreaRange(int row, int column) {
      this.rowStart = row;
      this.rowEnd = row + 1;
      this.columnStart = column;
      this.columnEnd = column + 1;
    }

    private void include(int row, int column) {
      rowStart = Math.min(rowStart, row);
      rowEnd = Math.max(rowEnd, row + 1);
      columnStart = Math.min(columnStart, column);
      columnEnd = Math.max(columnEnd, column + 1);
    }

    private AreaRange toRange() {
      return new AreaRange(rowStart, rowEnd, columnStart, columnEnd);
    }
  }
}
