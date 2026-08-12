package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.lwjgl.system.MemoryUtil.memFree;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSnapshot;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.jupiter.api.Test;

class NvgUtf8StagingTest {

  @Test
  void encodeCountsExactUtf8PayloadAndCurrentAllocationWithoutChangingBytes() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));

    ByteBuffer textBuffer = NvgUtf8Staging.encode("A\u96ea", diagnostics);
    try {
      assertEquals(List.of(0x41, 0xE9, 0x9B, 0xAA), unsignedBytes(textBuffer));
      assertEquals(4, diagnostics.snapshot().value(NvgDiagnosticCounter.UTF8_PAYLOAD_BYTES));
      assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.UTF8_ALLOCATION_CALLS));
      assertEquals(
          textBuffer.capacity(),
          diagnostics.snapshot().value(NvgDiagnosticCounter.UTF8_ALLOCATED_BYTES));
    } finally {
      memFree(textBuffer);
    }
  }

  @Test
  void disabledEncodeKeepsTheStableNoResultSnapshot() {
    DiagnosticSession diagnostics = DiagnosticSession.disabled();
    DiagnosticSnapshot before = diagnostics.snapshot();

    ByteBuffer textBuffer = NvgUtf8Staging.encode("same", diagnostics);
    try {
      assertEquals(List.of(0x73, 0x61, 0x6D, 0x65), unsignedBytes(textBuffer));
      assertSame(before, diagnostics.snapshot());
    } finally {
      memFree(textBuffer);
    }
  }

  private List<Integer> unsignedBytes(ByteBuffer buffer) {
    return java.util.stream.IntStream.range(buffer.position(), buffer.limit())
        .map(index -> Byte.toUnsignedInt(buffer.get(index)))
        .boxed()
        .toList();
  }
}
