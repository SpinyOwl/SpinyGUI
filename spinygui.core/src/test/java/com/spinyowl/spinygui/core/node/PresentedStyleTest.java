package com.spinyowl.spinygui.core.node;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OPACITY;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSFORM;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Transform;
import com.spinyowl.spinygui.core.style.types.length.Length.PixelLength;
import org.junit.jupiter.api.Test;

class PresentedStyleTest {

  @Test
  void fallsBackToTheExactComputedValues() {
    Element element = elementWithComputedPaintValues();

    assertEquals(0.25f, element.presentedStyle().opacity());
    assertEquals(Color.RED, element.presentedStyle().color());
    assertEquals(Color.BLUE, element.presentedStyle().backgroundColor());
    assertEquals(Color.GREEN, element.presentedStyle().borderLeftColor());
    assertEquals(Color.YELLOW, element.presentedStyle().borderRightColor());
    assertEquals(Color.WHITE, element.presentedStyle().borderTopColor());
    assertEquals(Color.BLACK, element.presentedStyle().borderBottomColor());
    assertEquals(
        new Transform.Translate(new PixelLength(4f), new PixelLength(8f)),
        element.presentedStyle().transform());
  }

  @Test
  void usesPresentationOverridesWithoutChangingComputedValues() {
    Element element = elementWithComputedPaintValues();
    Transform presentedTransform = new Transform.Scale(2f, 3f);

    element.presentationState().setValue(OPACITY, 0.75f);
    element.presentationState().setValue(COLOR, Color.BLUE);
    element.presentationState().setValue(BACKGROUND_COLOR, Color.RED);
    element.presentationState().setValue(BORDER_LEFT_COLOR, Color.BLACK);
    element.presentationState().setValue(BORDER_RIGHT_COLOR, Color.WHITE);
    element.presentationState().setValue(BORDER_TOP_COLOR, Color.YELLOW);
    element.presentationState().setValue(BORDER_BOTTOM_COLOR, Color.GREEN);
    element.presentationState().setValue(TRANSFORM, presentedTransform);

    assertEquals(0.75f, element.presentedStyle().opacity());
    assertEquals(Color.BLUE, element.presentedStyle().color());
    assertEquals(Color.RED, element.presentedStyle().backgroundColor());
    assertEquals(Color.BLACK, element.presentedStyle().borderLeftColor());
    assertEquals(Color.WHITE, element.presentedStyle().borderRightColor());
    assertEquals(Color.YELLOW, element.presentedStyle().borderTopColor());
    assertEquals(Color.GREEN, element.presentedStyle().borderBottomColor());
    assertEquals(presentedTransform, element.presentedStyle().transform());

    assertEquals(0.25f, element.resolvedStyle().opacity());
    assertEquals(Color.RED, element.resolvedStyle().color());
    assertEquals(
        new Transform.Translate(new PixelLength(4f), new PixelLength(8f)),
        element.resolvedStyle().transform());
  }

  private Element elementWithComputedPaintValues() {
    Element element = new Element("div");
    element.resolvedStyle().opacity(0.25f);
    element.resolvedStyle().color(Color.RED);
    element.resolvedStyle().backgroundColor(Color.BLUE);
    element.resolvedStyle().borderLeftColor(Color.GREEN);
    element.resolvedStyle().borderRightColor(Color.YELLOW);
    element.resolvedStyle().borderTopColor(Color.WHITE);
    element.resolvedStyle().borderBottomColor(Color.BLACK);
    element.resolvedStyle().transform(
        new Transform.Translate(new PixelLength(4f), new PixelLength(8f)));
    return element;
  }
}
