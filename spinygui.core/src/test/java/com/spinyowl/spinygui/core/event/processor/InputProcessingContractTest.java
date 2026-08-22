package com.spinyowl.spinygui.core.event.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.event.WindowRefreshEvent;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemWindowRefreshEvent;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessorImpl;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProviderImpl;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class InputProcessingContractTest {

  @Test
  void fullRefreshDominatesBatchAndProcessorAggregation() {
    assertEquals(
        InputImpact.NO_IMPACT,
        InputImpact.NO_IMPACT.combine(InputImpact.NO_IMPACT));
    assertEquals(
        InputImpact.FULL_REFRESH,
        InputImpact.NO_IMPACT.combine(InputImpact.FULL_REFRESH));
    assertEquals(
        InputImpact.FULL_REFRESH,
        InputImpact.FULL_REFRESH.combine(InputImpact.NO_IMPACT));

    InputProcessingBatch batch = new InputProcessingBatch();
    batch.markKnownEffect();
    batch.markUnknownFallback();

    assertEquals(InputProcessingClassification.UNKNOWN_FALLBACK, batch.classification());
    assertEquals(InputImpact.FULL_UNKNOWN, batch.impact());
    assertEquals(
        InputImpact.FULL_UNKNOWN,
        InputImpact.HOVER_STYLE.combine(InputImpact.FULL_UNKNOWN));
  }

  @Test
  void emptyEventBatchIsProvenUnchangedAndCounted() {
    DefaultEventProcessor processor = new DefaultEventProcessor();

    assertEquals(InputImpact.NO_IMPACT, processor.processEvents());
    assertEquals(
        new InputProcessingCounters.Snapshot(1, 0, 0), processor.inputProcessingCounters());
  }

  @Test
  void eventWithNoListenerAndLegacyListenerAreUnknown() {
    Frame frame = new Frame();
    DefaultEventProcessor processor = new DefaultEventProcessor();
    WindowRefreshEvent event = event(frame);

    processor.push(event);
    assertEquals(InputImpact.FULL_UNKNOWN, processor.processEvents());
    assertEquals(
        new InputProcessingCounters.Snapshot(0, 0, 1), processor.inputProcessingCounters());

    frame.addListener(WindowRefreshEvent.class, ignored -> {});
    processor.push(event(frame));
    assertEquals(InputImpact.FULL_UNKNOWN, processor.processEvents());
    assertEquals(
        new InputProcessingCounters.Snapshot(0, 0, 2), processor.inputProcessingCounters());
  }

  @Test
  void legacySystemListenerUsesUnknownFallback() {
    Frame frame = new Frame();
    SystemEventListenerProviderImpl provider = new SystemEventListenerProviderImpl();
    provider.listener(SystemWindowRefreshEvent.class, (event, targetFrame) -> {});
    SystemEventProcessorImpl processor =
        SystemEventProcessorImpl.builder().eventListenerProvider(provider).build();

    processor.push(SystemWindowRefreshEvent.builder().frame(frame).build());

    assertEquals(InputImpact.FULL_UNKNOWN, processor.processEvents());
    assertEquals(
        new InputProcessingCounters.Snapshot(0, 0, 1), processor.inputProcessingCounters());
  }

  @Test
  void explicitKnownEffectCanBeReportedWithoutChangingLegacyDispatch() {
    InputProcessingBatch batch = new InputProcessingBatch();
    batch.markKnownEffect();

    assertEquals(InputProcessingClassification.KNOWN_EFFECT, batch.classification());
    assertEquals(InputImpact.FULL_REFRESH, batch.impact());
  }

  @Test
  void hoverDetailIsReturnedDirectly() {
    InputProcessingBatch batch = new InputProcessingBatch();
    batch.markHoverStyleEffect();

    assertEquals(InputProcessingClassification.KNOWN_EFFECT, batch.classification());
    assertEquals(InputImpact.HOVER_STYLE, batch.impact());
  }

  @Test
  void resultAwareDispatchPreservesLegacyEventDelivery() {
    Frame frame = new Frame();
    AtomicInteger legacyDeliveries = new AtomicInteger();
    AtomicInteger resultAwareDeliveries = new AtomicInteger();
    frame.addListener(WindowRefreshEvent.class, ignored -> legacyDeliveries.incrementAndGet());

    DefaultEventProcessor legacyProcessor = new DefaultEventProcessor();
    legacyProcessor.push(event(frame));
    legacyProcessor.processEvents();

    frame.removeListener(
        WindowRefreshEvent.class, frame.getListeners(WindowRefreshEvent.class).get(0));
    frame.addListener(WindowRefreshEvent.class, ignored -> resultAwareDeliveries.incrementAndGet());
    DefaultEventProcessor resultAwareProcessor = new DefaultEventProcessor();
    resultAwareProcessor.push(event(frame));
    assertEquals(
        InputImpact.FULL_UNKNOWN,
        resultAwareProcessor.processEvents());

    assertEquals(legacyDeliveries.get(), resultAwareDeliveries.get());
  }

  private static WindowRefreshEvent event(Frame frame) {
    return WindowRefreshEvent.builder().source(frame).target(frame).timestamp(0).build();
  }
}
