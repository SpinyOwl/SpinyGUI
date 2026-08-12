package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.system.MemoryUtil.memUTF8;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import java.nio.ByteBuffer;

/** Existing unbounded UTF-8 staging with an optional diagnostic hook. */
final class NvgUtf8Staging {
  private NvgUtf8Staging() {}

  static ByteBuffer encode(String text, DiagnosticSession diagnostics) {
    ByteBuffer textBuffer = memUTF8(text, false);
    diagnostics.increment(NvgDiagnosticCounter.UTF8_ALLOCATION_CALLS);
    diagnostics.add(NvgDiagnosticCounter.UTF8_PAYLOAD_BYTES, textBuffer.remaining());
    diagnostics.add(NvgDiagnosticCounter.UTF8_ALLOCATED_BYTES, textBuffer.capacity());
    return textBuffer;
  }
}
