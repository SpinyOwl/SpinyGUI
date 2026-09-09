package com.spinyowl.spinygui.core.style.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStoreProvider;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class FlexStyleManagerTest {

  @ParameterizedTest
  @CsvSource({"1,1,1,0", "0,0,1,0", "2 3,2,3,0", "2 3 0,2,3,0",
      "2 3 10px,2,3,10", "10px 2 3,2,3,10", "2 10px,2,1,10", "10px,1,1,10",
      "0px,1,1,0", "0 0,0,0,0", "0 0 0,0,0,0", "1.5 0.25 8px,1.5,0.25,8"})
  void flexExpandsNumbersAndLengths(String value, float grow, float shrink, float pixels) {
    var style = styledElement("flex: " + value).resolvedStyle();
    assertEquals(grow, style.flexGrow());
    assertEquals(shrink, style.flexShrink());
    assertEquals(Length.pixel(pixels), style.flexBasis());
  }

  @ParameterizedTest
  @CsvSource({"auto,1,1", "none,0,0", "initial,0,1", "2 auto,2,1",
      "auto 2 3,2,3", "2 3 auto,2,3", "AUTO,1,1", "NONE,0,0"})
  void flexExpandsAutoBasis(String value, float grow, float shrink) {
    var style = styledElement("flex: " + value).resolvedStyle();
    assertEquals(grow, style.flexGrow());
    assertEquals(shrink, style.flexShrink());
    assertEquals(Unit.AUTO, style.flexBasis());
  }

  @Test
  void flexPreservesPercentageBasis() {
    var style = styledElement("flex: 2 3 25%").resolvedStyle();
    assertEquals(25F, style.flexBasis().asLength().convert(100));
    assertEquals("%", style.flexBasis().asLength().type());
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "1 -2", "1 1 -3px", "1 1 -5%", "1 2 3", "1 2 3 4",
      "auto auto", "none 1", "1, 2", "1 / 2", "1 auto 2", "0 auto 0", "bogus"})
  void invalidFlexPreservesEarlierComponents(String value) {
    var style = styledElement("flex: 4 5 6px; flex: " + value).resolvedStyle();
    assertEquals(4F, style.flexGrow());
    assertEquals(5F, style.flexShrink());
    assertEquals(Length.pixel(6), style.flexBasis());
  }

  @ParameterizedTest
  @CsvSource({"row wrap,row,wrap", "wrap row,row,wrap",
      "column-reverse wrap-reverse,column-reverse,wrap-reverse",
      "column,column,nowrap", "wrap,row,wrap", "initial,row,nowrap"})
  void flowExpandsAndResetsMissingComponents(String value, String direction, String wrap) {
    var style = styledElement("flex-flow: column wrap-reverse; flex-flow: " + value).resolvedStyle();
    assertEquals(FlexDirection.find(direction), style.flexDirection());
    assertEquals(FlexWrap.find(wrap), style.flexWrap());
  }

  @ParameterizedTest
  @ValueSource(strings = {"row column", "wrap nowrap", "row wrap auto", "row, wrap",
      "row / wrap", "row 0", "bogus"})
  void invalidFlowPreservesEarlierComponents(String value) {
    var style = styledElement("flex-flow: column wrap; flex-flow: " + value).resolvedStyle();
    assertEquals(FlexDirection.COLUMN, style.flexDirection());
    assertEquals(FlexWrap.WRAP, style.flexWrap());
  }

  @Test
  void shorthandAndLonghandRespectDeclarationOrder() {
    var style = styledElement("flex-grow: 9; flex-shrink: 8; flex-basis: 7px; flex: 2;"
        + "flex-shrink: 3; flex-flow: column wrap; flex-direction: row-reverse").resolvedStyle();
    assertEquals(2F, style.flexGrow());
    assertEquals(3F, style.flexShrink());
    assertEquals(Length.pixel(0), style.flexBasis());
    assertEquals(FlexDirection.ROW_REVERSE, style.flexDirection());
    assertEquals(FlexWrap.WRAP, style.flexWrap());
  }

  @Test
  void explicitInheritanceCopiesExpandedComponents() {
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var manager = new StyleManagerImpl(propertyStore, StyleSheetParserFactory.createParser(propertyStore));
    Frame frame = new Frame();
    Element parent = NodeBuilder.div();
    Element child = NodeBuilder.div();
    parent.setAttribute("style", "flex: 2 3 20px; flex-flow: column wrap");
    child.setAttribute("style", "flex: inherit; flex-flow: inherit");
    parent.addChild(child);
    frame.addChild(parent);
    manager.recalculate(frame);
    assertEquals(2F, child.resolvedStyle().flexGrow());
    assertEquals(3F, child.resolvedStyle().flexShrink());
    assertEquals(Length.pixel(20), child.resolvedStyle().flexBasis());
    assertEquals(FlexDirection.COLUMN, child.resolvedStyle().flexDirection());
    assertEquals(FlexWrap.WRAP, child.resolvedStyle().flexWrap());
  }

  @Test
  void recalculate_storesFlexGrowAndShrinkAsFloats() {
    Element element = styledElement("flex-grow: 1.5; flex-shrink: 0.25");

    assertEquals(1.5F, element.resolvedStyle().flexGrow());
    assertEquals(0.25F, element.resolvedStyle().flexShrink());
  }

  @Test
  void integerLonghandsOverrideShorthandAndRejectNegativeFactors() {
    var style = styledElement("flex: 1; flex-grow: 2; flex-shrink: 3;"
        + "flex-grow: -1; flex-shrink: -2").resolvedStyle();
    assertEquals(2F, style.flexGrow());
    assertEquals(3F, style.flexShrink());
  }

  @Test
  void recalculate_storesDefaultFlexGrowAndShrinkAsFloats() {
    Element element = styledElement("");

    assertEquals(0F, element.resolvedStyle().flexGrow());
    assertEquals(0F, element.resolvedStyle().flexShrink());
  }

  private Element styledElement(String declarations) {
    PropertyStoreProvider provider = new DefaultPropertyStoreProvider();
    var propertyStore = provider.createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(propertyStore);
    var styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element element = NodeBuilder.div();
    element.setAttribute("style", declarations);
    frame.addChild(element);

    styleManager.recalculate(frame);

    return element;
  }
}
