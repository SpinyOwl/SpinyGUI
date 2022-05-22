package com.spinyowl.spinygui.core.util;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.PointerEvents;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public final class NodeUtilities {

  private static final Function<Node, Integer> comparator =
      node -> {
        if (node instanceof Element element) {
          Integer index = element.calculatedStyle().zIndex();
          return index == null ? 0 : index;
        }
        return 0;
      };

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
      var pos = new Vector2f(0, 0);
      var rect = new Vector2f(0, 0);
      var absolutePosition = node.box().borderBoxPosition();

      Vector2fc currentSize = node.box().contentSize();
      Vector2fc currentPos = node.box().contentPosition();

      float lx = absolutePosition.x;
      float rx = absolutePosition.x + currentSize.x();
      float ty = absolutePosition.y;
      float by = absolutePosition.y + currentSize.y();

      // check top parent

      Vector2f parentPaddingBoxSize = node.parent().box().paddingBoxSize();
      if (currentPos.x() > parentPaddingBoxSize.x
          || currentPos.x() + currentSize.x() < 0
          || currentPos.y() > parentPaddingBoxSize.y
          || currentPos.y() + currentSize.y() < 0) {
        return false;
      }
      if (parentList.size() != 1) {
        // check from bottom parent to top parent
        for (int i = parentList.size() - 1; i >= 1; i--) {
          Node parent = parentList.get(i);
          pos.add(parent.box().contentPosition());
          rect.set(pos).add(parent.box().contentSize());

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
   * @param searchOnlyClickable if true - searches only for clickable target.
   * @return top element from element intersected by vector or null if point is null.
   */
  public static Element getTargetElement(
      final Element element, final Vector2fc vector, final boolean searchOnlyClickable) {
    return getTargetElement(element, vector, null, searchOnlyClickable);
  }

  /**
   * Used to search target element (under point) in element. Target means top element which
   * intersected by provided point(vector).
   *
   * @param element source element to search target.
   * @param vector vector to point.
   * @param initialTarget initial target.
   * @return the top visible element under point or null if point is null.
   */
  public static Element getTargetElement(
      final Element element, final Vector2fc vector, final Element initialTarget) {
    return getTargetElement(element, vector, initialTarget, false);
  }

  /**
   * Used to search target element (under point) in element. Target means top element which
   * intersected by provided point(vector).
   *
   * @param element source element to search target.
   * @param vector vector to point.
   * @param initialTarget initial target.
   * @param searchOnlyClickable if true - searches only for clickable target.
   * @return the top visible element under point or null if point is null.
   */
  public static Element getTargetElement(
      final Element element,
      final Vector2fc vector,
      final Element initialTarget,
      final boolean searchOnlyClickable) {
    Element retarget = initialTarget;
    if (visible(element) && element.intersection().intersects(element, vector)) {
      if (!searchOnlyClickable || clickable(element)) {
        retarget = element;
      }
      List<Element> childElements = element.children();

      childElements.sort(Comparator.comparing(comparator));
      for (Element child : childElements) {
        retarget = getTargetElement(child, vector, retarget);
      }
    }
    return retarget;
  }

  private static boolean clickable(final Element element) {
    return !PointerEvents.NONE.equals(element.calculatedStyle().pointerEvents());
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
    if (visible(element) && element.intersection().intersects(element, vector)) {
      targetList.add(element);
      element.children().forEach(child -> fillTargetElementList(vector, child, targetList));
    }
    return targetList;
  }

  public static boolean visible(Node node) {
    if (node instanceof Text) return true;
    if (node instanceof Element element) {
      return !Display.NONE.equals(element.calculatedStyle().display());
    }
    return false;
  }

}
