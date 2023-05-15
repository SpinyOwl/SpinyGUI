package com.spinyowl.spinygui.core.layout;

import static com.spinyowl.spinygui.core.layout.mode.LayoutUtils.isPositioned;
import static com.spinyowl.spinygui.core.style.types.Overflow.AUTO;
import static com.spinyowl.spinygui.core.style.types.Overflow.SCROLL;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.pseudo.After;
import com.spinyowl.spinygui.core.node.pseudo.Before;
import com.spinyowl.spinygui.core.node.pseudo.PseudoElement;
import com.spinyowl.spinygui.core.node.pseudo.Scrollbar;
import com.spinyowl.spinygui.core.node.pseudo.Scrollbar.Orientation;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import java.util.LinkedList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LayoutTreeBuilderImpl implements LayoutTreeBuilder {

  @NonNull private final StyleTreeBuilder styleTreeBuilder;

  @Override
  public LayoutNode build(StyledNode root) {
    LayoutNode layoutElement = new LayoutNode(root);
    updateLayoutNodes(layoutElement, layoutElement);
    return layoutElement;
  }

  /**
   * Used to update provided layout node and its children.
   *
   * @param layoutNode layout node to update.
   * @param positionedAncestor positioned ancestor.
   */
  private void updateLayoutNodes(LayoutNode layoutNode, LayoutNode positionedAncestor) {
    List<LayoutNode> children = new LinkedList<>();
    List<LayoutNode> normalFlowChildren = new LinkedList<>();
    List<LayoutNode> positionedChildren = new LinkedList<>();

    if (layoutNode.isElement()) {
      Element element = layoutNode.element();
      addScrollbars(layoutNode, element, positionedChildren);
      addBeforePseudoElement(layoutNode, element, normalFlowChildren);
      updateLayoutNodeAndChildren(
          layoutNode, positionedAncestor, normalFlowChildren, positionedChildren);
      addAfterPseudoElement(layoutNode, element, normalFlowChildren, positionedChildren);
    }
    children.addAll(normalFlowChildren);
    children.addAll(positionedChildren);
    layoutNode.children(children);
    layoutNode.normalFlowChildren(normalFlowChildren);
    layoutNode.positionedChildren(positionedChildren);
  }

  private void addBeforePseudoElement(
      LayoutNode layoutNode, Element element, List<LayoutNode> normalFlowChildren) {
    if (element.hasResolvedStyle(Before.NAME) && !(element instanceof PseudoElement)) {
      ResolvedStyle beforeStyles = element.resolvedStyle(Before.NAME);
      Element beforeElement = new Before(beforeStyles.content(), element);
      StyledNode styledNode = styleTreeBuilder.build(beforeElement, List.of());
      LayoutNode childLayoutNode = new LayoutNode(styledNode, layoutNode);
      normalFlowChildren.add(childLayoutNode);
      updateLayoutNodes(childLayoutNode, childLayoutNode);
    }
  }

  private void addAfterPseudoElement(
      LayoutNode layoutNode,
      Element element,
      List<LayoutNode> normalFlowChildren,
      List<LayoutNode> positionedChildren) {
    if (element.hasResolvedStyle(After.NAME) && !(element instanceof PseudoElement)) {
      ResolvedStyle afterStyles = element.resolvedStyle(After.NAME);
      Element afterElement = new After(afterStyles.content(), element);
      StyledNode styledNode = styleTreeBuilder.build(afterElement, List.of());
      LayoutNode childLayoutNode = new LayoutNode(styledNode, layoutNode);
      if (isPositioned(afterElement)) {
        positionedChildren.add(childLayoutNode);
      } else {
        normalFlowChildren.add(childLayoutNode);
      }
      updateLayoutNodes(childLayoutNode, childLayoutNode);
    }
  }

  private void addScrollbars(
      LayoutNode layoutNode, Element element, List<LayoutNode> positionedChildren) {
    Overflow overflowX = element.resolvedStyle().overflowX();
    if ((SCROLL.equals(overflowX)) || (AUTO.equals(overflowX))) {
      Scrollbar scrollbar = new Scrollbar(Orientation.HORIZONTAL, element);
      StyledNode styledNode = styleTreeBuilder.build(scrollbar, List.of());
      LayoutNode childLayoutNode = new LayoutNode(styledNode, layoutNode);
      layoutNode.horizontalScrollbarLayoutNode(childLayoutNode);
      positionedChildren.add(childLayoutNode);
      updateLayoutNodes(childLayoutNode, layoutNode);
    }

    Overflow overflowY = element.resolvedStyle().overflowY();
    if ((SCROLL.equals(overflowY)) || (AUTO.equals(overflowY))) {
      Scrollbar scrollbar = new Scrollbar(Orientation.VERTICAL, element);
      StyledNode styledNode = styleTreeBuilder.build(scrollbar, List.of());
      LayoutNode childLayoutNode = new LayoutNode(styledNode, layoutNode);
      layoutNode.verticalScrollbarLayoutNode(childLayoutNode);
      positionedChildren.add(childLayoutNode);
      updateLayoutNodes(childLayoutNode, layoutNode);
    }
  }

  private void updateLayoutNodeAndChildren(
      LayoutNode layoutNode,
      LayoutNode positionedAncestor,
      List<LayoutNode> normalFlowChildren,
      List<LayoutNode> positionedChildren) {
    layoutNode.styledNode().children().stream()
        .filter(sn -> NodeUtilities.visible(sn.node()))
        .forEach(
            sn ->
                fillAndGenerateSubTrees(
                    positionedAncestor, normalFlowChildren, positionedChildren, sn));
  }

  private void fillAndGenerateSubTrees(
      LayoutNode positionedAncestor,
      List<LayoutNode> normalFlowChildren,
      List<LayoutNode> positionedChildren,
      StyledNode styledNode) {
    if (styledNode.node() instanceof Text) {
      normalFlowChildren.add(new LayoutNode(styledNode, positionedAncestor));
    } else if (styledNode.node() instanceof Element childElement) {
      if (isPositioned(childElement)) {
        var childLayoutNode = new LayoutNode(styledNode, positionedAncestor);
        positionedChildren.add(childLayoutNode);
        updateLayoutNodes(childLayoutNode, positionedAncestor);
      } else {
        var childLayoutNode = new LayoutNode(styledNode, positionedAncestor);
        normalFlowChildren.add(childLayoutNode);
        updateLayoutNodes(childLayoutNode, childLayoutNode);
      }
    }
  }
}
