package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.isPositioned;
import static com.spinyowl.spinygui.core.style.types.Display.BLOCK;
import static com.spinyowl.spinygui.core.style.types.Display.FLEX;
import static com.spinyowl.spinygui.core.style.types.Display.NONE;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.LinkedList;
import lombok.NonNull;

/** Layout service is an entry point to layout system. Used to layout provided element. */
public class LayoutServiceImpl implements LayoutService {

  private final NoneLayout noneLayout;
  private final FlexLayout flexLayout;
  private final BlockLayout blockLayout;
  private final TextLayout textLayout;

  public LayoutServiceImpl(
      @NonNull SystemEventProcessor systemEventProcessor,
      @NonNull EventProcessor eventProcessor,
      @NonNull TimeService timeService,
      @NonNull FontService fontService) {
    noneLayout = new NoneLayout();
    textLayout = new TextLayout(fontService);
    blockLayout = new BlockLayout(this);
    flexLayout = new FlexLayout(systemEventProcessor, eventProcessor, timeService, this);
  }

  @Override
  public void layout(@NonNull Frame frame) {
    LayoutContext context = new LayoutContext();
    layoutNode(frame, context);
    updateLayoutNodes(frame);
  }

  public void layoutNode(@NonNull Node node, @NonNull LayoutContext context) {
    if (node instanceof Element element) {
      if (visible(element)) {
        Display display = element.calculatedStyle().display();
        if (NONE.equals(display)) {
          noneLayout.layout(element, context);
        } else if (BLOCK.equals(display)) {
          blockLayout.layout(element, context);
        } else if (FLEX.equals(display)) {
          blockLayout.layout(element, true, context);
          flexLayout.layout(element, context);
        }
        // TODO: inline layout
      }
    } else if (node instanceof Text text) {
      textLayout.layout(text, context);
    }

    if (node instanceof Text text) {
      context.lastTextEndX(text.textEndX());
      context.lastTextEndY(text.textEndY());
    }
    // in case if we do not know what type of node it is, we do not layout it.
  }

  @Override
  public void layoutChildNodes(@NonNull Element parent, @NonNull LayoutContext context) {
    var childNodes = parent.childNodes();
    if (childNodes.isEmpty()) {
      return;
    }

    //    if (!INLINE.equals(parent.calculatedStyle().display())) {
    //      context.cleanup();
    //    }

    LayoutContext inner = new LayoutContext();
    childNodes.forEach(node -> layoutNode(node, inner));
  }

  private void updateLayoutNodes(Frame frame) {
    updateLayoutNodes(frame, frame, new LinkedList<>(), new LinkedList<>());
  }

  private void updateLayoutNodes(
      Node node,
      Node positionedAncestor,
      LinkedList<Node> normalFlowChildren,
      LinkedList<Node> positionedChildren) {

    for (Node childNode : node.childNodes()) {
      if (childNode instanceof Text) {
        normalFlowChildren.add(childNode);
      } else if (childNode instanceof Element childElement
          && !NONE.equals(childElement.calculatedStyle().display())) {
        if (isPositioned(childElement)) {
          positionedChildren.add(childElement);
        } else {
          normalFlowChildren.add(childElement);
        }
      }
    }

    normalFlowChildren.forEach(
        c -> {
          updateLayoutNodes(c, positionedAncestor, new LinkedList<>(), positionedChildren);
          //          LayoutNodeImpl ln = new LayoutNodeImpl(c, layoutNode);
          //          layoutNode.normalFlowChildren().add(fillLayoutNode(c, ln, positionedParent));
        });
    positionedChildren.forEach(
        c -> {
          updateLayoutNodes(c, c, new LinkedList<>(), new LinkedList<>());
          //          LayoutNodeImpl ln = new LayoutNodeImpl(c, positionedParent);
          //          positionedParent.positionedChildren().add(fillLayoutNode(c, ln, ln));
        });


    node.layoutChildNodes();
  }

  public static class LNW {
    @NonNull private Node node;
  }

  //  private LayoutTree createLayoutTree(Frame frame) {
  //    var layoutTree = new LayoutTreeImpl(frame);
  //    collectChildrenAndPassToAppropriateParent(frame, layoutTree, layoutTree);
  //    return layoutTree;
  //  }
  //
  //  private LayoutNode fillLayoutNode(
  //      Node node, TwoListNode layoutNode, TwoListNode positionedParent) {
  //    if (node instanceof Element element && !element.childNodes().isEmpty()) {
  //      collectChildrenAndPassToAppropriateParent(element, layoutNode, positionedParent);
  //    }
  //    return layoutNode;
  //  }
  //
  //  private void collectChildrenAndPassToAppropriateParent(
  //      Element element, TwoListNode layoutNode, TwoListNode positionedParent) {
  //    var normalFlowChildren = new ArrayList<Node>();
  //    var positionedChildren = new ArrayList<Node>();
  //
  //    collectChildNodes(element, normalFlowChildren, positionedChildren);
  //
  //    normalFlowChildren.forEach(
  //        c -> {
  //          LayoutNodeImpl ln = new LayoutNodeImpl(c, layoutNode);
  //          layoutNode.normalFlowChildren().add(fillLayoutNode(c, ln, positionedParent));
  //        });
  //    positionedChildren.forEach(
  //        c -> {
  //          LayoutNodeImpl ln = new LayoutNodeImpl(c, positionedParent);
  //          positionedParent.positionedChildren().add(fillLayoutNode(c, ln, ln));
  //        });
  //  }
  //
  //  private void collectChildNodes(
  //      Element element, List<Node> normalFlowChildren, List<Node> positionedChildren) {
  //    for (Node childNode : element.childNodes()) {
  //      if (childNode instanceof Text childText) {
  //        normalFlowChildren.add(childText);
  //      } else if (childNode instanceof Element childElement
  //          && !NONE.equals(childElement.calculatedStyle().display())) {
  //        if (isPositioned(childElement)) {
  //          positionedChildren.add(childElement);
  //        } else {
  //          normalFlowChildren.add(childElement);
  //        }
  //      }
  //    }
  //  }
  //
  //  private interface TwoListNode extends LayoutNode {
  //    List<LayoutNode> normalFlowChildren();
  //
  //    List<LayoutNode> positionedChildren();
  //  }
  //
  //  @Data
  //  @RequiredArgsConstructor
  //  private static class LayoutTreeImpl implements LayoutTree, TwoListNode {
  //    @NonNull private Frame node;
  //    private final List<LayoutNode> normalFlowChildren = new LinkedList<>();
  //    private final List<LayoutNode> positionedChildren = new LinkedList<>();
  //
  //    @Override
  //    public @NonNull List<LayoutNode> children() {
  //      ArrayList<LayoutNode> children = new ArrayList<>(normalFlowChildren);
  //      children.addAll(positionedChildren);
  //      return children;
  //    }
  //  }
  //
  //  @Data
  //  @RequiredArgsConstructor
  //  @ToString(exclude = "parent")
  //  private static class LayoutNodeImpl implements LayoutNode, TwoListNode {
  //    @NonNull private Node node;
  //    private final LayoutNode parent;
  //    private final List<LayoutNode> normalFlowChildren = new LinkedList<>();
  //    private final List<LayoutNode> positionedChildren = new LinkedList<>();
  //
  //    @Override
  //    public @NonNull List<LayoutNode> children() {
  //      ArrayList<LayoutNode> children = new ArrayList<>(normalFlowChildren);
  //      children.addAll(positionedChildren);
  //      return children;
  //    }
  //  }
}
