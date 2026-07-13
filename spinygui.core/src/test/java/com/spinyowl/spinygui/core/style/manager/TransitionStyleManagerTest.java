package com.spinyowl.spinygui.core.style.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.transition.TransitionPropertyName;
import com.spinyowl.spinygui.core.style.types.transition.TransitionTimingFunction;
import org.junit.jupiter.api.Test;

class TransitionStyleManagerTest {
  @Test void longhandsAndShorthandResolveToTheSameTypedConfiguration() {
    var store = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(store);
    var frame = new Frame();
    var longhand = new Element("div");
    longhand.style("transition-property: opacity, color; transition-duration: 200ms, 1s; transition-delay: 50ms; transition-timing-function: linear, ease-in");
    var shorthand = new Element("div");
    shorthand.style("transition: opacity 200ms linear 50ms, color 1s ease-in 50ms");
    frame.addChildren(longhand, shorthand);
    new StyleManagerImpl(store, parser).recalculate(frame);
    assertEquals(longhand.resolvedStyle().transitionConfiguration(), shorthand.resolvedStyle().transitionConfiguration());
    assertEquals(TransitionPropertyName.OPACITY, ((com.spinyowl.spinygui.core.style.types.transition.TransitionPropertySelection.Named) longhand.resolvedStyle().transitionConfiguration().properties().getFirst()).property());
    assertEquals(TransitionTimingFunction.Named.EASE_IN, longhand.resolvedStyle().transitionConfiguration().timingFunctions().get(1));
  }
}
