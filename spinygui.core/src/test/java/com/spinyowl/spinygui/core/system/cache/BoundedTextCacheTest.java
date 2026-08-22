package com.spinyowl.spinygui.core.system.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BoundedTextCacheTest {
  @Test
  void boundsWeightAndLruEvictionAreDeterministic() {
    BoundedTextCache<String, String> cache = new BoundedTextCache<>(2, 5, String::length);
    assertTrue(cache.put("a", "aa"));
    assertTrue(cache.put("b", "bbb"));
    assertEquals("aa", cache.get("a"));
    assertTrue(cache.put("c", "cc"));

    assertEquals("aa", cache.get("a"));
    assertEquals("cc", cache.get("c"));
    assertEquals(null, cache.get("b"));
    assertEquals(2, cache.stats().entries());
    assertEquals(1, cache.stats().evictions());
    assertEquals(1, cache.stats().misses());
  }

  @Test
  void oversizedValuesAreRejectedWithoutRetention() {
    BoundedTextCache<String, String> cache = new BoundedTextCache<>(2, 3, String::length);
    assertFalse(cache.put("large", "long"));
    assertEquals(1, cache.stats().rejections());
    assertEquals(0, cache.stats().entries());
  }

  @Test
  void disabledModeDoesNotLookupOrRetain() {
    BoundedTextCache<String, String> cache = new BoundedTextCache<>(2, 10, String::length, false);
    assertFalse(cache.put("key", "value"));
    assertEquals(null, cache.get("key"));
    assertEquals(0, cache.stats().entries());
    assertEquals(0, cache.stats().admissions());
  }

  @Test
  void closeIsIdempotentButRejectsFurtherUse() {
    BoundedTextCache<String, String> cache = new BoundedTextCache<>(1, 10, String::length);
    cache.close();
    cache.close();
    assertThrows(IllegalStateException.class, () -> cache.get("key"));
  }

  @Test
  void diagnosticsCanResetIndependentlyOfWarmEntries() {
    BoundedTextCache<String, String> cache = new BoundedTextCache<>(1, 10, String::length);
    cache.put("key", "value");
    assertEquals("value", cache.get("key"));
    cache.get("missing");
    cache.resetDiagnostics();

    assertEquals("value", cache.get("key"));
    assertEquals(1, cache.stats().hits());
    assertEquals(0, cache.stats().misses());
    assertEquals(1, cache.stats().entries());
  }
}
