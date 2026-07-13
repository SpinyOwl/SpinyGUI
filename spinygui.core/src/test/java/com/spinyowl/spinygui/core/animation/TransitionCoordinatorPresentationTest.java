package com.spinyowl.spinygui.core.animation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Transform;
import com.spinyowl.spinygui.core.time.TimeService;
import org.junit.jupiter.api.Test;

class TransitionCoordinatorPresentationTest {

  @Test
  void transitionCoordinator_presentsInitialMidpointRetargetedAndCompletedValues() {
    Fixture fixture = new Fixture();
    fixture.element.style(style("0", "#000000", "#000000", "0px"));
    fixture.recalculateAndTick();

    fixture.element.style(style("1", "#ffffff", "#ffffff", "100px"));
    fixture.manager.recalculate(fixture.frame);
    fixture.coordinator.tick();
    assertPresented(fixture.element, 0f, Color.BLACK, 0f);

    fixture.clock.time(0.5);
    fixture.coordinator.tick();
    assertPresented(fixture.element, 0.5f, new Color(0.5f, 0.5f, 0.5f), 50f);

    fixture.element.style(style("0", "#000000", "#000000", "0px"));
    fixture.manager.recalculate(fixture.frame);
    fixture.clock.time(0.75);
    fixture.coordinator.tick();
    assertPresented(fixture.element, 0.375f, new Color(0.375f, 0.375f, 0.375f), 37.5f);

    fixture.clock.time(1.75);
    fixture.coordinator.tick();
    assertPresented(fixture.element, 0f, Color.BLACK, 0f);
    assertEquals(0, fixture.coordinator.activeTrackCount());
  }

  @Test
  void transitionCoordinator_holdsDelayedValueAndCancelsWhenHidden() {
    Fixture fixture = new Fixture();
    fixture.element.style("opacity: 0; transition: opacity 1s linear 250ms");
    fixture.recalculateAndTick();
    fixture.element.style("opacity: 1; transition: opacity 1s linear 250ms");
    fixture.manager.recalculate(fixture.frame);

    fixture.clock.time(0.2);
    fixture.coordinator.tick();
    assertEquals(0f, fixture.element.presentedStyle().opacity());

    fixture.element.style("display: none; opacity: 1; transition: opacity 1s linear 250ms");
    fixture.manager.recalculate(fixture.frame);
    fixture.coordinator.tick();

    assertEquals(0, fixture.coordinator.activeTrackCount());
    assertEquals(1f, fixture.element.presentedStyle().opacity());
  }

  @Test
  void transitionCoordinator_interpolatesCompatibleParsedTransformOperationLists() {
    Fixture fixture = new Fixture();
    fixture.element.style(
        "transform: translateX(0px) scale(1) rotate(0deg); transition: transform 1s linear");
    fixture.recalculateAndTick();

    fixture.element.style(
        "transform: translateX(100px) scale(3) rotate(90deg); transition: transform 1s linear");
    fixture.manager.recalculate(fixture.frame);
    fixture.clock.time(0.5);
    fixture.coordinator.tick();

    Transform.Operations midpoint =
        assertInstanceOf(Transform.Operations.class, fixture.element.presentedStyle().transform());
    assertEquals(
        java.util.List.of(
            new Transform.Translate(
                com.spinyowl.spinygui.core.style.types.length.Length.pixel(50f),
                com.spinyowl.spinygui.core.style.types.length.Length.pixel(0f)),
            new Transform.Scale(2f, 2f),
            new Transform.Rotate(45f)),
        midpoint.values());

    fixture.clock.time(1);
    fixture.coordinator.tick();
    assertEquals(0, fixture.coordinator.activeTrackCount());
    assertEquals(
        new Transform.Operations(
            java.util.List.of(
                new Transform.Translate(
                    com.spinyowl.spinygui.core.style.types.length.Length.pixel(100f),
                    com.spinyowl.spinygui.core.style.types.length.Length.pixel(0f)),
                new Transform.Scale(3f, 3f),
                new Transform.Rotate(90f))),
        fixture.element.presentedStyle().transform());
  }

  @Test
  void transitionCoordinator_interpolatesFromNoneToParsedTransformOperations() {
    Fixture fixture = new Fixture();
    fixture.element.style("transform: none; transition: transform 1s linear");
    fixture.recalculateAndTick();

    fixture.element.style(
        "transform: translate(24px, -8px) rotate(4deg) scale(1.06, 1.06); "
            + "transition: transform 1s linear");
    fixture.manager.recalculate(fixture.frame);
    fixture.clock.time(0.5);
    fixture.coordinator.tick();

    Transform.Operations midpoint =
        assertInstanceOf(Transform.Operations.class, fixture.element.presentedStyle().transform());
    assertEquals(
        java.util.List.of(
            new Transform.Translate(
                com.spinyowl.spinygui.core.style.types.length.Length.pixel(12f),
                com.spinyowl.spinygui.core.style.types.length.Length.pixel(-4f)),
            new Transform.Rotate(2f),
            new Transform.Scale(1.03f, 1.03f)),
        midpoint.values());
  }

  @Test
  void transitionCoordinator_appliesChangesImmediatelyWithoutATransition() {
    Fixture fixture = new Fixture();
    fixture.element.style("opacity: 0");
    fixture.recalculateAndTick();

    fixture.element.style("opacity: 1");
    fixture.manager.recalculate(fixture.frame);
    fixture.coordinator.tick();

    assertEquals(0, fixture.coordinator.activeTrackCount());
    assertEquals(1f, fixture.element.presentedStyle().opacity());
  }

  @Test
  void transitionCoordinator_appliesIncompatibleTransformOperationListsImmediately() {
    Fixture fixture = new Fixture();
    fixture.element.style("transform: translateX(0px); transition: transform 1s linear");
    fixture.recalculateAndTick();

    fixture.element.style("transform: scale(2); transition: transform 1s linear");
    fixture.manager.recalculate(fixture.frame);
    fixture.coordinator.tick();

    assertEquals(0, fixture.coordinator.activeTrackCount());
    assertEquals(
        new Transform.Operations(java.util.List.of(new Transform.Scale(2f, 2f))),
        fixture.element.presentedStyle().transform());
  }

  @Test
  void transitionCoordinator_preservesPercentageTranslationsInParsedOperationLists() {
    Fixture fixture = new Fixture();
    fixture.element.style("transform: translate(0%, 0%); transition: transform 1s linear");
    fixture.recalculateAndTick();

    fixture.element.style("transform: translate(100%, 50%); transition: transform 1s linear");
    fixture.manager.recalculate(fixture.frame);
    fixture.clock.time(0.5);
    fixture.coordinator.tick();

    Transform.Operations midpoint =
        assertInstanceOf(Transform.Operations.class, fixture.element.presentedStyle().transform());
    Transform.Translate translation =
        assertInstanceOf(Transform.Translate.class, midpoint.values().getFirst());
    assertEquals("%", translation.x().type());
    assertEquals("%", translation.y().type());
    assertEquals(0.5f, translation.x().convert(), 0.0001f);
    assertEquals(0.25f, translation.y().convert(), 0.0001f);
  }

  private static void assertPresented(
      Element element, float opacity, Color color, float translateX) {
    assertEquals(opacity, element.presentedStyle().opacity(), 0.0001f);
    assertEquals(color, element.presentedStyle().color());
    assertEquals(color, element.presentedStyle().backgroundColor());
    Transform.Operations transform =
        assertInstanceOf(Transform.Operations.class, element.presentedStyle().transform());
    Transform.Translate translate =
        assertInstanceOf(Transform.Translate.class, transform.values().getFirst());
    assertEquals(translateX, translate.x().convert(), 0.0001f);
  }

  private static String style(String opacity, String color, String backgroundColor, String translateX) {
    return "opacity: %s; color: %s; background-color: %s; transform: translate(%s, 0px); "
            .formatted(opacity, color, backgroundColor, translateX)
        + "transition: opacity 1s linear, color 1s linear, background-color 1s linear, "
        + "transform 1s linear";
  }

  private static final class Fixture {
    private final FakeClock clock = new FakeClock();
    private final TransitionCoordinator coordinator = new TransitionCoordinator(clock);
    private final PropertyStore store = new DefaultPropertyStoreProvider().createPropertyStore();
    private final StyleManagerImpl manager =
        new StyleManagerImpl(store, StyleSheetParserFactory.createParser(store), coordinator);
    private final Frame frame = new Frame();
    private final Element element = new Element("div");

    private Fixture() {
      frame.addChild(element);
    }

    private void recalculateAndTick() {
      manager.recalculate(frame);
      coordinator.tick();
    }
  }

  private static final class FakeClock implements TimeService {
    private double currentTime;

    @Override
    public double currentTime() {
      return currentTime;
    }

    private void time(double currentTime) {
      this.currentTime = currentTime;
    }
  }
}
