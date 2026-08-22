package com.spinyowl.spinygui.core.system.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Calculation-path characterization for cold, warm, churn, and disabled M7 modes. */
class TextCacheModeCharacterizationTest {
  @Test
  void coldWarmChurnAndDisabledModesHaveReproducibleAggregateBounds() {
    ResolvedPrimitiveKey primitiveKey = new ResolvedPrimitiveKey(
        "mode-text", List.of("font@1"), 12f, "round", "fallback");
    ResolvedPrimitiveValue primitiveValue = new ResolvedPrimitiveValue(List.of(
        new ResolvedPrimitiveValue.Primitive(0, 1, 'm', "font", 1, 5f, List.of())));
    WrappedLayoutKey wrapKey = new WrappedLayoutKey(
        primitiveKey, 80f, 0f, "12/16", "word", "unicode");
    WrappedLayoutValue layout = new WrappedLayoutValue(List.of(
        new WrappedLayoutValue.Line(0, 4, List.of(0f, 5f))));

    ResolvedPrimitiveCache cold = new ResolvedPrimitiveCache(2, 100, true);
    WrappedLayoutCache coldWrap = new WrappedLayoutCache(2, 100, true);
    assertNull(cold.get(primitiveKey));
    assertTrue(cold.put(primitiveKey, primitiveValue));
    assertNull(coldWrap.get(wrapKey));
    assertTrue(coldWrap.put(wrapKey, layout));

    assertSame(primitiveValue, cold.get(primitiveKey));
    assertSame(layout, coldWrap.get(wrapKey));
    assertEquals(2, cold.stats().hits() + coldWrap.stats().hits());

    for (int i = 0; i < 20; i++) {
      ResolvedPrimitiveKey churnKey = new ResolvedPrimitiveKey(
          "mode-" + i, List.of("font@1"), 12f, "round", "fallback");
      cold.put(churnKey, primitiveValue);
      coldWrap.put(new WrappedLayoutKey(churnKey, 80f + i, 0f, "12/16", "word", "unicode"), layout);
    }
    assertTrue(cold.stats().entries() <= 2);
    assertTrue(coldWrap.stats().entries() <= 2);
    assertTrue(cold.stats().retainedWeight() + coldWrap.stats().retainedWeight() <= 200);

    ResolvedPrimitiveCache disabled = new ResolvedPrimitiveCache(2, 100, false);
    WrappedLayoutCache disabledWrap = new WrappedLayoutCache(2, 100, false);
    assertNull(disabled.get(primitiveKey));
    assertNull(disabledWrap.get(wrapKey));
    assertEquals(0, disabled.stats().entries() + disabledWrap.stats().entries());
    assertEquals(0, disabled.stats().admissions() + disabledWrap.stats().admissions());
  }
}
