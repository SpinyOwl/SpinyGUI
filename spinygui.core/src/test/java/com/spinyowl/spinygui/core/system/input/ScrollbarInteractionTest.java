package com.spinyowl.spinygui.core.system.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.style.types.AffineTransform;
import com.spinyowl.spinygui.core.style.types.Overflow;
import java.util.List;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

class ScrollbarInteractionTest {

  @Test
  void clickVerticalTrack_usesGlobalPointerCoordinatesForNestedScrollbar() {
    ScrollbarInteraction interaction = new ScrollbarInteraction();
    Element element = nestedScrollable(Overflow.HIDDEN, Overflow.AUTO, 100, 300);
    element.scrollTop(100);

    ScrollbarInteraction.Hit above = interaction.hit(List.of(element), new Vector2f(101, 14));
    assertNotNull(above);
    interaction.clickTrack(above, new Vector2f(101, 14));
    assertEquals(0, element.scrollTop(), 0.0001f);

    ScrollbarInteraction.Hit below = interaction.hit(List.of(element), new Vector2f(101, 90));
    assertNotNull(below);
    interaction.clickTrack(below, new Vector2f(101, 90));
    assertEquals(100, element.scrollTop(), 0.0001f);
  }

  @Test
  void dragVerticalThumb_usesGlobalPointerCoordinatesForNestedScrollbar() {
    ScrollbarInteraction interaction = new ScrollbarInteraction();
    Element element = nestedScrollable(Overflow.HIDDEN, Overflow.AUTO, 100, 300);

    interaction.beginDrag(interaction.hit(List.of(element), new Vector2f(101, 14)), new Vector2f(101, 14));
    interaction.dragTo(new Vector2f(101, 81));
    assertEquals(200, element.scrollTop(), 0.0001f);

    interaction.endDrag();
    interaction.beginDrag(interaction.hit(List.of(element), new Vector2f(101, 80)), new Vector2f(101, 80));
    interaction.dragTo(new Vector2f(101, 13));
    assertEquals(0, element.scrollTop(), 0.0001f);
  }

  @Test
  void clickAndDragHorizontalScrollbar_usesGlobalPointerCoordinatesForNestedScrollbar() {
    ScrollbarInteraction interaction = new ScrollbarInteraction();
    Element element = nestedScrollable(Overflow.AUTO, Overflow.HIDDEN, 300, 100);
    element.scrollLeft(100);

    ScrollbarInteraction.Hit before = interaction.hit(List.of(element), new Vector2f(14, 101));
    assertNotNull(before);
    interaction.clickTrack(before, new Vector2f(14, 101));
    assertEquals(0, element.scrollLeft(), 0.0001f);

    interaction.beginDrag(interaction.hit(List.of(element), new Vector2f(14, 101)), new Vector2f(14, 101));
    interaction.dragTo(new Vector2f(81, 101));
    assertEquals(200, element.scrollLeft(), 0.0001f);
  }

  @Test
  void hit_followsPresentationTransformForNestedScrollbar() {
    ScrollbarInteraction interaction = new ScrollbarInteraction();
    Element element = nestedScrollable(Overflow.HIDDEN, Overflow.AUTO, 100, 300);
    element.presentationState().transform(AffineTransform.translation(20, 30));

    ScrollbarInteraction.Hit hit = interaction.hit(List.of(element), new Vector2f(121, 44));

    assertNotNull(hit);
    assertEquals(ScrollbarInteraction.Axis.VERTICAL, hit.axis());
    assertEquals(ScrollbarInteraction.HitPart.THUMB, hit.part());
  }

  private Element nestedScrollable(
      Overflow overflowX, Overflow overflowY, float scrollWidth, float scrollHeight) {
    Element parent = NodeBuilder.div();
    parent.box().contentPosition(12, 12);
    Element element = NodeBuilder.div();
    element.box().contentPosition(1, 1);
    element.box().contentSize(100, 100);
    element.clientWidth(100);
    element.clientHeight(100);
    element.scrollWidth(scrollWidth);
    element.scrollHeight(scrollHeight);
    element.resolvedStyle().overflowX(overflowX);
    element.resolvedStyle().overflowY(overflowY);
    parent.addChild(element);
    element.offsetParent(parent);
    return element;
  }
}
