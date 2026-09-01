package com.spinyowl.spinygui.core.node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.util.NodeUtilities;
import com.spinyowl.spinygui.core.style.types.Overflow;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

class TopLayerTest {

  @Test
  void admitsPointerAndFocusOnlyToTopmostModal() {
    Frame frame = new Frame();
    Element background = sizedElement("background", 100, 100);
    Element firstModal = sizedElement("first-modal", 80, 80);
    Element secondModal = sizedElement("second-modal", 60, 60);
    Element firstFocus = new Element("first-focus");
    Element secondFocus = new Element("second-focus");
    frame.addChildren(background, firstModal, secondModal);
    firstModal.addChild(firstFocus);
    secondModal.addChild(secondFocus);
    background.focused(true);

    frame.topLayer().showModal(firstModal);
    firstFocus.focused(true);
    frame.topLayer().showModal(secondModal);
    secondFocus.focused(true);

    assertSame(secondModal, NodeUtilities.getTargetElement(frame, new Vector2f(10, 10)));
    assertSame(secondFocus, frame.getFocusedElement());
    assertFalse(background.focused());
    assertFalse(firstFocus.focused());
    assertTrue(frame.topLayer().allowsInteraction(secondFocus));
    assertFalse(frame.topLayer().allowsInteraction(background));
  }

  @Test
  void closesInLifoOrderAndRestoresPreviousFocusSurface() {
    Frame frame = new Frame();
    Element background = new Element("background");
    Element firstModal = new Element("first-modal");
    Element secondModal = new Element("second-modal");
    Element firstFocus = new Element("first-focus");
    Element secondFocus = new Element("second-focus");
    frame.addChildren(background, firstModal, secondModal);
    firstModal.addChild(firstFocus);
    secondModal.addChild(secondFocus);
    background.focused(true);

    frame.topLayer().showModal(firstModal);
    firstFocus.focused(true);
    frame.topLayer().showModal(secondModal);
    secondFocus.focused(true);

    assertSame(secondModal, frame.topLayer().closeTopModal());
    assertSame(firstFocus, frame.getFocusedElement());
    assertSame(firstModal, frame.topLayer().topModal());
    assertSame(firstModal, frame.topLayer().closeTopModal());
    assertSame(background, frame.getFocusedElement());
    assertFalse(frame.topLayer().hasModal());
    assertNull(frame.topLayer().closeTopModal());
  }

  @Test
  void effectiveMutationsInvalidateButEquivalentOperationsDoNot() {
    Frame frame = cleanFrame();
    Element modal = new Element("modal");
    frame.addChild(modal);
    clean(frame);

    long beforeOpen = frame.revision();
    frame.topLayer().showModal(modal);
    assertTrue(frame.revision() > beforeOpen);
    long afterOpen = frame.revision();
    frame.topLayer().showModal(modal);
    assertEquals(afterOpen, frame.revision());

    frame.topLayer().closeTopModal();
    long afterClose = frame.revision();
    assertTrue(afterClose > afterOpen);
    frame.topLayer().closeTopModal();
    assertEquals(afterClose, frame.revision());
  }

  @Test
  void rejectsModalRootsOutsideOwningFrame() {
    Frame frame = new Frame();
    Element detached = new Element("detached");

    assertThrows(IllegalArgumentException.class, () -> frame.topLayer().showModal(detached));
  }

  @Test
  void movingOpenModalToAnotherFrameReconcilesStackAndRestoresFocus() {
    Frame firstFrame = new Frame();
    Frame secondFrame = new Frame();
    Element background = new Element("background");
    Element modal = new Element("modal");
    firstFrame.addChildren(background, modal);
    background.focused(true);
    firstFrame.topLayer().showModal(modal);

    secondFrame.addChild(modal);

    assertFalse(firstFrame.topLayer().hasModal());
    assertSame(background, firstFrame.getFocusedElement());
    assertSame(secondFrame, modal.frame());
    assertNull(firstFrame.topLayer().backdrop().parent());
  }

  @Test
  void promotedModalHitTestingEscapesClippingAncestorsLikeRendering() {
    Frame frame = new Frame();
    Element clippedParent = sizedElement("clipped-parent", 20, 20);
    clippedParent.resolvedStyle().overflowX(Overflow.HIDDEN);
    clippedParent.resolvedStyle().overflowY(Overflow.HIDDEN);
    Element modal = sizedElement("modal", 20, 20);
    modal.box().contentPosition(50, 50);
    frame.addChild(clippedParent);
    clippedParent.addChild(modal);
    frame.topLayer().showModal(modal);

    assertSame(modal, NodeUtilities.getTargetElement(frame, new Vector2f(55, 55)));
  }

  private static Element sizedElement(String name, float width, float height) {
    Element element = new Element(name);
    element.box().contentSize(width, height);
    return element;
  }

  private static Frame cleanFrame() {
    Frame frame = new Frame();
    clean(frame);
    return frame;
  }

  private static void clean(Frame frame) {
    long revision = frame.revision();
    frame.completePreparation(revision, true, true, true);
    frame.markPainted(revision);
  }
}
