package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.style.types.Display.FLEX;
import static com.spinyowl.spinygui.core.style.types.Display.GRID;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.types.length.Unit;

/**
 * Bounded pre-layout measurements used as candidates for intrinsic Grid tracks.
 *
 * <p>The candidate is always the existing pre-layout border box. A final content reflow is
 * required only for layouts whose descendants can change after Grid assigns their width.
 */
final class GridIntrinsicContribution {

  private GridIntrinsicContribution() {}

  static Measurement measure(Element element) {
    return new Measurement(
        element.box().borderBox().width(),
        element.box().borderBox().height(),
        requiresFinalReflow(element));
  }

  private static boolean requiresFinalReflow(Element element) {
    if (element instanceof InputElement || element instanceof TextareaElement) {
      return false;
    }
    if (FLEX.equals(element.resolvedStyle().display()) || GRID.equals(element.resolvedStyle().display())) {
      return true;
    }
    return (isAuto(element.resolvedStyle().width()) || isAuto(element.resolvedStyle().height()))
        && hasFlowDescendants(element);
  }

  private static boolean isAuto(Unit value) {
    return value == null || value.isAuto();
  }

  private static boolean hasFlowDescendants(Element element) {
    for (Node child : element.childNodes()) {
      if (child instanceof Text || child instanceof Element) {
        return true;
      }
    }
    return false;
  }

  record Measurement(float width, float height, boolean requiresFinalReflow) {}
}
