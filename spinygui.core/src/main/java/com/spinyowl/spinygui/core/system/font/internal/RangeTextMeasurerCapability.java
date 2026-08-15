package com.spinyowl.spinygui.core.system.font.internal;

import com.spinyowl.spinygui.core.font.Font;
import java.util.List;

/** Optional internal capability for final zero-copy measurement of one shared-source range. */
public interface RangeTextMeasurerCapability {

  ResolvedMeasurement measureRange(
      String source,
      int start,
      int end,
      float offsetX,
      List<Font> fonts,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap);
}
