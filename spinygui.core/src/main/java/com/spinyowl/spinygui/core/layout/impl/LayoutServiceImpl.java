package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.hasPosition;
import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.isPositioned;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import static com.spinyowl.spinygui.core.util.OverflowUtils.clampScrollOffsets;

import com.spinyowl.spinygui.core.layout.ElementLayout;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.layout.TextLayout;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.layout.Box;
import com.spinyowl.spinygui.core.node.layout.Edges;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.util.ScrollbarGeometry;
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
    // layout all the nodes in the frame.
    layoutNode(frame, context);
    // update layout tree.
    updateLayoutNodes(frame);
    // update client size and scroll size for all the nodes in the frame.
    updateScrollAndClientSize(frame);
  }

  private void updateScrollAndClientSize(Element element) {
    float scrollWidth = 0;
    float scrollHeight = 0;

    for (Node node : element.childNodes()) {
      if (affectsScrollSize(node)) {
        Rect rect = node.box().marginBox();
        scrollWidth = Math.max(scrollWidth, rect.x() + rect.width());
        scrollHeight = Math.max(scrollHeight, rect.y() + rect.height());
      }
    }

    Box box = element.box();
    scrollWidth = Math.max(0, scrollWidth - box.border().left() - box.padding().left());
    scrollHeight = Math.max(0, scrollHeight - box.border().top() - box.padding().top());

    ScrollbarGeometry.Metrics scrollbarMetrics = null;
    float clientWidth = box.content().width();
    float clientHeight = box.content().height();
    if (ScrollbarGeometry.canShowScrollbars(element)) {
      scrollbarMetrics = ScrollbarGeometry.compute(element, scrollWidth, scrollHeight);
      clientWidth = scrollbarMetrics.clientWidth();
      clientHeight = scrollbarMetrics.clientHeight();
    }

    element.scrollWidth(scrollWidth);
    element.scrollHeight(scrollHeight);

    element.clientWidth(clientWidth);
    element.clientHeight(clientHeight);
    element.scrollbarMetrics(scrollbarMetrics);

    clampScrollOffsets(element);

    // update scroll and client size for all the children.
    element.childNodes().stream()
        .filter(child -> child instanceof Element childElement && visible(childElement))
        .map(Element.class::cast)
        .forEach(this::updateScrollAndClientSize);
  }

  private boolean affectsScrollSize(Node node) {
    if (node instanceof Element element) {
      return visible(element) && !hasPosition(element, Position.ABSOLUTE);
    }
    return true;
  }

  public void layoutNode(@NonNull Node node, @NonNull LayoutContext context) {
    if (node instanceof Element element) {
      if (!visible(element)) {
        clearHiddenSubtree(element);
        return;
      }
      Display display = element.resolvedStyle().display();
      ElementLayout layout = layoutMap.get(display);
      if (layout != null) {
        layout.layout(element, context);
      }
    } else if (node instanceof Text text) {
      textLayout.layout(text, context);
    }

    if (node instanceof Text text) {
      context.lastTextEndX(text.textEndX());
      context.lastTextEndY(text.textEndY());
    }
  }

  private void clearHiddenSubtree(Element element) {
    clearLayoutState(element);
    for (Node child : element.childNodes()) {
      if (child instanceof Element childElement) {
        clearHiddenSubtree(childElement);
      } else {
        clearLayoutState(child);
        if (child instanceof Text text) {
          text.inlineFragments(List.of());
          text.textStartX(0);
          text.textStartY(0);
          text.textEndX(0);
          text.textEndY(0);
        }
      }
    }
  }

  private void clearLayoutState(Node node) {
    node.layoutChildNodes(List.of());
    node.offsetParent(null);
    clearBox(node.box());
    if (node instanceof Element element) {
      element.inlineFragments(List.of());
      element.scrollWidth(0);
      element.scrollHeight(0);
      element.clientWidth(0);
      element.clientHeight(0);
      element.scrollbarMetrics(null);
    }
  }

  private void clearBox(Box box) {
    box.contentPosition(0, 0);
    box.contentSize(0, 0);
    clearEdges(box.padding());
    clearEdges(box.border());
    clearEdges(box.margin());
  }

  private void clearEdges(Edges edges) {
    edges.top(0);
    edges.right(0);
    edges.bottom(0);
    edges.left(0);
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
    LayoutNodeWrapper wrapper = LayoutNodeWrapper.of(frame);
    updateLayoutNodes(wrapper, wrapper);
    populateLayoutNodes(wrapper, null);
  }

  /**
   * Updates layout nodes and updates layout parent of each node. Layout parent is a node which in
   * fact is a parent of node in layout tree. Layout parent is used later as parent of the node
   * during rendering.
   *
   * @param wrapper layout node wrapper.
   * @param parent layout parent for node in wrapper.
   */
  private void populateLayoutNodes(LayoutNodeWrapper wrapper, Element parent) {
    wrapper.node.offsetParent(parent);
    var layoutChildNodes = new LinkedList<Node>();
    fillChildNodes(wrapper.normalFlowChildren, layoutChildNodes, wrapper.node);
    fillChildNodes(wrapper.positionedChildren, layoutChildNodes, wrapper.node);
    wrapper.node.layoutChildNodes(layoutChildNodes);
  }

  /**
   * Fills child nodes list with nodes from provided list of wrappers. Populates layout parent for
   * each node in wrappers.
   *
   * @param layoutNodeWrappers list of wrappers.
   * @param layoutChildNodes list of child nodes.
   * @param node current node.
   */
  private void fillChildNodes(
      List<LayoutNodeWrapper> layoutNodeWrappers, LinkedList<Node> layoutChildNodes, Node node) {
    for (LayoutNodeWrapper lnw : layoutNodeWrappers) {
      layoutChildNodes.add(lnw.node);
      populateLayoutNodes(lnw, node.asElement());
    }
  }

  /**
   * Collects all normal flow and positioned children of provided node. Afterwards performs same
   * operation for each child.
   *
   * @param nodeWrapper node wrapper to process.
   * @param positionedAncestor positioned ancestor of provided node wrapper.
   */
  private void updateLayoutNodes(
      LayoutNodeWrapper nodeWrapper, LayoutNodeWrapper positionedAncestor) {
    var normalFlowChildren = new LinkedList<Node>();
    var positionedChildren = new LinkedList<Node>();

    for (Node childNode : nodeWrapper.node.childNodes()) {
      if (childNode instanceof Text) {
        normalFlowChildren.add(childNode);
      } else if (childNode instanceof Element childElement) {
        if (!visible(childElement)) {
          clearHiddenSubtree(childElement);
          continue;
        }
        if (isPositioned(childElement)) {
          positionedChildren.add(childElement);
        } else {
          normalFlowChildren.add(childElement);
        }
      }
    }

    normalFlowChildren.forEach(
        c -> {
          LayoutNodeWrapper lnw = LayoutNodeWrapper.of(c);
          updateLayoutNodes(lnw, positionedAncestor);
          nodeWrapper.normalFlowChildren.add(lnw);
        });
    positionedChildren.forEach(
        c -> {
          LayoutNodeWrapper lnw = LayoutNodeWrapper.of(c);
          updateLayoutNodes(lnw, lnw);
          positionedAncestor.positionedChildren.add(lnw);
        });
  }

  /** Used to calculate layout tree. */
  @Getter
  @RequiredArgsConstructor
  private static class LayoutNodeWrapper {
    @NonNull private final Node node;
    @NonNull private final List<LayoutNodeWrapper> normalFlowChildren = new LinkedList<>();
    @NonNull private final List<LayoutNodeWrapper> positionedChildren = new LinkedList<>();

    private static LayoutNodeWrapper of(@NonNull Node node) {
      return new LayoutNodeWrapper(node);
    }
  }
}
