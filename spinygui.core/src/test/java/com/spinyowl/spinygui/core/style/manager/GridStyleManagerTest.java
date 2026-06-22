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
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ROW_GAP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermInteger;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.grid.GridFraction;
import com.spinyowl.spinygui.core.style.types.length.Length;
import org.junit.jupiter.api.Test;

class GridStyleManagerTest {

  @Test
  void recalculateAppliesGridDisplayParsedByRealParser() {
    Element element = styledElement("display: grid");

    assertEquals(Display.GRID, element.resolvedStyle().display());
  }

  @Test
  void recalculateStoresGridTrackTermsParsedByRealParser() {
    Element element = styledElement("grid-template-columns: 120px 1fr minmax(20px, 2fr);");

    TermList columns =
        assertInstanceOf(TermList.class, element.resolvedStyle().getSafe(GRID_TEMPLATE_COLUMNS));
    assertInstanceOf(TermLength.class, columns.get(0));
    assertEquals(GridFraction.fr(1), columns.get(1).value());
    assertEquals("minmax(20.0px, 2.0fr)", columns.get(2).toString());
  }

  @Test
  void recalculateStoresGridAutoFlowAndGapLonghands() {
    Element element =
        styledElement("grid-auto-flow: row dense; grid-row-gap: 4px; grid-column-gap: 8px;");

    assertEquals("row dense", element.resolvedStyle().getSafe(GRID_AUTO_FLOW).toString());
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

    assertEquals(new TermInteger(2), element.resolvedStyle().getSafe(GRID_ROW_START));
    assertEquals("span 3", element.resolvedStyle().getSafe(GRID_ROW_END).toString());
    assertEquals(new TermIdent("main"), element.resolvedStyle().getSafe(GRID_COLUMN_START));
    assertEquals(new TermInteger(4), element.resolvedStyle().getSafe(GRID_COLUMN_END));
  }

  @Test
  void recalculateExpandsGridPlacementWhenSpanAppearsBeforeSlash() {
    Element element = styledElement("grid-row: span 2 / 4;");

    assertEquals("span 2", element.resolvedStyle().getSafe(GRID_ROW_START).toString());
    assertEquals(new TermInteger(4), element.resolvedStyle().getSafe(GRID_ROW_END));
  }

  @Test
  void recalculateRejectsUnsupportedGridAutoFlowValue() {
    Element element = styledElement("grid-auto-flow: sideways;");

    assertEquals(new TermIdent("row"), element.resolvedStyle().getSafe(GRID_AUTO_FLOW));
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
