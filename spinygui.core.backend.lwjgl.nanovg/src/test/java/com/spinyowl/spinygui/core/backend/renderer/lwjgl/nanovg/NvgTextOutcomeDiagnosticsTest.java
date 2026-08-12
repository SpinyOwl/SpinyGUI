package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgTextOutcomeDiagnostics.TextPath;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSnapshot;
import java.util.List;
import org.junit.jupiter.api.Test;

class NvgTextOutcomeDiagnosticsTest {

  @Test
  void faceSelectionFailureIsAPathSpecificTerminalOutcomeAndNeverAClipCull() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));

    for (TextPath path : TextPath.values()) {
      NvgTextOutcomeDiagnostics.considered(diagnostics, path);
      NvgTextOutcomeDiagnostics.submitted(diagnostics, path);
      NvgTextOutcomeDiagnostics.considered(diagnostics, path);
      NvgTextOutcomeDiagnostics.faceSelectionFailed(diagnostics, path);
    }

    DiagnosticSnapshot snapshot = diagnostics.snapshot();
    for (TextPath path : TextPath.values()) {
      assertEquals(
          snapshot.value(path.consideredCounter()),
          snapshot.value(path.submittedCounter())
              + snapshot.value(path.culledCounter())
              + snapshot.value(path.faceSelectionFailedCounter()));
    }
    long pathFailures =
        java.util.Arrays.stream(TextPath.values())
            .mapToLong(path -> snapshot.value(path.faceSelectionFailedCounter()))
            .sum();
    assertEquals(pathFailures, snapshot.value(NvgDiagnosticCounter.FONT_FACE_FAILURES));
    assertEquals(0, snapshot.value(NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_CULLED));
    assertEquals(0, snapshot.value(NvgDiagnosticCounter.INPUT_TEXT_ITEMS_CULLED));
    assertEquals(0, snapshot.value(NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_CULLED));
    assertEquals(
        0, snapshot.value(NvgDiagnosticCounter.NORMAL_TEXT_CULLED_OUTSIDE_EFFECTIVE_CLIP));
    assertEquals(
        0, snapshot.value(NvgDiagnosticCounter.INPUT_TEXT_CULLED_OUTSIDE_EFFECTIVE_CLIP));
    assertEquals(
        0, snapshot.value(NvgDiagnosticCounter.TEXTAREA_TEXT_CULLED_OUTSIDE_EFFECTIVE_CLIP));
  }
}
