package com.spinyowl.spinygui.core.system.font.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.cache.TextCacheConfiguration;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** M7 calculation-path evidence: measurements, not pre-laid-out rendering scenes. */
class M7CalculationPathCharacterizationTest {
  private FontServiceImpl service;

  @AfterEach
  void closeService() {
    if (service != null) service.close();
  }

  @Test
  void coldWarmAndWidthChurnInvokeBothCalculationCacheFamilies() {
    service = new FontServiceImpl(new FontStorageImpl(), false,
        com.spinyowl.spinygui.core.diagnostic.DiagnosticSession.disabled(),
        TextCacheConfiguration.boundedDefaults());
    service.installSemanticOwner();

    TextMetrics cold = service.measureText("cache path", 0, Font.DEFAULT, 16, 1.2f, 80, true);
    long primitiveMisses = service.resolvedPrimitiveCacheStats().misses();
    long wrapMisses = service.wrappedLayoutCacheStats().misses();
    TextMetrics warm = service.measureText("cache path", 0, Font.DEFAULT, 16, 1.2f, 80, true);
    assertEquals(cold, warm);
    assertTrue(service.resolvedPrimitiveCacheStats().hits() > 0);
    assertTrue(service.wrappedLayoutCacheStats().hits() > 0);
    assertTrue(primitiveMisses > 0 && wrapMisses > 0);

    for (int width = 40; width < 80; width++) {
      service.measureText("cache path", 0, Font.DEFAULT, 16, 1.2f, width, true);
    }
    assertTrue(service.wrappedLayoutCacheStats().entries() <= 128);
    assertTrue(service.resolvedPrimitiveCacheStats().entries() <= 256);
    var aggregate = service.cacheAggregateObservation(List.of(16L));
    assertEquals(1, aggregate.currentSnapshotWeights().size());
    assertTrue(aggregate.nativeWeight() > 0);
    assertTrue(aggregate.retainedWeight() >= aggregate.nativeWeight() + 16);
  }

  @Test
  void disabledModeExecutesUncachedCalculationPathWithoutRetention() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(TextDiagnosticCounter.values()));
    service =
        new FontServiceImpl(
            new FontStorageImpl(), false, diagnostics, TextCacheConfiguration.disabled());
    service.installSemanticOwner();
    TextMetrics first = service.measureText("uncached", 0, Font.DEFAULT, 16, 1.2f, 80, true);
    var firstCounters = diagnostics.snapshot();
    diagnostics.reset();
    TextMetrics second = service.measureText("uncached", 0, Font.DEFAULT, 16, 1.2f, 80, true);
    var secondCounters = diagnostics.snapshot();

    assertEquals(first, second);
    assertEquals(firstCounters.values(), secondCounters.values());
    assertEquals(firstCounters.saturatedCounterIds(), secondCounters.saturatedCounterIds());
    assertTrue(firstCounters.value(TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES) > 0);
    assertEquals(0, service.resolvedPrimitiveCacheStats().entries());
    assertEquals(0, service.wrappedLayoutCacheStats().entries());
    assertEquals(0, service.resolvedPrimitiveCacheStats().admissions());
    assertEquals(0, service.wrappedLayoutCacheStats().admissions());
  }
}
