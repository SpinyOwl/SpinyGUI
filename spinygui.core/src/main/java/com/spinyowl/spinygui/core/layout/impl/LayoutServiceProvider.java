package com.spinyowl.spinygui.core.layout.impl;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.layout.ElementLayout;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
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

    if (!(fontService instanceof TextMeasurer textMeasurer)) {
      throw new IllegalArgumentException("FontService must also implement TextMeasurer");
    }
    return create(systemEventProcessor, eventProcessor, timeService, fontService, textMeasurer);
  }

  public static LayoutService create(
      @NonNull SystemEventProcessor systemEventProcessor,
      @NonNull EventProcessor eventProcessor,
      @NonNull TimeService timeService,
      @NonNull FontService fontService,
      @NonNull TextMeasurer textMeasurer) {

    var textLayout = new TextLayoutImpl(fontService, textMeasurer);
    var inlineFormattingContext = new InlineFormattingContext(textMeasurer);
    var elementLayoutMap = new HashMap<Display, ElementLayout>();
    LayoutService layoutService = new LayoutServiceImpl(textLayout, elementLayoutMap);

    elementLayoutMap.put(Display.NONE, new NoneLayout());

    var blockLayout = new BlockLayout(layoutService, inlineFormattingContext, textMeasurer);
    inlineFormattingContext.inlineBlockLayout(blockLayout::layoutInlineBlock);
    elementLayoutMap.put(Display.BLOCK, blockLayout);

    var flexLayout =
        new FlexLayout(
            systemEventProcessor, eventProcessor, timeService, blockLayout, layoutService);
    elementLayoutMap.put(Display.FLEX, flexLayout);

    return layoutService;
  }
}
