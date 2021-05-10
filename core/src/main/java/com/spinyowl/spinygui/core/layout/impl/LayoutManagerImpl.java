package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visibleInParents;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.layout.Layout;
import com.spinyowl.spinygui.core.layout.LayoutManager;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.style.types.Display;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

public class LayoutManagerImpl implements LayoutManager {

  private final Map<Display, Layout> layoutMap = new ConcurrentHashMap<>();

  public LayoutManagerImpl(
      @NonNull EventProcessor eventProcessor, @NonNull TimeService timeService) {
    registerLayout(Display.NONE, new DisplayNoneLayout());
    registerLayout(Display.FLEX, new FlexLayout(eventProcessor, timeService));
  }

  @Override
  public void registerLayout(Display displayType, Layout layout) {
    Objects.requireNonNull(displayType);
    Objects.requireNonNull(layout);

    layoutMap.put(displayType, layout);
  }

  @Override
  public void layout(Element element) {
    if (element != null && visible(element) && visibleInParents(element)) {
      var layout = layoutMap.get(element.style().display());
      if (layout != null) {
        layout.layout(element);
      }

      element.children().forEach(this::layout);
    }
  }
}
