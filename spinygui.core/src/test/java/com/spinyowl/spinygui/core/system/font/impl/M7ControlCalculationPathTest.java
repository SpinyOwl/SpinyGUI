package com.spinyowl.spinygui.core.system.font.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.cache.TextCacheConfiguration;
import com.spinyowl.spinygui.core.system.input.ControlTextLayoutService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** M7 control workload evidence through the M5 current-snapshot calculation path. */
class M7ControlCalculationPathTest {
  private FontServiceImpl service;

  @AfterEach
  void closeService() {
    if (service != null) service.close();
  }

  @Test
  void textareaWidthChurnInvokesWarmPrimitiveAndDistinctWrapPaths() {
    service = new FontServiceImpl(new FontStorageImpl(), false,
        com.spinyowl.spinygui.core.diagnostic.DiagnosticSession.disabled(),
        TextCacheConfiguration.boundedDefaults());
    service.installSemanticOwner();
    TextareaElement textarea = new TextareaElement("control calculation path");
    textarea.box().contentSize(160, 40);
    ControlTextLayoutService controls = new ControlTextLayoutService(service);

    var first = controls.query(textarea);
    long primitiveEntries = service.resolvedPrimitiveCacheStats().entries();
    long wrapEntries = service.wrappedLayoutCacheStats().entries();
    textarea.box().content().width(120);
    var second = controls.query(textarea);
    assertEquals(first.key().value(), second.key().value());
    assertEquals(primitiveEntries, service.resolvedPrimitiveCacheStats().entries());
    assertTrue(service.wrappedLayoutCacheStats().entries() >= wrapEntries);
    assertTrue(service.resolvedPrimitiveCacheStats().hits() > 0);
  }

  @Test
  void inputSnapshotWarmReuseAndDisabledMeasurementRemainCorrect() {
    service = new FontServiceImpl(new FontStorageImpl(), false,
        com.spinyowl.spinygui.core.diagnostic.DiagnosticSession.disabled(),
        TextCacheConfiguration.disabled());
    service.installSemanticOwner();
    InputElement input = new InputElement();
    input.value("uncached control");
    ControlTextLayoutService controls = new ControlTextLayoutService(service);
    var first = controls.query(input);
    var second = controls.query(input);
    assertEquals(first, second);
    assertEquals(0, service.resolvedPrimitiveCacheStats().entries());
    assertEquals(0, service.wrappedLayoutCacheStats().entries());
  }
}
