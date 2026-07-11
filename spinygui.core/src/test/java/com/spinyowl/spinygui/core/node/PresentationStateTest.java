package com.spinyowl.spinygui.core.node;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.AffineTransform;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import org.junit.jupiter.api.Test;

class PresentationStateTest {

  @Test
  void presentationTransformDoesNotOverwriteComputedStyle() {
    Element element = new Element("div");
    element.resolvedStyle().color(Color.RED);

    element.presentationState().transform(AffineTransform.translation(12f, 4f));
    element.presentationState().reset();

    assertEquals(Color.RED, element.resolvedStyle().color());
    assertEquals(AffineTransform.IDENTITY, element.presentationState().transform());
  }

  @Test
  void presentationTransformLeavesLayoutGeometryAndComputedStyleUntouched() {
    Element element = new Element("div");
    element.box().contentPosition(10f, 20f);
    element.box().contentSize(30f, 40f);
    element.resolvedStyle().color(Color.BLUE);

    element.presentationState().transform(AffineTransform.rotationDegrees(45f));

    assertEquals(10f, element.box().contentPosition().x);
    assertEquals(20f, element.box().contentPosition().y);
    assertEquals(30f, element.box().contentSize().x);
    assertEquals(40f, element.box().contentSize().y);
    assertEquals(Color.BLUE, element.resolvedStyle().color());
  }

  @Test
  void recalculationResetsPresentationStateWithoutClearingComputedStyle() {
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var frame = new Frame();
    var element = new Element("div");
    element.style("color: red");
    frame.addChild(element);
    new StyleManagerImpl(propertyStore, StyleSheetParserFactory.createParser(propertyStore)).recalculate(frame);
    element.presentationState().transform(AffineTransform.scale(2f, 2f));

    new StyleManagerImpl(propertyStore, StyleSheetParserFactory.createParser(propertyStore)).recalculate(frame);

    assertEquals(Color.RED, element.resolvedStyle().color());
    assertEquals(AffineTransform.IDENTITY, element.presentationState().transform());
  }

  @Test
  void hiddenAndDetachedElementsResetPresentationState() {
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var frame = new Frame();
    var element = new Element("div");
    element.style("display: none");
    frame.addChild(element);
    element.presentationState().transform(AffineTransform.translation(5f, 6f));

    new StyleManagerImpl(propertyStore, StyleSheetParserFactory.createParser(propertyStore)).recalculate(frame);

    assertEquals(Display.NONE, element.resolvedStyle().display());
    assertEquals(AffineTransform.IDENTITY, element.presentationState().transform());
    element.presentationState().transform(AffineTransform.translation(5f, 6f));
    frame.removeChild(element);
    assertEquals(AffineTransform.IDENTITY, element.presentationState().transform());
  }
}
