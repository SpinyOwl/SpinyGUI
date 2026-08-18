package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.hasPosition;
import static com.spinyowl.spinygui.core.style.types.Position.ABSOLUTE;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.time.TimeService;
import lombok.NonNull;

/**
 * Flex layout variant that preserves block-level auto-height semantics after Yoga positions its
 * children.
 *
 * <p>{@link FlexLayout} performs a block pre-pass before child flex layout. For an auto-height flex
 * container that pre-pass cannot know the intrinsic height of its flex children yet, so the element
 * can otherwise retain a zero-height content box while its children are visibly laid out below it.
 * That makes following siblings overlap the flex contents. This wrapper updates the auto-height from
 * the final normal-flow child geometry before the parent flex layout consumes this element's size.
 */
final class IntrinsicFlexLayout extends FlexLayout {

  IntrinsicFlexLayout(
      @NonNull SystemEventProcessor systemEventProcessor,
      @NonNull EventProcessor eventProcessor,
      @NonNull TimeService timeService,
      @NonNull BlockLayout blockLayout,
      @NonNull LayoutService layoutService) {
    super(systemEventProcessor, eventProcessor, timeService, blockLayout, layoutService);
  }

  @Override
  public void layout(Element parent, LayoutContext context) {
    super.layout(parent, context);
    updateAutoHeight(parent);
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
}
