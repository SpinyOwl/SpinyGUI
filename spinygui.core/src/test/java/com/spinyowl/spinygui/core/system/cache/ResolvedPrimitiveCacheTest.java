package com.spinyowl.spinygui.core.system.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ResolvedPrimitiveCacheTest {
  private static ResolvedPrimitiveKey key(String source, String font) {
    return new ResolvedPrimitiveKey(source, List.of(font + "@7"), 14f, "round-half-even", "replace-missing");
  }

  private static ResolvedPrimitiveValue value(String font) {
    return new ResolvedPrimitiveValue(List.of(new ResolvedPrimitiveValue.Primitive(
        0, 1, 'a', font, 3, 7.5f, List.of())));
  }

  @Test
  void coldWarmAndWidthIndependentReuseAreObservable() {
    ResolvedPrimitiveCache cache = new ResolvedPrimitiveCache(4, 1024, true);
    ResolvedPrimitiveKey first = key("abc", "latin");
    assertNull(cache.get(first));
    ResolvedPrimitiveValue primitives = value("latin");
    assertTrue(cache.put(first, primitives));
    assertSame(primitives, cache.get(first));
    assertEquals(1, cache.stats().hits());
    assertEquals(1, cache.stats().misses());
  }

  @Test
  void exactSemanticInputsAndGenerationChangeMiss() {
    ResolvedPrimitiveCache cache = new ResolvedPrimitiveCache(4, 1024, true);
    cache.put(key("abc", "latin"), value("latin"));
    assertNull(cache.get(key("abcd", "latin")));
    assertNull(cache.get(key("abc", "latin@8")));
    assertEquals(1, cache.stats().entries());
  }

  @Test
  void churnAndOversizedValuesRemainBounded() {
    ResolvedPrimitiveCache cache = new ResolvedPrimitiveCache(2, 100, true);
    for (int i = 0; i < 20; i++) cache.put(key("text-" + i, "latin"), value("latin"));
    assertTrue(cache.stats().entries() <= 2);
    assertTrue(cache.stats().retainedWeight() <= 100);
    assertFalse(cache.put(key("large", "latin"), new ResolvedPrimitiveValue(
        java.util.stream.IntStream.range(0, 20).mapToObj(i -> new ResolvedPrimitiveValue.Primitive(
            i, i + 1, 'x', "latin", i, 1, List.of())).toList())));
    assertTrue(cache.stats().rejections() >= 1);
  }

  @Test
  void disabledModeDoesNotRetainOrLookup() {
    ResolvedPrimitiveCache cache = new ResolvedPrimitiveCache(2, 100, false);
    assertFalse(cache.put(key("abc", "latin"), value("latin")));
    assertNull(cache.get(key("abc", "latin")));
    assertEquals(0, cache.stats().entries());
    assertEquals(0, cache.stats().admissions());
  }
}
