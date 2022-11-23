package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Represents render tree. Render tree is a tree of {@link LayoutNode} instances. Each node contains
 * reference to {@link Node} instance that forms this node.
 */
@Data
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LayoutElement extends LayoutNode {

  static LayoutElement of(@NonNull Element element, @NonNull LayoutElement parent) {
    LayoutElement layoutElement = new LayoutElement();
    layoutElement.node(element);
    layoutElement.parent(parent);
    element.offsetParent(parent.element());
    return layoutElement;
  }

  public Element element() {
    return node().asElement();
  }
}
