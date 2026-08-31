package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.impl.InlineFormattingContext;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.cache.TextCacheAggregateObservation;
import com.spinyowl.spinygui.core.system.cache.TextCacheConfiguration;
import com.spinyowl.spinygui.core.system.font.FontResourceObservation;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import com.spinyowl.spinygui.core.system.input.ControlTextLayoutService;
import com.spinyowl.spinygui.core.node.InputElement;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** M7 production calculation-path evidence including M4 and backend resource classes. */
class M7ProductionAggregateIntegrationTest {
  private FontServiceImpl service;

  @AfterEach
  void closeService() {
    if (service != null) service.close();
  }

  @Test
  void enabledAndDisabledContextsReportRealMixedFamilyRetention() {
    service = new FontServiceImpl(new FontStorageImpl(), false,
        com.spinyowl.spinygui.core.diagnostic.DiagnosticSession.disabled(),
        TextCacheConfiguration.boundedDefaults());
    service.installSemanticOwner();
    InlineFormattingContext enabled =
        new InlineFormattingContext(service, TextCacheConfiguration.boundedDefaults());
    Element parent = parent();
    Text text = NodeBuilder.text("aggregate cache path");
    parent.addChild(text);
    enabled.layout(parent, List.of(text), 0);
    enabled.layout(parent, List.of(text), 0);
    InputElement input = new InputElement();
    input.value("snapshot");
    ControlTextLayoutService controls = new ControlTextLayoutService(service);
    var snapshot = controls.query(input);

    NvgFontResourceObservation nativeFaces =
        new NvgFontResourceObservation(1, 11, 1, 2, 2, 2, 0, 0, 0, Set.of());
    NvgTextStagingObservation staging = new NvgTextStagingObservation(128, 1024, 2, 1, 0, 0, 32, false);
    FontResourceObservation core = service.resourceObservation();
    Map<String, com.spinyowl.spinygui.core.system.cache.BoundedTextCache.Stats> families = new HashMap<>(enabled.fontCalculationCacheStats());
    families.put("m4-prepared", enabled.preparedTextCacheStats());
    TextCacheAggregateObservation aggregate = service.cacheAggregateObservation(
        families,
        Map.of("core-font-bytes", core.ownerByteCapacity(),
            "nanovg-staging", (long) staging.retainedCapacityBytes()),
        Map.of("core-stb-info", (long) core.ownerStbInfoEntries(),
            "nanovg-faces", (long) nativeFaces.faceEntries(),
            "nanovg-buffers", (long) nativeFaces.bufferEntries()),
        List.of(snapshot.retainedWeight()));

    assertTrue(aggregate.families().get("m4-prepared").entries() > 0);
    assertTrue(aggregate.families().get("m4-prepared").hits() > 0);
    assertTrue(aggregate.families().get("font-chain").entries() > 0);
    assertTrue(aggregate.families().get("metrics").entries() > 0);
    assertTrue(aggregate.nativeWeight() >= core.ownerByteCapacity() + staging.retainedCapacityBytes());
    assertTrue(aggregate.nativeEntryCount() >= core.ownerStbInfoEntries() + nativeFaces.faceEntries());
    assertTrue(aggregate.retainedWeight() > aggregate.nativeWeight());
    enabled.close();
    service.close();
    service = new FontServiceImpl(new FontStorageImpl(), false,
        com.spinyowl.spinygui.core.diagnostic.DiagnosticSession.disabled(),
        TextCacheConfiguration.disabled());
    service.installSemanticOwner();

    InlineFormattingContext disabled =
        new InlineFormattingContext(service, TextCacheConfiguration.disabled());
    Element disabledParent = parent();
    Text disabledText = NodeBuilder.text("aggregate cache path");
    disabledParent.addChild(disabledText);
    disabled.layout(disabledParent, List.of(disabledText), 0);
    disabled.layout(disabledParent, List.of(disabledText), 0);
    Map<String, com.spinyowl.spinygui.core.system.cache.BoundedTextCache.Stats> disabledFamilies = new HashMap<>(disabled.fontCalculationCacheStats());
    disabledFamilies.put("m4-prepared", disabled.preparedTextCacheStats());
    FontResourceObservation disabledCore = service.resourceObservation();
    TextCacheAggregateObservation disabledAggregate = service.cacheAggregateObservation(
        disabledFamilies,
        Map.of("core-font-bytes", disabledCore.ownerByteCapacity()),
        Map.of("core-stb-info", (long) disabledCore.ownerStbInfoEntries()),
        List.of(16L));
    assertEquals(0, disabledAggregate.families().get("m4-prepared").entries());
    assertEquals(0, disabledAggregate.families().get("m4-prepared").retainedWeight());
    assertEquals(0, disabledAggregate.javaRetainedWeight());
    disabled.close();
  }

  private static Element parent() {
    Element parent = NodeBuilder.div();
    parent.resolvedStyle().display(Display.BLOCK);
    parent.resolvedStyle().fontFamilies(List.of("Roboto"));
    parent.resolvedStyle().whiteSpace(WhiteSpace.NORMAL);
    parent.resolvedStyle().tabSize(4);
    parent.resolvedStyle().fontSize(Length.pixel(16));
    parent.resolvedStyle().lineHeight(1.2f);
    parent.box().contentSize(160, 100);
    return parent;
  }
}
