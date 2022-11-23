package com.spinyowl.spinygui.core.layout;

import static com.spinyowl.spinygui.core.layout.LayoutUtils.hasPosition;
import static com.spinyowl.spinygui.core.layout.LayoutUtils.isPositioned;
import static com.spinyowl.spinygui.core.style.types.Overflow.AUTO;
import static com.spinyowl.spinygui.core.style.types.Overflow.SCROLL;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.layout.Box;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.node.pseudo.Scrollbar;
import com.spinyowl.spinygui.core.node.pseudo.Scrollbar.Orientation;
import com.spinyowl.spinygui.core.style.manager.StyleManager;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Layout service is an entry point to layout system. Used to layout provided element. */
@RequiredArgsConstructor
public class LayoutServiceImpl implements LayoutService {

  @NonNull private final StyleManager styleManager;
  @NonNull private final TextLayout textLayout;
  @NonNull private final Map<Display, ElementLayout> layoutMap;

  @Override
  public Viewport layout(@NonNull Frame frame) {
    // first of all we need to generate layout tree before we can start layout process.
    Viewport viewport = Viewport.of(frame);
    updateLayoutNodes(viewport, viewport);

    // start layout process
    layoutNode(viewport, new LayoutContext());

    // update scrollbars and client size.
    updateScrollAndClientSize(viewport);

    return viewport;
  }

  @Override
  public void layoutNode(@NonNull LayoutNode layoutNode, @NonNull LayoutContext context) {
    Node node = layoutNode.node();
    if (layoutNode instanceof LayoutElement layoutElement) {
      if (visible(layoutElement.element())) {
        Display display = layoutElement.element().resolvedStyle().display();
        ElementLayout layout = layoutMap.get(display);
        if (layout != null) {
          layout.layout(layoutElement, context);
        }
      }
    } else if (node instanceof Text) {
      textLayout.layout(layoutNode, context);
    }

    if (node instanceof Text text) {
      context.lastTextEndX(text.textEndX());
      context.lastTextEndY(text.textEndY());
    }
  }

  @Override
  public void layoutChildNodes(@NonNull LayoutElement element, @NonNull LayoutContext context) {
    var childNodes = element.children();
    if (childNodes.isEmpty()) {
      return;
    }

    LayoutContext inner = new LayoutContext();
    childNodes.forEach(node -> layoutNode(node, inner));
  }

  private void updateScrollAndClientSize(LayoutElement layoutElement) {
    Element element = layoutElement.element();
    if (!visible(element)) return;

    float scrollWidth = 0;
    float scrollHeight = 0;

    for (Node childNode : element.childNodes()) {
      if (affectsScrollSize(childNode)) {
        Rect rect = childNode.box().marginBox();
        scrollWidth = Math.max(scrollWidth, rect.x() + rect.width());
        scrollHeight = Math.max(scrollHeight, rect.y() + rect.height());
      }
    }

    Box box = element.box();
    scrollWidth = scrollWidth - box.border().left() - box.padding().left();
    scrollHeight = scrollHeight - box.border().top() - box.padding().top();
    element.scrollWidth(scrollWidth);
    element.scrollHeight(scrollHeight);

    float clientWidth = calculateClientWidth(layoutElement, element, scrollWidth, box);
    float clientHeight = calculateClientHeight(layoutElement, element, scrollHeight, box);

    element.clientWidth(clientWidth);
    element.clientHeight(clientHeight);

    updateScrollLeft(element, scrollWidth, clientWidth);
    updateScrollTop(element, scrollHeight, clientHeight);

    layoutElement.children().stream()
        .filter(LayoutElement.class::isInstance)
        .map(LayoutElement.class::cast)
        .forEach(this::updateScrollAndClientSize);
  }

  private static float calculateClientWidth(
      LayoutElement layoutElement, Element element, float scrollWidth, Box box) {
    float boxContentWidth = box.content().width();
    float clientWidth = boxContentWidth;
    Overflow overflowX = element.resolvedStyle().overflowX();
    if ((SCROLL.equals(overflowX)) || (AUTO.equals(overflowX) && scrollWidth > clientWidth)) {
      clientWidth -= layoutElement.horizontalScrollbar().box().borderBox().width();
    }

    box.content().width(clientWidth);
    box.scroll().right(boxContentWidth - clientWidth);
    return clientWidth;
  }

  private static float calculateClientHeight(
      LayoutElement layoutElement, Element element, float scrollHeight, Box box) {
    float clientHeight = box.content().height();
    Overflow overflowY = element.resolvedStyle().overflowY();
    if ((SCROLL.equals(overflowY)) || (AUTO.equals(overflowY) && scrollHeight > clientHeight)) {
      clientHeight -= layoutElement.verticalScrollbar().box().borderBox().width();
    }
    return clientHeight;
  }

  private static void updateScrollTop(Element element, float scrollHeight, float clientHeight) {
    if (clientHeight >= scrollHeight) {
      element.scrollTop(0);
    } else {
      float scrollTop = element.scrollTop();
      if (scrollTop < 0) {
        element.scrollTop(0);
      } else if (scrollTop > scrollHeight - clientHeight) {
        element.scrollTop(scrollHeight - clientHeight);
      }
    }
  }

  private static void updateScrollLeft(Element element, float scrollWidth, float clientWidth) {
    if (clientWidth >= scrollWidth) {
      element.scrollLeft(0);
    } else {
      float scrollLeft = element.scrollLeft();
      if (scrollLeft < 0) {
        element.scrollLeft(0);
      } else if (scrollLeft > scrollWidth - clientWidth) {
        element.scrollLeft(scrollWidth - clientWidth);
      }
    }
  }

  private boolean affectsScrollSize(Node node) {
    if (node instanceof Element element) {
      return !hasPosition(element, Position.ABSOLUTE);
    }
    return true;
  }

  // LAYOUT TREE GENERATION

  private static void updateLayoutNodes(LayoutNode layoutNode, LayoutElement positionedAncestor) {
    List<LayoutNode> children = new LinkedList<>();
    List<LayoutNode> normalFlowChildren = new LinkedList<>();
    List<LayoutNode> positionedChildren = new LinkedList<>();

    if (layoutNode instanceof LayoutElement layoutElement) {
      Element element = layoutElement.element();
      generatePseudoElements(layoutElement, element, normalFlowChildren, positionedChildren);
      updateLayoutNodeAndChildren(
          layoutNode, positionedAncestor, normalFlowChildren, positionedChildren);
    }
    children.addAll(normalFlowChildren);
    children.addAll(positionedChildren);
    layoutNode.children(children);
  }

  private static void generatePseudoElements(
      LayoutElement layoutElement,
      Element element,
      List<LayoutNode> normalFlowChildren,
      List<LayoutNode> positionedChildren) {
    Overflow overflowX = element.resolvedStyle().overflowX();
    if ((SCROLL.equals(overflowX)) || (AUTO.equals(overflowX))) {
      Scrollbar scrollbar = new Scrollbar(Orientation.HORIZONTAL, element);
      layoutElement.horizontalScrollbar(scrollbar);

      LayoutElement childLayoutNode = LayoutElement.of(scrollbar, layoutElement);
      positionedChildren.add(childLayoutNode);
      updateLayoutNodes(childLayoutNode, layoutElement);
    }

    Overflow overflowY = element.resolvedStyle().overflowY();
    if ((SCROLL.equals(overflowY)) || (AUTO.equals(overflowY))) {
      Scrollbar scrollbar = new Scrollbar(Orientation.VERTICAL, element);
      layoutElement.verticalScrollbar(scrollbar);

      LayoutElement childLayoutNode = LayoutElement.of(scrollbar, layoutElement);
      positionedChildren.add(childLayoutNode);
      updateLayoutNodes(childLayoutNode, layoutElement);
    }
  }

  private static void updateLayoutNodeAndChildren(
      LayoutNode layoutNode,
      LayoutElement positionedAncestor,
      List<LayoutNode> normalFlowChildren,
      List<LayoutNode> positionedChildren) {
    layoutNode.node().childNodes().stream()
        .takeWhile(NodeUtilities::visible)
        .forEach(
            childNode ->
                fillAndGenerateSubTrees(
                    positionedAncestor, normalFlowChildren, positionedChildren, childNode));
  }

  private static void fillAndGenerateSubTrees(
      LayoutElement positionedAncestor,
      List<LayoutNode> normalFlowChildren,
      List<LayoutNode> positionedChildren,
      Node childNode) {
    if (childNode instanceof Text) {
      normalFlowChildren.add(LayoutNode.of(childNode, positionedAncestor));
    } else if (childNode instanceof Element childElement) {
      if (isPositioned(childElement)) {
        LayoutElement childLayoutNode = LayoutElement.of(childElement, positionedAncestor);
        positionedChildren.add(childLayoutNode);
        updateLayoutNodes(childLayoutNode, positionedAncestor);
      } else {
        LayoutElement childLayoutNode = LayoutElement.of(childElement, positionedAncestor);
        normalFlowChildren.add(childLayoutNode);
        updateLayoutNodes(childLayoutNode, childLayoutNode);
      }
    }
  }
}
