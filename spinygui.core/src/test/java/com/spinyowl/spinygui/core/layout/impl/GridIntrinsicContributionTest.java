package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import org.junit.jupiter.api.Test;

class GridIntrinsicContributionTest {

  @Test
  void usesThePreLayoutBorderBoxForBlocksAndControls() {
    Element block = measuredElement(40, 10);
    InputElement button = NodeBuilder.input(NodeBuilder.TYPE_BUTTON, "save", "Save");
    button.box().contentSize(36, 12);
    TextareaElement textarea = NodeBuilder.textarea("notes");
    textarea.box().contentSize(60, 24);

    assertMeasurement(GridIntrinsicContribution.measure(block), 40, 10, false);
    assertMeasurement(GridIntrinsicContribution.measure(button), 36, 12, false);
    assertMeasurement(GridIntrinsicContribution.measure(textarea), 60, 24, false);
  }

  @Test
  void marksWrappedTextAndNestedLayoutsForOneFinalReflow() {
    Element textBlock = measuredElement(80, 20);
    textBlock.addChild(NodeBuilder.text("wrapped text contribution"));
    Element flex = measuredElement(60, 20);
    flex.resolvedStyle().display(Display.FLEX);
    Element nestedGrid = measuredElement(50, 30);
    nestedGrid.resolvedStyle().display(Display.GRID);
    Element blockWithFlowChild = measuredElement(40, 16);
    blockWithFlowChild.addChild(measuredElement(20, 8));

    assertTrue(GridIntrinsicContribution.measure(textBlock).requiresFinalReflow());
    assertTrue(GridIntrinsicContribution.measure(flex).requiresFinalReflow());
    assertTrue(GridIntrinsicContribution.measure(nestedGrid).requiresFinalReflow());
    assertTrue(GridIntrinsicContribution.measure(blockWithFlowChild).requiresFinalReflow());
  }

  @Test
  void definiteBlockWithNoWidthDependentDescendantsDoesNotNeedFinalReflow() {
    Element block = measuredElement(48, 18);
    block.resolvedStyle().width(Length.pixel(48));

    assertFalse(GridIntrinsicContribution.measure(block).requiresFinalReflow());
  }

  private static Element measuredElement(float width, float height) {
    Element element = NodeBuilder.div();
    element.resolvedStyle().display(Display.BLOCK);
    element.resolvedStyle().width(Unit.AUTO);
    element.box().contentSize(width, height);
    return element;
  }

  private static void assertMeasurement(
      GridIntrinsicContribution.Measurement measurement,
      float width,
      float height,
      boolean requiresFinalReflow) {
    assertEquals(width, measurement.width(), .001f);
    assertEquals(height, measurement.height(), .001f);
    assertEquals(requiresFinalReflow, measurement.requiresFinalReflow());
  }
}
