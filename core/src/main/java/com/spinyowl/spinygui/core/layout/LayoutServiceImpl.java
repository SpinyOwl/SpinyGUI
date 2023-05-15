package com.spinyowl.spinygui.core.layout;

import static com.spinyowl.spinygui.core.layout.mode.LayoutUtils.hasPosition;
import static com.spinyowl.spinygui.core.style.types.Overflow.AUTO;
import static com.spinyowl.spinygui.core.style.types.Overflow.SCROLL;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;

import com.spinyowl.spinygui.core.layout.mode.Layout;
import com.spinyowl.spinygui.core.layout.mode.TextLayout;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.layout.Box;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.node.pseudo.Scrollbar;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.style.types.Position;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Layout service is an entry point to layout system. Used to layout provided element. */
@RequiredArgsConstructor
public class LayoutServiceImpl implements LayoutService {

  @NonNull private final TextLayout textLayout;
  @NonNull private final Map<Display, Layout> layoutMap;

  public void layout(@NonNull LayoutNode layoutNode) {
    layoutNode(layoutNode, new LayoutContext());
    updateScrollAndClientSize(layoutNode);
  }

  @Override
  public void layoutNode(@NonNull LayoutNode layoutNode, @NonNull LayoutContext context) {
    Node node = layoutNode.node();
    if (layoutNode.isElement()) {
      Element element = layoutNode.element();
      if (visible(element)) {
        Display display = element.resolvedStyle().display();
        Layout layout = layoutMap.get(display);
        if (layout != null) {
          layout.layout(layoutNode, context);
        }
      }
    } else if (node instanceof Text) {
      textLayout.layout(layoutNode, context);
    }

    if (node instanceof Text text) {
      context.lastTextEndX(text.textEndX());
      context.lastTextEndY(text.textEndY());
    }
    //    if (node instanceof Element e && e.resolvedStyle().display().equals(Display.BLOCK)) {
    //      context.lastTextEndX(0F);
    //      context.lastTextEndY(0F);
    //    }
  }

  @Override
  public void layoutChildNodes(@NonNull LayoutNode element, @NonNull LayoutContext context) {
    LayoutContext inner = new LayoutContext();
    element.children().stream()
        .filter(n -> !(n.node() instanceof Scrollbar))
        .forEach(node -> layoutNode(node, inner));
  }

  private void updateScrollAndClientSize(LayoutNode layoutNode) {
    Element element = layoutNode.element();
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

    float boxContentWidth = box.content().width();
    float clientWidth = boxContentWidth;
    Overflow overflowX = element.resolvedStyle().overflowX();
    // TODO wrong calculation - need to check additionally if scrollbar is visible before
    // subtracting
    if ((SCROLL.equals(overflowX)) || (AUTO.equals(overflowX) && scrollWidth > clientWidth)) {
      if (layoutNode.verticalScrollbar() != null) {
        float scrollbarWidth = layoutNode.verticalScrollbar().box().borderBox().width();
        clientWidth -= scrollbarWidth;
      }
    }

    box.content().width(clientWidth);
    box.scroll().right(boxContentWidth - clientWidth);
    float clientHeight = box.content().height();
    Overflow overflowY = element.resolvedStyle().overflowY();
    if ((SCROLL.equals(overflowY)) || (AUTO.equals(overflowY) && scrollHeight > clientHeight)) {
      if (layoutNode.horizontalScrollbar() != null) {
        float scrollbarHeight = layoutNode.horizontalScrollbar().box().borderBox().height();
        clientHeight -= scrollbarHeight;
        box.scroll().bottom(scrollbarHeight);
      }
    }

    element.clientWidth(clientWidth);
    element.clientHeight(clientHeight);

    updateScrollLeft(element, scrollWidth, clientWidth);
    updateScrollTop(element, scrollHeight, clientHeight);

    layoutNode.children().stream()
        .filter(LayoutNode::isElement)
        .forEach(this::updateScrollAndClientSize);
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
}
