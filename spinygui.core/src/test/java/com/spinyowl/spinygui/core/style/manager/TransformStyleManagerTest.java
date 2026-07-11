package com.spinyowl.spinygui.core.style.manager;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSFORM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSFORM_ORIGIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Transform;
import com.spinyowl.spinygui.core.style.types.TransformOrigin;
import org.junit.jupiter.api.Test;

class TransformStyleManagerTest {

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
}
