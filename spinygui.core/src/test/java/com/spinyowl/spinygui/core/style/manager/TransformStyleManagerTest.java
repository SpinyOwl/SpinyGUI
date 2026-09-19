package com.spinyowl.spinygui.core.style.manager;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSFORM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSFORM_ORIGIN;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.CURSOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFunction;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermInteger;
import com.spinyowl.spinygui.core.style.types.Transform;
import com.spinyowl.spinygui.core.style.types.TransformOrigin;
import com.spinyowl.spinygui.core.style.types.CursorType;
import org.junit.jupiter.api.Test;

class TransformStyleManagerTest {

  @Test
  void invalidCursorKeepsEarlierValidDeclaration() {
    var store = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(store);
    var frame = new Frame();
    var element = new Element("div");
    element.style("cursor: move; cursor: wait");
    frame.addChild(element);

    new StyleManagerImpl(store, parser).recalculate(frame);

    assertEquals(CursorType.MOVE, element.resolvedStyle().get(CURSOR));
  }

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.CsvSource({
      "left top,0,0", "top left,0,0", "right bottom,220,100", "bottom right,220,100",
      "center top,110,0", "top center,110,0", "left,0,50", "top,110,0",
      "center,110,50", "bottom,110,100", "right,220,50", "20px,20,50",
      "left 20px,0,20", "20px bottom,20,100", "25% 10px,55,10"
  })
  void originKeywordsResolveAxes(String css, float x, float y) {
    var store = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(store);
    var frame = new Frame();
    var element = new Element("div");
    element.style("transform-origin:" + css);
    frame.addChild(element);
    new StyleManagerImpl(store, parser).recalculate(frame);
    var origin = element.resolvedStyle().transformOrigin();
    assertEquals(x, origin.x().convert(220), .001f);
    assertEquals(y, origin.y().convert(100), .001f);
  }

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.ValueSource(strings = {
      "left right", "top bottom", "top 20px", "20px left", "unknown", "left top 5px"
  })
  void invalidOriginKeepsEarlierValidDeclaration(String invalid) {
    var store = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(store);
    var frame = new Frame();
    var element = new Element("div");
    element.style("transform-origin:10px 20px; transform-origin:" + invalid);
    frame.addChild(element);
    new StyleManagerImpl(store, parser).recalculate(frame);
    assertEquals(10, element.resolvedStyle().transformOrigin().x().convert(220));
    assertEquals(20, element.resolvedStyle().transformOrigin().y().convert(100));
  }

  @Test
  void propertyStoreDiscoversTransformPropertiesAndResolvesDefaultsFromParsedCss() {
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(propertyStore);
    var manager = new StyleManagerImpl(propertyStore, parser);
    var frame = new Frame();
    var element = new Element("div");
    frame.addChild(element);

    manager.recalculate(frame);

    assertNotNull(propertyStore.getProperty(TRANSFORM));
    assertNotNull(propertyStore.getProperty(TRANSFORM_ORIGIN));
    assertEquals(Transform.NONE, element.resolvedStyle().transform());
    assertEquals(TransformOrigin.CENTER, element.resolvedStyle().transformOrigin());
  }

  @Test
  void parsedStylesheetAndInlineDeclarationsUseTheSameTransformProviders() {
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(propertyStore);
    var manager = new StyleManagerImpl(propertyStore, parser);
    var frame = new Frame();
    var stylesheetElement = new Element("div");
    stylesheetElement.setAttribute("class", "stylesheet-transform");
    var inlineElement = new Element("div");
    inlineElement.style("transform: none; transform-origin: 50% 50%");
    frame.addChildren(stylesheetElement, inlineElement);
    frame.styleSheets().add(parser.parse(".stylesheet-transform { transform: none; transform-origin: 50% 50%; }"));

    manager.recalculate(frame);

    assertEquals(Transform.NONE, stylesheetElement.resolvedStyle().transform());
    assertEquals(TransformOrigin.CENTER, stylesheetElement.resolvedStyle().transformOrigin());
    assertEquals(Transform.NONE, inlineElement.resolvedStyle().transform());
    assertEquals(TransformOrigin.CENTER, inlineElement.resolvedStyle().transformOrigin());
  }

  @Test
  void parsedCssPreservesSupportedTransformOrderAndOriginValues() {
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(propertyStore);
    var manager = new StyleManagerImpl(propertyStore, parser);
    var frame = new Frame();
    var element = new Element("div");
    element.style("transform: translate(10px, 25%) scale(2, 3) rotate(45deg); transform-origin: 10px 25%");
    frame.addChild(element);

    manager.recalculate(frame);

    assertEquals(
        new Transform.Operations(
            java.util.List.of(
                new Transform.Translate(com.spinyowl.spinygui.core.style.types.length.Length.pixel(10f), com.spinyowl.spinygui.core.style.types.length.Length.percent(0.25f)),
                new Transform.Scale(2f, 3f), new Transform.Rotate(45f))),
        element.resolvedStyle().transform());
    assertEquals(
        new TransformOrigin(com.spinyowl.spinygui.core.style.types.length.Length.pixel(10f), com.spinyowl.spinygui.core.style.types.length.Length.percent(0.25f)),
        element.resolvedStyle().transformOrigin());
  }

  @Test
  void invalidTransformDeclarationDoesNotApplyAValidPrefix() {
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(propertyStore);
    var manager = new StyleManagerImpl(propertyStore, parser);
    var frame = new Frame();
    var element = new Element("div");
    element.style("transform: translateX(10px) skew(20deg)");
    frame.addChild(element);

    manager.recalculate(frame);

    assertEquals(Transform.NONE, element.resolvedStyle().transform());
  }

  @Test
  void parsedCssSupportsAxisSpecificTransformFunctions() {
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(propertyStore);
    var manager = new StyleManagerImpl(propertyStore, parser);
    var frame = new Frame();
    var element = new Element("div");
    element.style("transform: translateX(10px) translateY(20%) scaleX(2) scaleY(3)");
    frame.addChild(element);

    manager.recalculate(frame);

    assertEquals(4, ((Transform.Operations) element.resolvedStyle().transform()).values().size());
  }

  @Test
  void parsedCssSupportsSingleArgumentScaleFunction() {
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(propertyStore);
    var manager = new StyleManagerImpl(propertyStore, parser);
    var frame = new Frame();
    var element = new Element("div");
    element.style("transform: scale(2)");
    frame.addChild(element);

    var term = (TermFunction) parser.parseDeclarations("transform: scale(2)").getFirst().term();
    assertEquals("scale", term.name());
    assertEquals(java.util.List.of(new TermInteger(2)), term.terms());
    parser.parseDeclarations("transform: scale(2)").getFirst().apply(element);
    assertEquals(
        new Transform.Operations(java.util.List.of(new Transform.Scale(2f, 2f))),
        element.resolvedStyle().transform());
    element.resolvedStyle().styles().clear();

    manager.recalculate(frame);

    assertEquals(
        new Transform.Operations(java.util.List.of(new Transform.Scale(2f, 2f))),
        element.resolvedStyle().transform());
  }
}
