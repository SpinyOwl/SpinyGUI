package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgTextOutcomeDiagnostics.TextPath;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.style.types.Color;

/** The sole face-selection and terminal-outcome branch used by all text paths. */
final class NvgTextSubmission {
  private final NvgTextCommandSink commands;
  private final DiagnosticSession diagnostics;

  NvgTextSubmission(NvgTextCommandSink commands, DiagnosticSession diagnostics) {
    this.commands = commands;
    this.diagnostics = diagnostics;
  }

  boolean submit(
      long context,
      NvgTextCommand.TextPath commandPath,
      TextPath diagnosticPath,
      Font font,
      Float fontSize,
      Color color,
      String text,
      float x,
      float baseline) {
    NvgTextOutcomeDiagnostics.considered(diagnostics, diagnosticPath);
    commands.outcome(commandPath, diagnosticPath.consideredCounter());
    if (!commands.selectFace(context, commandPath, font)) {
      NvgTextOutcomeDiagnostics.faceSelectionFailed(diagnostics, diagnosticPath);
      commands.outcome(commandPath, diagnosticPath.faceSelectionFailedCounter());
      return false;
    }
    if (fontSize != null) commands.fontSize(context, fontSize);
    if (color != null) commands.fillColor(context, color);
    NvgTextOutcomeDiagnostics.submitted(diagnostics, diagnosticPath);
    commands.outcome(commandPath, diagnosticPath.submittedCounter());
    commands.text(context, commandPath, text, x, baseline);
    return true;
  }
}
