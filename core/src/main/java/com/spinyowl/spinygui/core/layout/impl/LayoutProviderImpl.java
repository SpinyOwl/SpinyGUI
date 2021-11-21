package com.spinyowl.spinygui.core.layout.impl;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.layout.Layout;
import com.spinyowl.spinygui.core.layout.LayoutProvider;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;

public class LayoutProviderImpl implements LayoutProvider {

  private final Map<Display, Layout> layoutMap = new HashMap<>();

  public LayoutProviderImpl(
      @NonNull SystemEventProcessor systemEventProcessor,
      @NonNull EventProcessor eventProcessor,
      @NonNull TimeService timeService) {
    addLayoutManager(Display.NONE, NoneLayout.builder().build());
    addLayoutManager(
        Display.FLEX,
        FlexLayout.builder()
            .systemEventProcessor(systemEventProcessor)
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .build());
  }

  @Override
  public void addLayoutManager(@NonNull Display displayType, @NonNull Layout layout) {
    layoutMap.put(displayType, layout);
  }

  @Override
  public void removeLayoutManager(@NonNull Display displayType) {
    layoutMap.remove(displayType);
  }

  @Override
  public Layout getLayoutManager(Display displayType) {
    return layoutMap.get(displayType);
  }
}