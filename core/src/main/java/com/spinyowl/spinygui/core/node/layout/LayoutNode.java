package com.spinyowl.spinygui.core.node.layout;

import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public abstract class LayoutNode {

  /**
   * Parent defined by layout service.
   *
   * <p>It could be different from nodes original parent based on position property (for example if
   * node was removed from normal flow).
   */
  @Getter @Setter private LayoutNode layoutParent;

  /**
   * List of child nodes defined by layout service that belong to normal flow.
   *
   * <p>It could be different from nodes original parent based on position property (for example if
   * node was removed from normal flow).
   */
  @Getter private final List<LayoutNode> normalFlowChildNodes = new LinkedList<>();

  /**
   * List of child nodes defined by layout service that belong to positioned flow.
   *
   * <p>It could be different from nodes original parent based on position property (for example if
   * node was removed from normal flow).
   */
  @Getter private final List<LayoutNode> positionedChildNodes = new LinkedList<>();

  /** Dimensions of node defined by layout service. */
  @Getter private final Dimensions dimensions = new Dimensions();

  public List<LayoutNode> layoutNodes() {
    var layoutNodes = new LinkedList<>(normalFlowChildNodes);
    layoutNodes.addAll(positionedChildNodes);
    return layoutNodes;
  }
}
