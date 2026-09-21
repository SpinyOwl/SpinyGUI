package com.spinyowl.spinygui.demo.complex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.flex.AlignContent;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;
import com.spinyowl.spinygui.core.style.types.grid.GridFraction;
import com.spinyowl.spinygui.core.style.types.grid.GridTrackList;
import com.spinyowl.spinygui.core.style.types.grid.GridTrackSize;
import com.spinyowl.spinygui.core.style.types.length.Length;
import org.junit.jupiter.api.Test;

class GridStyleExampleTest {

  @Test
  void m7PanelUsesAlignedMixedTracksWithNestedControlsAndScrollCell() {
    GridStyleExample example = new GridStyleExample();
    example.nodeParser = new DefaultNodeParser();
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(propertyStore);
    example.styleSheetParser = parser;

    Frame frame = example.createGuiElements(720, 560);
    new StyleManagerImpl(propertyStore, parser).recalculate(frame);

    var panel = frame.getElementById("m7-grid-panel");
    assertNotNull(panel);
    assertEquals(Display.GRID, panel.resolvedStyle().display());
    assertEquals(JustifyContent.SPACE_EVENLY, panel.resolvedStyle().justifyContent());
    assertEquals(AlignContent.SPACE_EVENLY, panel.resolvedStyle().alignContent());
    GridTrackList columns = panel.resolvedStyle().gridTemplateColumns();
    assertEquals(3, columns.tracks().size());
    assertEquals(GridTrackSize.AUTO, columns.tracks().get(0).size());
    assertEquals(GridTrackSize.flexible(GridFraction.fr(.25f)), columns.tracks().get(1).size());
    assertEquals(
        GridTrackSize.minmax(
            GridTrackSize.fixed(Length.pixel(96)),
            GridTrackSize.flexible(GridFraction.fr(.25f))),
        columns.tracks().get(2).size());
    assertInstanceOf(InputElement.class, frame.getElementById("m7-check"));
    assertNotNull(frame.getElementById("m7-apply"));
    assertNotNull(frame.getElementById("m7-scroll"));
  }

}
