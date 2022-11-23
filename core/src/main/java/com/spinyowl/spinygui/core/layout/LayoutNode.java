package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.pseudo.Scrollbar;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Data
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LayoutNode {
  /** Node that forms this layout node. */
  private Node node;
  /** Parent layout node. */
  private LayoutElement parent;
  /** List of child layout nodes. */
  private List<LayoutNode> children;

  private Scrollbar verticalScrollbar;
  private Scrollbar horizontalScrollbar;

  static LayoutNode of(@NonNull Node node, @NonNull LayoutElement parent) {
    LayoutNode layoutNode = new LayoutNode();
    layoutNode.node(node);
    layoutNode.parent(parent);
    node.offsetParent(parent.element());
    return layoutNode;
  }

  public boolean hasParent() {
    return parent != null;
  }
}
