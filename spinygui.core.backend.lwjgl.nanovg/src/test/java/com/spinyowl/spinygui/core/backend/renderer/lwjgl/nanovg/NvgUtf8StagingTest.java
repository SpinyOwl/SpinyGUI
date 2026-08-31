package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSnapshot;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class NvgUtf8StagingTest {

  @Test
  void submitCountsExactUtf8PayloadAndReusesBoundedStorageWithoutChangingBytes() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));

    List<List<Integer>> submissions = new ArrayList<>();
    try (NvgUtf8Staging staging = new NvgUtf8Staging(diagnostics, 4, 8)) {
      staging.submit("A\u96ea", buffer -> submissions.add(unsignedBytes(buffer)));
      staging.submit("same", buffer -> submissions.add(unsignedBytes(buffer)));

      assertEquals(
          List.of(List.of(0x41, 0xE9, 0x9B, 0xAA), List.of(0x73, 0x61, 0x6D, 0x65)),
          submissions);
      assertEquals(8, diagnostics.snapshot().value(NvgDiagnosticCounter.UTF8_PAYLOAD_BYTES));
      assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.UTF8_ALLOCATION_CALLS));
      assertEquals(2, diagnostics.snapshot().value(NvgDiagnosticCounter.UTF8_REUSE_CALLS));
      assertEquals(4, staging.observation().retainedCapacityBytes());
      assertEquals(8, staging.observation().maximumRetainedCapacityBytes());
    }
  }

  @Test
  void oversizedSubmissionFreesOneShotEvenWhenNativeCallFailsAndDoesNotGrowRetention() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));
    NvgUtf8Staging staging = new NvgUtf8Staging(diagnostics, 4, 8);
    staging.submit("12345678", ignored -> {});

    IllegalStateException failure =
        assertThrows(
            IllegalStateException.class,
            () -> staging.submit("123456789", ignored -> { throw new IllegalStateException("native"); }));

    assertEquals("native", failure.getMessage());
    assertEquals(8, staging.observation().retainedCapacityBytes());
    assertEquals(1, staging.observation().oversizedAllocationCalls());
    assertEquals(9, staging.observation().oversizedFreedBytes());
    assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.UTF8_OVERSIZED_ALLOCATION_CALLS));
    assertEquals(9, diagnostics.snapshot().value(NvgDiagnosticCounter.UTF8_OVERSIZED_FREED_BYTES));
    staging.close();
    staging.close();
    assertTrue(staging.observation().closed());
    assertEquals(0, staging.observation().retainedCapacityBytes());
    assertThrows(IllegalStateException.class, () -> staging.submit("x", ignored -> {}));
  }

  @Test
  void resetFrameRetainsTheCapAndAllowsSubsequentReuse() {
    NvgUtf8Staging staging = new NvgUtf8Staging(DiagnosticSession.disabled(), 4, 8);
    staging.submit("12345678", ignored -> {});
    staging.resetFrame();
    staging.submit("x", ignored -> {});

    assertEquals(8, staging.observation().retainedCapacityBytes());
    assertEquals(2, staging.observation().reuseCalls());
    staging.close();
  }

  @Test
  void emptyTextUsesAnExplicitZeroLengthRange() {
    try (NvgUtf8Staging staging =
        new NvgUtf8Staging(DiagnosticSession.disabled(), 4, 8)) {
      staging.submit(
          "",
          buffer -> {
            assertEquals(0, buffer.position());
            assertEquals(0, buffer.remaining());
          });
      assertEquals(4, staging.observation().retainedCapacityBytes());
    }
  }

  @Test
  void resolvedRunStagesGlyphCodePointsDirectlyIncludingReplacementAndSupplementaryOutput() {
    ResolvedTextRun run =
        new ResolvedTextRun(
            0,
            3,
            Font.DEFAULT,
            List.of(
                new ResolvedGlyph(0, 1, 'A', 'A', Font.DEFAULT, false),
                new ResolvedGlyph(1, 2, 0x10FFFF, 0xFFFD, Font.DEFAULT, true),
                new ResolvedGlyph(2, 3, 0x1F600, 0x1F600, Font.DEFAULT, false)),
            12);
    try (NvgUtf8Staging staging =
        new NvgUtf8Staging(DiagnosticSession.disabled(), 4, 16)) {
      List<List<Integer>> bytes = new ArrayList<>();
      staging.submit(NvgRenderedText.run(run), buffer -> bytes.add(unsignedBytes(buffer)));

      assertEquals(
          List.of(List.of(0x41, 0xEF, 0xBF, 0xBD, 0xF0, 0x9F, 0x98, 0x80)), bytes);
      assertEquals(8, staging.observation().retainedCapacityBytes());
    }
  }

  @Test
  void disabledSubmissionKeepsTheStableNoResultSnapshot() {
    DiagnosticSession diagnostics = DiagnosticSession.disabled();
    DiagnosticSnapshot before = diagnostics.snapshot();

    try (NvgUtf8Staging staging = new NvgUtf8Staging(diagnostics, 4, 8)) {
      staging.submit(
          "same",
          textBuffer ->
              assertEquals(List.of(0x73, 0x61, 0x6D, 0x65), unsignedBytes(textBuffer)));
      assertSame(before, diagnostics.snapshot());
    }
  }

  private List<Integer> unsignedBytes(ByteBuffer buffer) {
    return java.util.stream.IntStream.range(buffer.position(), buffer.limit())
        .map(index -> Byte.toUnsignedInt(buffer.get(index)))
        .boxed()
        .toList();
  }
}
