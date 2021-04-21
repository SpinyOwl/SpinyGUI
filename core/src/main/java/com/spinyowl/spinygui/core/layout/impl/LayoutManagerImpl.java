package com.spinyowl.spinygui.core.layout.impl;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.layout.Layout;
import com.spinyowl.spinygui.core.layout.LayoutManager;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.style.types.Display;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

public class LayoutManagerImpl implements LayoutManager {

  @NonNull private final EventProcessor eventProcessor;

  private final Map<Display, Layout> layoutMap = new ConcurrentHashMap<>();

  public LayoutManagerImpl(@NonNull EventProcessor eventProcessor) {
    this.eventProcessor = eventProcessor;
    registerLayout(Display.NONE, new DisplayNoneLayout());
    registerLayout(Display.FLEX, new FlexLayout(eventProcessor));
  }

  @Override
  public void registerLayout(Display displayType, Layout layout) {
    Objects.requireNonNull(displayType);
    Objects.requireNonNull(layout);

    layoutMap.put(displayType, layout);
  }

  @Override
  public void layout(Element element) {
    if (element != null && element.visible() && NodeUtilities.visibleInParents(element)) {
      Layout layout = layoutMap.get(element.style().display());
      if (layout != null) {
        layout.layout(element);
      }

      element.children().forEach(this::layout);
    }
  }
}
