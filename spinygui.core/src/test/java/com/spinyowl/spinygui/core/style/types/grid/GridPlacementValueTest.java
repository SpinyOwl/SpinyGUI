package com.spinyowl.spinygui.core.style.types.grid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class GridPlacementValueTest {

  @Test
  void placementsRejectInvalidLinesAndSpans() {
    assertThrows(IllegalArgumentException.class, () -> GridPlacement.line(0));
    assertThrows(IllegalArgumentException.class, () -> GridPlacement.line(""));
    assertThrows(IllegalArgumentException.class, () -> GridPlacement.span(0));
    assertThrows(IllegalArgumentException.class, () -> GridPlacement.span(""));
  }

  @Test
  void templateAreasPreserveRowsAndRanges() {
    var areas =
        GridTemplateAreas.of(
            List.of(
                List.of("header", "header"),
                List.of("sidebar", "main"),
                List.of("sidebar", "main")));

    assertEquals(3, areas.rows().size());
    assertEquals(new GridTemplateAreas.AreaRange(0, 1, 0, 2), areas.areas().get("header"));
    assertEquals(new GridTemplateAreas.AreaRange(1, 3, 0, 1), areas.areas().get("sidebar"));
    assertEquals(new GridTemplateAreas.AreaRange(1, 3, 1, 2), areas.areas().get("main"));
  }

  @Test
  void templateAreasRejectRaggedOrNonRectangularRows() {
    assertThrows(
        IllegalArgumentException.class,
        () -> GridTemplateAreas.of(List.of(List.of("a"), List.of("a", "b"))));
    assertThrows(
        IllegalArgumentException.class,
        () -> GridTemplateAreas.of(List.of(List.of("a", "b"), List.of("a", "a"))));
  }
}
