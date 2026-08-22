package com.spinyowl.spinygui.core.system.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.system.font.FontResourceObservation;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TextCacheAggregateObservationTest {
  @Test
  void aggregatesJavaNativeAndOneCurrentSnapshotPerControlWithoutDoubleCounting() {
    BoundedTextCache.Stats primitives = new BoundedTextCache.Stats(2, 3, 1, 0, 0, 1, 40);
    BoundedTextCache.Stats wraps = new BoundedTextCache.Stats(1, 2, 1, 0, 0, 1, 24);
    FontResourceObservation nativeResources =
        new FontResourceObservation(2, 128, 1, 0,
            FontResourceObservation.AliasLifetime.JVM_MANAGED_CALLER_RETAINABLE);
    TextCacheAggregateObservation aggregate = new TextCacheAggregateObservation(
        Map.of("resolved-primitive", primitives, "wrapped-layout", wraps),
        nativeResources,
        List.of(16L, 20L),
        Map.of("core-font-bytes", 128L, "nanovg-staging", 64L),
        Map.of("core-stb-info", 1L, "nanovg-faces", 2L));

    assertEquals(2, aggregate.javaEntries());
    assertEquals(64, aggregate.javaRetainedWeight());
    assertEquals(192, aggregate.nativeWeight());
    assertEquals(3, aggregate.nativeEntryCount());
    assertEquals(36, aggregate.snapshotWeight());
    assertEquals(292, aggregate.retainedWeight());
  }
}
