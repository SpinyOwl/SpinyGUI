package com.spinyowl.spinygui.benchmark.diagnostic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSnapshot;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class DiagnosticVocabularyTest {
  @Test
  void coreOnlySnapshotsRejectNanoVgReadsWhileDisabledSnapshotRemainsUniversalZero() {
    DiagnosticSnapshot coreOnly =
        DiagnosticSession.enabled(List.of(TextDiagnosticCounter.values())).snapshot();
    DiagnosticSnapshot disabled = DiagnosticSession.disabled().snapshot();

    assertThrows(
        IllegalArgumentException.class,
        () -> coreOnly.value(NvgDiagnosticCounter.TEXT_CALLS));
    assertThrows(
        IllegalArgumentException.class,
        () -> coreOnly.saturated(NvgDiagnosticCounter.TEXT_CALLS));
    assertEquals(0, disabled.value(NvgDiagnosticCounter.TEXT_CALLS));
    assertFalse(disabled.saturated(NvgDiagnosticCounter.TEXT_CALLS));
  }

  @Test
  void completeVocabularyHasStableUniqueIdsUnitsAndDescriptions() {
    Set<String> expected =
        Set.of(
            "core.control.input-complete-layouts",
            "core.control.textarea-complete-layouts",
            "core.text.character-builder-appends",
            "core.text.character-builder-freezes",
            "core.text.caret-boundary-builder-appends",
            "core.text.caret-boundary-builder-freezes",
            "core.text.caret-stop-search-comparisons",
            "core.text.complete-measurements",
            "core.text.font-chain-resolutions",
            "core.text.advance-slot-builder-appends",
            "core.text.advance-slot-builder-freezes",
            "core.text.glyph-slot-builder-appends",
            "core.text.glyph-slot-builder-freezes",
            "core.text.glyph-slots-copied",
            "core.text.glyph-slots-moved",
            "core.text.initial-resolution.glyph-slots-copied",
            "core.text.logical-glyph-resolutions",
            "core.text.line-builder-appends",
            "core.text.line-builder-freezes",
            "core.text.native-glyph-advance-calls",
            "core.text.native-glyph-index-probes",
            "core.text.native-kerning-calls",
            "core.text.normalization-scans",
            "core.text.range-materialization.glyph-slots-copied",
            "core.text.range-preparations",
            "core.text.range-temporary-strings",
            "core.text.result-builder-freezes",
            "core.text.run-builder-appends",
            "core.text.run-builder-freezes",
            "core.text.source-code-points-scanned",
            "core.text.wrap.primitive-visits",
            "core.text-measurer.get-text-caret-metrics-font-list.entries",
            "core.text-measurer.get-text-caret-metrics-font.entries",
            "core.text-measurer.get-text-line-metrics-font-list.entries",
            "core.text-measurer.get-text-line-metrics-font.entries",
            "core.text-measurer.get-text-metrics-font.entries",
            "core.text-measurer.measure-text-font-full.entries",
            "core.text-measurer.measure-text-font-list-full.entries",
            "core.text-measurer.measure-text-font-list.entries",
            "core.text-measurer.measure-text-font.entries",
            "nanovg.calls.fill-color",
            "nanovg.calls.font-face",
            "nanovg.calls.font-size",
            "nanovg.calls.intersect-scissor",
            "nanovg.calls.reset-scissor",
            "nanovg.calls.restore",
            "nanovg.calls.save",
            "nanovg.calls.scissor",
            "nanovg.calls.text",
            "nanovg.calls.text-align",
            "nanovg.calls.transform",
            "nanovg.calls.translate",
            "nanovg.input-text.cull-reason.outside-effective-clip",
            "nanovg.input-text.items-considered",
            "nanovg.input-text.items-culled",
            "nanovg.input-text.items-face-selection-failed",
            "nanovg.input-text.items-submitted",
            "nanovg.normal-text.cull-reason.outside-effective-clip",
            "nanovg.normal-text.items-considered",
            "nanovg.normal-text.items-culled",
            "nanovg.normal-text.items-face-selection-failed",
            "nanovg.normal-text.items-submitted",
            "nanovg.render.node-visits",
            "nanovg.results.font-face-failures",
            "nanovg.textarea.line-cull-reason.outside-effective-clip",
            "nanovg.textarea.lines-considered",
            "nanovg.textarea.lines-culled",
            "nanovg.textarea.lines-submitted",
            "nanovg.textarea.text-cull-reason.outside-effective-clip",
            "nanovg.textarea.text-items-considered",
            "nanovg.textarea.text-items-culled",
            "nanovg.textarea.text-items-face-selection-failed",
            "nanovg.textarea.text-items-submitted",
            "nanovg.utf8.allocated-bytes",
            "nanovg.utf8.allocation-calls",
            "nanovg.utf8.payload-bytes");
    Set<DiagnosticCounter> vocabulary =
        Stream.concat(
                Arrays.stream(TextDiagnosticCounter.values()),
                Arrays.stream(NvgDiagnosticCounter.values()))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    Set<String> actual = vocabulary.stream().map(DiagnosticCounter::id).collect(Collectors.toSet());

    assertEquals(expected, actual);
    assertEquals(expected.size(), vocabulary.size());
    assertEquals("core-text-diagnostics-7", TextDiagnosticCounter.VOCABULARY_VERSION);
    assertEquals("nanovg-text-diagnostics-3", NvgDiagnosticCounter.VOCABULARY_VERSION);
    vocabulary.forEach(
        counter -> {
          assertFalse(counter.description().isBlank(), counter.id());
          assertFalse(counter.unit().name().isBlank(), counter.id());
        });
  }
}
