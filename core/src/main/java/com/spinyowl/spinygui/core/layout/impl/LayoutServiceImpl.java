package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.isPositioned;
import static com.spinyowl.spinygui.core.style.types.Display.NONE;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;

import com.spinyowl.spinygui.core.layout.ElementLayout;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.layout.TextLayout;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Display;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Layout service is an entry point to layout system. Used to layout provided element. */
@RequiredArgsConstructor
public class LayoutServiceImpl implements LayoutService {

  @NonNull private final TextLayout textLayout;
  @NonNull private final Map<Display, ElementLayout> layoutMap;

  @Override
  public void layout(@NonNull Frame frame) {
    LayoutContext context = new LayoutContext();
    layoutNode(frame, context);
    updateLayoutNodes(frame);
  }

  public void layoutNode(@NonNull Node node, @NonNull LayoutContext context) {
    if (node instanceof Element element) {
      if (visible(element)) {
        Display display = element.resolvedStyle().display();
        ElementLayout layout = layoutMap.get(display);
        if (layout != null) {
          layout.layout(element, context);
        }
      }
    } else if (node instanceof Text text) {
      textLayout.layout(text, context);
    }

    if (node instanceof Text text) {
      context.lastTextEndX(text.textEndX());
      context.lastTextEndY(text.textEndY());
    }
  }

  @Override
  public void layoutChildNodes(@NonNull Element element, @NonNull LayoutContext context) {
    var childNodes = element.childNodes();
    if (childNodes.isEmpty()) {
      return;
    }

    LayoutContext inner = new LayoutContext();
    childNodes.forEach(node -> layoutNode(node, inner));
  }

  private void updateLayoutNodes(Frame frame) {
    LNW wrapper = LNW.of(frame);
    updateLayoutNodes(wrapper, wrapper);
    populateLayoutNodes(wrapper, null);
  }

  private void populateLayoutNodes(LNW wrapper, Node parent) {
    wrapper.node.layoutParent(parent);
    var layoutChildNodes = new LinkedList<Node>();
    fillChildNodes(wrapper.normalFlowChildren, layoutChildNodes, wrapper.node);
    fillChildNodes(wrapper.positionedChildren, layoutChildNodes, wrapper.node);
    wrapper.node.layoutChildNodes(layoutChildNodes);
  }

  private void fillChildNodes(List<LNW> lnwList, LinkedList<Node> layoutChildNodes, Node parent) {
    for (LNW lnw : lnwList) {
      layoutChildNodes.add(lnw.node);
      populateLayoutNodes(lnw, parent);
    }
  }

  private void updateLayoutNodes(LNW nodeWrapper, LNW positionedAncestor) {
    var normalFlowChildren = new LinkedList<Node>();
    var positionedChildren = new LinkedList<Node>();

    for (Node childNode : nodeWrapper.node.childNodes()) {
      if (childNode instanceof Text) {
        normalFlowChildren.add(childNode);
      } else if (childNode instanceof Element childElement
          && !NONE.equals(childElement.resolvedStyle().display())) {
        if (isPositioned(childElement)) {
          positionedChildren.add(childElement);
        } else {
          normalFlowChildren.add(childElement);
        }
      }
    }

    normalFlowChildren.forEach(
        c -> {
          LNW lnw = LNW.of(c);
          updateLayoutNodes(lnw, positionedAncestor);
          nodeWrapper.normalFlowChildren.add(lnw);
        });
    positionedChildren.forEach(
        c -> {
          LNW lnw = LNW.of(c);
          updateLayoutNodes(lnw, lnw);
          positionedAncestor.positionedChildren.add(lnw);
        });
  }

  @Getter
  @RequiredArgsConstructor
  private static class LNW {
    @NonNull private final Node node;
    @NonNull private final List<LNW> normalFlowChildren = new LinkedList<>();
    @NonNull private final List<LNW> positionedChildren = new LinkedList<>();

    private static LNW of(@NonNull Node node) {
      return new LNW(node);
    }
  }
}
