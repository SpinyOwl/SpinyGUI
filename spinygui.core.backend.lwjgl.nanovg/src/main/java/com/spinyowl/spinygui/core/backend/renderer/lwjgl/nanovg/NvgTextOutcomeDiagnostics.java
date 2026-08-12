package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;

/** Path-specific terminal text-submission outcomes, separate from conservative clip culling. */
final class NvgTextOutcomeDiagnostics {
  private NvgTextOutcomeDiagnostics() {}

  static void considered(DiagnosticSession diagnostics, TextPath path) {
    diagnostics.increment(path.considered);
  }

  static void submitted(DiagnosticSession diagnostics, TextPath path) {
    diagnostics.increment(path.submitted);
  }

  static void faceSelectionFailed(DiagnosticSession diagnostics, TextPath path) {
    diagnostics.increment(NvgDiagnosticCounter.FONT_FACE_FAILURES);
    diagnostics.increment(path.faceSelectionFailed);
  }

  enum TextPath {
    NORMAL(
        NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_CONSIDERED,
        NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_SUBMITTED,
        NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_CULLED,
        NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_FACE_SELECTION_FAILED),
    INPUT(
        NvgDiagnosticCounter.INPUT_TEXT_ITEMS_CONSIDERED,
        NvgDiagnosticCounter.INPUT_TEXT_ITEMS_SUBMITTED,
        NvgDiagnosticCounter.INPUT_TEXT_ITEMS_CULLED,
        NvgDiagnosticCounter.INPUT_TEXT_ITEMS_FACE_SELECTION_FAILED),
    TEXTAREA(
        NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_CONSIDERED,
        NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_SUBMITTED,
        NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_CULLED,
        NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_FACE_SELECTION_FAILED),
    ;

    private final NvgDiagnosticCounter considered;
    private final NvgDiagnosticCounter submitted;
    private final NvgDiagnosticCounter culled;
    private final NvgDiagnosticCounter faceSelectionFailed;

    TextPath(
        NvgDiagnosticCounter considered,
        NvgDiagnosticCounter submitted,
        NvgDiagnosticCounter culled,
        NvgDiagnosticCounter faceSelectionFailed) {
      this.considered = considered;
      this.submitted = submitted;
      this.culled = culled;
      this.faceSelectionFailed = faceSelectionFailed;
    }

    NvgDiagnosticCounter consideredCounter() {
      return considered;
    }

    NvgDiagnosticCounter submittedCounter() {
      return submitted;
    }

    NvgDiagnosticCounter culledCounter() {
      return culled;
    }

    NvgDiagnosticCounter faceSelectionFailedCounter() {
      return faceSelectionFailed;
    }
  }
}
