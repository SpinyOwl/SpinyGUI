package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.style.types.Color;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class NanoVgTextCommandSinkTest {

  @Test
  void exactStateIsSuppressedOnlyUntilAnUnknownBoundary() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));
    RecordingNativeApi nativeApi = new RecordingNativeApi();
    NanoVgTextCommandSink sink =
        new NanoVgTextCommandSink(
            new NvgFontRegistry(), diagnostics, (font, context) -> font.fontFamily(), nativeApi);

    sink.beginScope(7, NvgTextCommand.TextPath.NORMAL);
    emitState(sink);
    emitState(sink);
    sink.unknownMutation();
    emitState(sink);
    sink.endScope(7, NvgTextCommand.TextPath.NORMAL);

    assertEquals(
        List.of(
            "save", "align:65", "face:Roboto", "size:16.0", "color:" + Color.BLACK,
            "align:65", "face:Roboto", "size:16.0", "color:" + Color.BLACK, "restore"),
        nativeApi.calls);
    assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.TEXT_ALIGN_CALLS_SUPPRESSED));
    assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.FONT_FACE_CALLS_SUPPRESSED));
    assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.FONT_SIZE_CALLS_SUPPRESSED));
    assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.FILL_COLOR_CALLS_SUPPRESSED));
    sink.close();
  }

  @Test
  void failedFaceInvalidatesKnowledgeAndTextUsesBoundedStaging() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));
    RecordingNativeApi nativeApi = new RecordingNativeApi();
    NanoVgTextCommandSink sink =
        new NanoVgTextCommandSink(
            new NvgFontRegistry(),
            diagnostics,
            (font, context) -> font == Font.NOTO_SANS_CJK_SC_REGULAR ? null : font.fontFamily(),
            nativeApi);
    sink.beginScope(7, NvgTextCommand.TextPath.INPUT);

    assertTrue(sink.selectFace(7, NvgTextCommand.TextPath.INPUT, Font.DEFAULT));
    assertFalse(sink.selectFace(7, NvgTextCommand.TextPath.INPUT, Font.NOTO_SANS_CJK_SC_REGULAR));
    assertTrue(sink.selectFace(7, NvgTextCommand.TextPath.INPUT, Font.DEFAULT));
    sink.text(7, NvgTextCommand.TextPath.INPUT, NvgRenderedText.literal("A\u96ea"), 2, 3);
    sink.text(7, NvgTextCommand.TextPath.INPUT, NvgRenderedText.literal("same"), 4, 5);

    assertEquals(List.of("save", "face:Roboto", "face:Roboto", "text:A\u96ea", "text:same"), nativeApi.calls);
    assertEquals(1, sink.stagingObservation().allocationCalls());
    assertEquals(2, sink.stagingObservation().reuseCalls());
    assertEquals(1_024, sink.stagingObservation().retainedCapacityBytes());
    sink.close();
    assertTrue(sink.stagingObservation().closed());
  }

  @Test
  void failedNativeStateEmissionForcesTheNextRequestToReemit() {
    RecordingNativeApi nativeApi = new RecordingNativeApi();
    NanoVgTextCommandSink sink =
        new NanoVgTextCommandSink(
            new NvgFontRegistry(),
            DiagnosticSession.disabled(),
            (font, context) -> font.fontFamily(),
            nativeApi);
    sink.beginScope(7, NvgTextCommand.TextPath.NORMAL);
    sink.align(7, 65);
    nativeApi.failNextAlignment = true;

    assertThrows(IllegalStateException.class, () -> sink.align(7, 66));
    sink.align(7, 65);

    assertEquals(List.of("save", "align:65", "align:65"), nativeApi.calls);
    sink.close();
  }

  private static void emitState(NanoVgTextCommandSink sink) {
    sink.align(7, 65);
    sink.selectFace(7, NvgTextCommand.TextPath.NORMAL, Font.DEFAULT);
    sink.fontSize(7, 16);
    sink.fillColor(7, Color.BLACK);
  }

  private static final class RecordingNativeApi implements NanoVgTextCommandSink.NativeApi {
    private final List<String> calls = new ArrayList<>();
    private boolean failNextAlignment;

    public void save(long context) { calls.add("save"); }
    public void restore(long context) { calls.add("restore"); }
    public void scissor(long context,float x,float y,float width,float height) { calls.add("scissor"); }
    public void intersectScissor(long context,float x,float y,float width,float height) { calls.add("intersect"); }
    public void resetScissor(long context) { calls.add("reset-scissor"); }
    public void transform(long context,float a,float b,float c,float d,float tx,float ty) { calls.add("transform"); }
    public void translate(long context,float x,float y) { calls.add("translate"); }
    public void align(long context,int value) {
      if (failNextAlignment) {
        failNextAlignment = false;
        throw new IllegalStateException("injected alignment failure");
      }
      calls.add("align:" + value);
    }
    public void fontFace(long context,String face) { calls.add("face:" + face); }
    public void fontSize(long context,float value) { calls.add("size:" + value); }
    public void fillColor(long context,Color color) { calls.add("color:" + color); }
    public void text(long context,float x,float y,ByteBuffer utf8) {
      calls.add("text:" + org.lwjgl.system.MemoryUtil.memUTF8(utf8));
    }
  }
}
