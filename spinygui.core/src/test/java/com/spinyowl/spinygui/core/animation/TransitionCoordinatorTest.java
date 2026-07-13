package com.spinyowl.spinygui.core.animation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.time.TimeService;
import org.junit.jupiter.api.Test;

class TransitionCoordinatorTest {
  @Test void styleChangesRetargetFromThePresentedValueAndComplete() {
    var clock = new FakeClock();
    var coordinator = new TransitionCoordinator(clock);
    var store = new DefaultPropertyStoreProvider().createPropertyStore();
    var manager = new StyleManagerImpl(store, StyleSheetParserFactory.createParser(store), coordinator);
    var frame = new Frame(); var element = new Element("div");
    element.style("opacity: 0; transition: opacity 1s linear"); frame.addChild(element);
    manager.recalculate(frame); coordinator.tick();
    element.style("opacity: 1; transition: opacity 1s linear"); manager.recalculate(frame);
    clock.time = .5; coordinator.tick();
    assertEquals(.5f, element.presentationState().value("opacity", 0f), .0001f);
    element.style("opacity: 0; transition: opacity 1s linear"); manager.recalculate(frame);
    clock.time = .75; coordinator.tick();
    assertEquals(.375f, element.presentationState().value("opacity", 0f), .0001f);
    clock.time = 1.5; coordinator.tick();
    assertEquals(0f, element.presentationState().value("opacity", 1f), .0001f);
    assertEquals(0, coordinator.activeTrackCount());
  }
  private static final class FakeClock implements TimeService { double time; @Override public double currentTime() { return time; } }
}
