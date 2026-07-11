package com.spinyowl.spinygui.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.types.AffineTransform;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Overflow;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

class NodeUtilitiesTransformHitTest {

  @Test
  void translatedTarget_hitsOnlyAtItsVisualLocation() {
    Frame frame = frame();
    Element target = element("target", 10, 10, 20, 20);
    target.presentationState().transform(AffineTransform.translation(40, 0));
    attach(frame, target);

    assertEquals(target, NodeUtilities.getTargetElement(frame, new Vector2f(55, 15)));
    assertEquals(frame, NodeUtilities.getTargetElement(frame, new Vector2f(15, 15)));
  }

  @Test
  void scaledTarget_hitsOnlyInsideItsVisualBounds() {
    Frame frame = frame();
    Element target = element("target", 10, 10, 20, 20);
    target.presentationState().transform(AffineTransform.scale(2, 2));
    attach(frame, target);

    assertEquals(target, NodeUtilities.getTargetElement(frame, new Vector2f(35, 35)));
    assertEquals(frame, NodeUtilities.getTargetElement(frame, new Vector2f(55, 55)));
  }

  @Test
  void rotatedTarget_hitsAtVisualLocationNotAtItsStaleLayoutLocation() {
    Frame frame = frame();
    Element target = element("target", 20, 20, 20, 10);
    target.presentationState().transform(AffineTransform.rotationDegrees(90));
    attach(frame, target);

    assertEquals(target, NodeUtilities.getTargetElement(frame, new Vector2f(15, 25)));
    assertEquals(frame, NodeUtilities.getTargetElement(frame, new Vector2f(35, 25)));
  }

  @Test
  void singularTransform_makesTheSubtreeNonTargetable() {
    Frame frame = frame();
    Element target = element("target", 10, 10, 20, 20);
    target.presentationState().transform(AffineTransform.scale(0, 1));
    attach(frame, target);

    assertEquals(frame, NodeUtilities.getTargetElement(frame, new Vector2f(10, 15)));
  }

  @Test
  void transformedTargetOutsideOverflowClip_isNotTargetable() {
    Frame frame = frame();
    Element clip = element("clip", 0, 0, 20, 20);
    clip.resolvedStyle().overflowX(Overflow.HIDDEN);
    clip.resolvedStyle().overflowY(Overflow.HIDDEN);
    Element target = element("target", 10, 0, 20, 20);
    target.presentationState().transform(AffineTransform.translation(15, 0));
    attach(frame, clip);
    clip.addChild(target);
    target.offsetParent(clip);

    assertEquals(frame, NodeUtilities.getTargetElement(frame, new Vector2f(30, 10)));
  }

  @Test
  void nestedTransformAndScroll_targetTextInputButtonAndTextareaAtVisualPositions() {
    Frame frame = frame();
    Element scrollContainer = element("scroll", 10, 10, 80, 80);
    scrollContainer.resolvedStyle().overflowX(Overflow.HIDDEN);
    scrollContainer.resolvedStyle().overflowY(Overflow.HIDDEN);
    scrollContainer.scrollLeft(20);
    scrollContainer.scrollTop(10);
    scrollContainer.presentationState().transform(AffineTransform.translation(5, 0));
    attach(frame, scrollContainer);

    InputElement text = input(NodeBuilder.TYPE_TEXT, 30, 20, 20, 10);
    text.presentationState().transform(AffineTransform.translation(10, 0));
    InputElement button = input(NodeBuilder.TYPE_BUTTON, 30, 45, 20, 10);
    button.presentationState().transform(AffineTransform.scale(2, 2));
    TextareaElement textarea = textarea(30, 70, 20, 10);
    textarea.presentationState().transform(AffineTransform.rotationDegrees(90));
    attach(scrollContainer, text);
    attach(scrollContainer, button);
    attach(scrollContainer, textarea);

    assertEquals(text, NodeUtilities.getTargetElement(frame, new Vector2f(40, 25)));
    assertEquals(button, NodeUtilities.getTargetElement(frame, new Vector2f(35, 55)));
    assertEquals(textarea, NodeUtilities.getTargetElement(frame, new Vector2f(25, 80)));
    assertEquals(scrollContainer, NodeUtilities.getTargetElement(frame, new Vector2f(75, 25)));
    assertEquals(frame, NodeUtilities.getTargetElement(frame, new Vector2f(25, 95)));
    assertEquals(30, text.box().contentPosition().x());
    assertEquals(20, text.box().contentPosition().y());
    assertEquals(20, text.box().contentSize().x());
    assertEquals(10, text.box().contentSize().y());
    assertEquals(30, button.box().contentPosition().x());
    assertEquals(45, button.box().contentPosition().y());
    assertEquals(30, textarea.box().contentPosition().x());
    assertEquals(70, textarea.box().contentPosition().y());
  }

  private Frame frame() {
    Frame frame = new Frame();
    frame.resolvedStyle().display(Display.BLOCK);
    frame.box().contentPosition(0, 0);
    frame.box().contentSize(200, 200);
    return frame;
  }

  private Element element(String name, float x, float y, float width, float height) {
    Element element = new Element(name);
    element.resolvedStyle().display(Display.BLOCK);
    element.box().contentPosition(x, y);
    element.box().contentSize(width, height);
    return element;
  }

  private InputElement input(String type, float x, float y, float width, float height) {
    InputElement input = new InputElement();
    input.type(type);
    input.resolvedStyle().display(Display.BLOCK);
    input.box().contentPosition(x, y);
    input.box().contentSize(width, height);
    return input;
  }

  private TextareaElement textarea(float x, float y, float width, float height) {
    TextareaElement textarea = new TextareaElement();
    textarea.resolvedStyle().display(Display.BLOCK);
    textarea.box().contentPosition(x, y);
    textarea.box().contentSize(width, height);
    return textarea;
  }

  private void attach(Frame frame, Element element) {
    frame.addChild(element);
    element.offsetParent(frame);
  }

  private void attach(Element parent, Element element) {
    parent.addChild(element);
    element.offsetParent(parent);
  }
}
