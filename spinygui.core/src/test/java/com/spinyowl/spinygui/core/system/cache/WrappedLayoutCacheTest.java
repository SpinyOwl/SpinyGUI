package com.spinyowl.spinygui.core.system.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class WrappedLayoutCacheTest {
  private static ResolvedPrimitiveKey primitive() {
    return new ResolvedPrimitiveKey("hello world", List.of("latin@3"), 14f, "round", "fallback");
  }

  private static WrappedLayoutKey key(float width) {
    return new WrappedLayoutKey(primitive(), width, 0f, "14/18", "word", "unicode");
  }

  private static WrappedLayoutValue layout(int end) {
    return new WrappedLayoutValue(List.of(new WrappedLayoutValue.Line(0, end, List.of(0f, 4f, 8f))));
  }

  @Test
  void exactWidthHitsAndEveryLineAffectingFieldMisses() {
    WrappedLayoutCache cache = new WrappedLayoutCache(4, 1024, true);
    WrappedLayoutValue value = layout(5);
    assertTrue(cache.put(key(100f), value));
    assertSame(value, cache.get(key(100f)));
    assertNull(cache.get(key(100.0001f)));
    assertNull(cache.get(new WrappedLayoutKey(primitive(), 100f, 1f, "14/18", "word", "unicode")));
    assertEquals(1, cache.stats().hits());
    assertEquals(2, cache.stats().misses());
  }

  @Test
  void widthChurnCannotExceedEntryOrWeightBound() {
    WrappedLayoutCache cache = new WrappedLayoutCache(3, 100, true);
    for (int i = 0; i < 30; i++) cache.put(key(50f + i), layout(5));
    assertTrue(cache.stats().entries() <= 3);
    assertTrue(cache.stats().retainedWeight() <= 100);
    assertTrue(cache.stats().evictions() > 0);
  }

  @Test
  void disabledModePreservesUncachedPathShape() {
    WrappedLayoutCache cache = new WrappedLayoutCache(3, 100, false);
    WrappedLayoutValue value = layout(5);
    assertFalse(cache.put(key(50f), value));
    assertNull(cache.get(key(50f)));
    assertEquals(0, cache.stats().entries());
  }
}
