package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import com.spinyowl.spinygui.core.layout.LayoutNode;
import com.spinyowl.spinygui.core.layout.LayoutProvider;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.layout.LayoutTree;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Node.Box;
import com.spinyowl.spinygui.core.node.Node.Edges;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Position;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Layout service is an entry point to layout system. Used to layout provided element. */
@RequiredArgsConstructor
public class LayoutServiceImpl implements LayoutService {

  @NonNull private final LayoutProvider layoutProvider;

  @Override
  public LayoutTree layout(Frame frame) {
    if (frame != null && visible(frame)) {
      var layoutManager = layoutProvider.getLayoutManager(frame.calculatedStyle().display());
      if (layoutManager != null) {
        layoutManager.layout(frame);
      }
      Box dimensions = frame.dimensions();
      Edges padding = dimensions.padding();
      dimensions.contentSize(
          frame
              .frameSize()
              .sub(padding.left() + padding.right(), padding.top() + padding.bottom()));

      frame.children().forEach(this::layoutElement);
    }
    return createLayoutTree(frame);
  }

  private void layoutElement(Element element) {
    if (element != null && visible(element)) {
      var layoutManager = layoutProvider.getLayoutManager(element.calculatedStyle().display());
      if (layoutManager != null) {
        layoutManager.layout(element);
      }

      element.children().forEach(this::layoutElement);
    }
  }

  private LayoutTree createLayoutTree(Frame frame) {
    return new LayoutTree(frame, createChildren(frame));
  }

  private LayoutNode createLayoutNode(Node node) {
    if (node instanceof Element element) {
      return new LayoutNode(element, createChildren(element));
    }
    return new LayoutNode(node, List.of());
  }

  private List<LayoutNode> createChildren(Element element) {
    if (element.childNodes().isEmpty()) {
      return List.of();
    } else {
      var children = new ArrayList<LayoutNode>();

      var normalFlowChildren = new ArrayList<Node>();
      var positionedChildren = new ArrayList<Element>();

      for (Node childNode : element.childNodes()) {
        if (childNode instanceof Text childText) {
          normalFlowChildren.add(childText);
        } else if (childNode instanceof Element childElement
            && !Display.NONE.equals(childElement.calculatedStyle().display())) {
          Position elementPosition = childElement.calculatedStyle().position();
          if (Position.STATIC.equals(elementPosition)) {
            normalFlowChildren.add(childElement);
          } else if (Position.RELATIVE.equals(elementPosition)) {
            positionedChildren.add(childElement);
          } else if (Position.ABSOLUTE.equals(elementPosition)) {
            positionedChildren.add(childElement);
          } // unreachable
        }
      }
      normalFlowChildren.forEach(c -> children.add(createLayoutNode(c)));
      positionedChildren.forEach(c -> children.add(createLayoutNode(c)));
      return children;
    }
  }
}
