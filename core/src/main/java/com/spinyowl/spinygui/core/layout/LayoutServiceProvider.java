package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.tree.LayoutService;
import com.spinyowl.spinygui.core.system.tree.LayoutServiceImpl;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.HashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LayoutServiceProvider {
  public static LayoutService create(
      @NonNull SystemEventProcessor systemEventProcessor,
      @NonNull EventProcessor eventProcessor,
      @NonNull TimeService timeService,
      @NonNull FontService fontService) {

    var textLayout = new TextLayoutImpl(fontService);
    var elementLayoutMap = new HashMap<Display, Layout>();
    LayoutService layoutService = new LayoutServiceImpl(textLayout, elementLayoutMap);

    elementLayoutMap.put(Display.NONE, new NoneLayout());

    var blockLayout = new BlockLayout(layoutService);
    elementLayoutMap.put(Display.BLOCK, blockLayout);

    var flexLayout =
        new FlexLayout(
            systemEventProcessor, eventProcessor, timeService, blockLayout, layoutService);
    elementLayoutMap.put(Display.FLEX, flexLayout);

    return layoutService;
  }
}
