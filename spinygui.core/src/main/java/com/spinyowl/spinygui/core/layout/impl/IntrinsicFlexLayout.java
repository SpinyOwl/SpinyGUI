package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.hasPosition;
import static com.spinyowl.spinygui.core.style.types.Position.ABSOLUTE;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;

import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.style.types.Display;
import lombok.NonNull;

/**
 * Flex layout variant that preserves intrinsic sizing after the block pre-pass used by {@link
 * FlexLayout}.
 *
 * <p>The pre-pass cannot know the final size of nested flex contents. Without a correction,
 * auto-height containers can remain shorter than their flex children and following siblings overlap
 * them. Likewise, a flex container used as an auto-width flex item can retain the block pre-pass
 * width (the full containing block) instead of its intrinsic content width, pushing later flex
 * items outside the viewport.
 */
final class IntrinsicFlexLayout extends FlexLayout {

  IntrinsicFlexLayout(@NonNull BlockLayout blockLayout, @NonNull LayoutService layoutService) {
    super(blockLayout, layoutService);
  }

  @Override
  public void layout(Element parent, LayoutContext context) {
    super.layout(parent, context);
    updateAutoHeight(parent);
    updateAutoWidthWhenFlexItem(parent);
  }

  private void updateAutoHeight(Element parent) {
    if (!parent.resolvedStyle().height().isAuto()) {
      return;
    }

    float contentTop = parent.box().border().top() + parent.box().padding().top();
    float intrinsicBottom = contentTop;
    for (Element child : parent.children()) {
      if (!visible(child) || hasPosition(child, ABSOLUTE)) {
        continue;
      }
      Rect marginBox = child.box().marginBox();
      intrinsicBottom = Math.max(intrinsicBottom, marginBox.y() + marginBox.height());
    }

    float intrinsicHeight = Math.max(0f, intrinsicBottom - contentTop);
    parent.box().content().height(Math.max(parent.box().content().height(), intrinsicHeight));
  }

  private void updateAutoWidthWhenFlexItem(Element element) {
    if (!element.resolvedStyle().width().isAuto() || !isNormalFlowFlexItem(element)) {
      return;
    }

    element.box().content().width(Math.max(0f, intrinsicContentWidth(element)));
  }

  private boolean isNormalFlowFlexItem(Element element) {
    Element parent = element.parent();
    return parent != null
        && Display.FLEX.equals(parent.resolvedStyle().display())
        && !hasPosition(element, ABSOLUTE);
  }
}
