package com.spinyowl.spinygui.core.util;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.style.types.PointerEvents;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public final class NodeUtilities {

  private static final Function<Node, Integer> comparator =
      node -> node instanceof Element ? ((Element) node).style().zIndex() : 0;

  private NodeUtilities() {}

  /**
   * Used to determine if node is visible in parent nodes.
   *
   * @param node node to check.
   * @return true if node is visible in all chain of parent nodes.
   */
  public static boolean visibleInParents(Node node) {
    // TODO: Add overflow check (if overflow support will be added).
    List<Node> parentList = new ArrayList<>();
    for (Node parent = node.parent(); parent != null; parent = parent.parent()) {
      parentList.add(parent);
    }

    if (!parentList.isEmpty()) {
      Vector2f pos = new Vector2f(0, 0);
      Vector2f rect = new Vector2f(0, 0);
      Vector2f absolutePosition = node.absolutePosition();

      Vector2fc cSize = node.size();
      Vector2fc cPos = node.position();

      float lx = absolutePosition.x;
      float rx = absolutePosition.x + cSize.x();
      float ty = absolutePosition.y;
      float by = absolutePosition.y + cSize.y();

      // check top parent

      if (cPos.x() > node.parent().size().x()
          || cPos.x() + cSize.x() < 0
          || cPos.y() > node.parent().size().y()
          || cPos.y() + cSize.y() < 0) {
        return false;
      }
      if (parentList.size() != 1) {
        // check from bottom parent to top parent
        for (int i = parentList.size() - 1; i >= 1; i--) {
          Node parent = parentList.get(i);
          pos.add(parent.position());
          rect.set(pos).add(parent.size());

          if (lx > rect.x || rx < pos.x || ty > rect.y || by < pos.y) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Used to find target element for provided element and vector. Target means top element which
   * intersected by provided point(vector).
   *
   * @param element element to search.
   * @param vector point to search.
   * @return top element from element intersected by vector or null if point is null.
   */
  public static Element getTargetElement(final Element element, final Vector2fc vector) {
    return getTargetElement(element, vector, false);
  }

  /**
   * Used to find target element for provided element and vector. Target means top element which
   * intersected by provided point(vector).
   *
   * @param element element to search.
   * @param vector point to search.
   * @param clickable if true - searches only for clickable target.
   * @return top element from element intersected by vector or null if point is null.
   */
  public static Element getTargetElement(
      final Element element, final Vector2fc vector, final boolean clickable) {
    return getTargetElement(vector, element, null, clickable);
  }

  /**
   * Used to search target element (under point) in element. Target means top element which
   * intersected by provided point(vector).
   *
   * @param vector vector to point.
   * @param element source element to search target.
   * @param initialTarget initial target.
   * @return the top visible element under point or null if point is null.
   */
  public static Element getTargetElement(
      final Vector2fc vector, final Element element, final Element initialTarget) {
    return getTargetElement(vector, element, initialTarget, false);
  }

  /**
   * Used to search target element (under point) in element. Target means top element which
   * intersected by provided point(vector).
   *
   * @param vector vector to point.
   * @param element source element to search target.
   * @param initialTarget initial target.
   * @param clickable if true - searches only for clickable target.
   * @return the top visible element under point or null if point is null.
   */
  public static Element getTargetElement(
      final Vector2fc vector,
      final Element element,
      final Element initialTarget,
      final boolean clickable) {
    Element retarget = initialTarget;
    if (element.visible() && element.intersection().intersects(element, vector)) {
      if (!clickable || clickable(element)) {
        retarget = element;
      }
      List<Element> childElements = element.children();

      childElements.sort(Comparator.comparing(comparator));
      for (Element child : childElements) {
        retarget = getTargetElement(vector, child, retarget);
      }
    }
    return retarget;
  }

  private static boolean clickable(final Element element) {
    return !PointerEvents.NONE.equals(element.style().pointerEvents());
  }

  /**
   * Used to search all elements (under point) in element.
   *
   * @param vector vector to point.
   * @param element element to search in.
   * @return all top visible elements in element under point(vector).
   */
  public static List<Element> getTargetElementList(final Element element, final Vector2fc vector) {
    return fillTargetElementList(vector, element, new ArrayList<>());
  }

  /**
   * Used to search all elements (under point) in element. New located target element will be added
   * to target list.
   *
   * @param vector vector to point.
   * @param element source element to search target.
   * @param targetList current target list.
   */
  public static List<Element> fillTargetElementList(
      final Vector2fc vector, final Element element, final List<Element> targetList) {
    if (element.visible() && element.intersection().intersects(element, vector)) {
      targetList.add(element);
      element.children().forEach(child -> fillTargetElementList(vector, child, targetList));
    }
    return targetList;
  }
}
