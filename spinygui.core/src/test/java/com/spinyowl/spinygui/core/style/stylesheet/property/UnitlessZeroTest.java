package com.spinyowl.spinygui.core.style.stylesheet.property;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFunction;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermInteger;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import com.spinyowl.spinygui.core.style.types.length.Length;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UnitlessZeroTest {

  @Test
  void doesNotTurnUnknownFunctionsIntoLengthLists() {
    var element = NodeBuilder.div();
    var margin = new MarginPropertyProvider().getProperties().getFirst();
    element.resolvedStyle().styles().put("margin-top", Length.pixel(7));
    margin.apply(element, new TermFunction("unknown", Operator.SPACE, new TermInteger(0)));
    assertEquals(Length.pixel(7), element.resolvedStyle().marginTop());
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "0.0", "-0", "-0.0", "+0"})
  void resolvesZeroLengthsInLonghandsAndShorthands(String zero) {
    var frame = NodeBuilder.frame();
    var store = new DefaultPropertyStoreProvider().createPropertyStore();
    var manager = new StyleManagerImpl(store, StyleSheetParserFactory.createParser(store));
    frame.style("margin:4px " + zero + " 10px; padding:" + zero
        + " 8px; width:" + zero + "; top:" + zero + "; border:" + zero + " solid red");
    manager.recalculate(frame);
    var style = frame.resolvedStyle();
    assertEquals(Length.pixel(4), style.marginTop());
    assertEquals(Length.ZERO, style.marginRight());
    assertEquals(Length.pixel(10), style.marginBottom());
    assertEquals(Length.ZERO, style.marginLeft());
    assertEquals(Length.ZERO, style.paddingTop());
    assertEquals(Length.pixel(8), style.paddingRight());
    assertEquals(Length.ZERO, style.width());
    assertEquals(Length.ZERO, style.top());
    assertEquals(Length.ZERO, style.styles().get("border-top-width"));
  }

  @Test
  void preservesNumericPropertiesAndRejectsNonzeroUnitlessLengths() {
    var frame = NodeBuilder.frame();
    var store = new DefaultPropertyStoreProvider().createPropertyStore();
    var manager = new StyleManagerImpl(store, StyleSheetParserFactory.createParser(store));
    frame.style("opacity:0; z-index:0; line-height:2; margin:4px 1 10px; padding-left:2");
    manager.recalculate(frame);
    assertEquals(0f, frame.resolvedStyle().styles().get("opacity"));
    assertEquals(0, frame.resolvedStyle().styles().get("z-index"));
    assertEquals(2f, frame.resolvedStyle().styles().get("line-height"));
    assertEquals(Length.ZERO, frame.resolvedStyle().marginTop());
    assertEquals(Length.ZERO, frame.resolvedStyle().marginBottom());
    assertEquals(Length.ZERO, frame.resolvedStyle().paddingLeft());
  }
}
