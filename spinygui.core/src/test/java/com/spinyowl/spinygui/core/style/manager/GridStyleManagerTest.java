package com.spinyowl.spinygui.core.style.manager;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.COLUMN_GAP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_AUTO_FLOW;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_COLUMN_END;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_COLUMN_GAP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_COLUMN_START;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_ROW_END;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_ROW_GAP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_ROW_START;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_TEMPLATE_COLUMNS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_TEMPLATE_ROWS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ROW_GAP;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.flex.AlignContent;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;
import com.spinyowl.spinygui.core.style.types.grid.GridAutoFlow;
import com.spinyowl.spinygui.core.style.types.grid.GridPlacement;
import com.spinyowl.spinygui.core.style.types.grid.GridTemplateAreas;
import com.spinyowl.spinygui.core.style.types.grid.GridTrackList;
import com.spinyowl.spinygui.core.style.types.grid.GridTrackSize;
import com.spinyowl.spinygui.core.style.types.length.Length;
import org.junit.jupiter.api.Test;

class GridStyleManagerTest {

  @Test
  void recalculateKeepsGridContainerAlignmentValuesTyped() {
    Element element = styledElement("justify-content: space-evenly; align-content: space-evenly;");

    assertEquals(JustifyContent.SPACE_EVENLY, element.resolvedStyle().justifyContent());
    assertEquals(AlignContent.SPACE_EVENLY, element.resolvedStyle().alignContent());
  }

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.CsvSource({
      "gap: 8px, 8, 8",
      "gap: 4px 8px, 4, 8",
      "gap: 0, 0, 0",
      "gap: normal 8px, 0, 8",
      "gap: 8px; row-gap: 3px, 3, 8",
      "gap: 8px; grid-column-gap: 3px, 8, 3",
      "row-gap: 3px; gap: 8px, 8, 8",
      "grid-gap: 3px; gap: 8px, 8, 8",
      "gap: 8px; grid-gap: 3px, 3, 3",
      "gap: 8px; gap: -1px, 8, 8",
      "gap: 8px; gap: 2, 8, 8",
      "gap: 8px; gap: 1px 2px 3px, 8, 8",
      "gap: 8px; gap: auto, 8, 8"
  })
  void gapExpandsAndRespectsAliasesAndValidation(String css, float row, float column) {
    Element element = styledElement(css);
    assertEquals(row, element.resolvedStyle().gridRowGap().convert());
    assertEquals(column, element.resolvedStyle().gridColumnGap().convert());
    assertEquals(element.resolvedStyle().getSafe(GRID_ROW_GAP),
        element.resolvedStyle().getSafe(ROW_GAP));
    assertEquals(element.resolvedStyle().getSafe(GRID_COLUMN_GAP),
        element.resolvedStyle().getSafe(COLUMN_GAP));
  }

  @Test
  void gapPreservesPercentages() {
    Element element = styledElement("gap: 10% 20%;");
    assertEquals(Length.percent(.1f), element.resolvedStyle().gridRowGap());
    assertEquals(Length.percent(.2f), element.resolvedStyle().gridColumnGap());
  }

  @Test
  void recalculateAppliesGridDisplayParsedByRealParser() {
    Element element = styledElement("display: grid");

    assertEquals(Display.GRID, element.resolvedStyle().display());
  }

  @Test
  void recalculateStoresTypedGridTracksParsedByRealParser() {
    Element element = styledElement("grid-template-columns: 120px 1fr minmax(20px, 2fr);");

    GridTrackList columns = element.resolvedStyle().gridTemplateColumns();
    assertEquals(3, columns.tracks().size());
    assertEquals(GridTrackSize.fixed(Length.pixel(120)), columns.tracks().get(0).size());
    assertEquals(GridTrackSize.flexible(com.spinyowl.spinygui.core.style.types.grid.GridFraction.fr(1)), columns.tracks().get(1).size());
    assertEquals(
        GridTrackSize.minmax(
            GridTrackSize.fixed(Length.pixel(20)),
            GridTrackSize.flexible(com.spinyowl.spinygui.core.style.types.grid.GridFraction.fr(2))),
        columns.tracks().get(2).size());
  }

  @Test
  void recalculateStoresGridAutoFlowAndGapLonghands() {
    Element element =
        styledElement("grid-auto-flow: row dense; grid-row-gap: 4px; grid-column-gap: 8px;");

    assertEquals(GridAutoFlow.ROW_DENSE, element.resolvedStyle().getSafe(GRID_AUTO_FLOW));
    assertEquals(Length.pixel(4), element.resolvedStyle().getSafe(GRID_ROW_GAP));
    assertEquals(Length.pixel(8), element.resolvedStyle().getSafe(GRID_COLUMN_GAP));
  }

  @Test
  void recalculateExpandsGridGapShorthand() {
    Element element = styledElement("grid-gap: 4px 8px;");

    assertEquals(Length.pixel(4), element.resolvedStyle().getSafe(GRID_ROW_GAP));
    assertEquals(Length.pixel(4), element.resolvedStyle().getSafe(ROW_GAP));
    assertEquals(Length.pixel(8), element.resolvedStyle().getSafe(GRID_COLUMN_GAP));
    assertEquals(Length.pixel(8), element.resolvedStyle().getSafe(COLUMN_GAP));
  }

  @Test
  void recalculateExpandsGridRowAndColumnShorthands() {
    Element element = styledElement("grid-row: 2 / span 3; grid-column: main / 4;");

    assertEquals(GridPlacement.line(2), element.resolvedStyle().getSafe(GRID_ROW_START));
    assertEquals(GridPlacement.span(3), element.resolvedStyle().getSafe(GRID_ROW_END));
    assertEquals(GridPlacement.line("main"), element.resolvedStyle().getSafe(GRID_COLUMN_START));
    assertEquals(GridPlacement.line(4), element.resolvedStyle().getSafe(GRID_COLUMN_END));
  }

  @Test
  void recalculateExpandsGridPlacementWhenSpanAppearsBeforeSlash() {
    Element element = styledElement("grid-row: span 2 / 4;");

    assertEquals(GridPlacement.span(2), element.resolvedStyle().getSafe(GRID_ROW_START));
    assertEquals(GridPlacement.line(4), element.resolvedStyle().getSafe(GRID_ROW_END));
  }

  @Test
  void recalculateRejectsUnsupportedGridAutoFlowValue() {
    Element element = styledElement("grid-auto-flow: sideways;");

    assertEquals(GridAutoFlow.ROW, element.resolvedStyle().getSafe(GRID_AUTO_FLOW));
  }

  @Test
  void recalculateProvidesTypedGridDefaults() {
    Element element = styledElement("display: grid;");

    assertEquals(GridTrackList.NONE, element.resolvedStyle().getSafe(GRID_TEMPLATE_COLUMNS));
    assertEquals(GridTrackList.NONE, element.resolvedStyle().getSafe(GRID_TEMPLATE_ROWS));
    assertEquals(GridAutoFlow.ROW, element.resolvedStyle().gridAutoFlow());
    assertEquals(GridPlacement.AUTO, element.resolvedStyle().gridRowStart());
    assertEquals(GridPlacement.AUTO, element.resolvedStyle().gridColumnStart());
  }

  @Test
  void recalculateStoresTypedTemplateAreasAndNamedGridArea() {
    Element element =
        styledElement(
            "grid-template-areas: \"header header\" \"sidebar main\";"
                + "grid-area: main;");

    GridTemplateAreas areas = element.resolvedStyle().gridTemplateAreas();
    assertEquals(new GridTemplateAreas.AreaRange(0, 1, 0, 2), areas.areas().get("header"));
    assertEquals(new GridTemplateAreas.AreaRange(1, 2, 0, 1), areas.areas().get("sidebar"));
    assertEquals(new GridTemplateAreas.AreaRange(1, 2, 1, 2), areas.areas().get("main"));
    assertEquals(GridPlacement.line("main"), element.resolvedStyle().gridRowStart());
    assertEquals(GridPlacement.AUTO, element.resolvedStyle().gridColumnStart());
  }

  @Test
  void recalculateExpandsSupportedGridTemplateAndGridShorthands() {
    Element template = styledElement("grid-template: 20px 1fr / 40px 2fr;");
    Element grid = styledElement("grid: 20px 1fr / 40px 2fr;");

    assertEquals(2, template.resolvedStyle().gridTemplateRows().tracks().size());
    assertEquals(2, template.resolvedStyle().gridTemplateColumns().tracks().size());
    assertEquals(template.resolvedStyle().gridTemplateRows(), grid.resolvedStyle().gridTemplateRows());
    assertEquals(template.resolvedStyle().gridTemplateColumns(), grid.resolvedStyle().gridTemplateColumns());
  }

  @Test
  void recalculateAcceptsOnlyUnitlessZeroInsets() {
    Element zero = styledElement("top: 0; right: 0; bottom: 0; left: 0;");
    Element nonZero = styledElement("top: 1; right: 1; bottom: 1; left: 1;");

    assertEquals(Length.ZERO, zero.resolvedStyle().top());
    assertEquals(Length.ZERO, zero.resolvedStyle().right());
    assertEquals(Length.ZERO, zero.resolvedStyle().bottom());
    assertEquals(Length.ZERO, zero.resolvedStyle().left());
    assertEquals(com.spinyowl.spinygui.core.style.types.length.Unit.AUTO, nonZero.resolvedStyle().top());
    assertEquals(com.spinyowl.spinygui.core.style.types.length.Unit.AUTO, nonZero.resolvedStyle().right());
    assertEquals(com.spinyowl.spinygui.core.style.types.length.Unit.AUTO, nonZero.resolvedStyle().bottom());
    assertEquals(com.spinyowl.spinygui.core.style.types.length.Unit.AUTO, nonZero.resolvedStyle().left());
  }

  private Element styledElement(String style) {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element element = new Element("div");
    element.style(style);
    frame.addChild(element);

    styleManager.recalculate(frame);

    return element;
  }
}
