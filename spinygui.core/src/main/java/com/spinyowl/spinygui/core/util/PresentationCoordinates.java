package com.spinyowl.spinygui.core.util;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.style.types.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/** Maps viewport input through the same presentation transform and scroll stack used for paint. */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class PresentationCoordinates {

  /**
   * Maps a viewport point into the layout coordinate space of {@code node}.
   *
   * <p>An uninvertible transform makes the complete visual subtree non-targetable.
   */
  public static Optional<Vector2f> toLayout(Node node, Vector2fc viewportPoint) {
    return viewportTransform(node)
        .inverse()
        .map(transform -> {
          AffineTransform.Point point = transform.apply(viewportPoint.x(), viewportPoint.y());
          return new Vector2f(point.x(), point.y());
        });
  }

  public static boolean containsBorderBox(Node node, Vector2fc viewportPoint) {
    return toLayout(node, viewportPoint)
        .map(
            point -> {
              Vector2f position = node.layoutAbsolutePosition();
              Vector2f size = node.size();
              return point.x >= position.x
                  && point.x < position.x + size.x
                  && point.y >= position.y
                  && point.y < position.y + size.y;
            })
        .orElse(false);
  }

  public static boolean containsContentBox(Element element, Vector2fc viewportPoint) {
    return toLayout(element, viewportPoint)
        .map(
            point -> {
              Vector2f position = element.layoutAbsolutePosition();
              var box = element.box();
              float x = position.x + box.border().left() + box.padding().left();
              float y = position.y + box.border().top() + box.padding().top();
              Vector2f size = box.contentSize();
              return point.x >= x && point.x < x + size.x && point.y >= y && point.y < y + size.y;
            })
        .orElse(false);
  }

  private static AffineTransform viewportTransform(Node node) {
    List<Node> ancestors = new ArrayList<>();
    for (Node current = node; current != null; current = current.offsetParent()) {
      ancestors.add(current);
    }
    Collections.reverse(ancestors);

    AffineTransform transform = AffineTransform.IDENTITY;
    Element previous = null;
    for (Node current : ancestors) {
      if (previous != null) {
        transform =
            transform.multiply(AffineTransform.translation(-previous.scrollLeft(), -previous.scrollTop()));
      }
      if (current instanceof Element element) {
        transform = transform.multiply(transformAroundBorderBox(element));
        previous = element;
      }
    }
    return transform;
  }

  private static AffineTransform transformAroundBorderBox(Element element) {
    Vector2f position = element.layoutAbsolutePosition();
    return AffineTransform.translation(position.x, position.y)
        .multiply(element.presentationState().transform())
        .multiply(AffineTransform.translation(-position.x, -position.y));
  }
}
