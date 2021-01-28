package com.spinyowl.spinygui.core.util;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public final class NodeUtilities {

  private static final Function<Node, Integer> comparator = node ->
      node instanceof Element ? ((Element) node).style().zIndex() : 0;

  private NodeUtilities() {
  }

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

      if (cPos.x() > node.parent().size().x() ||
          cPos.x() + cSize.x() < 0 ||
          cPos.y() > node.parent().size().y() ||
          cPos.y() + cSize.y() < 0
      ) {
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
   * Used to find target node for provided node and vector. Target means top node which intersected
   * by provided point(vector).
   *
   * @param node   node to search.
   * @param vector point to search.
   * @return top node from node intersected by vector.
   */
  public static Node getTargetNode(Node node, Vector2f vector) {
    return getTargetNode(vector, node, null);
  }

  /**
   * Used to search target node (under point) in node. Target means top node which intersected by
   * provided point(vector).
   *
   * @param vector        vector to point.
   * @param node          source node to search target.
   * @param initialTarget initial target.
   * @return the top visible node under point.
   */
  public static Node getTargetNode(Vector2f vector, Node node, Node initialTarget) {
    Node retarget = initialTarget;
    if (node.visible() && node.intersection().intersects(node, vector)) {
      retarget = node;
      List<Node> childNodes = node.childNodes();

      childNodes.sort(Comparator.comparing(comparator));
      for (Node child : childNodes) {
        retarget = getTargetNode(vector, child, retarget);
      }
    }
    return retarget;
  }


  /**
   * Used to search all nodes (under point) in node.
   *
   * @param vector vector to point.
   * @param node   node to search in.
   * @return all top visible nodes in node under point(vector).
   */
  public static List<Node> getTargetNodeList(Node node, Vector2f vector) {
    return fillTargetNodeList(vector, node, new ArrayList<>());
  }


  /**
   * Used to search all nodes (under point) in node. New located target node will be added to target
   * list.
   *
   * @param vector     vector to point.
   * @param node       source node to search target.
   * @param targetList current target list.
   */
  public static List<Node> fillTargetNodeList(Vector2f vector, Node node, List<Node> targetList) {
    if (node.visible() && node.intersection().intersects(node, vector)) {
      targetList.add(node);
      node.childNodes().forEach(child -> fillTargetNodeList(vector, child, targetList));
    }
    return targetList;
  }
}
