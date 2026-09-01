package com.spinyowl.spinygui.core.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Owns the modal presentation state for one frame.
 *
 * <p>Modal roots keep their normal DOM parent for layout and ownership. Renderers promote the
 * backdrop and modal roots to top-layer paint order, while shared input boundaries admit only the
 * topmost modal subtree.
 */
public final class TopLayer {

  /** The frame whose source revision and interaction surface this top layer controls. */
  private final Frame frame;

  /** The frame-owned backdrop attached only while at least one modal is open. */
  private final Element backdrop;

  /** Modal entries retained in opening order for LIFO close and focus restoration. */
  private final List<ModalEntry> modalEntries = new ArrayList<>();

  /** Creates the single modal state owner used by {@link Frame} for its lifetime. */
  TopLayer(Frame frame) {
    this.frame = Objects.requireNonNull(frame, "frame");
    backdrop = new Element("modal-backdrop");
    backdrop.setAttribute("aria-hidden", "true");
    backdrop.style(
        "position: absolute; left: 0; top: 0; width: 100%; height: 100%; "
            + "background-color: #000000; opacity: 0.45");
  }

  /**
   * Promotes an attached frame element to the top of the modal stack.
   *
   * <p>Showing an element already present in this top layer is a no-op.
   *
   * @param modalRoot modal subtree root already attached to this frame
   * @throws NullPointerException when {@code modalRoot} is null
   * @throws IllegalArgumentException when the root is not attached to this frame
   */
  public void showModal(Element modalRoot) {
    Objects.requireNonNull(modalRoot, "modalRoot");
    reconcileModalEntries();
    if (containsModalRootRaw(modalRoot)) {
      return;
    }
    if (modalRoot == frame || modalRoot == backdrop || modalRoot.frame() != frame) {
      throw new IllegalArgumentException("modalRoot must be attached to the owning frame");
    }

    Element previousFocus = frame.getFocusedElement();
    if (previousFocus != null) {
      previousFocus.focused(false);
      previousFocus.pressed(false);
    }
    if (modalEntries.isEmpty()) {
      frame.addChild(backdrop);
    }
    modalEntries.add(new ModalEntry(modalRoot, previousFocus));
    frame.invalidatePaint();
  }

  /**
   * Closes and returns the topmost modal, restoring focus to the preceding interaction surface.
   *
   * @return the closed modal root, or {@code null} when no modal is open
   */
  public Element closeTopModal() {
    reconcileModalEntries();
    if (modalEntries.isEmpty()) {
      return null;
    }

    ModalEntry closed = modalEntries.remove(modalEntries.size() - 1);
    if (modalEntries.isEmpty()) {
      frame.removeChild(backdrop);
    }
    restoreFocus(closed.previousFocus());
    frame.invalidatePaint();
    return closed.root();
  }

  /**
   * Returns whether this frame currently has modal presentation state.
   *
   * @return {@code true} when at least one modal is open
   */
  public boolean hasModal() {
    reconcileModalEntries();
    return !modalEntries.isEmpty();
  }

  /**
   * Returns the topmost modal interaction root.
   *
   * @return topmost modal root, or {@code null} when no modal is open
   */
  public Element topModal() {
    reconcileModalEntries();
    return topModalRaw();
  }

  private Element topModalRaw() {
    return modalEntries.isEmpty() ? null : modalEntries.get(modalEntries.size() - 1).root();
  }

  /**
   * Returns modal roots in bottom-to-top paint order.
   *
   * @return immutable modal-root snapshot
   */
  public List<Element> modalRoots() {
    reconcileModalEntries();
    return modalEntries.stream().map(ModalEntry::root).toList();
  }

  /**
   * Returns the frame-owned backdrop element used by renderers while a modal is open.
   *
   * @return backdrop element
   */
  public Element backdrop() {
    return backdrop;
  }

  /**
   * Reports whether an element belongs to the currently interactive modal subtree.
   *
   * @param element candidate interaction target
   * @return {@code true} when no modal is open or the element is within the topmost modal
   */
  public boolean allowsInteraction(Element element) {
    Objects.requireNonNull(element, "element");
    reconcileModalEntries();
    return allowsInteractionRaw(element);
  }

  private boolean allowsInteractionRaw(Element element) {
    Element modal = topModalRaw();
    if (modal == null) {
      return element.frame() == frame;
    }
    for (Element current = element; current != null; current = current.parent()) {
      if (current == modal) {
        return true;
      }
    }
    return false;
  }

  /**
   * Reports whether a node is promoted and must be skipped by normal render traversal.
   *
   * @param node candidate render node
   * @return {@code true} for the active backdrop or any open modal root
   */
  public boolean isPresentationRoot(Node node) {
    reconcileModalEntries();
    return (node == backdrop && !modalEntries.isEmpty())
        || (node instanceof Element element && containsModalRootRaw(element));
  }

  private boolean containsModalRootRaw(Element element) {
    for (ModalEntry entry : modalEntries) {
      if (entry.root() == element) {
        return true;
      }
    }
    return false;
  }

  private void restoreFocus(Element previousFocus) {
    if (previousFocus != null
        && previousFocus.frame() == frame
        && allowsInteractionRaw(previousFocus)) {
      previousFocus.focused(true);
    }
  }

  private void reconcileModalEntries() {
    boolean changed = false;
    for (int index = 0; index < modalEntries.size(); ) {
      ModalEntry entry = modalEntries.get(index);
      if (entry.root().frame() == frame) {
        index++;
        continue;
      }

      modalEntries.remove(index);
      changed = true;
      if (index < modalEntries.size()) {
        ModalEntry next = modalEntries.get(index);
        modalEntries.set(index, new ModalEntry(next.root(), entry.previousFocus()));
      } else {
        restoreFocus(entry.previousFocus());
      }
    }

    if (modalEntries.isEmpty()) {
      if (backdrop.parent() == frame) {
        frame.removeChild(backdrop);
      }
    } else if (backdrop.frame() != frame) {
      frame.addChild(backdrop);
      changed = true;
    }
    if (changed) {
      frame.invalidatePaint();
    }
  }

  /** One modal root and the focus target to restore when that root closes. */
  private record ModalEntry(Element root, Element previousFocus) {}
}
