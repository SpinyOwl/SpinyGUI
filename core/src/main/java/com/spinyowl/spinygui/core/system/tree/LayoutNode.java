package com.spinyowl.spinygui.core.system.tree;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.pseudo.Scrollbar;
import com.spinyowl.spinygui.core.style.StyledNode;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(exclude = "parent")
public class LayoutNode {
  /** Node that forms this layout node. */
  @NonNull StyledNode styledNode;
  /** Parent layout node. */
  private LayoutNode parent;
  /** List of child layout nodes. */
  private List<LayoutNode> children = List.of();

  private List<LayoutNode> normalFlowChildren = List.of();
  private List<LayoutNode> positionedChildren = List.of();

  private Scrollbar verticalScrollbar;
  private LayoutNode verticalScrollbarLayoutNode;
  private Scrollbar horizontalScrollbar;
  private LayoutNode horizontalScrollbarLayoutNode;

  public LayoutNode(@NonNull StyledNode styledNode) {
    this.styledNode = styledNode;
  }

  public LayoutNode(@NonNull StyledNode styledNode, @NonNull LayoutNode parent) {
    this.styledNode = styledNode;
    this.parent = parent;
    this.node().offsetParent(parent.element());
  }

  public Element element() {
    return styledNode.node().asElement();
  }

  public Node node() {
    return styledNode.node();
  }

  public boolean isElement() {
    return styledNode.node() instanceof Element;
  }

  public boolean hasParent() {
    return parent != null;
  }

}
