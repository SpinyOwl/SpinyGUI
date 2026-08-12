package com.spinyowl.spinygui.core.diagnostic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class DiagnosticSessionTest {
  private static volatile DiagnosticSession measuredDisabledSession = DiagnosticSession.disabled();

  @Test
  void distinguishesLogicalResolutionFromCandidateNativeProbes() {
    DiagnosticSession session = session();

    session.increment(TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS);
    session.add(TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES, 3);

    DiagnosticSnapshot snapshot = session.snapshot();
    assertEquals(1, snapshot.value(TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS));
    assertEquals(3, snapshot.value(TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES));
  }

  @Test
  void actualDefaultMethodsAddEveryEnteredApiCounterButOnlyOneCompleteMeasurement() {
    DiagnosticSession session = session();
    CountingTextMeasurer fixture = new CountingTextMeasurer(session);

    fixture.measureText("one", List.of(Font.DEFAULT), 16, 1.2f);
    assertCounts(
        session.snapshot(),
        TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_ENTRIES,
        TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES,
        TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES);

    session.reset();
    fixture.measureText("two", 0, List.of(Font.DEFAULT), 16, 1.2f, 100, false);
    assertCounts(
        session.snapshot(),
        TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES,
        TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES);

    session.reset();
    fixture.getTextLineMetrics("three", List.of(Font.DEFAULT), 16, 1.2f);
    assertCounts(
        session.snapshot(),
        TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_LIST_ENTRIES,
        TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_ENTRIES,
        TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES,
        TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES);

    session.reset();
    fixture.getTextCaretMetrics("four", List.of(Font.DEFAULT), 16, 1);
    assertCounts(
        session.snapshot(),
        TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_LIST_ENTRIES,
        TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_ENTRIES);
  }

  @Test
  void resetAndSnapshotsDoNotLeakAcrossSamples() {
    DiagnosticSession session = session();
    session.add(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED, 5);
    DiagnosticSnapshot first = session.snapshot();

    session.reset();
    DiagnosticSnapshot reset = session.snapshot();
    session.add(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED, 2);
    DiagnosticSnapshot second = session.snapshot();

    assertEquals(5, first.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED));
    assertEquals(0, reset.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED));
    assertEquals(2, second.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED));
    assertEquals(5, first.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED));
    assertThrows(
        UnsupportedOperationException.class,
        () -> second.values().put("core.text.illegal", 1L));
  }

  @Test
  void disabledHooksAndReadsAreStableNoOpsOnAnyThread() throws InterruptedException {
    DiagnosticSession disabled = DiagnosticSession.disabled();
    DiagnosticSnapshot first = disabled.snapshot();
    disabled.increment(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS);
    disabled.add(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED, Long.MAX_VALUE);
    disabled.reset();
    AtomicReference<DiagnosticSnapshot> otherThreadSnapshot = new AtomicReference<>();
    Thread thread = new Thread(() -> otherThreadSnapshot.set(disabled.snapshot()));
    thread.start();
    thread.join();

    assertFalse(disabled.enabled());
    assertSame(first, disabled.snapshot());
    assertSame(first, otherThreadSnapshot.get());
    assertTrue(first.values().isEmpty());
    assertEquals(0, first.value(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS));
    DiagnosticCounter unknown = testCounter("test.disabled-unknown");
    assertEquals(0, first.value(unknown));
    assertFalse(first.saturated(unknown));
  }

  @Test
  void repeatedDisabledHooksAllocateZeroBytesAndDeclareNoSynchronization() {
    com.sun.management.ThreadMXBean allocationBean =
        (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
    assertTrue(allocationBean.isThreadAllocatedMemorySupported());
    if (!allocationBean.isThreadAllocatedMemoryEnabled()) {
      allocationBean.setThreadAllocatedMemoryEnabled(true);
    }
    long threadId = Thread.currentThread().threadId();
    exerciseDisabledHooks(1_000_000);

    long before = allocationBean.getThreadAllocatedBytes(threadId);
    long checksum = exerciseDisabledHooks(1_000_000);
    long allocatedBytes = allocationBean.getThreadAllocatedBytes(threadId) - before;

    assertEquals(0, checksum);
    assertEquals(0, allocatedBytes, "disabled diagnostic hooks allocated bytes");
    Set<String> measuredHooks = Set.of("enabled", "increment", "add", "reset", "snapshot");
    for (Method method : DiagnosticSession.class.getDeclaredMethods()) {
      if (measuredHooks.contains(method.getName())) {
        assertFalse(
            Modifier.isSynchronized(method.getModifiers()),
            () -> method.getName() + " must not synchronize");
      }
    }
  }

  @Test
  void overflowSaturatesAndResetClearsTheVisibleSaturationFlag() {
    DiagnosticSession session = session();
    session.add(TextDiagnosticCounter.GLYPH_SLOTS_MOVED, Long.MAX_VALUE);
    session.increment(TextDiagnosticCounter.GLYPH_SLOTS_MOVED);

    DiagnosticSnapshot saturated = session.snapshot();
    assertEquals(Long.MAX_VALUE, saturated.value(TextDiagnosticCounter.GLYPH_SLOTS_MOVED));
    assertTrue(saturated.saturated(TextDiagnosticCounter.GLYPH_SLOTS_MOVED));

    session.reset();
    DiagnosticSnapshot reset = session.snapshot();
    assertEquals(0, reset.value(TextDiagnosticCounter.GLYPH_SLOTS_MOVED));
    assertFalse(reset.saturated(TextDiagnosticCounter.GLYPH_SLOTS_MOVED));
  }

  @Test
  void enabledSessionRejectsCrossThreadAggregation() throws InterruptedException {
    DiagnosticSession session = session();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread thread =
        new Thread(
            () -> {
              try {
                session.increment(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS);
              } catch (Throwable thrown) {
                failure.set(thrown);
              }
            });
    thread.start();
    thread.join();

    assertTrue(failure.get() instanceof IllegalStateException);
    assertEquals(0, session.snapshot().value(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS));
  }

  @Test
  void enabledVocabularyIsClosedAndRejectsNegativeCounts() {
    DiagnosticSession session = session();
    DiagnosticCounter unregistered = testCounter("test.unregistered");

    assertThrows(IllegalArgumentException.class, () -> session.increment(unregistered));
    assertThrows(
        IllegalArgumentException.class,
        () -> session.add(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS, -1));
    assertThrows(IllegalArgumentException.class, () -> DiagnosticSession.enabled(List.of()));
  }

  @Test
  void enabledSnapshotsRejectReadsOutsideTheirDeclaredVocabulary() {
    DiagnosticSnapshot snapshot = session().snapshot();
    DiagnosticCounter unknown = testCounter("test.snapshot-unknown");

    assertThrows(IllegalArgumentException.class, () -> snapshot.value(unknown));
    assertThrows(IllegalArgumentException.class, () -> snapshot.saturated(unknown));
  }

  @Test
  void enabledVocabularyRejectsMalformedAndDuplicateIds() {
    for (String malformed :
        List.of("a.", "a..b", "a.-b", "a-.b", "a--b", ".a", "a.b-", "a._b")) {
      assertThrows(
          IllegalArgumentException.class,
          () -> DiagnosticSession.enabled(List.of(testCounter(malformed))),
          malformed);
    }
    assertThrows(
        IllegalArgumentException.class,
        () ->
            DiagnosticSession.enabled(
                List.of(testCounter("test.duplicate"), testCounter("test.duplicate"))));
  }

  @Test
  void everyTextMeasurerEntryPointHasOneDistinctEntryCounter() throws NoSuchMethodException {
    Map<Method, TextDiagnosticCounter> entries =
        Map.ofEntries(
            Map.entry(
                TextMeasurer.class.getDeclaredMethod(
                    "measureText", String.class, List.class, float.class, float.class),
                TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_ENTRIES),
            Map.entry(
                TextMeasurer.class.getDeclaredMethod(
                    "measureText",
                    String.class,
                    float.class,
                    List.class,
                    float.class,
                    float.class,
                    float.class,
                    boolean.class),
                TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES),
            Map.entry(
                TextMeasurer.class.getDeclaredMethod(
                    "getTextLineMetrics", String.class, List.class, float.class, float.class),
                TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_LIST_ENTRIES),
            Map.entry(
                TextMeasurer.class.getDeclaredMethod(
                    "getTextCaretMetrics", String.class, List.class, float.class, float.class),
                TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_LIST_ENTRIES),
            Map.entry(
                TextMeasurer.class.getDeclaredMethod(
                    "measureText", String.class, Font.class, float.class, float.class),
                TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_ENTRIES),
            Map.entry(
                TextMeasurer.class.getDeclaredMethod(
                    "measureText",
                    String.class,
                    float.class,
                    Font.class,
                    float.class,
                    float.class,
                    float.class,
                    boolean.class),
                TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES),
            Map.entry(
                TextMeasurer.class.getDeclaredMethod(
                    "getTextMetrics",
                    String.class,
                    float.class,
                    Font.class,
                    float.class,
                    float.class,
                    float.class,
                    boolean.class),
                TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_METRICS_FONT_ENTRIES),
            Map.entry(
                TextMeasurer.class.getDeclaredMethod(
                    "getTextLineMetrics", String.class, Font.class, float.class, float.class),
                TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_ENTRIES),
            Map.entry(
                TextMeasurer.class.getDeclaredMethod(
                    "getTextCaretMetrics", String.class, Font.class, float.class, float.class),
                TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_ENTRIES));
    Set<Method> declaredEntries =
        Arrays.stream(TextMeasurer.class.getDeclaredMethods())
            .filter(method -> !method.isSynthetic() && !method.getName().equals("diagnostics"))
            .collect(Collectors.toSet());
    Set<TextDiagnosticCounter> vocabularyEntries =
        Arrays.stream(TextDiagnosticCounter.values())
            .filter(counter -> counter.name().startsWith("TEXT_MEASURER_"))
            .collect(Collectors.toSet());

    assertEquals(declaredEntries, entries.keySet());
    assertEquals(vocabularyEntries, Set.copyOf(entries.values()));
    assertEquals(entries.size(), Set.copyOf(entries.values()).size());
  }

  private static DiagnosticSession session() {
    return DiagnosticSession.enabled(List.of(TextDiagnosticCounter.values()));
  }

  private static long exerciseDisabledHooks(int repetitions) {
    DiagnosticSession disabled = measuredDisabledSession;
    long checksum = 0;
    for (int index = 0; index < repetitions; index++) {
      disabled.increment(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS);
      disabled.add(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED, 2);
      disabled.reset();
      checksum += disabled.enabled() ? 1 : 0;
      checksum += disabled.snapshot().values().size();
    }
    return checksum;
  }

  private static void assertCounts(
      DiagnosticSnapshot snapshot, TextDiagnosticCounter... enteredApiCounters) {
    Set<TextDiagnosticCounter> expected = Set.of(enteredApiCounters);
    for (TextDiagnosticCounter counter : TextDiagnosticCounter.values()) {
      long expectedValue =
          expected.contains(counter) || counter == TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS
              ? 1
              : 0;
      assertEquals(expectedValue, snapshot.value(counter), counter.id());
    }
  }

  private static DiagnosticCounter testCounter(String id) {
    return new DiagnosticCounter() {
      @Override
      public String id() {
        return id;
      }

      @Override
      public DiagnosticUnit unit() {
        return DiagnosticUnit.CALLS;
      }

      @Override
      public String description() {
        return "Test counter.";
      }
    };
  }

  private static final class CountingTextMeasurer implements TextMeasurer {
    private static final FontMetrics FONT_METRICS = new FontMetrics(8, 2, 0, 10, 8);

    private final DiagnosticSession session;

    private CountingTextMeasurer(DiagnosticSession session) {
      this.session = session;
    }

    @Override
    public DiagnosticSession diagnostics() {
      return session;
    }

    @Override
    public TextMetrics measureText(String text, Font font, float fontSize, float lineHeight) {
      session.increment(TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_ENTRIES);
      return completeTextMeasurement(text);
    }

    @Override
    public TextMetrics measureText(
        String text,
        float offsetX,
        Font font,
        float fontSize,
        float lineHeight,
        float maxWidth,
        boolean wordWrap) {
      session.increment(TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES);
      return completeTextMeasurement(text);
    }

    @Override
    public TextMetrics getTextMetrics(
        String text,
        float offsetX,
        Font font,
        float fontSize,
        float lineHeight,
        float maxWidth,
        boolean wordWrap) {
      session.increment(TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_METRICS_FONT_ENTRIES);
      return completeTextMeasurement(text);
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        String text, Font font, float fontSize, float lineHeight) {
      session.increment(TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_ENTRIES);
      return completeTextMeasurement(text).lines().getFirst();
    }

    @Override
    public TextCaretMetrics getTextCaretMetrics(
        String text, Font font, float fontSize, float offsetX) {
      session.increment(TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_ENTRIES);
      session.increment(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS);
      return new TextCaretMetrics(0, 0);
    }

    private TextMetrics completeTextMeasurement(String text) {
      session.increment(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS);
      TextLineMetrics line =
          TextLineMetrics.builder()
              .characters(text)
              .startIndex(0)
              .endIndex(text.length())
              .charCount(text.length())
              .fontMetrics(FONT_METRICS)
              .build();
      return TextMetrics.builder().line(line).fontMetrics(FONT_METRICS).build();
    }
  }
}
